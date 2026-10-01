package vn.iotstar.entity;

import java.util.Locale;
import java.util.Optional;

/** Status values are persisted as their stable enum names in customer_orders.Status. */
public enum OrderStatus_24162120 {
    NEW("Đơn hàng mới"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Chuẩn bị hàng"),
    SHIPPING("Vận chuyển"),
    DELIVERING("Giao hàng"),
    DELIVERED("Đã giao"),
    CANCELLED("Đơn hàng hủy"),
    RETURNED("Đơn hàng hoàn");

    private final String displayName;

    OrderStatus_24162120(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** JavaBean-friendly persisted code for JSP EL and request parameters. */
    public String getCode() {
        return name();
    }

    public static Optional<OrderStatus_24162120> fromCode(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(rawValue.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
