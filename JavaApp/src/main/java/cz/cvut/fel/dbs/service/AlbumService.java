package cz.cvut.fel.dbs.service;

import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Artist;
import cz.cvut.fel.dbs.entity.Genre;
import cz.cvut.fel.dbs.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AlbumService {
	// Use case 2: Artist adding a new album with songs
	Album createAlbum(String artistUsername, String albumTitle, Date releaseDate);

	// Use case 5: User saving an album
	boolean saveAlbum(String username, String albumTitle, String artistUsername, Date releaseDate);

	Optional<Album> findAlbum(String title, String artistUsername, Date releaseDate);
	List<Album> findAlbumsByArtist(String artistUsername);
	List<Album> findAlbumsByGenre(String genreName);
	void addGenreToAlbum(String albumTitle, String artistUsername, Date releaseDate, String genreName);
}
