package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Song;

import java.util.List;

public interface SongDao extends GenericDao<Song, Song.SongId> {
    List<Song> findByAlbum(Album album);
    List<Song> findByGenre(String genreName);
    List<Song> findByTitleContaining(String title);
}
