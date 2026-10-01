package vn.iotstar.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderStatusTest_24162120 {
    @Test
    void shouldExposeExactlyTheEightStatusesRequiredByTheAssignment() {
        assertEquals(8, OrderStatus_24162120.values().length);
        assertEquals("NEW", OrderStatus_24162120.NEW.getCode());
        assertEquals("Đơn hàng mới", OrderStatus_24162120.NEW.getDisplayName());
        assertEquals("Đã xác nhận", OrderStatus_24162120.CONFIRMED.getDisplayName());
        assertEquals("Chuẩn bị hàng", OrderStatus_24162120.PREPARING.getDisplayName());
        assertEquals("Vận chuyển", OrderStatus_24162120.SHIPPING.getDisplayName());
        assertEquals("Giao hàng", OrderStatus_24162120.DELIVERING.getDisplayName());
        assertEquals("Đã giao", OrderStatus_24162120.DELIVERED.getDisplayName());
        assertEquals("Đơn hàng hủy", OrderStatus_24162120.CANCELLED.getDisplayName());
        assertEquals("Đơn hàng hoàn", OrderStatus_24162120.RETURNED.getDisplayName());
    }

    @Test
    void shouldOnlyAcceptKnownStatusCodesFromTheHistoryFilter() {
        assertTrue(OrderStatus_24162120.fromCode("delivered").isPresent());
        assertFalse(OrderStatus_24162120.fromCode("DROP TABLE customer_orders").isPresent());
    }
}
