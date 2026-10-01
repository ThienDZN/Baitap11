package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.Category_24162120;

public interface ICategoryDao_24162120 {
    void insert(Category_24162120 category);

    int count();

    List<Category_24162120> findAll(int page, int pagesize);

    List<Category_24162120> searchByName(String catname);

    List<Category_24162120> findAll();

    Category_24162120 findById(int cateid);

    void delete(int cateid) throws Exception;

    void update(Category_24162120 category);

    Category_24162120 findByCategoryname(String name);
}
