package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.PlaylistDao;
import cz.cvut.fel.dbs.entity.Playlist;
import cz.cvut.fel.dbs.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PlaylistDaoImpl extends GenericDaoImpl<Playlist, Playlist.PlaylistId> implements PlaylistDao {

    public PlaylistDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<Playlist> findByCreator(User creator) {
        TypedQuery<Playlist> query = entityManager.createQuery(
            "SELECT p FROM Playlist p WHERE p.creator = :creator", Playlist.class);
        query.setParameter("creator", creator);
        return query.getResultList();
    }

    @Override
    public List<Playlist> findPublicPlaylists() {
        TypedQuery<Playlist> query = entityManager.createQuery(
            "SELECT p FROM Playlist p WHERE p.isPublic = true", Playlist.class);
        return query.getResultList();
    }

    @Override
    public List<Playlist> findByTitleContaining(String title) {
        TypedQuery<Playlist> query = entityManager.createQuery(
            "SELECT p FROM Playlist p WHERE p.id.title LIKE :title", Playlist.class);
        query.setParameter("title", "%" + title + "%");
        return query.getResultList();
    }
}
