package exam.veracityid.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LocalitiesDto implements Serializable {

    private List<String> localities;

}
