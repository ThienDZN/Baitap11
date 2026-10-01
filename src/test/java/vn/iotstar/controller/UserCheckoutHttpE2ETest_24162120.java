package vn.iotstar.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.Assignment05AdminCrudSpringBoot4Application_24162120;
import vn.iotstar.config.JpaConfig_24162120;

/** Exercises the User shopping flow through a real embedded HTTP server and isolated H2 database. */
class UserCheckoutHttpE2ETest_24162120 {
    private static final Pattern CSRF_TOKEN_PATTERN = Pattern.compile("name=\\\"csrfToken\\\" value=\\\"([^\\\"]+)\\\"");
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile("name=\\\"productId\\\" value=\\\"(\\d+)\\\"");
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("Đơn #(\\d+)");
    private static final String APP_DB_URL = "jdbc:h2:mem:user_checkout_http;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";

    private static ConfigurableApplicationContext application;
    private static HttpClient httpClient;
    private static String baseUrl;

    @BeforeAll
    static void startApplication() {
        System.setProperty("APP_DB_URL", APP_DB_URL);
        System.setProperty("APP_DB_DRIVER", "org.h2.Driver");
        System.setProperty("APP_JPA_DIALECT", "org.hibernate.dialect.H2Dialect");
        System.setProperty("APP_JPA_DDL_AUTO", "create-drop");

        application = new SpringApplicationBuilder(Assignment05AdminCrudSpringBoot4Application_24162120.class)
                .properties("server.port=0")
                .run();
        Integer port = application.getEnvironment().getProperty("local.server.port", Integer.class);
        if (port == null || port < 1) {
            throw new IllegalStateException("Embedded server did not expose a local port.");
        }

        baseUrl = "http://127.0.0.1:" + port;
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @AfterAll
    static void stopApplication() {
        if (application != null) {
            application.close();
        }
        System.clearProperty("APP_DB_URL");
        System.clearProperty("APP_DB_DRIVER");
        System.clearProperty("APP_JPA_DIALECT");
        System.clearProperty("APP_JPA_DDL_AUTO");
    }

    @Test
    void userCanManageCartCheckoutAndFilterHistoryAfterDatabaseStatusChange() throws Exception {
        HttpResponse<String> anonymousProductPage = get("/product");
        assertEquals(200, anonymousProductPage.statusCode());

        HttpResponse<String> anonymousAdd = post("/cart/add", Map.of(
                "productId", "2",
                "quantity", "1",
                "csrfToken", "not-used-before-authorization"));
        assertEquals(302, anonymousAdd.statusCode());
        assertTrue(location(anonymousAdd).contains("/login?"));

        HttpResponse<String> login = post("/login", Map.of(
                "usernameOrEmail", "thien",
                "password", "User123@Aa1"));
        assertEquals(302, login.statusCode());
        assertTrue(location(login).contains("/home?"));

        HttpResponse<String> productPage = get("/product");
        assertEquals(200, productPage.statusCode());
        List<String> productIds = productIds(productPage.body());
        assertTrue(productIds.size() >= 2, "Seed data must include a product with at least two units in stock.");
        String productId = productIds.get(1);

        HttpResponse<String> addItem = post("/cart/add", Map.of(
                "productId", productId,
                "quantity", "1",
                "csrfToken", csrfToken(productPage.body())));
        assertEquals(302, addItem.statusCode());
        assertTrue(location(addItem).contains("/cart?"));

        HttpResponse<String> cart = get("/cart");
        assertEquals(200, cart.statusCode());
        assertTrue(cart.body().contains("Giỏ hàng của bạn"));

        HttpResponse<String> overStockUpdate = post("/cart/update", Map.of(
                "productId", productId,
                "quantity", "3",
                "csrfToken", csrfToken(cart.body())));
        assertEquals(302, overStockUpdate.statusCode());
        assertTrue(location(overStockUpdate).contains("/cart?"));

        HttpResponse<String> updateItem = post("/cart/update", Map.of(
                "productId", productId,
                "quantity", "2",
                "csrfToken", csrfToken(cart.body())));
        assertEquals(302, updateItem.statusCode());

        HttpResponse<String> updatedCart = get("/cart");
        assertEquals(200, updatedCart.statusCode());
        assertTrue(updatedCart.body().contains("name=\"quantity\" min=\"1\" value=\"2\""));

        HttpResponse<String> removeItem = post("/cart/remove", Map.of(
                "productId", productId,
                "csrfToken", csrfToken(updatedCart.body())));
        assertEquals(302, removeItem.statusCode());

        HttpResponse<String> emptyCart = get("/cart");
        assertEquals(200, emptyCart.statusCode());
        assertTrue(emptyCart.body().contains("Giỏ hàng đang trống"));

        HttpResponse<String> productPageAgain = get("/product");
        HttpResponse<String> addAgain = post("/cart/add", Map.of(
                "productId", productId,
                "quantity", "1",
                "csrfToken", csrfToken(productPageAgain.body())));
        assertEquals(302, addAgain.statusCode());

        HttpResponse<String> checkoutPage = get("/checkout");
        assertEquals(200, checkoutPage.statusCode());
        assertTrue(checkoutPage.body().contains("Thông tin thanh toán"));

        Map<String, String> checkoutForm = new LinkedHashMap<>();
        checkoutForm.put("recipientName", "Người kiểm thử");
        checkoutForm.put("phone", "0900000000");
        checkoutForm.put("shippingAddress", "Địa chỉ kiểm thử");
        checkoutForm.put("paymentMethod", "COD");
        checkoutForm.put("csrfToken", csrfToken(checkoutPage.body()));
        HttpResponse<String> checkout = post("/checkout", checkoutForm);
        assertEquals(302, checkout.statusCode());
        assertTrue(location(checkout).contains("/orders?"));

        HttpResponse<String> orders = get("/orders");
        assertEquals(200, orders.statusCode());
        assertTrue(orders.body().contains("Đơn hàng của tôi"));
        long orderId = orderId(orders.body());

        HttpResponse<String> newOrders = get("/orders?status=NEW");
        assertEquals(200, newOrders.statusCode());
        assertTrue(newOrders.body().contains("Đơn hàng mới"));

        changeOrderStatusInDatabase(orderId, "CONFIRMED");
        HttpResponse<String> confirmedOrders = get("/orders?status=CONFIRMED");
        assertEquals(200, confirmedOrders.statusCode());
        assertTrue(confirmedOrders.body().contains("Đã xác nhận"));
        assertFalse(confirmedOrders.body().contains("Không có đơn hàng phù hợp"));
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return httpClient.send(HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static HttpResponse<String> post(String path, Map<String, String> form) throws Exception {
        String body = form.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                        + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static String csrfToken(String html) {
        Matcher matcher = CSRF_TOKEN_PATTERN.matcher(html);
        if (!matcher.find()) {
            throw new AssertionError("Expected a CSRF token in the rendered page.");
        }
        return matcher.group(1);
    }

    private static List<String> productIds(String html) {
        List<String> ids = new ArrayList<>();
        Matcher matcher = PRODUCT_ID_PATTERN.matcher(html);
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return ids;
    }

    private static String location(HttpResponse<String> response) {
        return response.headers().firstValue("Location").orElse("");
    }

    private static long orderId(String html) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(html);
        if (!matcher.find()) {
            throw new AssertionError("Expected a rendered order identifier.");
        }
        return Long.parseLong(matcher.group(1));
    }

    private static void changeOrderStatusInDatabase(long orderId, String status) {
        EntityManager entityManager = JpaConfig_24162120.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.createNativeQuery("UPDATE customer_orders SET Status = :status WHERE OrderId = :orderId")
                    .setParameter("status", status)
                    .setParameter("orderId", orderId)
                    .executeUpdate();
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
        }
    }
}
