package vn.iotstar.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.dto.ShoppingCart_24162120;

/** Owns the one session attribute used for a signed-in user's shopping cart. */
public final class CartSession_24162120 {
    private CartSession_24162120() {
    }

    public static ShoppingCart_24162120 getOrCreate(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Object current = session.getAttribute(SessionConstants_24162120.SHOPPING_CART);
        if (current instanceof ShoppingCart_24162120 cart) {
            return cart;
        }

        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        session.setAttribute(SessionConstants_24162120.SHOPPING_CART, cart);
        return cart;
    }
}
