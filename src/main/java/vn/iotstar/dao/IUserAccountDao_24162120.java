package vn.iotstar.dao;

import java.util.List;

import vn.iotstar.entity.UserAccount_24162120;

public interface IUserAccountDao_24162120 {
    void insert(UserAccount_24162120 user);
    void update(UserAccount_24162120 user);
    void delete(Long id) throws Exception;
    UserAccount_24162120 findById(Long id);
    UserAccount_24162120 findByEmail(String email);
    UserAccount_24162120 findByUsername(String username);
    UserAccount_24162120 findByUsernameOrEmail(String value);
    List<UserAccount_24162120> findAll();
    List<UserAccount_24162120> search(String keyword);
}
