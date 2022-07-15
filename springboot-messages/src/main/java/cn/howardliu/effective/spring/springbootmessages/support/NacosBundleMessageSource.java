package cn.howardliu.effective.spring.springbootmessages.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.exception.NacosException;

import cn.howardliu.effective.spring.springbootmessages.NacosConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 看山 <a href="mailto:howardliu1988@163.com">Howard Liu</a>
 * Created on 2022-07-15
 */
@Getter
@Setter
public class NacosBundleMessageSource extends AbstractResourceBasedMessageSource {
    private boolean concurrentRefresh = true;
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private NacosConfigManager nacosConfigManager;
    private String nacosGroup;

    // Cache to hold filename lists per Locale
    private final ConcurrentMap<String, Map<Locale, List<String>>> cachedFilenames = new ConcurrentHashMap<>();

    // Cache to hold already loaded properties per filename
    private final ConcurrentMap<String, PropertiesHolder> cachedProperties = new ConcurrentHashMap<>();

    // Cache to hold already loaded properties per filename
    private final ConcurrentMap<Locale, PropertiesHolder> cachedMergedProperties = new ConcurrentHashMap<>();

    private static final List<Locale> INIT_LOAD_FILE_LOCALES = new ArrayList<>(2);

    static {
        INIT_LOAD_FILE_LOCALES.add(Locale.CHINA);
        INIT_LOAD_FILE_LOCALES.add(Locale.US);
    }

    /**
     * Resolves the given message code as key in the retrieved bundle files,
     * returning the value found in the bundle as-is (without MessageFormat parsing).
     */
    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            PropertiesHolder propHolder = getMergedProperties(locale);
            return propHolder.getProperty(code);
        } else {
            for (String basename : getBasenameSet()) {
                List<String> filenames = calculateAllFilenames(basename, locale);
                for (String filename : filenames) {
                    PropertiesHolder propHolder = getProperties(filename);
                    String result = propHolder.getProperty(code);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Resolves the given message code as key in the retrieved bundle files,
     * using a cached MessageFormat instance per message code.
     */
    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        if (getCacheMillis() < 0) {
            PropertiesHolder propHolder = getMergedProperties(locale);
            return propHolder.getMessageFormat(code, locale);
        } else {
            for (String basename : getBasenameSet()) {
                List<String> filenames = calculateAllFilenames(basename, locale);
                for (String filename : filenames) {
                    PropertiesHolder propHolder = getProperties(filename);
                    MessageFormat result = propHolder.getMessageFormat(code, locale);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public void forceRefresh(String fileName, String config) throws IOException {
        synchronized (this) {
            final Properties props = newProperties();
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8))) {
                this.propertiesPersister.load(props, inputStream);
            }

            final long fileTimestamp = -1;
            final PropertiesHolder propHolder = new PropertiesHolder(props, fileTimestamp);

            this.cachedProperties.put(fileName, propHolder);
            this.cachedMergedProperties.clear();
        }
    }

    /**
     * Get a PropertiesHolder that contains the actually visible properties
     * for a Locale, after merging all specified resource bundles.
     * Either fetches the holder from the cache or freshly loads it.
     * <p>Only used when caching resource bundle contents forever, i.e.
     * with cacheSeconds < 0. Therefore, merged properties are always
     * cached forever.
     */
    public PropertiesHolder getMergedProperties(Locale locale) {
        PropertiesHolder mergedHolder = this.cachedMergedProperties.get(locale);
        if (mergedHolder != null) {
            return mergedHolder;
        }
        Properties mergedProps = newProperties();
        long latestTimestamp = -1;
        String[] basenames = StringUtils.toStringArray(getBasenameSet());
        for (int i = basenames.length - 1; i >= 0; i--) {
            List<String> filenames = calculateAllFilenames(basenames[i], locale);
            for (int j = filenames.size() - 1; j >= 0; j--) {
                String filename = filenames.get(j);
                PropertiesHolder propHolder = getProperties(filename);
                if (propHolder.getProperties() != null) {
                    mergedProps.putAll(propHolder.getProperties());
                    if (propHolder.getFileTimestamp() > latestTimestamp) {
                        latestTimestamp = propHolder.getFileTimestamp();
                    }
                }
            }
        }
        mergedHolder = new PropertiesHolder(mergedProps, latestTimestamp);
        PropertiesHolder existing = this.cachedMergedProperties.putIfAbsent(locale, mergedHolder);
        if (existing != null) {
            mergedHolder = existing;
        }
        return mergedHolder;
    }

    /**
     * Calculate all filenames for the given bundle basename and Locale.
     * Will calculate filenames for the given Locale, the system Locale
     * (if applicable), and the default file.
     *
     * @param basename the basename of the bundle
     * @param locale the locale
     * @return the List of filenames to check
     * @see #setFallbackToSystemLocale
     * @see #calculateFilenamesForLocale
     */
    public List<String> calculateAllFilenames(String basename, Locale locale) {
        Map<Locale, List<String>> localeMap = this.cachedFilenames.get(basename);
        if (localeMap != null) {
            List<String> filenames = localeMap.get(locale);
            if (filenames != null) {
                return filenames;
            }
        }
        List<String> filenames = new ArrayList<>(7);
        filenames.addAll(calculateFilenamesForLocale(basename, locale));
        if (isFallbackToSystemLocale() && !locale.equals(Locale.getDefault())) {
            List<String> fallbackFilenames = calculateFilenamesForLocale(basename, Locale.getDefault());
            for (String fallbackFilename : fallbackFilenames) {
                if (!filenames.contains(fallbackFilename)) {
                    // Entry for fallback locale that isn't already in filenames list.
                    filenames.add(fallbackFilename);
                }
            }
        }
        filenames.add(basename);
        if (localeMap == null) {
            localeMap = new ConcurrentHashMap<>();
            Map<Locale, List<String>> existing = this.cachedFilenames.putIfAbsent(basename, localeMap);
            if (existing != null) {
                localeMap = existing;
            }
        }
        localeMap.put(locale, filenames);
        return filenames;
    }

    public List<Locale> defaultLocales() {
        return INIT_LOAD_FILE_LOCALES;
    }

    /**
     * Calculate the filenames for the given bundle basename and Locale,
     * appending language code, country code, and variant code.
     * E.g.: basename "messages", Locale "de_AT_oo" -> "messages_de_AT_OO",
     * "messages_de_AT", "messages_de".
     * <p>Follows the rules defined by {@link java.util.Locale#toString()}.
     *
     * @param basename the basename of the bundle
     * @param locale the locale
     * @return the List of filenames to check
     */
    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);

        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }

    /**
     * Get a PropertiesHolder for the given filename, either from the
     * cache or freshly loaded.
     *
     * @param filename the bundle filename (basename + Locale)
     * @return the current PropertiesHolder for the bundle
     */
    protected PropertiesHolder getProperties(String filename) {
        PropertiesHolder propHolder = this.cachedProperties.get(filename);
        long originalTimestamp = -2;

        if (propHolder != null) {
            originalTimestamp = propHolder.getRefreshTimestamp();
            if (originalTimestamp == -1 || originalTimestamp > System.currentTimeMillis() - getCacheMillis()) {
                // Up to date
                return propHolder;
            }
        } else {
            propHolder = new PropertiesHolder();
            PropertiesHolder
                    existingHolder = this.cachedProperties.putIfAbsent(filename, propHolder);
            if (existingHolder != null) {
                propHolder = existingHolder;
            }
        }

        // At this point, we need to refresh...
        if (this.concurrentRefresh && propHolder.getRefreshTimestamp() >= 0) {
            // A populated but stale holder -> could keep using it.
            if (!propHolder.refreshLock.tryLock()) {
                // Getting refreshed by another thread already ->
                // let's return the existing properties for the time being.
                return propHolder;
            }
        } else {
            propHolder.refreshLock.lock();
        }
        try {
            PropertiesHolder existingHolder = this.cachedProperties.get(filename);
            if (existingHolder != null && existingHolder.getRefreshTimestamp() > originalTimestamp) {
                return existingHolder;
            }
            return refreshProperties(filename, propHolder);
        } finally {
            propHolder.refreshLock.unlock();
        }
    }

    /**
     * Refresh the PropertiesHolder for the given bundle filename.
     * The holder can be {@code null} if not cached before, or a timed-out cache entry
     * (potentially getting re-validated against the current last-modified timestamp).
     *
     * @param filename the bundle filename (basename + Locale)
     * @param propHolder the current PropertiesHolder for the bundle
     */
    protected PropertiesHolder refreshProperties(String filename, @Nullable PropertiesHolder propHolder) {
        long refreshTimestamp = (getCacheMillis() < 0 ? -1 : System.currentTimeMillis());

        try {
            Properties props = loadProperties(filename);
            propHolder = new PropertiesHolder(props, -1);
        } catch (NacosException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not get properties form nacos ", ex);
            }
            // Empty holder representing "not valid".
            propHolder = new PropertiesHolder();
        } catch (IOException ex) {
            if (logger.isInfoEnabled()) {
                logger.info("Could not get properties form nacos, the message is " + ex.getMessage());
            }
            // Empty holder representing "not valid".
            propHolder = new PropertiesHolder();
        }

        propHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder);
        logger.info("Refreshed properties for " + filename);
        return propHolder;
    }

    /**
     * Load the properties from the given resource.
     *
     * @param filename the original bundle filename (basename + Locale)
     * @return the populated Properties instance
     * @throws IOException if properties loading failed
     */
    protected Properties loadProperties(String filename) throws IOException, NacosException {
        final Properties props = newProperties();
        final String dataId = filename + NacosConstants.PROPERTIES_SUFFIX;
        logger.info("Loading properties for " + dataId);
        final String config = nacosConfigManager.getConfigService().getConfig(dataId, nacosGroup, 5000);
        if (StringUtils.isEmpty(config)) {
            logger.info("No properties found for " + dataId);
            throw new NoSuchFileException(dataId);
        }

        try (Reader reader = new StringReader(config)) {
            this.propertiesPersister.load(props, reader);
            logger.info("Loaded properties for " + dataId);
        }
        return props;
    }

    /**
     * Template method for creating a plain new {@link Properties} instance.
     * The default implementation simply calls {@link Properties#Properties()}.
     * <p>Allows for returning a custom {@link Properties} extension in subclasses.
     * Overriding methods should just instantiate a custom {@link Properties} subclass,
     * with no further initialization or population to be performed at that point.
     *
     * @return a plain Properties instance
     * @since 4.2
     */
    protected Properties newProperties() {
        return new Properties();
    }

    /**
     * Clear the resource bundle cache.
     * Subsequent resolve calls will lead to reloading of the properties files.
     */
    public void clearCache() {
        logger.debug("Clearing entire resource bundle cache");
        this.cachedProperties.clear();
        this.cachedMergedProperties.clear();
    }

    /**
     * Clear the resource bundle caches of this MessageSource and all its ancestors.
     *
     * @see #clearCache
     */
    public void clearCacheIncludingAncestors() {
        clearCache();

        final MessageSource parentMessageSource = getParentMessageSource();
        if (parentMessageSource == null) {
            return;
        }

        if (parentMessageSource instanceof NacosBundleMessageSource) {
            ((NacosBundleMessageSource) parentMessageSource).clearCacheIncludingAncestors();
        }
    }


    @Override
    public String toString() {
        return getClass().getName() + ": basenames=" + getBasenameSet();
    }

    /**
     * PropertiesHolder for caching.
     * Stores the last-modified timestamp of the source file for efficient
     * change detection, and the timestamp of the last refresh attempt
     * (updated every time the cache entry gets re-validated).
     */
    protected class PropertiesHolder {

        @Nullable
        private final Properties properties;

        private final long fileTimestamp;

        private volatile long refreshTimestamp = -2;

        private final ReentrantLock refreshLock = new ReentrantLock();

        /**
         * Cache to hold already generated MessageFormats per message code.
         */
        private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats =
                new ConcurrentHashMap<>();

        public PropertiesHolder() {
            this.properties = null;
            this.fileTimestamp = -1;
        }

        public PropertiesHolder(Properties properties, long fileTimestamp) {
            this.properties = properties;
            this.fileTimestamp = fileTimestamp;
        }

        @Nullable
        public Properties getProperties() {
            return this.properties;
        }

        public long getFileTimestamp() {
            return this.fileTimestamp;
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }

        @Nullable
        public String getProperty(String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }

        @Nullable
        public MessageFormat getMessageFormat(String code, Locale locale) {
            if (this.properties == null) {
                return null;
            }
            Map<Locale, MessageFormat> localeMap = this.cachedMessageFormats.get(code);
            if (localeMap != null) {
                MessageFormat result = localeMap.get(locale);
                if (result != null) {
                    return result;
                }
            }
            String msg = this.properties.getProperty(code);
            if (msg != null) {
                if (localeMap == null) {
                    localeMap = new ConcurrentHashMap<>();
                    Map<Locale, MessageFormat> existing = this.cachedMessageFormats.putIfAbsent(code, localeMap);
                    if (existing != null) {
                        localeMap = existing;
                    }
                }
                MessageFormat result = createMessageFormat(msg, locale);
                localeMap.put(locale, result);
                return result;
            }
            return null;
        }
    }
}
