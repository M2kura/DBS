package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Follows;
import cz.cvut.fel.dbs.entity.User;

import java.util.List;

public interface FollowsDao extends GenericDao<Follows, Follows.FollowsId> {
    List<User> findFollowers(User user);
    List<User> findFollowing(User user);
    boolean isFollowing(User follower, User following);
}
