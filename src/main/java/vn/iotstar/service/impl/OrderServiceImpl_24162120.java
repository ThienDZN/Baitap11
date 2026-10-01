package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.dao.IOrderDao_24162120;
import vn.iotstar.dao.impl.OrderDao_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.UserAccount_24162120;
import vn.iotstar.service.IOrderService_24162120;

public class OrderServiceImpl_24162120 implements IOrderService_24162120 {
    private final IOrderDao_24162120 orderDao;

    public OrderServiceImpl_24162120() {
        this(new OrderDao_24162120());
    }

    public OrderServiceImpl_24162120(IOrderDao_24162120 orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public CustomerOrder_24162120 checkout(UserAccount_24162120 user, ShoppingCart_24162120 cart,
                                            CheckoutRequest_24162120 checkoutRequest) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("Vui lòng đăng nhập trước khi thanh toán.");
        }
        if (cart == null || cart.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }
        if (checkoutRequest == null) {
            throw new IllegalArgumentException("Thông tin thanh toán không hợp lệ.");
        }
        List<String> validationErrors = checkoutRequest.validate();
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", validationErrors));
        }
        return orderDao.checkout(user.getUserId(), cart.getItems(), checkoutRequest);
    }

    @Override
    public List<CustomerOrder_24162120> findForUser(Long userId, OrderStatus_24162120 status) {
        if (userId == null) {
            throw new IllegalArgumentException("Không xác định được người dùng.");
        }
        return orderDao.findForUser(userId, status);
    }
}
