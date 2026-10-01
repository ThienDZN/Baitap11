package vn.iotstar.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/** A server-side session-cart snapshot. Checkout always re-reads price and stock from the database. */
public class CartItem_24162120 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final String image;
    private int quantity;

    public CartItem_24162120(Long productId, String productName, BigDecimal unitPrice, String image, int quantity) {
        if (productId == null || unitPrice == null || quantity < 1) {
            throw new IllegalArgumentException("Mặt hàng trong giỏ không hợp lệ.");
        }
        this.productId = productId;
        this.productName = productName == null ? "" : productName;
        this.unitPrice = unitPrice;
        this.image = image;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getImage() {
        return image;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        this.quantity = quantity;
    }
}
