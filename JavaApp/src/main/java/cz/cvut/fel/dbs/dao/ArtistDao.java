package cz.cvut.fel.dbs.dao;

import cz.cvut.fel.dbs.entity.Artist;
import java.util.List;
import java.util.Optional;

public interface ArtistDao extends GenericDao<Artist, String> {
    Optional<Artist> findByStageName(String stageName);
    List<Artist> findByGenre(String genreName);
}
