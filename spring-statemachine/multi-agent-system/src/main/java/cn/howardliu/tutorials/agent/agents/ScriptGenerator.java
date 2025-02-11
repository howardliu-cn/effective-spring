package cn.howardliu.tutorials.agent.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@AiService
public interface ScriptGenerator {
    @UserMessage("""
            你是一名Python专家。根据给定的需求，仅创建Python CLI应用程序脚本。
            仅包含代码本身，不要包含任何解释、注释或额外文本。

            需求: {{requirements}}
            """)
    String generateScript(@V("requirements") String requirements);
}
