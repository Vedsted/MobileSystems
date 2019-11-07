const fs = require('fs');

let files = ["Walking"];


files.forEach(file => {
    let routeMobile = [];

    let lineReader = require('readline').createInterface({
        input: require('fs').createReadStream("./" + file + ".csv")
    });

    lineReader.on('line', (line) => {
        line = line.split(',');
        line = line.map(l => parseFloat(l));
        routeMobile.push([line[3], line[4]]);
    });


    lineReader.on('close', () => {
        routeMobile.shift();
        createFile(file, JSON.stringify(routeMobile))

    });

    function createFile(filename, content) {
        fs.writeFile("./" + filename, content, function (err) {

            if (err) {
                return console.log(err);
            }

            console.log("The file " + filename + ", was saved!");
        });
    }
});