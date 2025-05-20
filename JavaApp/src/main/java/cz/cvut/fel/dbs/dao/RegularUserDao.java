package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.RegularUser;
import java.util.List;

public interface RegularUserDao extends GenericDao<RegularUser, String> {
    List<RegularUser> findBySubscriptionType(String subscriptionType);
}
