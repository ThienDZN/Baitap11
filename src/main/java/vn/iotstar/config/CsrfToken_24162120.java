package vn.iotstar.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Session-bound synchronizer token for the new state-changing user flows. */
public final class CsrfToken_24162120 {
    public static final String PARAMETER_NAME = "csrfToken";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CsrfToken_24162120() {
    }

    public static String ensureToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object current = session.getAttribute(SessionConstants_24162120.CSRF_TOKEN);
        if (current instanceof String token && !token.isBlank()) {
            return token;
        }

        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        session.setAttribute(SessionConstants_24162120.CSRF_TOKEN, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object expected = session.getAttribute(SessionConstants_24162120.CSRF_TOKEN);
        String supplied = request.getParameter(PARAMETER_NAME);
        if (!(expected instanceof String expectedToken) || supplied == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }
}
