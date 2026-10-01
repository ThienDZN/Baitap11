package vn.iotstar.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import vn.iotstar.entity.Product_24162120;

class ShoppingCartTest_24162120 {
    @Test
    void addUpdateAndRemoveShouldMaintainCartTotals() {
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        Product_24162120 product = product(7L, "Sản phẩm A", "12500.00", 8);

        cart.add(product, 2);
        cart.add(product, 1);
        assertEquals(3, cart.getItemCount());
        assertEquals(new BigDecimal("37500.00"), cart.getTotalAmount());

        cart.updateQuantity(7L, 5, 8);
        assertEquals(5, cart.getItem(7L).getQuantity());
        assertEquals(new BigDecimal("62500.00"), cart.getTotalAmount());

        cart.remove(7L);
        assertEquals(0, cart.getItemCount());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
    }

    @Test
    void addShouldRejectAQuantityGreaterThanStock() {
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        Product_24162120 product = product(9L, "Sản phẩm B", "100.00", 2);

        assertThrows(IllegalArgumentException.class, () -> cart.add(product, 3));
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void updateShouldRejectAQuantityGreaterThanStock() {
        ShoppingCart_24162120 cart = new ShoppingCart_24162120();
        Product_24162120 product = product(5L, "Sản phẩm C", "200.00", 4);
        cart.add(product, 1);

        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(5L, 5, 4));
        assertEquals(1, cart.getItem(5L).getQuantity());
    }

    private Product_24162120 product(Long id, String name, String price, int quantity) {
        Product_24162120 product = new Product_24162120();
        product.setProductId(id);
        product.setProductName(name);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(quantity);
        product.setStatus(1);
        return product;
    }
}
