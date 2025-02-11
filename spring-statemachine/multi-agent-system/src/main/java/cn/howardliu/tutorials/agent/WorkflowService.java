package cn.howardliu.tutorials.agent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Service;

import cn.howardliu.tutorials.agent.pojo.Variables;
import cn.howardliu.tutorials.agent.state.Events;
import cn.howardliu.tutorials.agent.state.States;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@Service
@Slf4j
@AllArgsConstructor
public class WorkflowService {
    private final StateMachineFactory<States, Events> factory;

    public String generateScript(String requirements) {
        final CompletableFuture<String> resultFuture = new CompletableFuture<String>();
        final StateMachine<States, Events> stateMachine = factory.getStateMachine();
        addStateListener(stateMachine, resultFuture);

        try {
            stateMachine.startReactively()
                    .doOnError(resultFuture::completeExceptionally)
                    .subscribe();

            // 设置初始变量（需求）
            stateMachine.getExtendedState().getVariables().put(Variables.REQUIREMENTS, requirements);
            // 触发第一个事件
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(Events.INPUT_RECEIVED).build())).subscribe();

            return resultFuture.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("状态机执行异常: {}", e.getMessage());
            throw new IllegalStateException(e);
        } finally {
            stateMachine.stopReactively().block();
        }
    }

    private void addStateListener(StateMachine<States, Events> stateMachine, CompletableFuture<String> resultFuture) {
        stateMachine.addStateListener(new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                log.info("状态变更, {} ---> {}",
                        Optional.ofNullable(from).map(State::getId).orElse(null),
                        Optional.ofNullable(to).map(State::getId).orElse(null));
                if (to == null) {
                    return;
                }

                switch (to.getId()) {
                    case SUCCESSFUL_COMPLETION: {
                        final Object resultObj = stateMachine.getExtendedState().getVariables().get(Variables.SCRIPT);
                        if (resultObj != null) {
                            resultFuture.complete(resultObj.toString());
                        } else {
                            log.error("状态机成功结束，但是结果为空，肯定出问题了");
                            resultFuture.completeExceptionally(
                                    new IllegalStateException("状态机成功结束，但是结果为空，肯定出问题了"));
                        }
                        break;
                    }
                    case INVALID_REQUIREMENTS: {
                        log.warn("检测到非法需求，工作流结束。");
                        resultFuture.complete("非法需求，一定是不合理的需求，描述不清晰或者太复杂。");
                        break;
                    }
                }
            }

            @Override
            public void stateMachineError(StateMachine<States, Events> stateMachine, Exception exception) {
                log.error("状态机运行异常: {}", exception.getMessage());
                resultFuture.completeExceptionally(exception);
            }
        });
    }
}
