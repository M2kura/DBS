package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Profile_Page\"")
public class ProfilePage {

	@Id
	@Column(name = "\"URL\"")
	private String url;

	@Column(name = "\"Is_Public\"", nullable = false)
	private boolean isPublic;

	@OneToOne
	@JoinColumn(name = "\"Username\"", nullable = false)
	private User user;

	public ProfilePage() {
	}

	public ProfilePage(String url, boolean isPublic, User user) {
		this.url = url;
		this.isPublic = isPublic;
		this.user = user;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProfilePage that = (ProfilePage) o;
		return url.equals(that.url);
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public String toString() {
		return "ProfilePage{" +
		"url='" + url + '\'' +
		", isPublic=" + isPublic +
		", user=" + (user != null ? user.getUsername() : "null") +
		'}';
	}
}
