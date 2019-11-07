const ps = require('./pathSmoother');
var cors = require('cors');
let arguments = process.argv.splice(2);

const express = require('express');
const app = express();

app.use(express.json()) // for parsing application/json
app.use(express.urlencoded({ extended: true })) // for parsing application/x-www-form-urlencoded
app.use(cors());

const port = arguments[0] || 3000;


app.post('/median', (req, res, next) => {
    let data = req.body.data;
    let filterGranularity = req.body.filterGranularity;

    res.send(ps.median(data, filterGranularity));
});

app.post('/mean', (req, res, next) => {
    let data = req.body.data;
    let filterGranularity = req.body.filterGranularity;

    res.send(ps.mean(data, filterGranularity));
});

app.listen(port, () => console.log(`Example app listening on port ${port}!`))
