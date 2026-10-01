package vn.iotstar.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.UserAccount_24162120;

/** Keeps authorization checks for User-only shopping flows in one place. */
public final class UserSessionAccess_24162120 {
    private UserSessionAccess_24162120() {
    }

    public static UserAccount_24162120 currentActiveUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object currentUser = session.getAttribute(SessionConstants_24162120.CURRENT_USER);
        if (!(currentUser instanceof UserAccount_24162120 user)
                || !user.isEnabled()
                || user.getStatus() != 1
                || !"USER".equalsIgnoreCase(user.getRoleName())) {
            return null;
        }
        return user;
    }
}
