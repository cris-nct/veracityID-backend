package exam.veracityid;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
class NearPlaces implements Serializable {

    private final List<Place> placesList = new ArrayList<>();

    private String locality;

    private long localityId;

    private double latitude;

    private double longitude;

    private String googlePlaceId;

    private String nextPageToken;

    private ReadDataFromType dataOrigin;

    void addPlace(Place place) {
        this.placesList.add(place);
    }

    void addAll(List<Place> placesList) {
        this.placesList.addAll(placesList);
    }

}
