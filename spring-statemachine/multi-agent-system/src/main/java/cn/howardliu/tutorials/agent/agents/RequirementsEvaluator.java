package cn.howardliu.tutorials.agent.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@AiService
public interface RequirementsEvaluator {
    @UserMessage("""
            评估给定的需求，以确定它们是否清晰、简洁且可以在单个Python文件中实现。
            如果需求清晰且可实现，则返回 true；否则，返回 false。

            ## 评估标准
                清晰：需求明确表达了要实现的功能
                简洁：需求没有冗余，直接了当
                可行：需求可以在单个Python文件中实现，而不依赖过多外部资源或复杂配置

            需求: {{requirements}}
            """)
    boolean areRequirementsFeasible(@V("requirements") String requirements);
}
