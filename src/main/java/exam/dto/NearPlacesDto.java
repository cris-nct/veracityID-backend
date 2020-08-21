package exam.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NearPlacesDto {

    private List<PlaceDto> placesList = new ArrayList<>();

    public void addPlace(PlaceDto place){
        this.placesList.add(place);
    }
}
