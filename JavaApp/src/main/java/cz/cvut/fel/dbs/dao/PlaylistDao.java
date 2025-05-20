package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Playlist;
import cz.cvut.fel.dbs.entity.User;

import java.util.List;

public interface PlaylistDao extends GenericDao<Playlist, Playlist.PlaylistId> {
    List<Playlist> findByCreator(User creator);
    List<Playlist> findPublicPlaylists();
    List<Playlist> findByTitleContaining(String title);
}
