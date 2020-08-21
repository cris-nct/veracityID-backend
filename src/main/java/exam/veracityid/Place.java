package exam.veracityid;

import lombok.Data;

@Data
class Place {

    private String name;

    private double latitude;

    private double longitude;

    private int photoWidth;

    private int photoHeight;

    private String photoReference;

    private byte[] image;
}
