package exam.veracityid.services;

import lombok.Data;

import java.io.Serializable;

@Data
public class Place implements Serializable {

    private String name;

    private long localityId;

    private double latitude;

    private double longitude;

    private int photoWidth;

    private int photoHeight;

    private String imageReference;

    private byte[] image;
}
