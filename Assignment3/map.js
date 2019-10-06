
var mymap, routeGPS, routeMobile, lineReader, file, unit, filter, filterGranularity;

function createMap(){
	mymap == null ? null : mymap.remove();

	mymap = L.map('mapid').setView([55.368877, 10.428296], 13);
	
	L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
		attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
		maxZoom: 18,
		id: 'mapbox.streets',
		accessToken: 'pk.eyJ1IjoidmVkc3RlZCIsImEiOiJjazEwaGtiNnEwMjl2M2NzNTF5M3hvdmdlIn0.kmwNOymubSmWYo_g39sKpQ'
	}).addTo(mymap);
}

function drawRoute() {  

	routeGPS = [];
	routeMobile = [];

	lineReader = require('readline').createInterface({
		input: require('fs').createReadStream(file)
	});

	lineReader.on('line', prepareLine);	
	lineReader.on('close', updateMap);
}

function prepareLine(line){
	line = line.split(',');
	line = line.map(l => parseFloat(l));
	routeGPS.push([line[1],line[2]]);
	routeMobile.push([line[3],line[4]]);
}

function updateMap() {
	
	var data;

	if (unit === "GPS") {
		routeGPS.shift();
		data = routeGPS;
	} else {
		routeMobile.shift();
		data = routeMobile;
	}

	if(filter === "mean") {
		data = meanFilter(data,  filterGranularity);
	} else if (filter === "median") {
		data = medianFilter(data,  filterGranularity);
	} 

	var poly = L.polyline(data, {color: 'red'}).addTo(mymap);

	// zoom the map to the polyline
	mymap.fitBounds(poly.getBounds());
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


function reload(){
	createMap();

	file = document.getElementById("fileSelector");
	file = file.options[file.selectedIndex].value
	
	unit = document.getElementById("unitSelector");
	unit = unit.options[unit.selectedIndex].value;
	
	filter = document.getElementById("filterSelector");
	filter = filter.options[filter.selectedIndex].value;
	
	filterGranularity = parseInt(document.getElementById("intervalInput").value);
	
	drawRoute();
}

reload();