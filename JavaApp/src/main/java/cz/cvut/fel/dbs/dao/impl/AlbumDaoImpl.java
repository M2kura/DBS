package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.AlbumDao;
import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlbumDaoImpl extends GenericDaoImpl<Album, Album.AlbumId> implements AlbumDao {

	public AlbumDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public List<Album> findByArtist(Artist artist) {
		TypedQuery<Album> query = entityManager.createQuery(
			"SELECT a FROM Album a WHERE a.primaryArtist = :artist", Album.class);
		query.setParameter("artist", artist);
		return query.getResultList();
	}

	@Override
	public List<Album> findByGenre(String genreName) {
		TypedQuery<Album> query = entityManager.createQuery(
			"SELECT a FROM Album a JOIN a.genres g WHERE g.name = :genreName", Album.class);
		query.setParameter("genreName", genreName);
		return query.getResultList();
	}

	@Override
	public List<Album> findByReleaseYear(int year) {
		Calendar startCal = Calendar.getInstance();
		startCal.set(year, Calendar.JANUARY, 1);
		Date startDate = startCal.getTime();

		Calendar endCal = Calendar.getInstance();
		endCal.set(year, Calendar.DECEMBER, 31);
		Date endDate = endCal.getTime();

		TypedQuery<Album> query = entityManager.createQuery(
			"SELECT a FROM Album a WHERE a.id.releaseDate BETWEEN :startDate AND :endDate", Album.class);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}
}
