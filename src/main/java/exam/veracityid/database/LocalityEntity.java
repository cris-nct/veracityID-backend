package exam.veracityid.database;

import lombok.Data;
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

}
