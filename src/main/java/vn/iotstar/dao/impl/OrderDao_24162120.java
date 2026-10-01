package vn.iotstar.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dao.IOrderDao_24162120;
import vn.iotstar.dto.CartItem_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderItem_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;

public class OrderDao_24162120 implements IOrderDao_24162120 {
    private final Supplier<EntityManager> entityManagerSupplier;

    public OrderDao_24162120() {
        this(JpaConfig_24162120::getEntityManager);
    }

    public OrderDao_24162120(Supplier<EntityManager> entityManagerSupplier) {
        this.entityManagerSupplier = Objects.requireNonNull(entityManagerSupplier, "entityManagerSupplier");
    }

    @Override
    public CustomerOrder_24162120 checkout(Long userId, List<CartItem_24162120> cartItems,
                                            CheckoutRequest_24162120 checkoutRequest) {
        EntityManager entityManager = entityManagerSupplier.get();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            UserAccount_24162120 user = entityManager.find(UserAccount_24162120.class, userId);
            if (user == null || !user.isEnabled() || user.getStatus() != 1
                    || !"USER".equalsIgnoreCase(user.getRoleName())) {
                throw new IllegalArgumentException("Tài khoản không còn quyền thanh toán.");
            }

            List<CartItem_24162120> sortedItems = new ArrayList<>(cartItems);
            sortedItems.sort(Comparator.comparing(CartItem_24162120::getProductId));

            CustomerOrder_24162120 order = new CustomerOrder_24162120();
            order.setUser(user);
            order.setRecipientName(checkoutRequest.getRecipientName());
            order.setPhone(checkoutRequest.getPhone());
            order.setShippingAddress(checkoutRequest.getShippingAddress());
            order.setPaymentMethod(checkoutRequest.getPaymentMethod());
            order.setPaymentStatus("PENDING");
            order.setStatus(OrderStatus_24162120.NEW);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartItem_24162120 cartItem : sortedItems) {
                Product_24162120 product = entityManager.find(
                        Product_24162120.class, cartItem.getProductId(), LockModeType.PESSIMISTIC_WRITE);
                if (product == null || product.getStatus() != 1) {
                    throw new IllegalArgumentException("Một sản phẩm trong giỏ không còn được bán.");
                }
                if (product.getQuantity() < cartItem.getQuantity()) {
                    throw new IllegalArgumentException("Sản phẩm '" + product.getProductName() + "' không đủ tồn kho.");
                }
                if (product.getPrice() == null || product.getPrice().signum() < 0) {
                    throw new IllegalArgumentException("Giá sản phẩm không hợp lệ.");
                }

                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                OrderItem_24162120 orderItem = new OrderItem_24162120();
                orderItem.setProductId(product.getProductId());
                orderItem.setProductName(product.getProductName());
                orderItem.setUnitPrice(product.getPrice());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setLineTotal(lineTotal);
                order.addItem(orderItem);
                totalAmount = totalAmount.add(lineTotal);
            }

            order.setTotalAmount(totalAmount);
            entityManager.persist(order);
            transaction.commit();
            return order;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<CustomerOrder_24162120> findForUser(Long userId, OrderStatus_24162120 status) {
        EntityManager entityManager = entityManagerSupplier.get();
        try {
            String jpql = "SELECT DISTINCT o FROM CustomerOrder_24162120 o LEFT JOIN FETCH o.items "
                    + "WHERE o.user.userId = :userId";
            if (status != null) {
                jpql += " AND o.status = :status";
            }
            jpql += " ORDER BY o.createdAt DESC, o.orderId DESC";

            TypedQuery<CustomerOrder_24162120> query = entityManager.createQuery(jpql, CustomerOrder_24162120.class);
            query.setParameter("userId", userId);
            if (status != null) {
                query.setParameter("status", status);
            }
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}
