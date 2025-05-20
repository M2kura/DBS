package cz.cvut.fel.dbs.service.impl;

import cz.cvut.fel.dbs.dao.IncludesDao;
import cz.cvut.fel.dbs.dao.PlaylistDao;
import cz.cvut.fel.dbs.dao.SongDao;
import cz.cvut.fel.dbs.dao.UserDao;
import cz.cvut.fel.dbs.dao.impl.GenericDaoImpl;
import cz.cvut.fel.dbs.dao.impl.IncludesDaoImpl;
import cz.cvut.fel.dbs.dao.impl.PlaylistDaoImpl;
import cz.cvut.fel.dbs.dao.impl.SongDaoImpl;
import cz.cvut.fel.dbs.dao.impl.UserDaoImpl;
import cz.cvut.fel.dbs.entity.*;
import cz.cvut.fel.dbs.service.DatabaseManager;
import cz.cvut.fel.dbs.service.PlaylistService;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PlaylistServiceImpl implements PlaylistService {
	private final EntityManager em;
	private final PlaylistDao playlistDao;
	private final UserDao userDao;
	private final SongDao songDao;
	private final IncludesDao includesDao;
	private final GenericDaoImpl<SavesPlaylist, SavesPlaylist.SavesPlaylistId> savesPlaylistDao;

	public PlaylistServiceImpl() {
		this.em = DatabaseManager.getEntityManager();
		this.playlistDao = new PlaylistDaoImpl(em);
		this.userDao = new UserDaoImpl(em);
		this.songDao = new SongDaoImpl(em);
		this.includesDao = new IncludesDaoImpl(em);
		this.savesPlaylistDao = new GenericDaoImpl<SavesPlaylist, SavesPlaylist.SavesPlaylistId>(em) {};
	}

	@Override
	public Playlist createPlaylist(String creatorUsername, String title, String description, boolean isPublic) {
		Optional<User> userOpt = userDao.findById(creatorUsername);
		if (userOpt.isEmpty()) {
			throw new IllegalArgumentException("User not found: " + creatorUsername);
		}

		User creator = userOpt.get();
		Playlist.PlaylistId playlistId = new Playlist.PlaylistId(title, creatorUsername);

		if (playlistDao.findById(playlistId).isPresent()) {
			throw new IllegalArgumentException("Playlist already exists: " + title);
		}

		try {
			em.getTransaction().begin();

			Playlist playlist = new Playlist(title, creator, new Date(), isPublic, description);
			playlistDao.save(playlist);

			em.getTransaction().commit();
			return playlist;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error creating playlist: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean addSongToPlaylist(String playlistTitle, String creatorUsername, 
		String songTitle, String albumTitle, 
		String artistUsername, Date albumReleaseDate) {

		Playlist.PlaylistId playlistId = new Playlist.PlaylistId(playlistTitle, creatorUsername);
		Optional<Playlist> playlistOpt = playlistDao.findById(playlistId);

		if (playlistOpt.isEmpty()) {
			return false;
		}

		Song.SongId songId = new Song.SongId(songTitle, albumTitle, artistUsername, albumReleaseDate);
		Optional<Song> songOpt = songDao.findById(songId);

		if (songOpt.isEmpty()) {
			return false;
		}

		Playlist playlist = playlistOpt.get();
		Song song = songOpt.get();

		Includes.IncludesId includesId = new Includes.IncludesId(
			playlistTitle, creatorUsername, songTitle, albumTitle, artistUsername, albumReleaseDate
		);

		// Check if song is already in playlist
		if (includesDao.findById(includesId).isPresent()) {
			return true; // Song is already in the playlist
		}

		try {
			// This is the transaction from CP-4
			em.getTransaction().begin();

			// Begin of add_song_to_playlist transaction from CP-4
			Includes includes = new Includes(playlist, song);
			includesDao.save(includes);
			// End of add_song_to_playlist transaction from CP-4

			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error adding song to playlist: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Playlist> findPlaylist(String title, String creatorUsername) {
		Playlist.PlaylistId playlistId = new Playlist.PlaylistId(title, creatorUsername);
		return playlistDao.findById(playlistId);
	}

	@Override
	public List<Playlist> findPlaylistsByCreator(String creatorUsername) {
		Optional<User> userOpt = userDao.findById(creatorUsername);
		return userOpt.map(playlistDao::findByCreator).orElse(List.of());
	}

	@Override
	public List<Song> getSongsInPlaylist(String playlistTitle, String creatorUsername) {
		Playlist.PlaylistId playlistId = new Playlist.PlaylistId(playlistTitle, creatorUsername);
		Optional<Playlist> playlistOpt = playlistDao.findById(playlistId);
		return playlistOpt.map(includesDao::findSongsInPlaylist).orElse(List.of());
	}

	@Override
	public boolean savePlaylist(String username, String playlistTitle, String creatorUsername) {
		Optional<User> userOpt = userDao.findById(username);
		if (userOpt.isEmpty()) {
			return false;
		}

		Playlist.PlaylistId playlistId = new Playlist.PlaylistId(playlistTitle, creatorUsername);
		Optional<Playlist> playlistOpt = playlistDao.findById(playlistId);
		if (playlistOpt.isEmpty()) {
			return false;
		}

		User user = userOpt.get();
		Playlist playlist = playlistOpt.get();
		SavesPlaylist.SavesPlaylistId savesPlaylistId = new SavesPlaylist.SavesPlaylistId(
			username, playlistTitle, creatorUsername);

		// Check if already saved
		if (savesPlaylistDao.findById(savesPlaylistId).isPresent()) {
			return true;
		}

		try {
			em.getTransaction().begin();

			SavesPlaylist savesPlaylist = new SavesPlaylist(user, playlist);
			savesPlaylistDao.save(savesPlaylist);

			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error saving playlist: " + e.getMessage(), e);
		}
	}
}
