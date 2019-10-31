const pathSmoother = require('./pathSmoother');
const express = require('express')
const app = express()
const port = 3000


app.get('/median', (req, res) => {
    console.log(req);
    
    res.send('Hello World!')
});

app.get('/mean', (req, res) => {
    res.send('Hello World!')
});

app.listen(port, () => console.log(`Example app listening on port ${port}!`))