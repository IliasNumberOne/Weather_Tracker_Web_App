package com.iliasDev.controller;

import com.iliasDev.service.SessionService;
import com.iliasDev.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;
    private final CookieUtil cookieUtil;

    public SessionInterceptor(SessionService sessionService, CookieUtil cookieUtil) {
        this.sessionService = sessionService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UUID sessionId = cookieUtil.getSessionId(request);

        if (sessionId == null) {
            response.sendRedirect(request.getContextPath() + "/auth/sign-in");
            return false;
        }

        var sessionOpt = sessionService.getValidSession(sessionId);
        if (sessionOpt.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/auth/sign-in");
            return false;
        }

        request.setAttribute("userId", sessionOpt.get().getUserId());

        return true;
    }
}