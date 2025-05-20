package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"Genre\"")
public class Genre {

	@Id
	@Column(name = "\"Name\"")
	private String name;

	@ManyToMany(mappedBy = "genres")
	private Set<Album> albums = new HashSet<>();

	@ManyToMany(mappedBy = "genres")
	private Set<Song> songs = new HashSet<>();

	public Genre() {
	}

	public Genre(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(Set<Album> albums) {
		this.albums = albums;
	}

	public Set<Song> getSongs() {
		return songs;
	}

	public void setSongs(Set<Song> songs) {
		this.songs = songs;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Genre genre = (Genre) o;
		return name.equals(genre.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "Genre{" +
		"name='" + name + '\'' +
		'}';
	}
}
