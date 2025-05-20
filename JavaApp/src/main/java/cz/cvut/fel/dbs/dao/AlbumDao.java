package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Album;
import cz.cvut.fel.dbs.entity.Artist;

import java.util.Date;
import java.util.List;

public interface AlbumDao extends GenericDao<Album, Album.AlbumId> {
    List<Album> findByArtist(Artist artist);
    List<Album> findByGenre(String genreName);
    List<Album> findByReleaseYear(int year);
}
