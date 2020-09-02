package exam.veracityid.services;

import exam.veracityid.ReadDataFromType;
import exam.veracityid.database.LocalitiesRepository;
import exam.veracityid.database.LocalityEntity;
import exam.veracityid.database.PlaceEntity;
import exam.veracityid.database.PlacesRepository;
import exam.veracityid.exceptions.PlaceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlacesService {

    @Autowired
    private PlacesRepository placesRepo;

    @Autowired
    private LocalitiesRepository localitiesRepo;

    @Autowired
    private GooglePlacesService googleService;

    @Transactional
    public NearPlaces getNearPlaces(String locality) throws PlaceNotFoundException {
        NearPlaces places;
        final LocalityEntity localityEntity = this.localitiesRepo.findByName(locality);
        if (localityEntity == null || localityEntity.isNotUpdatedWithDataFromGoogle()) {
            places = this.googleService.fetchNearPlaces(locality);
        } else {
            places = this.getNearPlacesFromDB(locality);
            if (places.getPlacesList().isEmpty()) {
                places = this.googleService.fetchNearPlaces(locality);
            }
        }
        return places;
    }

    private NearPlaces getNearPlacesFromDB(String locality) {
        final LocalityEntity localityEntity = this.localitiesRepo.findByName(locality);
        final List<PlaceEntity> placeEntities = this.placesRepo.findByLocalityID(localityEntity.getId());
        final List<Place> allPlacesFound = placeEntities.stream().map(this::toDto).collect(Collectors.toList());

        final NearPlaces places = new NearPlaces();
        places.setLocality(locality);
        places.setGooglePlaceId(localityEntity.getGooglePlaceId());
        places.setLatitude(localityEntity.getLatitude());
        places.setLongitude(localityEntity.getLongitude());
        places.addAll(allPlacesFound);
        places.setDataOrigin(ReadDataFromType.DATABASE);

        return places;
    }

    @Transactional
    public void removePlaces(String locality) {
        final LocalityEntity localityEntity = this.localitiesRepo.findByName(locality);
        if (localityEntity != null) {
            final List<PlaceEntity> places = this.placesRepo.findByLocalityID(localityEntity.getId());
            this.placesRepo.deleteAll(places);
        }
    }

    @Transactional
    public void savePlacesToDB(NearPlaces places) {
        LocalityEntity locality = this.localitiesRepo.findByName(places.getLocality());
        if (locality == null || locality.isNotUpdatedWithDataFromGoogle()) {
            locality = this.saveLocalityToDB(places);
        }
        final long localityId = locality.getId();
        places.getPlacesList().forEach(place -> {
            PlacesService.this.savePlaceToDB(place, localityId);
        });
    }

    private LocalityEntity saveLocalityToDB(NearPlaces places) {
        LocalityEntity entity = this.localitiesRepo.findByName(places.getLocality());
        if (entity == null) {
            entity = new LocalityEntity();
        }
        entity.setName(places.getLocality());
        entity.setLatitude(places.getLatitude());
        entity.setLongitude(places.getLongitude());
        entity.setGooglePlaceId(places.getGooglePlaceId());
        return this.localitiesRepo.save(entity);
    }

    private void savePlaceToDB(Place place, long localityId) {
        PlaceEntity entity = new PlaceEntity();
        entity.setName(place.getName());
        entity.setLocalityID(localityId);
        entity.setLatitude(place.getLatitude());
        entity.setLongitude(place.getLongitude());
        entity.setImage(place.getImage());
        this.placesRepo.save(entity);
    }

    private Place toDto(PlaceEntity entity) {
        Place place = new Place();
        place.setName(entity.getName());
        place.setImage(entity.getImage());
        place.setLatitude(entity.getLatitude());
        place.setLongitude(entity.getLongitude());
        return place;
    }

    @Transactional(readOnly = true)
    public List<String> getAllLocalities() {
        final List<String> localities = new ArrayList<>();
        for (LocalityEntity locality : this.localitiesRepo.findAll()) {
            localities.add(locality.getName());
        }
        return localities;
    }

}