package cz.cvut.fel.dbs.service.impl;

import cz.cvut.fel.dbs.dao.AlbumDao;
import cz.cvut.fel.dbs.dao.ArtistDao;
import cz.cvut.fel.dbs.dao.GenreDao;
import cz.cvut.fel.dbs.dao.UserDao;
import cz.cvut.fel.dbs.dao.impl.AlbumDaoImpl;
import cz.cvut.fel.dbs.dao.impl.ArtistDaoImpl;
import cz.cvut.fel.dbs.dao.impl.GenericDaoImpl;
import cz.cvut.fel.dbs.dao.impl.UserDaoImpl;
import cz.cvut.fel.dbs.entity.*;
import cz.cvut.fel.dbs.service.AlbumService;
import cz.cvut.fel.dbs.service.DatabaseManager;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class AlbumServiceImpl implements AlbumService {
	private final EntityManager em;
	private final AlbumDao albumDao;
	private final ArtistDao artistDao;
	private final UserDao userDao;
	private final GenericDaoImpl<Genre, String> genreDao;
	private final GenericDaoImpl<SavesAlbum, SavesAlbum.SavesAlbumId> savesAlbumDao;

	public AlbumServiceImpl() {
		this.em = DatabaseManager.getEntityManager();
		this.albumDao = new AlbumDaoImpl(em);
		this.artistDao = new ArtistDaoImpl(em);
		this.userDao = new UserDaoImpl(em);
		this.genreDao = new GenericDaoImpl<Genre, String>(em) {};
		this.savesAlbumDao = new GenericDaoImpl<SavesAlbum, SavesAlbum.SavesAlbumId>(em) {};
	}

	@Override
	public Album createAlbum(String artistUsername, String albumTitle, Date releaseDate) {
		Optional<Artist> artistOpt = artistDao.findById(artistUsername);
		if (artistOpt.isEmpty()) {
			throw new IllegalArgumentException("Artist not found: " + artistUsername);
		}

		Artist artist = artistOpt.get();
		Album.AlbumId albumId = new Album.AlbumId(albumTitle, artistUsername, releaseDate);

		if (albumDao.findById(albumId).isPresent()) {
			throw new IllegalArgumentException("Album already exists: " + albumTitle);
		}

		try {
			em.getTransaction().begin();

			Album album = new Album(albumTitle, artist, releaseDate, 0);
			albumDao.save(album);

			em.getTransaction().commit();
			return album;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error creating album: " + e.getMessage(), e);
		}
	}

	@Override
	public boolean saveAlbum(String username, String albumTitle, String artistUsername, Date releaseDate) {
		Optional<User> userOpt = userDao.findById(username);
		if (userOpt.isEmpty()) {
			return false;
		}

		Album.AlbumId albumId = new Album.AlbumId(albumTitle, artistUsername, releaseDate);
		Optional<Album> albumOpt = albumDao.findById(albumId);
		if (albumOpt.isEmpty()) {
			return false;
		}

		User user = userOpt.get();
		Album album = albumOpt.get();
		SavesAlbum.SavesAlbumId savesAlbumId = new SavesAlbum.SavesAlbumId(
			username, albumTitle, artistUsername, releaseDate);

		// Check if already saved
		if (savesAlbumDao.findById(savesAlbumId).isPresent()) {
			return true;
		}

		try {
			em.getTransaction().begin();

			SavesAlbum savesAlbum = new SavesAlbum(user, album);
			savesAlbumDao.save(savesAlbum);

			em.getTransaction().commit();
			return true;
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error saving album: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Album> findAlbum(String title, String artistUsername, Date releaseDate) {
		Album.AlbumId albumId = new Album.AlbumId(title, artistUsername, releaseDate);
		return albumDao.findById(albumId);
	}

	@Override
	public List<Album> findAlbumsByArtist(String artistUsername) {
		Optional<Artist> artistOpt = artistDao.findById(artistUsername);
		return artistOpt.map(albumDao::findByArtist).orElse(List.of());
	}

	@Override
	public List<Album> findAlbumsByGenre(String genreName) {
		return albumDao.findByGenre(genreName);
	}

	@Override
	public void addGenreToAlbum(String albumTitle, String artistUsername, Date releaseDate, String genreName) {
		Album.AlbumId albumId = new Album.AlbumId(albumTitle, artistUsername, releaseDate);
		Optional<Album> albumOpt = albumDao.findById(albumId);

		if (albumOpt.isEmpty()) {
			throw new IllegalArgumentException("Album not found");
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

			Album album = albumOpt.get();
			album.addGenre(genre);

			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw new RuntimeException("Error adding genre to album: " + e.getMessage(), e);
		}
	}
}
