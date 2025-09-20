package com.cibersoft.demo.config;

import java.io.IOException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;

@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        String ip = event.getRequest().getRemoteAddr();
        String userAgent = event.getRequest().getHeader("User-Agent");

        String deviceInfo = parseUserAgent(userAgent);

        event.getResponse().sendRedirect(
            "/?concurrent=true&ip=" + ip + "&device=" + deviceInfo
        );    
    }

    private String parseUserAgent(String userAgent) {
        if (userAgent == null) return "Dispositivo desconocido";

        String os;
        if (userAgent.contains("Windows NT 10.0")) os = "Windows 10";
        else if (userAgent.contains("Windows NT 6.3")) os = "Windows 8.1";
        else if (userAgent.contains("Windows NT 6.1")) os = "Windows 7";
        else if (userAgent.contains("Mac OS X")) os = "Mac OS X";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("iPhone")) os = "iPhone";
        else if (userAgent.contains("Linux")) os = "Linux";
        else os = "Sistema desconocido";

        String browser;
        if (userAgent.contains("DuckDuckGo")) {
            browser = "DuckDuckGo Browser";
        } else if (userAgent.contains("Brave")) {
            browser = "Brave";
        } else if (userAgent.contains("Edg")) {
            browser = "Microsoft Edge";
        } else if (userAgent.contains("Chrome")) {
            browser = "Google Chrome";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            browser = "Safari";
        } else if (userAgent.contains("Firefox")) {
            browser = "Mozilla Firefox";
        } else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
            browser = "Opera";
        } else {
            browser = "Navegador desconocido";
        }
        return browser + " en " + os;
    }
}
