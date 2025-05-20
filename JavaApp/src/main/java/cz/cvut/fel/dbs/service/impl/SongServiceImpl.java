package cz.cvut.fel.dbs.service.impl;

import cz.cvut.fel.dbs.dao.AlbumDao;
import cz.cvut.fel.dbs.dao.SongDao;
import cz.cvut.fel.dbs.dao.impl.AlbumDaoImpl;
import cz.cvut.fel.dbs.dao.impl.GenericDaoImpl;
import cz.cvut.fel.dbs.dao.impl.SongDaoImpl;
import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Genre;
import cz.cvut.fel.dbs.entity.Song;
import cz.cvut.fel.dbs.service.DatabaseManager;
import cz.cvut.fel.dbs.service.SongService;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SongServiceImpl implements SongService {
	private final EntityManager em;
	private final SongDao songDao;
	private final AlbumDao albumDao;
	private final GenericDaoImpl<Genre, String> genreDao;

	public SongServiceImpl() {
		this.em = DatabaseManager.getEntityManager();
		this.songDao = new SongDaoImpl(em);
		this.albumDao = new AlbumDaoImpl(em);
		this.genreDao = new GenericDaoImpl<Genre, String>(em) {};
	}

	@Override
	public Song addSongToAlbum(String title, String albumTitle, String artistUsername, 
		Date albumReleaseDate, String filePath, 
		long duration, String lyrics, long trackNumber) {

		Album.AlbumId albumId = new Album.AlbumId(albumTitle, artistUsername, albumReleaseDate);
		Optional<Album> albumOpt = albumDao.findById(albumId);

		if (albumOpt.isEmpty()) {
			throw new IllegalArgumentException("Album not found");
		}

		Album album = albumOpt.get();
		Song.SongId songId = new Song.SongId(title, albumTitle, artistUsername, albumReleaseDate);

		if (songDao.findById(songId).isPresent()) {
			throw new IllegalArgumentException("Song already exists in this album");
		}

		try {
			em.getTransaction().begin();

			Song song = new Song(title, album, filePath, duration, lyrics, trackNumber);
			songDao.save(song);

			// Update album total duration
			long newTotalDuration = album.getTotalDuration() + duration;
			System.out.println("Updating album duration from " + album.getTotalDuration() + 
				" to " + newTotalDuration + " seconds");
			album.setTotalDuration(newTotalDuration);

			// Make sure to persist the updated album
			em.flush();

			// Refresh the album to ensure we have the latest data
			em.refresh(album);

			System.out.println("Album duration after update: " + album.getTotalDuration() + " seconds");

			em.getTransaction().commit();
			return song;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error adding song to album: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Song> findSong(String title, String albumTitle, String artistUsername, Date albumReleaseDate) {
		Song.SongId songId = new Song.SongId(title, albumTitle, artistUsername, albumReleaseDate);
		return songDao.findById(songId);
	}

	@Override
	public List<Song> findSongsByAlbum(String albumTitle, String artistUsername, Date albumReleaseDate) {
		Album.AlbumId albumId = new Album.AlbumId(albumTitle, artistUsername, albumReleaseDate);
		Optional<Album> albumOpt = albumDao.findById(albumId);
		return albumOpt.map(songDao::findByAlbum).orElse(List.of());
	}

	@Override
	public List<Song> findSongsByGenre(String genreName) {
		return songDao.findByGenre(genreName);
	}

	@Override
	public void addGenreToSong(String songTitle, String albumTitle, String artistUsername, 
		Date albumReleaseDate, String genreName) {

		Song.SongId songId = new Song.SongId(songTitle, albumTitle, artistUsername, albumReleaseDate);
		Optional<Song> songOpt = songDao.findById(songId);

		if (songOpt.isEmpty()) {
			throw new IllegalArgumentException("Song not found");
		}

		Optional<Genre> genreOpt = genreDao.findById(genreName);
		Genre genre;

		try {
			em.getTransaction().begin();

			if (genreOpt.isEmpty()) {
				genre = new Genre(genreName);
				genreDao.save(genre);
			} else {
				genre = genreOpt.get();
			}

			Song song = songOpt.get();
			song.addGenre(genre);

			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error adding genre to song: " + e.getMessage(), e);
		}
	}
}
