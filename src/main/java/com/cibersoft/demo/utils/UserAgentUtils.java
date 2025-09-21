package com.cibersoft.demo.utils;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentUtils {
    private static final Parser parser = new Parser();

    public static String getBrowserInfo(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Navegador desconocido";
        }

        Client client = parser.parse(userAgent);

        String browser = client.userAgent.family; // Chrome, Brave, DuckDuckGo, etc.
        String os = client.os.family;             // Windows, iOS, Android, etc.
        String device = client.device.family;     // PC, iPhone, Mobile, etc.

        return String.format("%s en %s (%s)", browser, os, device);
    }
}
