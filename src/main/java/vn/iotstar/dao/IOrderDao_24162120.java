package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.dto.CartItem_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;

public interface IOrderDao_24162120 {
    CustomerOrder_24162120 checkout(Long userId, List<CartItem_24162120> cartItems,
                                    CheckoutRequest_24162120 checkoutRequest);

    List<CustomerOrder_24162120> findForUser(Long userId, OrderStatus_24162120 status);
}
