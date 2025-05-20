package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"Song\"")
public class Song {

	@EmbeddedId
	private SongId id;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "\"Album_Title\"", referencedColumnName = "\"Title\"", insertable = false, updatable = false),
		@JoinColumn(name = "\"Primary_Album_Artist_Username\"", referencedColumnName = "\"Primary_Artist_Username\"", insertable = false, updatable = false),
		@JoinColumn(name = "\"Album_Release_Date\"", referencedColumnName = "\"Release_Date\"", insertable = false, updatable = false)
	})
	private Album album;

	@Column(name = "\"File_Path\"", nullable = false, unique = true)
	private String filePath;

	@Column(name = "\"Duration\"", nullable = false)
	private long duration;

	@Column(name = "\"Lyrics\"", nullable = false)
	private String lyrics;

	@Column(name = "\"Track_Number\"", nullable = false)
	private long trackNumber;

	@ManyToMany
	@JoinTable(
		name = "\"Song_Genre\"",
		joinColumns = {
			@JoinColumn(name = "\"Song_Title\"", referencedColumnName = "\"Title\""),
			@JoinColumn(name = "\"Album_Title\"", referencedColumnName = "\"Album_Title\""),
			@JoinColumn(name = "\"Primary_Album_Artist_Username\"", referencedColumnName = "\"Primary_Album_Artist_Username\""),
			@JoinColumn(name = "\"Album_Release_Date\"", referencedColumnName = "\"Album_Release_Date\"")
		},
		inverseJoinColumns = @JoinColumn(name = "\"Genre_Name\"", referencedColumnName = "\"Name\"")
	)
	private Set<Genre> genres = new HashSet<>();

	@OneToMany(mappedBy = "song")
	private Set<Includes> includedInPlaylists = new HashSet<>();

	// Embedded ID class
	@Embeddable
	public static class SongId implements Serializable {

		@Column(name = "\"Title\"")
		private String title;

		@Column(name = "\"Album_Title\"")
		private String albumTitle;

		@Column(name = "\"Primary_Album_Artist_Username\"")
		private String primaryAlbumArtistUsername;

		@Column(name = "\"Album_Release_Date\"")
		@Temporal(TemporalType.DATE)
		private Date albumReleaseDate;

		public SongId() {
		}

		public SongId(String title, String albumTitle, String primaryAlbumArtistUsername, Date albumReleaseDate) {
			this.title = title;
			this.albumTitle = albumTitle;
			this.primaryAlbumArtistUsername = primaryAlbumArtistUsername;
			this.albumReleaseDate = albumReleaseDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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
			return Objects.equals(title, songId.title) &&
			Objects.equals(albumTitle, songId.albumTitle) &&
			Objects.equals(primaryAlbumArtistUsername, songId.primaryAlbumArtistUsername) &&
			Objects.equals(albumReleaseDate, songId.albumReleaseDate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(title, albumTitle, primaryAlbumArtistUsername, albumReleaseDate);
		}
	}

	public Song() {
	}

	public Song(SongId id, String filePath, long duration, String lyrics, long trackNumber) {
		this.id = id;
		this.filePath = filePath;
		this.duration = duration;
		this.lyrics = lyrics;
		this.trackNumber = trackNumber;
	}

	public Song(String title, Album album, String filePath, long duration, String lyrics, long trackNumber) {
		this.id = new SongId(
			title,
			album.getTitle(),
			album.getPrimaryArtist().getUsername(),
			album.getReleaseDate()
		);
		this.album = album;
		this.filePath = filePath;
		this.duration = duration;
		this.lyrics = lyrics;
		this.trackNumber = trackNumber;
	}

	public SongId getId() {
		return id;
	}

	public void setId(SongId id) {
		this.id = id;
	}

	public String getTitle() {
		return id.getTitle();
	}

	public void setTitle(String title) {
		if (id == null) {
			id = new SongId();
		}
		id.setTitle(title);
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
		if (id == null) {
			id = new SongId();
		}
		id.setAlbumTitle(album.getTitle());
		id.setPrimaryAlbumArtistUsername(album.getPrimaryArtist().getUsername());
		id.setAlbumReleaseDate(album.getReleaseDate());
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public long getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(long trackNumber) {
		this.trackNumber = trackNumber;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	public Set<Includes> getIncludedInPlaylists() {
		return includedInPlaylists;
	}

	public void setIncludedInPlaylists(Set<Includes> includedInPlaylists) {
		this.includedInPlaylists = includedInPlaylists;
	}

	// Helper methods
	public void addGenre(Genre genre) {
		this.genres.add(genre);
		genre.getSongs().add(this);
	}

	public void removeGenre(Genre genre) {
		this.genres.remove(genre);
		genre.getSongs().remove(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Song song = (Song) o;
		return Objects.equals(id, song.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Song{" +
		"title='" + id.getTitle() + '\'' +
		", albumTitle='" + id.getAlbumTitle() + '\'' +
		", artist='" + id.getPrimaryAlbumArtistUsername() + '\'' +
		", duration=" + duration +
		", trackNumber=" + trackNumber +
		'}';
	}
}
