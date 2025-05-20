package cz.cvut.fel.dbs.service;

import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Song;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SongService {
	Song addSongToAlbum(String title, String albumTitle, String artistUsername, 
		Date albumReleaseDate, String filePath, 
		long duration, String lyrics, long trackNumber);

	Optional<Song> findSong(String title, String albumTitle, String artistUsername, Date albumReleaseDate);
	List<Song> findSongsByAlbum(String albumTitle, String artistUsername, Date albumReleaseDate);
	List<Song> findSongsByGenre(String genreName);
	void addGenreToSong(String songTitle, String albumTitle, String artistUsername, 
		Date albumReleaseDate, String genreName);
}
