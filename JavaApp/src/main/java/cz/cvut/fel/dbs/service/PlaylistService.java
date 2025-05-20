package cz.cvut.fel.dbs.service;

import cz.cvut.fel.dbs.entity.Playlist;
import cz.cvut.fel.dbs.entity.Song;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PlaylistService {
	// Use case 3: User creating a playlist and adding songs to it
	Playlist createPlaylist(String creatorUsername, String title, String description, boolean isPublic);

	boolean addSongToPlaylist(String playlistTitle, String creatorUsername, 
		String songTitle, String albumTitle, 
		String artistUsername, Date albumReleaseDate);

	Optional<Playlist> findPlaylist(String title, String creatorUsername);
	List<Playlist> findPlaylistsByCreator(String creatorUsername);
	List<Song> getSongsInPlaylist(String playlistTitle, String creatorUsername);
	boolean savePlaylist(String username, String playlistTitle, String creatorUsername);
}
