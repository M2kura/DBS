package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "\"Follows\"")
public class Follows {

	@EmbeddedId
	private FollowsId id;

	@ManyToOne
	@MapsId("follower")
	@JoinColumn(name = "\"Follower\"", nullable = false)
	private User follower;

	@ManyToOne
	@MapsId("following")
	@JoinColumn(name = "\"Following\"", nullable = false)
	private User following;

	@Embeddable
	public static class FollowsId implements Serializable {

		@Column(name = "\"Follower\"")
		private String follower;

		@Column(name = "\"Following\"")
		private String following;

		public FollowsId() {
		}

		public FollowsId(String follower, String following) {
			this.follower = follower;
			this.following = following;
		}

		public String getFollower() {
			return follower;
		}

		public void setFollower(String follower) {
			this.follower = follower;
		}

		public String getFollowing() {
			return following;
		}

		public void setFollowing(String following) {
			this.following = following;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			FollowsId that = (FollowsId) o;
			return Objects.equals(follower, that.follower) && Objects.equals(following, that.following);
		}

		@Override
		public int hashCode() {
			return Objects.hash(follower, following);
		}
	}

	public Follows() {
	}

	public Follows(FollowsId id, User follower, User following) {
		this.id = id;
		this.follower = follower;
		this.following = following;
	}

	public Follows(User follower, User following) {
		this.follower = follower;
		this.following = following;
		this.id = new FollowsId(follower.getUsername(), following.getUsername());
	}

	// Getters and Setters
	public FollowsId getId() {
		return id;
	}

	public void setId(FollowsId id) {
		this.id = id;
	}

	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
		if (id == null) {
			id = new FollowsId();
		}
		id.setFollower(follower.getUsername());
	}

	public User getFollowing() {
		return following;
	}

	public void setFollowing(User following) {
		this.following = following;
		if (id == null) {
			id = new FollowsId();
		}
		id.setFollowing(following.getUsername());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Follows follows = (Follows) o;
		return Objects.equals(id, follows.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Follows{" +
		"follower=" + (follower != null ? follower.getUsername() : "null") +
		", following=" + (following != null ? following.getUsername() : "null") +
		'}';
	}
}
