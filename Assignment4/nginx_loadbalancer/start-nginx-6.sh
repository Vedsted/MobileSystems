#!/bin/bash

function cleanup {
    echo "#################################"
    echo "Killing all node processes"
    killall node
}

trap cleanup EXIT

echo "#################################"
echo "Installing server dependencies"
npm install --prefix ../server

echo "#################################"
echo "Removing existing Nginx Docker containers"
docker stop nginx
docker rm nginx

echo "#################################"
echo "Starting Nginx load balancer"
docker run --name nginx -v $PWD/nginx-confs/nginx6.conf:/etc/nginx/nginx.conf:ro -p 3000:3000 -d nginx

echo "#################################"
echo "Starting servers"
NODE_ENV=production npm start --prefix ../server 3001 &
NODE_ENV=production npm start --prefix ../server 3002 &
NODE_ENV=production npm start --prefix ../server 3003 &
NODE_ENV=production npm start --prefix ../server 3004 &
NODE_ENV=production npm start --prefix ../server 3005 &
NODE_ENV=production npm start --prefix ../server 3006 
