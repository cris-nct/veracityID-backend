package exam.veracityid;

import exam.veracityid.dto.CitiesListDto;
import exam.veracityid.dto.NearPlacesDto;
import exam.veracityid.dto.PlaceDto;
import exam.veracityid.exceptions.PlaceNotFoundException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class Controller {

    @Autowired
    private PlacesService service;

    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    public CitiesListDto getCities() {
        final CitiesListDto dto = new CitiesListDto();
        final List<String> cities = service.getAllCities();
        dto.setCitiesList(cities);
        return dto;
    }

    @RequestMapping(value = "/nearplaces", method = RequestMethod.GET)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public NearPlacesDto getNearPlaces(
            @RequestParam(value = "locality") String locality,
            @RequestParam(value = "clearCache", defaultValue = "false") Boolean clearCache
    ) {
        NearPlacesDto dto = new NearPlacesDto();
        try {
            if (clearCache) {
                this.service.removePlaces(locality);
            }
            final NearPlaces places = service.getNearPlaces(locality);
            places.getPlacesList().stream().map(this::toDto).forEach(dto::addPlace);
            if (places.getDataOrigin() == ReadDataFrom.GOOGLE_API) {
                this.service.savePlacesToDB(places);
            }
        } catch (PlaceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found", e);
        }
        return dto;
    }

    private PlaceDto toDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setName(place.getName());
        placeDto.setImageData(place.getImage());
        return placeDto;
    }

}
