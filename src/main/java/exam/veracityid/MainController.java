package exam.veracityid;

import exam.dto.CitiesListDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@RestController
public class MainController {

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @RequestMapping(value = "/cities", method = RequestMethod.GET)
    public CitiesListDto getCities() {
        final CitiesListDto list = new CitiesListDto();
        list.setCitiesList(Arrays.asList("Cluj-Napoca", "Bucharest", "Iasi", "Brasov", "Sibiu"));
        return list;
    }


}
