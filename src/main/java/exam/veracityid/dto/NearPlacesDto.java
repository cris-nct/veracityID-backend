package exam.veracityid.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class NearPlacesDto implements Serializable {

    private List<PlaceDto> placesList = new ArrayList<>();

    public void addPlace(PlaceDto place){
        this.placesList.add(place);
    }
}
