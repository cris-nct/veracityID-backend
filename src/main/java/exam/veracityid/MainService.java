package exam.veracityid;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;

@Service
public class MainService {

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${google.nextPageTokenTime}")
    private int nextPageTokenTime;

    @Value("${config.radius}")
    private int radius;

    @Autowired
    private PlacesRepository repository;

    public NearPlaces getNearPlaces(String city, int heightImages) throws UnirestException {
        final String placeId = getPlaceId(city);
        final Point2D.Double location = getPlaceLocation(placeId);

        final NearPlaces places = getPlaces(location, heightImages);
        String token = places.getNextPageToken();
        while (token != null) {
            Util.sleep(nextPageTokenTime);
            final NearPlaces nextPagePlaces = getNextPlaces(token, heightImages);
            places.addAll(nextPagePlaces.getPlacesList());
            token = nextPagePlaces.getNextPageToken();
        }
        return places;
    }

    private NearPlaces getPlaces(Point2D.Double location, int heightImages) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryString("key", googleApiKey)
                .queryString("location", String.format("%f,%f", location.x, location.y))
                .queryString("radius", radius)
                .queryString("components", "country:ro")
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting places for location " + location.toString()));

        final JSONObject mainJsonObj = response.getBody().getObject();
        return parsePlacesList(mainJsonObj);
    }

    private NearPlaces getNextPlaces(String pageToken, int heightImages) {
        HttpResponse<JsonNode> response = Unirest.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryString("key", googleApiKey)
                .queryString("pagetoken", pageToken)
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting places on page " + pageToken));

        final JSONObject mainJsonObj = response.getBody().getObject();
        NearPlaces places = parsePlacesList(mainJsonObj);
        if (heightImages > 0) {
            for (Place place : places.getPlacesList()) {
                final byte[] photoData = downloadPhoto(place, heightImages);
                place.setImage(photoData);
            }
        }
        return places;
    }

    private NearPlaces parsePlacesList(JSONObject jsonObj) {
        NearPlaces places = new NearPlaces();
        places.setNextPageToken(jsonObj.optString("next_page_token", null));
        final JSONArray results = jsonObj.getJSONArray("results");
        for (Object obj : results) {
            JSONObject json = (JSONObject) obj;
            final Place place = new Place();
            String placeName = json.getString("name");
            place.setName(placeName);

            JSONObject jsonGeometry = json.optJSONObject("geometry");
            if (jsonGeometry != null && !jsonGeometry.isEmpty()) {
                JSONObject location = jsonGeometry.getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                place.setLatitude(latitude);
                place.setLongitude(longitude);
            }

            JSONArray jsonPhotos = json.optJSONArray("photos");
            if (jsonPhotos != null && !jsonPhotos.isEmpty()) {
                JSONObject photosJson = jsonPhotos.getJSONObject(0);
                String photoReference = photosJson.getString("photo_reference");
                int photoWidth = photosJson.getInt("width");
                int photoHeight = photosJson.getInt("height");

                place.setPhotoReference(photoReference);
                place.setPhotoWidth(photoWidth);
                place.setPhotoHeight(photoHeight);
            }
            places.addPlace(place);
        }
        return places;
    }

    private Point2D.Double getPlaceLocation(String placeId) {
        HttpResponse<JsonNode> response = Unirest.get("https://maps.googleapis.com/maps/api/place/details/json")
                .queryString("place_id", placeId)
                .queryString("fields", "geometry")
                .queryString("key", googleApiKey)
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting location for place " + placeId));

        final JSONObject result = response.getBody().getObject().getJSONObject("result");
        final JSONObject geometry = result.getJSONObject("geometry");
        final JSONObject location = geometry.getJSONObject("location");
        final double latitude = location.getDouble("lat");
        final double longitude = location.getDouble("lng");
        return new Point2D.Double(latitude, longitude);
    }

    private String getPlaceId(String locality) {
        HttpResponse<JsonNode> response = Unirest.get("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
                .queryString("input", locality)
                .queryString("inputtype", "textquery")
                .queryString("key", googleApiKey)
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting place id for locality " + locality));

        final JSONArray candidates = response.getBody().getObject().getJSONArray("candidates");
        return candidates.getJSONObject(0).getString("place_id");
    }

    private byte[] downloadPhoto(Place place, int height) {
        return Unirest.get("https://maps.googleapis.com/maps/api/place/photo")
                .queryString("maxheight", height)
                .queryString("photoreference", place.getPhotoReference())
                .queryString("key", googleApiKey)
                .asBytes()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting image " + place.getPhotoReference()))
                .getBody();
    }

    public void savePlaceToDB(Place place) {
        PlaceEntity entity = new PlaceEntity();
        entity.setName(place.getName());
        entity.setLatitude(place.getLatitude());
        entity.setLongitude(place.getLongitude());
        entity.setImage(place.getImage());
        repository.save(entity);
    }
}