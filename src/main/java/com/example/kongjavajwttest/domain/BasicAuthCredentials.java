package com.example.kongjavajwttest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
@AllArgsConstructor
public class BasicAuthCredentials {

    private String username;
    private String password;
    private long userId;


    public static BasicAuthCredentials parse(String basicAuthHeaderValue, long userId) {
        // Authorization: Basic basicAuthHeaderValue
        String base64Credentials = basicAuthHeaderValue.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String concatenatedCredentials = new String(credDecoded, StandardCharsets.UTF_8);
        // concatenatedCredentials = username:password
        final String[] values = concatenatedCredentials.split(":", 2);
        return new BasicAuthCredentials(values[0], values[1], userId);
    }

    public static boolean isAuthenticated(BasicAuthCredentials expected, BasicAuthCredentials actual) {
        if (expected == actual) return true;

        if (expected == null || actual == null) return false;

        return new EqualsBuilder().append(expected.username, actual.username).append(expected.password, actual.password).isEquals();
    }

    public static boolean isAuthorized(BasicAuthCredentials expected, BasicAuthCredentials actual) {
        if (expected == actual) return true;

        if (expected == null || actual == null) return false;

        return new EqualsBuilder().append(expected.userId, actual.userId).isEquals();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BasicAuthCredentials that = (BasicAuthCredentials) o;

        return new EqualsBuilder().append(userId, that.userId).append(username, that.username).append(password, that.password).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(username).append(password).append(userId).toHashCode();
    }
}
