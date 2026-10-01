package vn.iotstar.service;

import java.util.List;

import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.UserAccount_24162120;

public interface IOrderService_24162120 {
    CustomerOrder_24162120 checkout(UserAccount_24162120 user, ShoppingCart_24162120 cart,
                                    CheckoutRequest_24162120 checkoutRequest);

    List<CustomerOrder_24162120> findForUser(Long userId, OrderStatus_24162120 status);
}
