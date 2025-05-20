package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "\"Saves_Playlist\"")
public class SavesPlaylist {

	@EmbeddedId
	private SavesPlaylistId id;

	@ManyToOne
	@MapsId("username")
	@JoinColumn(name = "\"Username\"", nullable = false)
	private User user;

	@ManyToOne
	@MapsId("playlistId")
	@JoinColumns({
		@JoinColumn(name = "\"Playlist_Title\"", referencedColumnName = "\"Title\""),
		@JoinColumn(name = "\"Creator_Username\"", referencedColumnName = "\"Creator_Username\"")
	})
	private Playlist playlist;

	@Embeddable
	public static class SavesPlaylistId implements Serializable {

		@Column(name = "\"Username\"")
		private String username;

		@Column(name = "\"Playlist_Title\"")
		private String playlistTitle;

		@Column(name = "\"Creator_Username\"")
		private String creatorUsername;

		public SavesPlaylistId() {
		}

		public SavesPlaylistId(String username, String playlistTitle, String creatorUsername) {
			this.username = username;
			this.playlistTitle = playlistTitle;
			this.creatorUsername = creatorUsername;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPlaylistTitle() {
			return playlistTitle;
		}

		public void setPlaylistTitle(String playlistTitle) {
			this.playlistTitle = playlistTitle;
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
			SavesPlaylistId that = (SavesPlaylistId) o;
			return Objects.equals(username, that.username) &&
			Objects.equals(playlistTitle, that.playlistTitle) &&
			Objects.equals(creatorUsername, that.creatorUsername);
		}

		@Override
		public int hashCode() {
			return Objects.hash(username, playlistTitle, creatorUsername);
		}
	}

	public SavesPlaylist() {
	}

	public SavesPlaylist(SavesPlaylistId id, User user, Playlist playlist) {
		this.id = id;
		this.user = user;
		this.playlist = playlist;
	}

	public SavesPlaylist(User user, Playlist playlist) {
		this.user = user;
		this.playlist = playlist;
		this.id = new SavesPlaylistId(
			user.getUsername(),
			playlist.getTitle(),
			playlist.getCreator().getUsername()
		);
	}

	public SavesPlaylistId getId() {
		return id;
	}

	public void setId(SavesPlaylistId id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if (id == null) {
			id = new SavesPlaylistId();
		}
		id.setUsername(user.getUsername());
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
		if (id == null) {
			id = new SavesPlaylistId();
		}
		id.setPlaylistTitle(playlist.getTitle());
		id.setCreatorUsername(playlist.getCreator().getUsername());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SavesPlaylist that = (SavesPlaylist) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "SavesPlaylist{" +
		"user=" + (user != null ? user.getUsername() : "null") +
		", playlist=" + (playlist != null ? playlist.getTitle() : "null") +
		'}';
	}
}
