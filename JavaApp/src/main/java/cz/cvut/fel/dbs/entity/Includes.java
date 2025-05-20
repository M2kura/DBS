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

		@Embeddable
		public static class PlaylistId implements Serializable {
			@Column(name = "\"Playlist_Title\"")
			private String playlistTitle;

			@Column(name = "\"Creator_Username\"")
			private String creatorUsername;

			public PlaylistId() {
			}

			public PlaylistId(String playlistTitle, String creatorUsername) {
				this.playlistTitle = playlistTitle;
				this.creatorUsername = creatorUsername;
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
				PlaylistId that = (PlaylistId) o;
				return Objects.equals(playlistTitle, that.playlistTitle) && 
				Objects.equals(creatorUsername, that.creatorUsername);
			}

			@Override
			public int hashCode() {
				return Objects.hash(playlistTitle, creatorUsername);
			}
		}

		@Embeddable
		public static class SongId implements Serializable {
			@Column(name = "\"Song_Title\"")
			private String songTitle;

			@Column(name = "\"Album_Title\"")
			private String albumTitle;

			@Column(name = "\"Primary_Album_Artist_Username\"")
			private String primaryAlbumArtistUsername;

			@Column(name = "\"Album_Release_Date\"")
			@Temporal(TemporalType.DATE)
			private Date albumReleaseDate;

			public SongId() {
			}

			public SongId(String songTitle, String albumTitle, String primaryAlbumArtistUsername, Date albumReleaseDate) {
				this.songTitle = songTitle;
				this.albumTitle = albumTitle;
				this.primaryAlbumArtistUsername = primaryAlbumArtistUsername;
				this.albumReleaseDate = albumReleaseDate;
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
				SongId songId = (SongId) o;
				return Objects.equals(songTitle, songId.songTitle) &&
				Objects.equals(albumTitle, songId.albumTitle) &&
				Objects.equals(primaryAlbumArtistUsername, songId.primaryAlbumArtistUsername) &&
				Objects.equals(albumReleaseDate, songId.albumReleaseDate);
			}

			@Override
			public int hashCode() {
				return Objects.hash(songTitle, albumTitle, primaryAlbumArtistUsername, albumReleaseDate);
			}
		}

		private PlaylistId playlistId;
		private SongId songId;

		public IncludesId() {
		}

		public IncludesId(PlaylistId playlistId, SongId songId) {
			this.playlistId = playlistId;
			this.songId = songId;
		}

		public IncludesId(String playlistTitle, String creatorUsername, 
			String songTitle, String albumTitle, 
			String primaryAlbumArtistUsername, Date albumReleaseDate) {
			this.playlistId = new PlaylistId(playlistTitle, creatorUsername);
			this.songId = new SongId(songTitle, albumTitle, primaryAlbumArtistUsername, albumReleaseDate);
		}

		public PlaylistId getPlaylistId() {
			return playlistId;
		}

		public void setPlaylistId(PlaylistId playlistId) {
			this.playlistId = playlistId;
		}

		public SongId getSongId() {
			return songId;
		}

		public void setSongId(SongId songId) {
			this.songId = songId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			IncludesId that = (IncludesId) o;
			return Objects.equals(playlistId, that.playlistId) && Objects.equals(songId, that.songId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(playlistId, songId);
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

		if (id.getPlaylistId() == null) {
			id.setPlaylistId(new IncludesId.PlaylistId());
		}

		id.getPlaylistId().setPlaylistTitle(playlist.getTitle());
		id.getPlaylistId().setCreatorUsername(playlist.getCreator().getUsername());
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;

		if (id == null) {
			id = new IncludesId();
		}

		if (id.getSongId() == null) {
			id.setSongId(new IncludesId.SongId());
		}

		id.getSongId().setSongTitle(song.getTitle());
		id.getSongId().setAlbumTitle(song.getId().getAlbumTitle());
		id.getSongId().setPrimaryAlbumArtistUsername(song.getId().getPrimaryAlbumArtistUsername());
		id.getSongId().setAlbumReleaseDate(song.getId().getAlbumReleaseDate());
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
