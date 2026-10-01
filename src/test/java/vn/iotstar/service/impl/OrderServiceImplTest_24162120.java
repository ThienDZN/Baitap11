package vn.iotstar.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import vn.iotstar.dao.IOrderDao_24162120;
import vn.iotstar.dto.CartItem_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;

class OrderServiceImplTest_24162120 {
    @Test
    void validCheckoutShouldDelegateOnlyAfterInputValidation() {
        CapturingOrderDao dao = new CapturingOrderDao();
        OrderServiceImpl_24162120 service = new OrderServiceImpl_24162120(dao);
        ShoppingCart_24162120 cart = cartWithOneProduct();
        UserAccount_24162120 user = user();
        CheckoutRequest_24162120 request = new CheckoutRequest_24162120(
                "Người nhận", "0901234567", "Địa chỉ giao hàng", CheckoutRequest_24162120.BANK_TRANSFER);

        CustomerOrder_24162120 actual = service.checkout(user, cart, request);

        assertSame(dao.result, actual);
        assertEquals(1, dao.checkoutCalls);
        assertEquals(user.getUserId(), dao.userId);
        assertEquals(1, dao.cartItems.size());
    }

    @Test
    void invalidCheckoutMustNotReachPersistenceLayer() {
        CapturingOrderDao dao = new CapturingOrderDao();
        OrderServiceImpl_24162120 service = new OrderServiceImpl_24162120(dao);
        CheckoutRequest_24162120 invalidRequest = new CheckoutRequest_24162120("", "bad", "", "bad");

        assertThrows(IllegalArgumentException.class, () -> service.checkout(user(), cartWithOneProduct(), invalidRequest));
        assertEquals(0, dao.checkoutCalls);
    }

    private ShoppingCart_24162120 cartWithOneProduct() {
        Product_24162120 product = new Product_24162120();
        product.setProductId(44L);
        product.setProductName("Sản phẩm kiểm thử");
        product.setPrice(new BigDecimal("100000.00"));
        product.setQuantity(10);
        product.setStatus(1);
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        cart.add(product, 2);
        return cart;
    }

    private UserAccount_24162120 user() {
        UserAccount_24162120 user = new UserAccount_24162120();
        user.setUserId(18L);
        return user;
    }

    private static class CapturingOrderDao implements IOrderDao_24162120 {
        private final CustomerOrder_24162120 result = new CustomerOrder_24162120();
        private int checkoutCalls;
        private Long userId;
        private List<CartItem_24162120> cartItems;

        @Override
        public CustomerOrder_24162120 checkout(Long userId, List<CartItem_24162120> cartItems,
                                                CheckoutRequest_24162120 checkoutRequest) {
            this.checkoutCalls++;
            this.userId = userId;
            this.cartItems = cartItems;
            return result;
        }

        @Override
        public List<CustomerOrder_24162120> findForUser(Long userId, OrderStatus_24162120 status) {
            return List.of();
        }
    }
}
