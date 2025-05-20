package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.SongDao;
import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class SongDaoImpl extends GenericDaoImpl<Song, Song.SongId> implements SongDao {

    public SongDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<Song> findByAlbum(Album album) {
        TypedQuery<Song> query = entityManager.createQuery(
            "SELECT s FROM Song s WHERE s.album = :album ORDER BY s.trackNumber", Song.class);
        query.setParameter("album", album);
        return query.getResultList();
    }

    @Override
    public List<Song> findByGenre(String genreName) {
        TypedQuery<Song> query = entityManager.createQuery(
            "SELECT s FROM Song s JOIN s.genres g WHERE g.name = :genreName", Song.class);
        query.setParameter("genreName", genreName);
        return query.getResultList();
    }

    @Override
    public List<Song> findByTitleContaining(String title) {
        TypedQuery<Song> query = entityManager.createQuery(
            "SELECT s FROM Song s WHERE s.id.title LIKE :title", Song.class);
        query.setParameter("title", "%" + title + "%");
        return query.getResultList();
    }
}
