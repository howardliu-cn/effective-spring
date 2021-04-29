package cn.howard.demo.springfilter.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author liuxinghao
 * @version 1.0
 * @since 2021/4/29 下午3:44
 */
@WebFilter(filterName = "accessLogFilter", urlPatterns = "/*")
public class AccessLogFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);
    private Set<String> excludeUris = Sets.newHashSet();
    private static final PathMatcher URI_PATH_MATCHER = new AntPathMatcher();
    private static final List<String> DEFAULT_DOWNLOAD_CONTENT_TYPE = Lists.newArrayList(
            "application/vnd.ms-excel",//.xls
            "application/msexcel",//.xls
            "application/cvs",//.cvs
            MediaType.APPLICATION_OCTET_STREAM_VALUE,//.*（ 二进制流，不知道下载文件类型）
            "application/x-xls",//.xls
            "application/msword",//.doc
            MediaType.TEXT_PLAIN_VALUE,//.txt
            "application/x-gzip"//.gz
    );

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        // 如果是被排除的uri，不记录access_log
        if (matchExclude(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String requestMethod = request.getMethod();
        final boolean shouldWrapMethod = StringUtils.equalsIgnoreCase(requestMethod, HttpMethod.PUT.name())
                || StringUtils.equalsIgnoreCase(requestMethod, HttpMethod.POST.name());

        final boolean isFirstRequest = !isAsyncDispatch(request);

        final boolean shouldWrapRequest = isFirstRequest && !(request instanceof ContentCachingRequestWrapper) && shouldWrapMethod;
        final HttpServletRequest requestToUse = shouldWrapRequest ? new ContentCachingRequestWrapper(request) : request;

        final boolean shouldWrapResponse = !(response instanceof ContentCachingResponseWrapper) && shouldWrapMethod;
        final HttpServletResponse responseToUse = shouldWrapResponse ? new ContentCachingResponseWrapper(response) : response;

        final long startTime = System.currentTimeMillis();
        Throwable t = null;
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } catch (Exception e) {
            t = e;
            throw e;
        } finally {
            doSaveAccessLog(requestToUse, responseToUse, System.currentTimeMillis() - startTime, t);
        }
    }

    private void doSaveAccessLog(final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final long useTime,
                                 final Throwable t) {
        if (isAsyncStarted(request)) {
            copyResponse(response);
            return;
        }
        try {
            final String requestUri = request.getRequestURI();
            final String requestHeaders = getRequestHeaders(request);
            final String requestIp = getRequestIp(request);
            final String requestParams = getRequestParams(request);
            final String requestString = isUpload(request) ? StringUtils.EMPTY : getRequestString(request);
            final String responseString = isDownload(response) ? StringUtils.EMPTY : getResponseString(response);
            final int responseStatus = response.getStatus();

            final List<String> logs = Lists.newArrayList();
            logs.add("time=" + useTime + "ms");
            logs.add("ip=" + requestIp);
            logs.add("uri=" + requestUri);
            logs.add("headers=" + requestHeaders);
            logs.add("status=" + responseStatus);
            logs.add("requestContentType=" + request.getContentType());
            logs.add("responseContentType=" + response.getContentType());
            logs.add("params=" + requestParams);
            logs.add("request=" + requestString);
            logs.add("response=" + responseString);

            logger.info(String.join(",", logs));
        } catch (Throwable e) {
            logger.error("got an exception when saving access log", e);
        } finally {
            copyResponse(response);
        }
    }

    private String getRequestIp(final HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");// 这是一个可以伪造的头
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        // 最后一个为RemoteAddr
        int pos = ip.lastIndexOf(',');
        if (pos >= 0) {
            ip = ip.substring(pos);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    private String getRequestHeaders(final HttpServletRequest request) {
        final Enumeration<String> headerNames = request.getHeaderNames();
        final List<String> headers = Lists.newArrayList();
        while (headerNames.hasMoreElements()) {
            final String key = headerNames.nextElement();
            headers.add(key + ':' + request.getHeader(key));
        }
        return '[' + String.join(",", headers) + ']';
    }

    private String getRequestParams(final HttpServletRequest request) {
        final Map<String, String[]> requestParams = Maps.newHashMap(request.getParameterMap());
        final List<String> pairs = Lists.newArrayList();
        if (MapUtils.isNotEmpty(requestParams)) {
            for (final Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                final String name = entry.getKey();
                final String[] value = entry.getValue();
                if (value == null) {
                    pairs.add(name + "=");
                } else {
                    for (final String v : value) {
                        pairs.add(name + "=" + StringUtils.trimToEmpty(v));
                    }
                }
            }
        }
        String requestParamsStr = CollectionUtils.isEmpty(pairs) ? StringUtils.EMPTY : String.join("&", pairs);
        if (StringUtils.equalsIgnoreCase(request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            try {
                requestParamsStr = URLDecoder.decode(requestParamsStr, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException ignored) {
            }
        }
        return requestParamsStr;
    }

    private boolean isUpload(final HttpServletRequest request) {
        final String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        return StringUtils.containsIgnoreCase(contentType, MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private boolean isDownload(final HttpServletResponse response) {
        final String contentType = response.getContentType();
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        return DEFAULT_DOWNLOAD_CONTENT_TYPE.stream().anyMatch(it -> StringUtils.equalsIgnoreCase(it, contentType));
    }

    private String getRequestString(final HttpServletRequest request) {
        final ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            try {
                final byte[] buf = wrapper.getContentAsByteArray();
                return new String(buf, wrapper.getCharacterEncoding()).replaceAll("\n|\r", "");
            } catch (UnsupportedEncodingException e) {
                return "[UNKNOWN]";
            }
        }
        return StringUtils.EMPTY;
    }

    private String getResponseString(final HttpServletResponse response) {
        final ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                final byte[] buf = wrapper.getContentAsByteArray();
                return new String(buf, wrapper.getCharacterEncoding()).replaceAll("\n|\r", "");
            } catch (UnsupportedEncodingException e) {
                return "[UNKNOWN]";
            }
        }
        return StringUtils.EMPTY;
    }

    private void copyResponse(final HttpServletResponse response) {
        final ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                wrapper.copyBodyToResponse();
            } catch (IOException ignored) {
            }
        }
    }

    private boolean matchExclude(final String uri) {
        if (CollectionUtils.isEmpty(excludeUris)) {
            return false;
        }
        for (final String excludeUri : excludeUris) {
            if (URI_PATH_MATCHER.match(excludeUri, uri)) {
                return true;
            }
        }
        return false;
    }

    public void setExcludeUris(final Set<String> excludeUris) {
        this.excludeUris = excludeUris == null ? Sets.newHashSet() : excludeUris;
    }
}
