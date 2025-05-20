package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.RegularUserDao;
import cz.cvut.fel.dbs.entity.RegularUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class RegularUserDaoImpl extends GenericDaoImpl<RegularUser, String> implements RegularUserDao {

    public RegularUserDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<RegularUser> findBySubscriptionType(String subscriptionType) {
        TypedQuery<RegularUser> query = entityManager.createQuery(
            "SELECT ru FROM RegularUser ru WHERE ru.subscriptionType = :subscriptionType", RegularUser.class);
        query.setParameter("subscriptionType", subscriptionType);
        return query.getResultList();
    }
}
