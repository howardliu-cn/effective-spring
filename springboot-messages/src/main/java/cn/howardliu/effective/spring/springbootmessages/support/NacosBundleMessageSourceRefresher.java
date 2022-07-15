package cn.howardliu.effective.spring.springbootmessages.support;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosPropertySourceRepository;
import com.alibaba.cloud.nacos.refresh.NacosRefreshHistory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;

import cn.howardliu.effective.spring.springbootmessages.NacosConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-07-15
 */
@Slf4j
public class NacosBundleMessageSourceRefresher implements ApplicationListener<ApplicationReadyEvent> {

    private static final AtomicLong REFRESH_COUNT = new AtomicLong(0);

    private final NacosConfigProperties nacosConfigProperties;

    private final boolean isRefreshEnabled;

    private final NacosRefreshHistory nacosRefreshHistory;

    private final ConfigService configService;

    private final AtomicBoolean ready = new AtomicBoolean(false);

    private final Map<String, Listener> listenerMap = new ConcurrentHashMap<>(16);

    private final NacosBundleMessageSource nacosBundleMessageSource;

    public NacosBundleMessageSourceRefresher(NacosConfigManager nacosConfigManager,
            NacosRefreshHistory refreshHistory, NacosBundleMessageSource nacosBundleMessageSource) {
        this.nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
        this.nacosRefreshHistory = refreshHistory;
        this.configService = nacosConfigManager.getConfigService();
        this.isRefreshEnabled = this.nacosConfigProperties.isRefreshEnabled();
        this.nacosBundleMessageSource = nacosBundleMessageSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // many Spring context
        if (this.ready.compareAndSet(false, true)) {
            for (Locale defaultLocale : this.nacosBundleMessageSource.defaultLocales()) {
                this.nacosBundleMessageSource.getMergedProperties(defaultLocale);
            }
            this.registerNacosListenersForApplications();
        }
    }

    /**
     * register Nacos Listeners.
     */
    private void registerNacosListenersForApplications() {
        if (!isRefreshEnabled()) {
            return;
        }

        this.nacosBundleMessageSource.getBasenameSet().stream()
                .map(basename -> this.nacosBundleMessageSource.defaultLocales().stream()
                        .map(locale -> this.nacosBundleMessageSource.calculateAllFilenames(basename, locale))
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
                )
                .flatMap(List::stream)
                .forEach(x -> registerNacosListener(nacosBundleMessageSource.getNacosGroup(), x + NacosConstants.PROPERTIES_SUFFIX));
    }

    private void registerNacosListener(final String groupKey, final String dataKey) {
        final String key = NacosPropertySourceRepository.getMapKey(dataKey, groupKey);
        final Listener listener = listenerMap.computeIfAbsent(key, lst -> new AbstractSharedListener() {
            @Override
            public void innerReceive(String dataId, String group, String configInfo) {
                refreshCountIncrement();
                nacosRefreshHistory.addRefreshRecord(dataId, group, configInfo);
                try {
                    nacosBundleMessageSource.forceRefresh(dataId, configInfo);
                    if (log.isDebugEnabled()) {
                        log.debug("Refresh Nacos config group={},dataId={},configInfo={}", group, dataId, configInfo);
                    }
                } catch (IOException e) {
                    log.warn("Nacos refresh failed, dataId: {}, group: {}, configInfo: {}",
                            dataId, group, configInfo, e);
                }
            }
        });
        try {
            configService.addListener(dataKey, groupKey, listener);
        } catch (NacosException e) {
            log.warn("register fail for nacos listener ,dataId=[{}],group=[{}]", dataKey, groupKey, e);
        }
    }

    public boolean isRefreshEnabled() {
        if (null == nacosConfigProperties) {
            return isRefreshEnabled;
        }
        // Compatible with older configurations
        if (nacosConfigProperties.isRefreshEnabled() && !isRefreshEnabled) {
            return false;
        }
        return isRefreshEnabled;
    }

    public static long getRefreshCount() {
        return REFRESH_COUNT.get();
    }

    public static void refreshCountIncrement() {
        REFRESH_COUNT.incrementAndGet();
    }
}
