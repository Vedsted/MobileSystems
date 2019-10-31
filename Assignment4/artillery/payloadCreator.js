'use strict';

module.exports = {
    createBody
};

function createBody(userContext, events, done){
    let routeMobile = [];

    let lineReader = require('readline').createInterface({
        input: require('fs').createReadStream("Walking.csv")
    });

    lineReader.on('line', (line) => {
        line = line.split(',');
        line = line.map(l => parseFloat(l));
        routeMobile.push([line[3],line[4]]);
    });


    lineReader.on('close', () => {
        routeMobile.shift();

		//console.log(reqBody)
        userContext.vars.data = routeMobile;
        userContext.vars.filterGranularity = 40;
        done();
    });
}
