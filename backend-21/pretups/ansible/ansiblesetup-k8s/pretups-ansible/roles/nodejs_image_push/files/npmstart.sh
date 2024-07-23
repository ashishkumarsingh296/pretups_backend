#!/bin/bash

cd /usr/share/nodeserver-redis/node_pretups/
echo "starting"
npm install -g nodemon
npm start > npmstart.log
