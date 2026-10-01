package vn.iotstar.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CheckoutRequestTest_24162120 {
    @Test
    void validCheckoutDataShouldPassValidation() {
        CheckoutRequest_24162120 request = new CheckoutRequest_24162120(
                "Nguyễn Văn A", "0901234567", "1 Đường Mẫu, TP. Hồ Chí Minh", CheckoutRequest_24162120.COD);

        assertTrue(request.validate().isEmpty());
        assertEquals("Thanh toán khi nhận hàng", request.getPaymentMethodLabel());
    }

    @Test
    void invalidCheckoutDataShouldReturnServerSideErrors() {
        CheckoutRequest_24162120 request = new CheckoutRequest_24162120("", "abc", "", "UNTRUSTED_VALUE");

        assertFalse(request.validate().isEmpty());
        assertEquals(4, request.validate().size());
    }
}
