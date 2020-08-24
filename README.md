# veracityid exam
 
 API call: /localities
 E.g. response:
 {
    "citiesList": [
        "Cluj-Napoca",
        "Craiova",
        "Sibiu",
        "Alexandria",
        "Orsova",
        "Bucharest"
    ]
}
 
API call: /nearplaces?locality=Cluj-Napoca?clearCache=false
If clearCache is false then it will try to read the places from the database. If not found any place then will make a call to Goole Places API.
If clearCache is true then it will not read the places from the database but will make a call to Google Places API.
E.g. response:
{
    "placesList": [
        {
            "name": "Hotel Confort",
            "imageData": <byte array of image>
        },
        {
            "name": "Opera Plaza",
            "imageData": <byte array of image>
        }
        ........
 }
 
