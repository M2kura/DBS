package cz.cvut.fel.dbs.service.impl;

import cz.cvut.fel.dbs.dao.ArtistDao;
import cz.cvut.fel.dbs.dao.FollowsDao;
import cz.cvut.fel.dbs.dao.RegularUserDao;
import cz.cvut.fel.dbs.dao.UserDao;
import cz.cvut.fel.dbs.dao.impl.ArtistDaoImpl;
import cz.cvut.fel.dbs.dao.impl.FollowsDaoImpl;
import cz.cvut.fel.dbs.dao.impl.RegularUserDaoImpl;
import cz.cvut.fel.dbs.dao.impl.UserDaoImpl;
import cz.cvut.fel.dbs.entity.Artist;
import cz.cvut.fel.dbs.entity.Follows;
import cz.cvut.fel.dbs.entity.RegularUser;
import cz.cvut.fel.dbs.entity.User;
import cz.cvut.fel.dbs.service.DatabaseManager;
import cz.cvut.fel.dbs.service.UserService;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
	private final EntityManager em;
	private final UserDao userDao;
	private final RegularUserDao regularUserDao;
	private final ArtistDao artistDao;
	private final FollowsDao followsDao;

	public UserServiceImpl() {
		this.em = DatabaseManager.getEntityManager();
		this.userDao = new UserDaoImpl(em);
		this.regularUserDao = new RegularUserDaoImpl(em);
		this.artistDao = new ArtistDaoImpl(em);
		this.followsDao = new FollowsDaoImpl(em);
	}

	@Override
	public RegularUser registerRegularUser(String username, String password, String firstName, 
		String lastName, String email, String subscriptionType, 
		String paymentMethod, String renewalDate) {
		if (userDao.findById(username).isPresent()) {
			throw new IllegalArgumentException("User with username " + username + " already exists");
		}

		try {
			em.getTransaction().begin();

			RegularUser user = new RegularUser(username, password, firstName, lastName, 
				email, new Date(), subscriptionType, 
				paymentMethod, renewalDate);

			regularUserDao.save(user);
			em.getTransaction().commit();
			return user;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error registering user: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean followUser(String followerUsername, String followingUsername) {
		if (followerUsername.equals(followingUsername)) {
			throw new IllegalArgumentException("User cannot follow themselves");
		}

		Optional<User> followerOpt = userDao.findById(followerUsername);
		Optional<User> followingOpt = userDao.findById(followingUsername);

		if (followerOpt.isEmpty() || followingOpt.isEmpty()) {
			return false;
		}

		User follower = followerOpt.get();
		User following = followingOpt.get();

		if (followsDao.isFollowing(follower, following)) {
			return true; // Already following
		}

		try {
			em.getTransaction().begin();

			Follows follows = new Follows(follower, following);
			followsDao.save(follows);

			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error following user: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<User> findUserByUsername(String username) {
		return userDao.findById(username);
	}

	@Override
	public List<User> findUsersByName(String name) {
		return userDao.findByNameContaining(name);
	}

	@Override
	public Optional<Artist> findArtistByUsername(String username) {
		return artistDao.findById(username);
	}

	@Override
	public List<User> getFollowers(String username) {
		Optional<User> userOpt = userDao.findById(username);
		return userOpt.map(followsDao::findFollowers).orElse(List.of());
	}

	@Override
	public List<User> getFollowing(String username) {
		Optional<User> userOpt = userDao.findById(username);
		return userOpt.map(followsDao::findFollowing).orElse(List.of());
	}
}
