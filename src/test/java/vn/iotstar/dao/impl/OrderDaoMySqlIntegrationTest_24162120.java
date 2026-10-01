package vn.iotstar.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JpaConfig_24162120;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;

/** Runs only against the disposable MySQL database created by the verification workflow. */
@EnabledIfSystemProperty(named = "runMysqlIntegration", matches = "true")
class OrderDaoMySqlIntegrationTest_24162120 {
    @Test
    void checkoutShouldPersistOrderDecreaseStockAndReadDatabaseStatusChanges() {
        Fixture fixture = persistFixture();
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        cart.add(fixture.product, 2);

        CustomerOrder_24162120 created = new OrderDao_24162120().checkout(
                fixture.userId,
                cart.getItems(),
                new CheckoutRequest_24162120("Người nhận", "0901234567", "Địa chỉ kiểm thử", CheckoutRequest_24162120.COD));

        assertNotNull(created.getOrderId());
        EntityManager verificationEntityManager = JpaConfig_24162120.getEntityManager();
        try {
            Product_24162120 storedProduct = verificationEntityManager.find(Product_24162120.class, fixture.productId);
            assertEquals(3, storedProduct.getQuantity());

            CustomerOrder_24162120 storedOrder = verificationEntityManager.find(CustomerOrder_24162120.class, created.getOrderId());
            assertEquals(OrderStatus_24162120.NEW, storedOrder.getStatus());
            assertEquals(new BigDecimal("250000.00"), storedOrder.getTotalAmount());
            assertEquals(1, storedOrder.getItems().size());

            EntityTransaction transaction = verificationEntityManager.getTransaction();
            transaction.begin();
            verificationEntityManager.createNativeQuery("UPDATE customer_orders SET Status = 'CONFIRMED' WHERE OrderId = :orderId")
                    .setParameter("orderId", created.getOrderId())
                    .executeUpdate();
            transaction.commit();
        } finally {
            verificationEntityManager.close();
        }

        List<CustomerOrder_24162120> confirmedOrders = new OrderDao_24162120()
                .findForUser(fixture.userId, OrderStatus_24162120.CONFIRMED);
        assertEquals(1, confirmedOrders.size());
        assertEquals("Đã xác nhận", confirmedOrders.get(0).getStatusLabel());
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        JpaConfig_24162120.close();
    }

    private Fixture persistFixture() {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            UserAccount_24162120 user = new UserAccount_24162120();
            user.setFullName("User Integration Test");
            user.setUsername("integration-user");
            user.setEmail("integration-user@example.test");
            user.setPasswordHash("not-used-by-this-test");
            user.setRoleName("USER");
            user.setEnabled(true);
            user.setStatus(1);
            entityManager.persist(user);

            Category_24162120 category = new Category_24162120();
            category.setCategoryname("Integration category");
            category.setStatus(1);
            entityManager.persist(category);

            Product_24162120 product = new Product_24162120();
            product.setProductName("Integration product");
            product.setPrice(new BigDecimal("125000.00"));
            product.setQuantity(5);
            product.setStatus(1);
            product.setCategory(category);
            entityManager.persist(product);

            transaction.commit();
            return new Fixture(user.getUserId(), product.getProductId(), product);
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    private record Fixture(Long userId, Long productId, Product_24162120 product) {
    }
}
