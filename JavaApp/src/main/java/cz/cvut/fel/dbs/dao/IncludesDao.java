package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Includes;
import cz.cvut.fel.dbs.entity.Playlist;
import cz.cvut.fel.dbs.entity.Song;

import java.util.List;

public interface IncludesDao extends GenericDao<Includes, Includes.IncludesId> {
    List<Song> findSongsInPlaylist(Playlist playlist);
    List<Playlist> findPlaylistsContainingSong(Song song);
}
