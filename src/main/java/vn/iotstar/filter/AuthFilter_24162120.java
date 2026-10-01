package vn.iotstar.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.config.SessionConstants_24162120;
import vn.iotstar.entity.UserAccount_24162120;

public class AuthFilter_24162120 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        UserAccount_24162120 user = session == null ? null : (UserAccount_24162120) session.getAttribute(SessionConstants_24162120.CURRENT_USER);

        if (user == null
                || !"ADMIN".equalsIgnoreCase(user.getRoleName())
                || !user.isEnabled()
                || user.getStatus() != 1) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?message=Vui+l%C3%B2ng+%C4%91%C4%83ng+nh%E1%BA%ADp+t%C3%A0i+kho%E1%BA%A3n+admin");
            return;
        }
        chain.doFilter(request, response);
    }
}
