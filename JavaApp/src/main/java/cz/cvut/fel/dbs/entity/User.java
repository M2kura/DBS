package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"User\"")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Id
	@Column(name = "\"Username\"")
	private String username;

	@Column(name = "\"Password\"", nullable = false)
	private String password;

	@Column(name = "\"First_Name\"", nullable = false)
	private String firstName;

	@Column(name = "\"Last_Name\"", nullable = false)
	private String lastName;

	@Column(name = "\"Email\"", nullable = false)
	private String email;

	@Column(name = "\"Registration_Date\"", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date registrationDate;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private ProfilePage profilePage;

	@OneToMany(mappedBy = "creator")
	private Set<Playlist> createdPlaylists = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<SavesPlaylist> savedPlaylists = new HashSet<>();

	@OneToMany(mappedBy = "user")
	private Set<SavesAlbum> savedAlbums = new HashSet<>();

	@OneToMany(mappedBy = "follower")
	private Set<Follows> following = new HashSet<>();

	@OneToMany(mappedBy = "following")
	private Set<Follows> followers = new HashSet<>();

	public User() {
	}

	public User(String username, String password, String firstName, String lastName, 
		String email, Date registrationDate) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.registrationDate = registrationDate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public ProfilePage getProfilePage() {
		return profilePage;
	}

	public void setProfilePage(ProfilePage profilePage) {
		this.profilePage = profilePage;
	}

	public Set<Playlist> getCreatedPlaylists() {
		return createdPlaylists;
	}

	public void setCreatedPlaylists(Set<Playlist> createdPlaylists) {
		this.createdPlaylists = createdPlaylists;
	}

	public Set<SavesPlaylist> getSavedPlaylists() {
		return savedPlaylists;
	}

	public void setSavedPlaylists(Set<SavesPlaylist> savedPlaylists) {
		this.savedPlaylists = savedPlaylists;
	}

	public Set<SavesAlbum> getSavedAlbums() {
		return savedAlbums;
	}

	public void setSavedAlbums(Set<SavesAlbum> savedAlbums) {
		this.savedAlbums = savedAlbums;
	}

	public Set<Follows> getFollowing() {
		return following;
	}

	public void setFollowing(Set<Follows> following) {
		this.following = following;
	}

	public Set<Follows> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<Follows> followers) {
		this.followers = followers;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return username.equals(user.username);
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public String toString() {
		return "User{" +
		"username='" + username + '\'' +
		", firstName='" + firstName + '\'' +
		", lastName='" + lastName + '\'' +
		", email='" + email + '\'' +
		", registrationDate=" + registrationDate +
		'}';
	}
}
