package exam.veracityid.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CitiesListDto implements Serializable {

    private List<String> citiesList;

}
