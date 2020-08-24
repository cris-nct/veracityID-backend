<p><strong>Start it with this argument for VM: -Dgoogle.api.key=&lt;your key from Google Places API&gt;</strong>
</p>
<p><strong>API call: /localities</strong>
	<br />
	<br />E.g. response:
	<br />{
	<br />&nbsp; &nbsp; &nbsp; "localities": [
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"Cluj-Napoca",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"Craiova",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "Sibiu",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"Alexandria",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"Orsova",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "Bucharest"
	<br />&nbsp; &nbsp; &nbsp; &nbsp; ]
	<br />}</p>
<p><strong>API call: /nearplaces?locality=Cluj-Napoca?clearCache=false</strong>
	<br />- If clearCache is false then it will try to read the places from the database. If not found any place then will make a call to Goole Places API.
	<br />- If clearCache is true then it will not read the places from the database but will make a call to Google Places API.
	<br />- If you call this API with a place which doesn't exists in the list returned by "/localities" endpoint then that new locality is added in the database with the places around.
	<br />- The places are searched around the location within 5km.</p>
<p>E.g. response:
	<br />{
	<br />&nbsp; &nbsp; &nbsp;"placesList": [
	<br />&nbsp; &nbsp; &nbsp; {
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "name": "Hotel Confort",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "imageData": &lt;byte array of image&gt;
	<br />&nbsp; &nbsp; &nbsp; },
	<br />&nbsp; &nbsp; &nbsp; {
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"name": "Opera Plaza",
	<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;"imageData": &lt;byte array of image&gt;
	<br />&nbsp; &nbsp; &nbsp; &nbsp;}
	<br />&nbsp; &nbsp; &nbsp; ........</p>
