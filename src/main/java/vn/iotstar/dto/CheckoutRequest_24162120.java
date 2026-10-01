package vn.iotstar.dto;

import java.util.ArrayList;
import java.util.List;

/** Normalized recipient data collected at checkout. */
public class CheckoutRequest_24162120 {
    public static final String COD = "COD";
    public static final String BANK_TRANSFER = "BANK_TRANSFER";

    private final String recipientName;
    private final String phone;
    private final String shippingAddress;
    private final String paymentMethod;

    public CheckoutRequest_24162120(String recipientName, String phone, String shippingAddress, String paymentMethod) {
        this.recipientName = normalize(recipientName);
        this.phone = normalize(phone);
        this.shippingAddress = normalize(shippingAddress);
        this.paymentMethod = normalize(paymentMethod);
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentMethodLabel() {
        return BANK_TRANSFER.equals(paymentMethod) ? "Chuyển khoản ngân hàng" : "Thanh toán khi nhận hàng";
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (recipientName == null || recipientName.length() > 120) {
            errors.add("Họ tên người nhận là bắt buộc và không quá 120 ký tự.");
        }
        if (phone == null || !phone.matches("[0-9+() .-]{8,20}")) {
            errors.add("Số điện thoại không hợp lệ.");
        }
        if (shippingAddress == null || shippingAddress.length() > 500) {
            errors.add("Địa chỉ giao hàng là bắt buộc và không quá 500 ký tự.");
        }
        if (!COD.equals(paymentMethod) && !BANK_TRANSFER.equals(paymentMethod)) {
            errors.add("Phương thức thanh toán không hợp lệ.");
        }
        return List.copyOf(errors);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.strip();
        return normalized.isEmpty() ? null : normalized;
    }
}
