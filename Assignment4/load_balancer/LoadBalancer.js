/* eslint-env es6, node */

/**
 * Code originally by Ross Johnson
 * Introduction to Load Balancing using Node.js - Part 1
 * https://mazira.com/blog/introduction-load-balancing-nodejs
 */
let arguments = process.argv.splice(2);
let http      = require('http');
let httpProxy = require('http-proxy');

//
// Addresses to use in the round robin proxy
//
let addresses = [
    { target: 'http://localhost:3001' },
    { target: 'http://localhost:3002' },
    { target: 'http://localhost:3003' }
];

let i = 0;
let proxy = httpProxy.createProxyServer({});

http.createServer(function (req, res) {
    proxy.web(req, res, addresses[i]);
    i = (i + 1) % addresses.length;
}).listen(arguments[0] || 3000);
