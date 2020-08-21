package exam.veracityid;

import exam.dto.CitiesListDto;
import exam.dto.NearPlacesDto;
import exam.dto.PlaceDto;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private MainService service;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }


    @RequestMapping(value = "/oauth2/authorize", method = RequestMethod.GET)
    public Boolean authorize(@AuthenticationPrincipal OAuth2User principal) {
        return true;
    }

    @RequestMapping(value = "/oauth2/token", method = RequestMethod.GET)
    public String token(@AuthenticationPrincipal OAuth2User principal) {
        return "token1212121";
    }

    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    public CitiesListDto getCities() {
        final CitiesListDto list = new CitiesListDto();
        list.setCitiesList(Arrays.asList("Cluj-Napoca", "Bucharest", "Iasi", "Brasov", "Sibiu"));
        return list;
    }

    @RequestMapping(value = "/nearplaces", method = RequestMethod.GET)
    public NearPlacesDto getNearPlaces(
            @RequestParam(value = "locality") String locality,
            @RequestParam(value = "heightImages") int heightImages
    ) {
        try {
            NearPlacesDto dto = new NearPlacesDto();
            final NearPlaces places = service.getNearPlaces(locality, heightImages);
            for (Place place : places.getPlacesList()) {
                PlaceDto placeDto = new PlaceDto();
                placeDto.setName(place.getName());
                placeDto.setImageData(place.getImage());
                dto.addPlace(placeDto);

                service.savePlaceToDB(place);
            }
            return dto;
        } catch (UnirestException e) {
            return null;
        }
    }

}
