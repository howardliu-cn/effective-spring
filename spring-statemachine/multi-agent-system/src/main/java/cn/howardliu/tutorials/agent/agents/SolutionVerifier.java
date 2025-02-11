package cn.howardliu.tutorials.agent.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@AiService
public interface SolutionVerifier {
    @UserMessage("""
            审查提供的Python脚本，确保它准确地解决了需求中描述的问题。
            如果脚本不符合要求，返回 false 并指定问题。

            需求: {{requirements}}
            脚本: {{script}}
            """)
    boolean isScriptValid(@V("script") String script, @V("requirements") String requirements);
}
