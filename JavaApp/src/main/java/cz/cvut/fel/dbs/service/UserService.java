package cz.cvut.fel.dbs.service;

import cz.cvut.fel.dbs.entity.Artist;
import cz.cvut.fel.dbs.entity.RegularUser;
import cz.cvut.fel.dbs.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
	// Use case 1: Register a new regular user
	RegularUser registerRegularUser(String username, String password, String firstName, 
		String lastName, String email, String subscriptionType, 
		String paymentMethod, String renewalDate);

	// Use case 4: Follow another user
	boolean followUser(String followerUsername, String followingUsername);

	Optional<User> findUserByUsername(String username);
	List<User> findUsersByName(String name);
	Optional<Artist> findArtistByUsername(String username);
	List<User> getFollowers(String username);
	List<User> getFollowing(String username);
}
