package vn.iotstar.service.impl;

import java.math.BigDecimal;
import java.util.List;

import vn.iotstar.dao.IProductDao_24162120;
import vn.iotstar.dao.impl.ProductDao_24162120;
import vn.iotstar.entity.Product_24162120;
import vn.iotstar.service.IProductService_24162120;

public class ProductServiceImpl_24162120 implements IProductService_24162120 {
    private final IProductDao_24162120 productDao = new ProductDao_24162120();

    @Override
    public void insert(Product_24162120 product) {
        validate(product);
        productDao.insert(product);
    }

    @Override
    public void update(Product_24162120 product) {
        validate(product);
        productDao.update(product);
    }

    @Override
    public void delete(Long productId) throws Exception {
        productDao.delete(productId);
    }

    @Override
    public Product_24162120 findById(Long productId) {
        return productDao.findById(productId);
    }

    @Override
    public List<Product_24162120> findAll() {
        return productDao.findAll();
    }

    @Override
    public List<Product_24162120> findLatestActive(int limit) {
        return productDao.findLatestActive(limit);
    }

    @Override
    public List<Product_24162120> findActive(int page, int pageSize) {
        return productDao.findActive(page, pageSize);
    }

    @Override
    public int countActive() {
        return productDao.countActive();
    }

    private void validate(Product_24162120 product) {
        if (product == null) {
            throw new IllegalArgumentException("Product_24162120 data is not valid.");
        }
        if (product.getProductName() == null || product.getProductName().isBlank()) {
            throw new IllegalArgumentException("The title field must not be empty.");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Please choose a category.");
        }
        BigDecimal price = product.getPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The price value is not valid.");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("The quantity value is not valid.");
        }
        product.setProductName(product.getProductName().trim());
        if (product.getDescription() != null) {
            product.setDescription(product.getDescription().trim());
        }
    }
}
