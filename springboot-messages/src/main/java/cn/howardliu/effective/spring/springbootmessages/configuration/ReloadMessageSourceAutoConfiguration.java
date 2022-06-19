package cn.howardliu.effective.spring.springbootmessages.configuration;

import java.time.Duration;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import cn.howardliu.effective.spring.springbootmessages.configuration.ReloadMessageSourceAutoConfiguration.ReloadResourceBundleCondition;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-06-09
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, search = SearchStrategy.CURRENT)
@ConditionalOnProperty(name = "spring.messages-type", havingValue = "ReloadableResourceBundleMessageSource")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ReloadResourceBundleCondition.class)
@EnableConfigurationProperties
public class ReloadMessageSourceAutoConfiguration {

    private static final Resource[] NO_RESOURCES = {};

    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            final String[] originBaseNames = StringUtils
                    .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename()));
            final String[] baseNames = new String[originBaseNames.length];
            for (int i = 0; i < originBaseNames.length; i++) {
                if (originBaseNames[i].startsWith("classpath:")) {
                    baseNames[i] = originBaseNames[i];
                } else {
                    baseNames[i] = "classpath:" + originBaseNames[i];
                }
            }
            messageSource.setBasenames(baseNames);
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    protected static class ReloadResourceBundleCondition extends SpringBootCondition {
        private static final ConcurrentReferenceHashMap<String, ConditionOutcome> CACHE =
                new ConcurrentReferenceHashMap<>();

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String basename = context.getEnvironment().getProperty("spring.messages.basename", "messages");
            ConditionOutcome outcome = CACHE.get(basename);
            if (outcome == null) {
                outcome = getMatchOutcomeForBasename(context, basename);
                CACHE.put(basename, outcome);
            }
            return outcome;
        }

        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("ResourceBundle");
            for (String name : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename))) {
                for (Resource resource : getResources(context.getClassLoader(), name)) {
                    if (resource.exists()) {
                        return ConditionOutcome.match(message.found("bundle").items(resource));
                    }
                }
            }
            return ConditionOutcome.noMatch(message.didNotFind("bundle with basename " + basename).atAll());
        }

        private Resource[] getResources(ClassLoader classLoader, String name) {
            String target = name.replace('.', '/');
            try {
                return new PathMatchingResourcePatternResolver(classLoader)
                        .getResources("classpath*:" + target + ".properties");
            } catch (Exception ex) {
                return NO_RESOURCES;
            }
        }

    }

}
