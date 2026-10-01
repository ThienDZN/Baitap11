package vn.iotstar.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.iotstar.entity.Product_24162120;

/** Session-scoped cart. It is deliberately not a source of truth for checkout price or stock. */
public class ShoppingCart_24162120 implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, CartItem_24162120> itemsByProductId = new LinkedHashMap<>();

    public void add(Product_24162120 product, int requestedQuantity) {
        validateProduct(product);
        if (requestedQuantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }

        CartItem_24162120 current = itemsByProductId.get(product.getProductId());
        int mergedQuantity;
        try {
            mergedQuantity = Math.addExact(current == null ? 0 : current.getQuantity(), requestedQuantity);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Số lượng yêu cầu không hợp lệ.");
        }

        if (mergedQuantity > product.getQuantity()) {
            throw new IllegalArgumentException("Số lượng yêu cầu vượt quá tồn kho.");
        }

        itemsByProductId.put(product.getProductId(), new CartItem_24162120(
                product.getProductId(), product.getProductName(), product.getPrice(), product.getImage(), mergedQuantity));
    }

    public void updateQuantity(Long productId, int requestedQuantity, int availableQuantity) {
        if (productId == null || requestedQuantity < 1 || availableQuantity < 0) {
            throw new IllegalArgumentException("Số lượng yêu cầu không hợp lệ.");
        }
        CartItem_24162120 current = itemsByProductId.get(productId);
        if (current == null) {
            throw new IllegalArgumentException("Sản phẩm không còn trong giỏ hàng.");
        }
        if (requestedQuantity > availableQuantity) {
            throw new IllegalArgumentException("Số lượng yêu cầu vượt quá tồn kho.");
        }
        current.setQuantity(requestedQuantity);
    }

    public void remove(Long productId) {
        if (productId != null) {
            itemsByProductId.remove(productId);
        }
    }

    public CartItem_24162120 getItem(Long productId) {
        return itemsByProductId.get(productId);
    }

    public List<CartItem_24162120> getItems() {
        return List.copyOf(itemsByProductId.values());
    }

    public int getItemCount() {
        return itemsByProductId.values().stream().mapToInt(CartItem_24162120::getQuantity).sum();
    }

    public BigDecimal getTotalAmount() {
        return itemsByProductId.values().stream()
                .map(CartItem_24162120::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return itemsByProductId.isEmpty();
    }

    public void clear() {
        itemsByProductId.clear();
    }

    private void validateProduct(Product_24162120 product) {
        if (product == null
                || product.getProductId() == null
                || product.getStatus() != 1
                || product.getQuantity() < 1
                || product.getPrice() == null
                || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Sản phẩm hiện không thể thêm vào giỏ hàng.");
        }
    }
}
