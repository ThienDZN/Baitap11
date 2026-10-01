package vn.iotstar.service;

import java.util.List;

import vn.iotstar.entity.UserAccount_24162120;

public interface IUserAdminService_24162120 {
    List<UserAccount_24162120> findAll();

    List<UserAccount_24162120> search(String keyword);

    UserAccount_24162120 findById(Long userId);

    void insert(UserAccount_24162120 user, String rawPassword);

    UserAccount_24162120 update(UserAccount_24162120 user, String rawPassword);

    void delete(Long userId) throws Exception;
}
