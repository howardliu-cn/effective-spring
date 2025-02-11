package cn.howardliu.tutorials.agent.state;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import cn.howardliu.tutorials.agent.agents.RequirementsEvaluator;
import cn.howardliu.tutorials.agent.agents.RequirementsRewriter;
import cn.howardliu.tutorials.agent.agents.ScriptGenerator;
import cn.howardliu.tutorials.agent.agents.SolutionVerifier;
import cn.howardliu.tutorials.agent.pojo.Variables;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@Configuration
@EnableStateMachineFactory
@AllArgsConstructor
@Getter
@Slf4j
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> {
    private final RequirementsEvaluator requirementsEvaluator;
    private final ScriptGenerator scriptGenerator;
    private final SolutionVerifier solutionVerifier;
    private final RequirementsRewriter requirementsRewriter;

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states
                .withStates()
                .initial(States.AWAITING_INPUT)
                .state(States.REQUIREMENTS_EVALUATION, evaluateRequirementsAction())
                .state(States.SCRIPT_GENERATION, generateScriptAction())
                .state(States.SOLUTION_VERIFICATION, verifySolutionAction())
                .state(States.REQUIREMENTS_REVISION, rewriteRequirementsAction())
                .end(States.SUCCESSFUL_COMPLETION)
                .end(States.INVALID_REQUIREMENTS);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
                .withExternal()
                .source(States.AWAITING_INPUT)
                .target(States.REQUIREMENTS_EVALUATION)
                .event(Events.INPUT_RECEIVED)
                .and()
                .withExternal()
                .source(States.REQUIREMENTS_EVALUATION)
                .target(States.SCRIPT_GENERATION)
                .event(Events.REQUIREMENTS_EVALUATED)
                .and()
                .withExternal()
                .source(States.REQUIREMENTS_EVALUATION)
                .target(States.INVALID_REQUIREMENTS)
                .event(Events.REQUIREMENTS_REJECTED)
                .and()
                .withExternal()
                .source(States.SCRIPT_GENERATION)
                .target(States.SOLUTION_VERIFICATION)
                .event(Events.SCRIPT_GENERATED)
                .and()
                .withExternal()
                .source(States.SOLUTION_VERIFICATION)
                .target(States.SUCCESSFUL_COMPLETION)
                .event(Events.SOLUTION_VERIFIED)
                .and()
                .withExternal()
                .source(States.SOLUTION_VERIFICATION)
                .target(States.REQUIREMENTS_REVISION)
                .event(Events.SOLUTION_REJECTED)
                .and()
                .withExternal()
                .source(States.REQUIREMENTS_REVISION)
                .target(States.SCRIPT_GENERATION)
                .event(Events.REQUIREMENTS_REWRITTEN);
    }

    private Action<States, Events> evaluateRequirementsAction() {
        return stateContext -> {
            log.info("开始评估需求...");
            final String requirements = getVariable(stateContext, Variables.REQUIREMENTS);
            final boolean valid = requirementsEvaluator.areRequirementsFeasible(requirements);
            log.info("需求是否符合要求? {}", valid);
            if (valid) {
                sendEvent(stateContext.getStateMachine(), Events.REQUIREMENTS_EVALUATED);
            } else {
                sendEvent(stateContext.getStateMachine(), Events.REQUIREMENTS_REJECTED);
            }
        };
    }

    private Action<States, Events> generateScriptAction() {
        return stateContext -> {
            log.info("开始生成脚本...");
            final String requirements = getVariable(stateContext, Variables.REQUIREMENTS);
            final String script = scriptGenerator.generateScript(requirements);
            stateContext.getExtendedState().getVariables().put(Variables.SCRIPT, script);
            sendEvent(stateContext.getStateMachine(), Events.SCRIPT_GENERATED);
        };
    }

    private Action<States, Events> verifySolutionAction() {
        return stateContext -> {
            log.info("校验脚本是否能够解决问题...");
            final String requirements = getVariable(stateContext, Variables.REQUIREMENTS);
            final String script = getVariable(stateContext, Variables.SCRIPT);
            final boolean scriptValid = solutionVerifier.isScriptValid(requirements, script);
            if (scriptValid) {
                sendEvent(stateContext.getStateMachine(), Events.SOLUTION_VERIFIED);
            } else {
                sendEvent(stateContext.getStateMachine(), Events.SOLUTION_REJECTED);
            }
        };
    }

    private Action<States, Events> rewriteRequirementsAction() {
        return stateContext -> {
            log.info("重写需求...");
            final String requirements = getVariable(stateContext, Variables.REQUIREMENTS);
            final String script = getVariable(stateContext, Variables.SCRIPT);
            final String rewrittenRequirements = requirementsRewriter.rewriteRequirements(requirements, script);
            log.info("重写后的需求内容是：{}", rewrittenRequirements);
            stateContext.getExtendedState().getVariables().put(Variables.REQUIREMENTS, rewrittenRequirements);
            sendEvent(stateContext.getStateMachine(), Events.REQUIREMENTS_REWRITTEN);
        };
    }

    private String getVariable(StateContext<States, Events> stateContext, String key) {
        return stateContext.getExtendedState().getVariables().get(key).toString();
    }

    private void sendEvent(StateMachine<States, Events> stateMachine, Events event) {
        final Message<Events> message = MessageBuilder.withPayload(event).build();
        stateMachine.sendEvent(Mono.just(message)).subscribe();
    }
}
