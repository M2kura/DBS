package cz.cvut.fel.dbs.dao.impl;

import cz.cvut.fel.dbs.dao.IncludesDao;
import cz.cvut.fel.dbs.entity.Includes;
import cz.cvut.fel.dbs.entity.Playlist;
import cz.cvut.fel.dbs.entity.Song;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class IncludesDaoImpl extends GenericDaoImpl<Includes, Includes.IncludesId> implements IncludesDao {

    public IncludesDaoImpl(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    public List<Song> findSongsInPlaylist(Playlist playlist) {
        TypedQuery<Song> query = entityManager.createQuery(
            "SELECT i.song FROM Includes i WHERE i.playlist = :playlist", Song.class);
        query.setParameter("playlist", playlist);
        return query.getResultList();
    }

    @Override
    public List<Playlist> findPlaylistsContainingSong(Song song) {
        TypedQuery<Playlist> query = entityManager.createQuery(
            "SELECT i.playlist FROM Includes i WHERE i.song = :song", Playlist.class);
        query.setParameter("song", song);
        return query.getResultList();
    }
}
