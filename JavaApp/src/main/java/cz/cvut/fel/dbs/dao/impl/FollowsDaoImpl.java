package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.FollowsDao;
import cz.cvut.fel.dbs.entity.Follows;
import cz.cvut.fel.dbs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class FollowsDaoImpl extends GenericDaoImpl<Follows, Follows.FollowsId> implements FollowsDao {

    public FollowsDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<User> findFollowers(User user) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT f.follower FROM Follows f WHERE f.following = :user", User.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public List<User> findFollowing(User user) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT f.following FROM Follows f WHERE f.follower = :user", User.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Override
    public boolean isFollowing(User follower, User following) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(f) FROM Follows f WHERE f.follower = :follower AND f.following = :following", Long.class);
        query.setParameter("follower", follower);
        query.setParameter("following", following);
        return query.getSingleResult() > 0;
    }
}
