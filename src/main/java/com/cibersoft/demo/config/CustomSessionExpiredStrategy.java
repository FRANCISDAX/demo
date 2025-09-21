package com.cibersoft.demo.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import com.cibersoft.demo.utils.UserAgentUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        String userAgent = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String browserInfo = UserAgentUtils.getBrowserInfo(userAgent);

        String redirectUrl = "/?concurrent=true"
                + "&device=" + URLEncoder.encode(browserInfo, StandardCharsets.UTF_8)
                + "&ip=" + URLEncoder.encode(ip, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
        
    }

}
