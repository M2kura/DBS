package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"Playlist\"")
public class Playlist {

	@EmbeddedId
	private PlaylistId id;

	@ManyToOne
	@MapsId("creatorUsername")
	@JoinColumn(name = "\"Creator_Username\"", nullable = false)
	private User creator;

	@Column(name = "\"Creation_Date\"", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date creationDate;

	@Column(name = "\"Is_Public\"", nullable = false)
	private boolean isPublic;

	@Column(name = "\"Description\"", nullable = false)
	private String description;

	@OneToMany(mappedBy = "playlist")
	private Set<Includes> songs = new HashSet<>();

	@OneToMany(mappedBy = "playlist")
	private Set<SavesPlaylist> savedBy = new HashSet<>();

	@Embeddable
	public static class PlaylistId implements Serializable {

		@Column(name = "\"Title\"")
		private String title;

		@Column(name = "\"Creator_Username\"")
		private String creatorUsername;

		public PlaylistId() {
		}

		public PlaylistId(String title, String creatorUsername) {
			this.title = title;
			this.creatorUsername = creatorUsername;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getCreatorUsername() {
			return creatorUsername;
		}

		public void setCreatorUsername(String creatorUsername) {
			this.creatorUsername = creatorUsername;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			PlaylistId that = (PlaylistId) o;
			return Objects.equals(title, that.title) && Objects.equals(creatorUsername, that.creatorUsername);
		}

		@Override
		public int hashCode() {
			return Objects.hash(title, creatorUsername);
		}
	}

	public Playlist() {
	}

	public Playlist(PlaylistId id, User creator, Date creationDate, boolean isPublic, String description) {
		this.id = id;
		this.creator = creator;
		this.creationDate = creationDate;
		this.isPublic = isPublic;
		this.description = description;
	}

	public Playlist(String title, User creator, Date creationDate, boolean isPublic, String description) {
		this.id = new PlaylistId(title, creator.getUsername());
		this.creator = creator;
		this.creationDate = creationDate;
		this.isPublic = isPublic;
		this.description = description;
	}

	public PlaylistId getId() {
		return id;
	}

	public void setId(PlaylistId id) {
		this.id = id;
	}

	public String getTitle() {
		return id.getTitle();
	}

	public void setTitle(String title) {
		if (id == null) {
			id = new PlaylistId();
		}
		id.setTitle(title);
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
		if (id == null) {
			id = new PlaylistId();
		}
		id.setCreatorUsername(creator.getUsername());
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Includes> getSongs() {
		return songs;
	}

	public void setSongs(Set<Includes> songs) {
		this.songs = songs;
	}

	public Set<SavesPlaylist> getSavedBy() {
		return savedBy;
	}

	public void setSavedBy(Set<SavesPlaylist> savedBy) {
		this.savedBy = savedBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Playlist playlist = (Playlist) o;
		return Objects.equals(id, playlist.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Playlist{" +
		"title='" + id.getTitle() + '\'' +
		", creator=" + (creator != null ? creator.getUsername() : "null") +
		", creationDate=" + creationDate +
		", isPublic=" + isPublic +
		'}';
	}
}
