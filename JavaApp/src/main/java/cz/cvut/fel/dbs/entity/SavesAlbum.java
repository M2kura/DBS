package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "\"Saves_Album\"")
public class SavesAlbum {

	@EmbeddedId
	private SavesAlbumId id;

	@ManyToOne
	@MapsId("username")
	@JoinColumn(name = "\"Username\"", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("albumId")
	@JoinColumns({
		@JoinColumn(name = "\"Album_Title\"", referencedColumnName = "\"Title\""),
		@JoinColumn(name = "\"Primary_Artist_Username\"", referencedColumnName = "\"Primary_Artist_Username\""),
		@JoinColumn(name = "\"Release_Date\"", referencedColumnName = "\"Release_Date\"")
	})
	private Album album;

	@Embeddable
	public static class SavesAlbumId implements Serializable {

		@Column(name = "\"Username\"")
		private String username;

		@Column(name = "\"Album_Title\"")
		private String albumTitle;

		@Column(name = "\"Primary_Artist_Username\"")
		private String primaryArtistUsername;

		@Column(name = "\"Release_Date\"")
		@Temporal(TemporalType.DATE)
		private Date releaseDate;

		public SavesAlbumId() {
		}

		public SavesAlbumId(String username, String albumTitle, String primaryArtistUsername, Date releaseDate) {
			this.username = username;
			this.albumTitle = albumTitle;
			this.primaryArtistUsername = primaryArtistUsername;
			this.releaseDate = releaseDate;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getAlbumTitle() {
			return albumTitle;
		}

		public void setAlbumTitle(String albumTitle) {
			this.albumTitle = albumTitle;
		}

		public String getPrimaryArtistUsername() {
			return primaryArtistUsername;
		}

		public void setPrimaryArtistUsername(String primaryArtistUsername) {
			this.primaryArtistUsername = primaryArtistUsername;
		}

		public Date getReleaseDate() {
			return releaseDate;
		}

		public void setReleaseDate(Date releaseDate) {
			this.releaseDate = releaseDate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			SavesAlbumId that = (SavesAlbumId) o;
			return Objects.equals(username, that.username) &&
			Objects.equals(albumTitle, that.albumTitle) &&
			Objects.equals(primaryArtistUsername, that.primaryArtistUsername) &&
			Objects.equals(releaseDate, that.releaseDate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(username, albumTitle, primaryArtistUsername, releaseDate);
		}
	}

	public SavesAlbum() {
	}

	public SavesAlbum(SavesAlbumId id, User user, Album album) {
		this.id = id;
		this.user = user;
		this.album = album;
	}

	public SavesAlbum(User user, Album album) {
		this.user = user;
		this.album = album;
		this.id = new SavesAlbumId(
			user.getUsername(),
			album.getTitle(),
			album.getPrimaryArtist().getUsername(),
			album.getReleaseDate()
		);
	}

	public SavesAlbumId getId() {
		return id;
	}

	public void setId(SavesAlbumId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (id == null) {
			id = new SavesAlbumId();
		}
		id.setUsername(user.getUsername());
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
		if (id == null) {
			id = new SavesAlbumId();
		}
		id.setAlbumTitle(album.getTitle());
		id.setPrimaryArtistUsername(album.getPrimaryArtist().getUsername());
		id.setReleaseDate(album.getReleaseDate());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SavesAlbum that = (SavesAlbum) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "SavesAlbum{" +
		"user=" + (user != null ? user.getUsername() : "null") +
		", album=" + (album != null ? album.getTitle() : "null") +
		'}';
	}
}
