const express = require('express');
const redis = require('redis');

const PORT = process.env.PORT || 5000;
const REDIS_PORT = process.env.PORT || REDIS_NODEPORT;

const REDIS_HOST = 'REDIS_IP' ;
// Code to start node app 

const cors = require('cors');
const app = express();
app.use(cors());

app.listen(5000, () => {
    console.log(`App listening on port ${PORT}`);
  });

//  const client = redis.createClient(REDIS_PORT,REDIS_HOST,pretups123);// Redis Client created 
//const client = redis.createClient({
  //  host: REDIS_HOST,
   // port:REDIS_PORT ,
    //password: 'pretups123'
//});

const client = redis.createClient({
    host: REDIS_HOST,
    port:REDIS_PORT 
});



  //Incase any error pops up, log it
  client.on("error", function(err) {
    console.log("Error " + err);
  }) 
  // api for fetching data from redis Cache , (cache) is the method call to function with api logic 
app.get('/getPrefrenceCache', (cache));
app.get('/getLookupCache', (lookupCache));

function cache(req, res, next) {
   // get call for the key prefrenceMapCache
    client.hgetall('prefrenceMapCacheClient', (err, data) => {
      if (err) throw err;
  
      if (data !== null) {
        console.log(`Recived data from redis ${ data}`);
        res.json(data);
      } else {
        next();
      }
    });
  }
  
  
function lookupCache(req, res, next) {
   // get call for the key getLookupCache
    client.hgetall('lookupMapCacheClient', (err, data) => {
      if (err) throw err;
  
      if (data !== null) {
        console.log(`Recived data from redis ${ data}`);
        res.json(data);
      } else {
        next();
      }
    });
}
  