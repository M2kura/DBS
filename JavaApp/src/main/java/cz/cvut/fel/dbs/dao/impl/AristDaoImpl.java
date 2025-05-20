package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.ArtistDao;
import cz.cvut.fel.dbs.entity.Artist;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ArtistDaoImpl extends GenericDaoImpl<Artist, String> implements ArtistDao {

	public ArtistDaoImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public Optional<Artist> findByStageName(String stageName) {
		TypedQuery<Artist> query = entityManager.createQuery(
			"SELECT a FROM Artist a WHERE a.stageName = :stageName", Artist.class);
		query.setParameter("stageName", stageName);

		List<Artist> resultList = query.getResultList();
		return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
	}

	@Override
	public List<Artist> findByGenre(String genreName) {
		TypedQuery<Artist> query = entityManager.createQuery(
			"SELECT DISTINCT a FROM Artist a JOIN a.albums al JOIN al.genres g WHERE g.name = :genreName", Artist.class);
		query.setParameter("genreName", genreName);
		return query.getResultList();
	}
}
