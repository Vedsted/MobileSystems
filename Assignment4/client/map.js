const url = "http://localhost:3000"
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

async function updateMap() {

	let data;

	if (unit === "GPS") {
		routeGPS.shift();
		data = routeGPS;
	} else {
		routeMobile.shift();
		data = routeMobile;
	}

	if(filter === "mean") {
		data = await meanFilter(data, filterGranularity);
	} else if (filter === "median") {
		data = await medianFilter(data, filterGranularity);
	}

	let poly = L.polyline(data, {color: 'red'}).addTo(mymap);
	// zoom the map to the polyline
	mymap.fitBounds(poly.getBounds());
	
}

function createRequestBody(data, filterGranularity){
	return {
		data: data,
		filterGranularity: filterGranularity
	}
}

function meanFilter(data, filterGranularity) {
	return sendFilterRequest("mean", createRequestBody(data, filterGranularity))
}

function medianFilter(data, filterGranularity) {
	return sendFilterRequest("median", createRequestBody(data, filterGranularity))
}

async function sendFilterRequest(filter, body){
	let response = await fetch(`${url}/${filter}`, {
		method: 'POST',
		body: JSON.stringify(body), // data can be `string` or {object}!
		headers: {
		  'Content-Type': 'application/json'
		}
	  });
	return await response.json();
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