package vn.iotstar.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

class CsrfTokenTest_24162120 {
    @Test
    void shouldCreateReuseAndValidateASessionBoundToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        Map<String, Object> attributes = new HashMap<>();
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(anyString())).thenAnswer(invocation -> attributes.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            attributes.put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(session).setAttribute(anyString(), any());

        String token = CsrfToken_24162120.ensureToken(request);
        assertNotNull(token);
        assertSame(token, CsrfToken_24162120.ensureToken(request));

        when(request.getParameter(CsrfToken_24162120.PARAMETER_NAME)).thenReturn(token);
        assertTrue(CsrfToken_24162120.isValid(request));

        when(request.getParameter(CsrfToken_24162120.PARAMETER_NAME)).thenReturn("invalid-token");
        assertFalse(CsrfToken_24162120.isValid(request));
    }
}
