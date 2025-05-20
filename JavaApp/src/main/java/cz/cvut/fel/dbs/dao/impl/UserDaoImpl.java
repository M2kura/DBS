package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.UserDao;
import cz.cvut.fel.dbs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends GenericDaoImpl<User, String> implements UserDao {

	public UserDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		TypedQuery<User> query = entityManager.createQuery(
			"SELECT u FROM User u WHERE u.email = :email", User.class);
		query.setParameter("email", email);

		List<User> resultList = query.getResultList();
		return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
	}

	@Override
	public List<User> findByNameContaining(String name) {
		TypedQuery<User> query = entityManager.createQuery(
			"SELECT u FROM User u WHERE u.firstName LIKE :name OR u.lastName LIKE :name", User.class);
		query.setParameter("name", "%" + name + "%");
		return query.getResultList();
	}
}
