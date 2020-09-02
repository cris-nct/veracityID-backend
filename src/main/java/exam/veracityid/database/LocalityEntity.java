package exam.veracityid.database;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name = "localities")
@Data
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LocalityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LATITUDE")
    private double latitude;

    @Column(name = "LONGITUDE")
    private double longitude;

    @Column(name = "GOOGLE_PLACE_ID")
    private String googlePlaceId;

    /**
     * @return Returns true if this entity was not updated with data from Google Places API
     */
    public boolean isNotUpdatedWithDataFromGoogle() {
        return Strings.isEmpty(googlePlaceId) && latitude == 0 && longitude == 0;
    }
}
