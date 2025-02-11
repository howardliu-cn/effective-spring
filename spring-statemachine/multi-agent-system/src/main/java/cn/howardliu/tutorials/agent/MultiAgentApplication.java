package cn.howardliu.tutorials.agent;

import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author 看山 howarldiu.cn <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2025-02-07
 */
@SpringBootApplication
public class MultiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiAgentApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(WorkflowService workflowService) {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("""
                        欢迎使用Python CLI应用程序生成器！

                        请描述您需要的应用程序的需求，清楚且简洁地描述需求，需求不要太复杂，确保可以在单个Python文件中实现。

                        示例1：创建一个Python CLI，用于在摄氏度和华氏度之间转换温度。
                        示例2：给你两个按 非递减顺序 排列的整数数组 nums1 和 nums2，另有两个整数 m 和 n ，分别表示 nums1 和 nums2 中的元素数目。请你 合并 nums2 到 nums1 中，使合并后的数组同样按 非递减顺序 排列。注意：最终，合并后数组不应由函数返回，而是存储在数组 nums1 中。为了应对这种情况，nums1 的初始长度为 m + n，其中前 m 个元素表示应合并的元素，后 n 个元素为 0 ，应忽略。nums2 的长度为 n 。
                        示例3：计算10位圆周率

                        在下方输入您的需求：
                        """);

                System.out.print("> ");
                final String userInput = scanner.nextLine().trim();

                if (userInput.isEmpty()) {
                    System.err.println("输入为空，跳过执行。退出...");
                    return;
                }

                final String response = workflowService.generateScript(userInput);
                System.out.println("\n--- 结果 ---\n");
                System.out.println(response);
                System.out.println("\n----------------\n");
            }
        };
    }

}
