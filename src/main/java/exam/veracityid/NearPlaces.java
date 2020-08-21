package exam.veracityid;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
class NearPlaces {

    private final List<Place> placesList = new ArrayList<>();

    private String nextPageToken;

    void addPlace(Place place) {
        this.placesList.add(place);
    }

    void addAll(List<Place> placesList) {
        this.placesList.addAll(placesList);
    }

}
