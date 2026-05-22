package com.example.backend.service;

import com.example.backend.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    @Value("${app.jwt.secret:sideL-development-secret-change-this-before-production-342}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-seconds:86400}")
    private long expirationSeconds;

    public String generateToken(UserEntity user) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", user.getEmail());
            payload.put("userId", user.getUserID());
            payload.put("email", user.getEmail());
            payload.put("isAdmin", Boolean.TRUE.equals(user.getIsAdmin()));
            payload.put("isProvider", Boolean.TRUE.equals(user.getIsProvider()));
            payload.put("exp", Instant.now().plusSeconds(expirationSeconds).getEpochSecond());

            String encodedHeader = encodeJson(header);
            String encodedPayload = encodeJson(payload);
            String unsignedToken = encodedHeader + "." + encodedPayload;

            return unsignedToken + "." + sign(unsignedToken);
        } catch (Exception err) {
            throw new RuntimeException("Unable to create authentication token.");
        }
    }

    public Map<String, Object> validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Invalid token format.");
            }

            String unsignedToken = parts[0] + "." + parts[1];
            String expectedSignature = sign(unsignedToken);
            if (!constantTimeEquals(expectedSignature, parts[2])) {
                throw new RuntimeException("Invalid token signature.");
            }

            Map<String, Object> payload = parseJson(new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8));

            long expiration = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() >= expiration) {
                throw new RuntimeException("Token expired.");
            }

            return payload;
        } catch (Exception err) {
            throw new RuntimeException("Invalid or expired token.");
        }
    }

    private String encodeJson(Map<String, Object> value) throws Exception {
        return URL_ENCODER.encodeToString(toJson(value).getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(key);
        return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);
        if (leftBytes.length != rightBytes.length) {
            return false;
        }

        int result = 0;
        for (int index = 0; index < leftBytes.length; index++) {
            result |= leftBytes[index] ^ rightBytes[index];
        }
        return result == 0;
    }

    private String toJson(Map<String, Object> value) {
        return value.entrySet().stream()
                .map(entry -> "\"" + escape(entry.getKey()) + "\":" + toJsonValue(entry.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private String toJsonValue(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        return "\"" + escape(String.valueOf(value)) + "\"";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Map<String, Object> parseJson(String json) {
        Map<String, Object> values = new LinkedHashMap<>();
        String content = json.trim();
        if (content.startsWith("{")) {
            content = content.substring(1);
        }
        if (content.endsWith("}")) {
            content = content.substring(0, content.length() - 1);
        }
        if (content.isBlank()) {
            return values;
        }

        for (String part : content.split(",")) {
            String[] pair = part.split(":", 2);
            if (pair.length != 2) continue;

            String key = unquote(pair[0].trim());
            String rawValue = pair[1].trim();
            if ("true".equalsIgnoreCase(rawValue) || "false".equalsIgnoreCase(rawValue)) {
                values.put(key, Boolean.parseBoolean(rawValue));
            } else if (rawValue.matches("-?\\d+")) {
                values.put(key, Long.parseLong(rawValue));
            } else {
                values.put(key, unquote(rawValue));
            }
        }

        return values;
    }

    private String unquote(String value) {
        String result = value;
        if (result.startsWith("\"") && result.endsWith("\"") && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
