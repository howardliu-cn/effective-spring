package cn.howardliu.tutorials.agent.state;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
public enum States {
    AWAITING_INPUT,
    REQUIREMENTS_EVALUATION,
    SCRIPT_GENERATION,
    SOLUTION_VERIFICATION,
    REQUIREMENTS_REVISION,
    SUCCESSFUL_COMPLETION,
    INVALID_REQUIREMENTS
}
