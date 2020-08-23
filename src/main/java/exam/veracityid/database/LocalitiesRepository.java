package exam.veracityid.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalitiesRepository extends CrudRepository<LocalityEntity, Long> {

    LocalityEntity findByName(String name);

}
