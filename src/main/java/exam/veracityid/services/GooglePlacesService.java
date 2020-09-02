package exam.veracityid.services;

import exam.veracityid.ReadDataFromType;
import exam.veracityid.Util;
import exam.veracityid.exceptions.PlaceNotFoundException;
import exam.veracityid.exceptions.UnirestErrorConsumer;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;

@Service
public class GooglePlacesService {
    //The height of images in pixels, downloaded from Google Places API
    private static final int HEIGHT_IMAGES = 100;

    //The area around the locality location used for seaching
    private static final int LOOKUP_RADIUS = 5000;

    @Value("${google.nextPageTokenTime}")
    private int nextPageTokenTime;

    @Value("${google.api.key}")
    private String googleApiKey;

    public NearPlaces fetchNearPlaces(String locality) throws PlaceNotFoundException {
        final String placeId = this.fetchGooglePlaceId(locality);
        final Point2D.Double location = this.fetchPlaceLocation(placeId);
        final NearPlaces places = this.fetchPlaces(location);
        places.setLocality(locality);
        places.setLatitude(location.getX());
        places.setLongitude(location.getY());
        places.setGooglePlaceId(placeId);

        String token = places.getNextPageToken();
        while (token != null) {
            Util.sleep(nextPageTokenTime);
            final NearPlaces nextPagePlaces = this.fetchNextPlaces(token);
            places.addAll(nextPagePlaces.getPlacesList());
            token = nextPagePlaces.getNextPageToken();
        }

        return places;
    }

    private NearPlaces fetchNextPlaces(String pageToken) {
        JSONObject mainJsonObj = Unirest.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryString("key", googleApiKey)
                .queryString("pagetoken", pageToken)
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting places on page " + pageToken))
                .getBody()
                .getObject();

        NearPlaces places = this.parsePlacesList(mainJsonObj);
        this.fetchAndSetImages(places);
        return places;
    }

    private NearPlaces fetchPlaces(Point2D.Double location) {
        JSONObject mainJsonObj = Unirest.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryString("key", googleApiKey)
                .queryString("location", String.format("%f,%f", location.x, location.y))
                .queryString("radius", LOOKUP_RADIUS)
                .queryString("components", "country:ro")
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting places for location " + location.toString()))
                .getBody()
                .getObject();
        NearPlaces places = this.parsePlacesList(mainJsonObj);
        places.setDataOrigin(ReadDataFromType.GOOGLE_API);
        this.fetchAndSetImages(places);
        return places;
    }

    private void fetchAndSetImages(NearPlaces places) {
        places.getPlacesList().forEach(place -> {
            String imageRef = place.getImageReference();
            if (Strings.isNotEmpty(imageRef)) {
                byte[] image = this.fetchImage(imageRef);
                place.setImage(image);
            }
        });
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

            this.parseGeometry(json, place);
            this.parseImageData(json, place);

            places.addPlace(place);
        }
        return places;
    }

    private void parseGeometry(JSONObject json, Place place) {
        JSONObject jsonGeometry = json.optJSONObject("geometry");
        if (jsonGeometry != null && !jsonGeometry.isEmpty()) {
            JSONObject location = jsonGeometry.getJSONObject("location");
            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");
            place.setLatitude(latitude);
            place.setLongitude(longitude);
        }
    }

    private void parseImageData(JSONObject json, Place place) {
        JSONArray jsonPhotos = json.optJSONArray("photos");
        if (jsonPhotos != null && !jsonPhotos.isEmpty()) {
            JSONObject photosJson = jsonPhotos.getJSONObject(0);
            String imageReference = photosJson.getString("photo_reference");
            int photoWidth = photosJson.getInt("width");
            int photoHeight = photosJson.getInt("height");

            place.setImageReference(imageReference);
            place.setPhotoWidth(photoWidth);
            place.setPhotoHeight(photoHeight);
        }
    }

    private Point2D.Double fetchPlaceLocation(String placeId) {
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

    private String fetchGooglePlaceId(String locality) throws PlaceNotFoundException {
        HttpResponse<JsonNode> response = Unirest.get("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
                .queryString("input", locality)
                .queryString("inputtype", "textquery")
                .queryString("key", googleApiKey)
                .asJson()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting place id for locality " + locality));

        final JSONArray candidates = response.getBody().getObject().getJSONArray("candidates");
        if (candidates.isEmpty()) {
            throw new PlaceNotFoundException("Place not found for locality: " + locality);
        }
        return candidates.getJSONObject(0).getString("place_id");
    }

    private byte[] fetchImage(String photoReference) {
        return Unirest.get("https://maps.googleapis.com/maps/api/place/photo")
                .queryString("maxheight", HEIGHT_IMAGES)
                .queryString("photoreference", photoReference)
                .queryString("key", googleApiKey)
                .asBytes()
                .ifFailure(new UnirestErrorConsumer<>("Error at getting image " + photoReference))
                .getBody();
    }

}
