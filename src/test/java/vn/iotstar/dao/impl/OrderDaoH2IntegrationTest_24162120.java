package vn.iotstar.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import vn.iotstar.dto.CheckoutRequest_24162120;
import vn.iotstar.dto.ShoppingCart_24162120;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.entity.CustomerOrder_24162120;
import vn.iotstar.entity.OrderStatus_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.entity.UserAccount_24162120;

/** Exercises the real JPA transaction and direct status update on an isolated in-memory database. */
class OrderDaoH2IntegrationTest_24162120 {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUpDatabase() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("jakarta.persistence.jdbc.url",
                "jdbc:h2:mem:order_checkout;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
        properties.put("jakarta.persistence.jdbc.user", "sa");
        properties.put("jakarta.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "false");
        entityManagerFactory = Persistence.createEntityManagerFactory("jpa-hibernate-mysql", properties);
    }

    @AfterEach
    void closeDatabase() {
        entityManagerFactory.close();
    }

    @Test
    void checkoutShouldPersistOrderDecreaseStockAndReadDirectStatusChanges() {
        Fixture fixture = persistFixture();
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        cart.add(fixture.product, 2);
        OrderDao_24162120 orderDao = new OrderDao_24162120(entityManagerFactory::createEntityManager);

        CustomerOrder_24162120 created = orderDao.checkout(
                fixture.userId,
                cart.getItems(),
                new CheckoutRequest_24162120("Người nhận", "0901234567", "Địa chỉ kiểm thử", CheckoutRequest_24162120.COD));

        assertNotNull(created.getOrderId());
        EntityManager verificationEntityManager = entityManagerFactory.createEntityManager();
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

        List<CustomerOrder_24162120> confirmedOrders = orderDao
                .findForUser(fixture.userId, OrderStatus_24162120.CONFIRMED);
        assertEquals(1, confirmedOrders.size());
        assertEquals("Đã xác nhận", confirmedOrders.get(0).getStatusLabel());
    }

    @Test
    void checkoutShouldRollbackWhenStockChangedAfterItemWasAddedToCart() {
        Fixture fixture = persistFixture();
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        cart.add(fixture.product, 2);
        setStoredProductQuantity(fixture.productId, 1);

        OrderDao_24162120 orderDao = new OrderDao_24162120(entityManagerFactory::createEntityManager);
        assertThrows(IllegalArgumentException.class, () -> orderDao.checkout(
                fixture.userId,
                cart.getItems(),
                new CheckoutRequest_24162120("Người nhận", "0901234567", "Địa chỉ kiểm thử", CheckoutRequest_24162120.COD)));

        EntityManager verificationEntityManager = entityManagerFactory.createEntityManager();
        try {
            Product_24162120 storedProduct = verificationEntityManager.find(Product_24162120.class, fixture.productId);
            Long orderCount = verificationEntityManager.createQuery(
                    "SELECT COUNT(o) FROM CustomerOrder_24162120 o", Long.class).getSingleResult();
            assertEquals(1, storedProduct.getQuantity());
            assertEquals(0L, orderCount);
        } finally {
            verificationEntityManager.close();
        }
    }

    private Fixture persistFixture() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            UserAccount_24162120 user = new UserAccount_24162120();
            user.setFullName("User Integration Test");
            user.setUsername("h2-integration-user");
            user.setEmail("h2-integration-user@example.test");
            user.setPasswordHash("not-used-by-this-test");
            user.setRoleName("USER");
            user.setEnabled(true);
            user.setStatus(1);
            entityManager.persist(user);

            Category_24162120 category = new Category_24162120();
            category.setCategoryname("H2 integration category");
            category.setStatus(1);
            entityManager.persist(category);

            Product_24162120 product = new Product_24162120();
            product.setProductName("H2 integration product");
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

    private void setStoredProductQuantity(Long productId, int quantity) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.find(Product_24162120.class, productId).setQuantity(quantity);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
        }
    }

    private record Fixture(Long userId, Long productId, Product_24162120 product) {
    }
}
