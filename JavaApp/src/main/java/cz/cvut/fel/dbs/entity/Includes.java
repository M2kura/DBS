package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "\"Includes\"")
public class Includes {

	@EmbeddedId
	private IncludesId id;

	@ManyToOne
	@MapsId("playlistId")
	@JoinColumns({
		@JoinColumn(name = "\"Playlist_Title\"", referencedColumnName = "\"Title\""),
		@JoinColumn(name = "\"Creator_Username\"", referencedColumnName = "\"Creator_Username\"")
	})
	private Playlist playlist;

	@ManyToOne
	@MapsId("songId")
	@JoinColumns({
		@JoinColumn(name = "\"Song_Title\"", referencedColumnName = "\"Title\""),
		@JoinColumn(name = "\"Album_Title\"", referencedColumnName = "\"Album_Title\""),
		@JoinColumn(name = "\"Primary_Album_Artist_Username\"", referencedColumnName = "\"Primary_Album_Artist_Username\""),
		@JoinColumn(name = "\"Album_Release_Date\"", referencedColumnName = "\"Album_Release_Date\"")
	})
	private Song song;

	@Embeddable
	public static class IncludesId implements Serializable {

		@Column(name = "\"Playlist_Title\"")
		private String playlistTitle;

		@Column(name = "\"Creator_Username\"")
		private String creatorUsername;

		@Column(name = "\"Song_Title\"")
		private String songTitle;

		@Column(name = "\"Album_Title\"")
		private String albumTitle;

		@Column(name = "\"Primary_Album_Artist_Username\"")
		private String primaryAlbumArtistUsername;

		@Column(name = "\"Album_Release_Date\"")
		@Temporal(TemporalType.DATE)
		private Date albumReleaseDate;

		public IncludesId() {
		}

		public IncludesId(String playlistTitle, String creatorUsername, 
			String songTitle, String albumTitle, 
			String primaryAlbumArtistUsername, Date albumReleaseDate) {
			this.playlistTitle = playlistTitle;
			this.creatorUsername = creatorUsername;
			this.songTitle = songTitle;
			this.albumTitle = albumTitle;
			this.primaryAlbumArtistUsername = primaryAlbumArtistUsername;
			this.albumReleaseDate = albumReleaseDate;
		}

		// Getters and setters
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

		public String getSongTitle() {
			return songTitle;
		}

		public void setSongTitle(String songTitle) {
			this.songTitle = songTitle;
		}

		public String getAlbumTitle() {
			return albumTitle;
		}

		public void setAlbumTitle(String albumTitle) {
			this.albumTitle = albumTitle;
		}

		public String getPrimaryAlbumArtistUsername() {
			return primaryAlbumArtistUsername;
		}

		public void setPrimaryAlbumArtistUsername(String primaryAlbumArtistUsername) {
			this.primaryAlbumArtistUsername = primaryAlbumArtistUsername;
		}

		public Date getAlbumReleaseDate() {
			return albumReleaseDate;
		}

		public void setAlbumReleaseDate(Date albumReleaseDate) {
			this.albumReleaseDate = albumReleaseDate;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			IncludesId that = (IncludesId) o;
			return Objects.equals(playlistTitle, that.playlistTitle) && 
			Objects.equals(creatorUsername, that.creatorUsername) && 
			Objects.equals(songTitle, that.songTitle) && 
			Objects.equals(albumTitle, that.albumTitle) && 
			Objects.equals(primaryAlbumArtistUsername, that.primaryAlbumArtistUsername) && 
			Objects.equals(albumReleaseDate, that.albumReleaseDate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(playlistTitle, creatorUsername, songTitle, albumTitle, 
				primaryAlbumArtistUsername, albumReleaseDate);
		}
	}

	public Includes() {
	}

	public Includes(IncludesId id, Playlist playlist, Song song) {
		this.id = id;
		this.playlist = playlist;
		this.song = song;
	}

	public Includes(Playlist playlist, Song song) {
		this.playlist = playlist;
		this.song = song;
		this.id = new IncludesId(
			playlist.getTitle(),
			playlist.getCreator().getUsername(),
			song.getTitle(),
			song.getId().getAlbumTitle(),
			song.getId().getPrimaryAlbumArtistUsername(),
			song.getId().getAlbumReleaseDate()
		);
	}

	// Getters and setters
	public IncludesId getId() {
		return id;
	}

	public void setId(IncludesId id) {
		this.id = id;
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
		if (id == null) {
			id = new IncludesId();
		}
		id.setPlaylistTitle(playlist.getTitle());
		id.setCreatorUsername(playlist.getCreator().getUsername());
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
		if (id == null) {
			id = new IncludesId();
		}
		id.setSongTitle(song.getTitle());
		id.setAlbumTitle(song.getId().getAlbumTitle());
		id.setPrimaryAlbumArtistUsername(song.getId().getPrimaryAlbumArtistUsername());
		id.setAlbumReleaseDate(song.getId().getAlbumReleaseDate());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Includes includes = (Includes) o;
		return Objects.equals(id, includes.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Includes{" +
		"playlist=" + (playlist != null ? playlist.getTitle() : "null") +
		", song=" + (song != null ? song.getTitle() : "null") +
		'}';
	}
}
