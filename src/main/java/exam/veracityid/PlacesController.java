package exam.veracityid;

import exam.veracityid.dto.LocalitiesDto;
import exam.veracityid.dto.NearPlacesDto;
import exam.veracityid.dto.PlaceDto;
import exam.veracityid.exceptions.PlaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@RestController
public class PlacesController {

    @Autowired
    private PlacesService service;

    @RequestMapping(value = "/localities", method = RequestMethod.GET)
    public ResponseEntity<LocalitiesDto> getLocalities(HttpServletResponse response) {
        final LocalitiesDto dto = new LocalitiesDto();
        dto.setLocalities(service.getAllLocalities());

        response.addHeader("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/nearplaces", method = RequestMethod.GET)
    @Cacheable(value = "PlacesCache", key = "#places")
    public ResponseEntity<NearPlacesDto> getNearPlaces(
            @RequestParam(value = "locality") String locality,
            @RequestParam(value = "clearCache", defaultValue = "false") Boolean clearCache,
            HttpServletResponse response
    ) {
        NearPlacesDto dto = new NearPlacesDto();
        try {
            if (clearCache) {
                this.service.removePlaces(locality);
            }
            final NearPlaces places = service.getNearPlaces(locality);
            places.getPlacesList().stream().map(this::toDto).forEach(dto::addPlace);
            if (places.getDataOrigin() == ReadDataFromType.GOOGLE_API) {
                this.service.savePlacesToDB(places);
            }
        } catch (PlaceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found", e);
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private PlaceDto toDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setName(place.getName());
        placeDto.setImageData(place.getImage());
        return placeDto;
    }

}
