package com.tuitui.tool.http;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * web工具类
 *
 * @author liujianxue
 * @date 2018/1/8
 */
public final class WebUtil {

    private WebUtil() {

    }

    /**
     * 获取当前request
     *
     * @return 当前request
     */
    public static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取当前reqsponse
     *
     * @return 当前reqsponse
     */
    public static HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getResponse();
    }

    /**
     * 获取当前request uri
     *
     * @return 当前请求的uri
     */
    public static String getCurrentRequestUri() {
        HttpServletRequest request = getCurrentRequest();
        return request.getRequestURI();
    }

    /**
     * 获取当前请求IP
     *
     * @return
     */
    public static String getCurrentRequestIp() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        if (forwarded != null) {
            return forwarded.split(",")[0];
        }
        if (realIp != null) {
            return realIp;
        }
        return remoteAddr;
    }

    /**
     * 获取当前request uri
     *
     * @return 当前请求的uri
     */
    public static String getRequestId() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return String.valueOf(request.getAttribute("request_id"));
    }
}
