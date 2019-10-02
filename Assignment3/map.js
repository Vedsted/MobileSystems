
var mymap;
var routeGPS = [];
var routeMobile = [];


function createMap(){

	mymap = L.map('mapid').setView([55.368877, 10.428296], 13);
	
	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		maxZoom: 18,
		id: 'mapbox.streets',
		accessToken: 'pk.eyJ1IjoidmVkc3RlZCIsImEiOiJjazEwaGtiNnEwMjl2M2NzNTF5M3hvdmdlIn0.kmwNOymubSmWYo_g39sKpQ'
	}).addTo(mymap);
}

function drawRoute() {  
	

	var lineReader = require('readline').createInterface({
		input: require('fs').createReadStream('Walking.csv')
	});

	lineReader.on('line', function (line) {
		line = line.split(',');
		line = line.map(l => parseFloat(l));
		  routeGPS.push([line[1],line[2]]);
		  routeMobile.push([line[3],line[4]]);
	});	


	lineReader.on('close', () => {

		//routeGPS.shift();
		routeMobile.shift();

		//routeMobile = meanFilter(routeMobile, 5);
		routeMobile = medianFilter(routeMobile, 5);

		//var polylineGPS = L.polyline(routeGPS, {color: 'red'}).addTo(mymap);
		var polylineMobile = L.polyline(routeMobile, {color: 'Blue'}).addTo(mymap);

		// zoom the map to the polyline
		mymap.fitBounds(polylineMobile.getBounds());
	});
}

function meanFilter(positions, n){
	var ret = [];
	for (var i=0; i < positions.length; i+= n) {
		arr = positions.slice(i,i+n).filter(e => e != null);
		ret.push(arr.reduce((l1,l2) => [l1[0] + l2[0],l1[1] + l2[1]]).map(e => e/arr.length));
	}
	return ret;
}


function medianFilter(positions, n){
	var ret = [];
	for (var i=0; i < positions.length; i+= n) {
		arr = positions.slice(i,i+n).filter(e => e != null);

		var lats = arr.map(e => e[0]).sort();
		var longs = arr.map(e => e[1]).sort();

		var ll = Math.floor(lats.length/2)
		ret.push([lats[ll], longs[ll]]);
	}
	
	return ret;
}




createMap();
drawRoute();