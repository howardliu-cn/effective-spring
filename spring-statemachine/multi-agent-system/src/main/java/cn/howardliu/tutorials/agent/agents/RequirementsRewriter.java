package cn.howardliu.tutorials.agent.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@AiService
public interface RequirementsRewriter {
    @UserMessage("""
            Python脚本未能满足指定的需求。
            改写需求，保持原始意图不变的前提下，增加必要的改进内容，能够更加清晰描述需求。

            需求: {{requirements}}
            脚本: {{script}}

            返回改进后的需求。

            ## 具体步骤：
            1. 理解原始需求：仔细阅读并理解当前的需求和脚本。
            2. 识别问题：找出脚本未能满足需求的具体原因。
            3. 改写需求：根据问题点改进需求描述，确保新需求清晰、简洁且可实现。
            4. 保持意图：确保改写后的需求仍然表达了原始意图。
            
            ## 约束
            修改后的需求使用与原始需求相同的语言。
            """)
    String rewriteRequirements(@V("requirements") String requirements, @V("script") String script);
}
