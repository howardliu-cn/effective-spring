package cn.howardliu.effective.spring.springbootmessages.support;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.refresh.NacosRefreshHistory;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-07-15
 */
@Configuration
public class NacosConfiguration {

    @Bean("dynamicMessageSource")
    @ConditionalOnBean(NacosConfigManager.class)
    public NacosBundleMessageSource messageSource(NacosConfigManager nacosConfigManager,
            MessageSourceProperties messagesI18nConfig, Environment environment) {
        final NacosBundleMessageSource messageSource = new NacosBundleMessageSource();
        final String basename = messagesI18nConfig.getBasename();
        messageSource.setBasenames(getBaseNames(basename, environment));

        if (messagesI18nConfig.getEncoding() != null) {
            messageSource.setDefaultEncoding(messagesI18nConfig.getEncoding().name());
        }

        messageSource.setFallbackToSystemLocale(messagesI18nConfig.isFallbackToSystemLocale());
        final Duration cacheDuration = messagesI18nConfig.getCacheDuration();
        if (cacheDuration == null) {
            messageSource.setCacheSeconds(10);
        } else {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(messagesI18nConfig.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(messagesI18nConfig.isUseCodeAsDefaultMessage());

        messageSource.setNacosGroup(nacosConfigManager.getNacosConfigProperties().getGroup());
        messageSource.setNacosConfigManager(nacosConfigManager);
        return messageSource;
    }

    @Bean
    @ConditionalOnBean(NacosBundleMessageSource.class)
    public NacosBundleMessageSourceRefresher nacosBundleMessageSourceRefresher(NacosConfigManager nacosConfigManager,
            NacosRefreshHistory nacosRefreshHistory, NacosBundleMessageSource nacosBundleMessageSource) {
        return new NacosBundleMessageSourceRefresher(nacosConfigManager, nacosRefreshHistory, nacosBundleMessageSource);
    }

    private String[] getBaseNames(String basename, Environment environment) {
        final List<String> baseNames = new ArrayList<>();
        final String applicationName = environment.getProperty("spring.application.name");
        if (StringUtils.hasText(basename)) {
            final String[] baseNameArray =
                    StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename));
            for (String baseName : baseNameArray) {
                baseNames.add(applicationName + '_' + baseName);
            }
            baseNames.addAll(Arrays.asList(baseNameArray));
        } else {
            baseNames.add(applicationName + "_dynamic_messages");
            baseNames.add("dynamic_messages");
        }

        return baseNames.toArray(new String[0]);
    }
}
