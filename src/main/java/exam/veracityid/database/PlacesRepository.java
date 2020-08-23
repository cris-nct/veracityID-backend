package exam.veracityid.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacesRepository extends CrudRepository<PlaceEntity, Long> {

    List<PlaceEntity> findByLocalityID(long localityID);


}
