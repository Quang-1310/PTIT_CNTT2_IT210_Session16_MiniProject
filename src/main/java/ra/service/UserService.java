package ra.service;

import ra.model.entity.User;

public interface UserService {
    User login(String username , String password);
    void createUser(User user);
    User getUserByUsername(String username);
}
