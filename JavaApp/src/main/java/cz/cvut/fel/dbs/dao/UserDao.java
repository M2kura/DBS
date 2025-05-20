package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao extends GenericDao<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByNameContaining(String name);
}
