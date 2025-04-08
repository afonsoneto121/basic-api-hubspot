package com.afonso.api.hubspot.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static Authentication defaultAuthentication() {
        return new AnonymousAuthenticationToken("anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

}
