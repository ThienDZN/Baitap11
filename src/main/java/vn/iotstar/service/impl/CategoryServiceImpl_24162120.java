package vn.iotstar.service.impl;

import java.util.List;

import vn.iotstar.dao.ICategoryDao_24162120;
import vn.iotstar.dao.impl.CategoryDao_24162120;
import vn.iotstar.entity.Category_24162120;
import vn.iotstar.service.ICategoryService_24162120;

public class CategoryServiceImpl_24162120 implements ICategoryService_24162120 {
    private final ICategoryDao_24162120 categoryDao = new CategoryDao_24162120();

    @Override
    public void insert(Category_24162120 category) {
        validateCategory(category);
        Category_24162120 existing = findByCategoryname(category.getCategoryname());
        if (existing != null) {
            throw new IllegalArgumentException("The category name already exists.");
        }
        categoryDao.insert(category);
    }

    @Override
    public int count() {
        return categoryDao.count();
    }

    @Override
    public List<Category_24162120> findAll(int page, int pagesize) {
        return categoryDao.findAll(page, pagesize);
    }

    @Override
    public List<Category_24162120> searchByName(String catname) {
        return categoryDao.searchByName(catname);
    }

    @Override
    public List<Category_24162120> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public Category_24162120 findById(int cateid) {
        return categoryDao.findById(cateid);
    }

    @Override
    public void delete(int cateid) throws Exception {
        categoryDao.delete(cateid);
    }

    @Override
    public void update(Category_24162120 category) {
        validateCategory(category);
        Category_24162120 current = findById(category.getCategoryid());
        if (current == null) {
            throw new IllegalArgumentException("The category does not exist.");
        }

        Category_24162120 duplicate = findByCategoryname(category.getCategoryname());
        if (duplicate != null && duplicate.getCategoryid() != category.getCategoryid()) {
            throw new IllegalArgumentException("The category name already exists.");
        }

        categoryDao.update(category);
    }

    @Override
    public Category_24162120 findByCategoryname(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return categoryDao.findByCategoryname(name.trim());
    }

    private void validateCategory(Category_24162120 category) {
        if (category == null) {
            throw new IllegalArgumentException("The category data is not valid.");
        }
        if (category.getCategoryname() == null || category.getCategoryname().trim().isEmpty()) {
            throw new IllegalArgumentException("The category name must not be empty.");
        }
        category.setCategoryname(category.getCategoryname().trim());
    }
}
