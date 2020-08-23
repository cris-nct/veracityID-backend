package exam.veracityid.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlaceDto implements Serializable {

    private String name;

    private byte[] imageData;

}
