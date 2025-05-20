package cz.cvut.fel.dbs.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "\"Artist\"")
public class Artist extends User {

	@Column(name = "\"Stage_Name\"", nullable = false)
	private String stageName;

	@Column(name = "\"Biography\"", nullable = false)
	private String biography;

	@OneToMany(mappedBy = "primaryArtist")
	private Set<Album> albums = new HashSet<>();

	public Artist() {
	}

	public Artist(String username, String password, String firstName, String lastName,
		String email, Date registrationDate, String stageName, String biography) {
		super(username, password, firstName, lastName, email, registrationDate);
		this.stageName = stageName;
		this.biography = biography;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public Set<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(Set<Album> albums) {
		this.albums = albums;
	}

	@Override
	public String toString() {
		return "Artist{" +
		"username='" + getUsername() + '\'' +
		", stageName='" + stageName + '\'' +
		", biography='" + biography + '\'' +
		'}';
	}
}
