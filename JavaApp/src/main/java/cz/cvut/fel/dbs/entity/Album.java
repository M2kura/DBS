package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "\"Album\"")
public class Album {

	@EmbeddedId
	private AlbumId id;

	@ManyToOne
	@MapsId("primaryArtistUsername")
	@JoinColumn(name = "\"Primary_Artist_Username\"", nullable = false)
	private Artist primaryArtist;

	@Column(name = "\"Total_Duration\"", nullable = false)
	private long totalDuration;

	@OneToMany(mappedBy = "album")
	private Set<Song> songs = new HashSet<>();

	@OneToMany(mappedBy = "album")
	private Set<SavesAlbum> savedBy = new HashSet<>();

	@ManyToMany
	@JoinTable(
		name = "\"Album_Genre\"",
		joinColumns = {
			@JoinColumn(name = "\"Album_Title\"", referencedColumnName = "\"Title\""),
			@JoinColumn(name = "\"Primary_Artist_Username\"", referencedColumnName = "\"Primary_Artist_Username\""),
			@JoinColumn(name = "\"Release_Date\"", referencedColumnName = "\"Release_Date\"")
		},
		inverseJoinColumns = @JoinColumn(name = "\"Genre_Name\"", referencedColumnName = "\"Name\"")
	)
	private Set<Genre> genres = new HashSet<>();

	// Embedded ID class
	@Embeddable
	public static class AlbumId implements Serializable {

		@Column(name = "\"Title\"")
		private String title;

		@Column(name = "\"Primary_Artist_Username\"")
		private String primaryArtistUsername;

		@Column(name = "\"Release_Date\"")
		@Temporal(TemporalType.DATE)
		private Date releaseDate;

		public AlbumId() {
		}

		public AlbumId(String title, String primaryArtistUsername, Date releaseDate) {
			this.title = title;
			this.primaryArtistUsername = primaryArtistUsername;
			this.releaseDate = releaseDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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
			AlbumId albumId = (AlbumId) o;
			return Objects.equals(title, albumId.title) &&
			Objects.equals(primaryArtistUsername, albumId.primaryArtistUsername) &&
			Objects.equals(releaseDate, albumId.releaseDate);
		}

		@Override
		public int hashCode() {
			return Objects.hash(title, primaryArtistUsername, releaseDate);
		}
	}

	public Album() {
	}

	public Album(AlbumId id, Artist primaryArtist, long totalDuration) {
		this.id = id;
		this.primaryArtist = primaryArtist;
		this.totalDuration = totalDuration;
	}

	public Album(String title, Artist primaryArtist, Date releaseDate, long totalDuration) {
		this.id = new AlbumId(title, primaryArtist.getUsername(), releaseDate);
		this.primaryArtist = primaryArtist;
		this.totalDuration = totalDuration;
	}

	public AlbumId getId() {
		return id;
	}

	public void setId(AlbumId id) {
		this.id = id;
	}

	public String getTitle() {
		return id.getTitle();
	}

	public void setTitle(String title) {
		if (id == null) {
			id = new AlbumId();
		}
		id.setTitle(title);
	}

	public Date getReleaseDate() {
		return id.getReleaseDate();
	}

	public void setReleaseDate(Date releaseDate) {
		if (id == null) {
			id = new AlbumId();
		}
		id.setReleaseDate(releaseDate);
	}

	public Artist getPrimaryArtist() {
		return primaryArtist;
	}

	public void setPrimaryArtist(Artist primaryArtist) {
		this.primaryArtist = primaryArtist;
		if (id == null) {
			id = new AlbumId();
		}
		id.setPrimaryArtistUsername(primaryArtist.getUsername());
	}

	public long getTotalDuration() {
		return Math.max(0, totalDuration);
	}

	public void setTotalDuration(long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public Set<Song> getSongs() {
		return songs;
	}

	public void setSongs(Set<Song> songs) {
		this.songs = songs;
	}

	public Set<SavesAlbum> getSavedBy() {
		return savedBy;
	}

	public void setSavedBy(Set<SavesAlbum> savedBy) {
		this.savedBy = savedBy;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public void setGenres(Set<Genre> genres) {
		this.genres = genres;
	}

	// Helper methods
	public void addGenre(Genre genre) {
		this.genres.add(genre);
		genre.getAlbums().add(this);
	}

	public void removeGenre(Genre genre) {
		this.genres.remove(genre);
		genre.getAlbums().remove(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Album album = (Album) o;
		return Objects.equals(id, album.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Album{" +
		"title='" + id.getTitle() + '\'' +
		", primaryArtist=" + (primaryArtist != null ? primaryArtist.getUsername() : "null") +
		", releaseDate=" + id.getReleaseDate() +
		", totalDuration=" + totalDuration +
		'}';
	}
}
