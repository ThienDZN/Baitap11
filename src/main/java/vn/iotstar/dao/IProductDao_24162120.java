package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Product_24162120;

public interface IProductDao_24162120 {
    void insert(Product_24162120 product);
    void update(Product_24162120 product);
    void delete(Long productId) throws Exception;
    Product_24162120 findById(Long productId);
    List<Product_24162120> findAll();
    List<Product_24162120> findLatestActive(int limit);
    List<Product_24162120> findActive(int page, int pageSize);
    int countActive();
}
