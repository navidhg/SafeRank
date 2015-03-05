// Imports
var config = require("./config");
var pg = require("pg");
var express = require("express");
var bodyParser = require("body-parser")

// Set up Express
var app = express();
app.use(bodyParser.json());

// Set up postgres details
var conString = config.getPostgresConnectionString();
var client = new pg.Client(conString);

// POST routes

// Safety data
app.post("/upload", function(req, res) {
  // Write out POST body
  console.log('User ID: ' + req.body.userid);
  console.log('Sample time: ' + req.body.sampletime);
  console.log('Rating: ' + req.body.rating);
  console.log('Longitude: ' + req.body.longitude);
  console.log('Latitude: ' + req.body.latitude);
  console.log('Brightness: ' + req.body.brightness);
  res.end("success");

  // Connect to postgres and execute query
  pg.connect(conString, function(err, client, done) {
    if (err) {
      return console.error("Could not connect to postgres", err);
    }
    console.log("Querying...");
    client.query("INSERT INTO safetydetail VALUES ( ($1), ($2), ($3), ($4), ($5), ($6) )", [req.body.userid, req.body.sampletime, req.body.rating, req.body.latitude, req.body.longitude, req.body.brightness], function(err, result) {
      if (err) {
        return console.error("Error running query", err);
      }
      console.log("Data input was successful")
    });
  });
});

// Storing a new user given an email address
app.post("/register", function(req, res) {
  // Write out POST body
  console.log("Email: " + req.body.email);
  res.end("success");

  // Connect to postgres and execute query
  pg.connect(conString, function(err, client, done) {
    if (err) {
      return console.error("Could not connect to postgres", err);
    }
    console.log("Querying...");
    client.query("INSERT INTO userdetail (email) VALUES ( ($1) )", [req.body.email], function(err, result) {
      if (err) {
        return console.error("Error running query", err);
      }
      console.log("New user successfully created");
    });
  });
});

// GET routes

app.get('/data/rows', function(req, res) {
  pg.connect(conString, function(err, client, done) {
    if (err) {
      return console.error('Could not connect to postgres.', err);
    }
    console.log('Querying...');

    client.query('SELECT COUNT(*) FROM SafetyTest', function(err, result) {
      if (err) {
        return console.error('Could not connect to postgres.', err);
      }
      console.log("Query was successful");
      console.log(result.rows);
      res.type('text/plain');
      res.json(result.rows);
      res.end();
    });
  });
});

// Returning all public data past a certain row (rnum)
app.get('/data/test/:rnum', function(req, res) {
  pg.connect(conString, function(err, client, done) {
    if (err) {
      return console.error('Could not connect to postgres.', err);
    }
    console.log('Querying...');

    client.query('SELECT rowID, sampletime, rating, latitude, longitude, brightness FROM SafetyTest WHERE rowID > $1', 
      [req.params.rnum], function(err, result) {
      if (err) {
        return console.error('Error running query', err);
      }
      console.log("Query was successful");
      console.log(result.rows);
      res.type('text/plain');
      res.json(result.rows);
      res.end();
    });
  });
});

// Longitude and latitude are supplied in degrees
// Radius is supplied in km
app.get('/data/:latitude/:longitude/:radius', function(req, res) {
  pg.connect(conString, function(err, client, done) {
    if (err) {
      return console.error('Could not connect to postgres.', err);
    }
    console.log('Querying...');
    
    // User supplies long/lat in degrees - need to convert this into radians
    var latitudeRad = req.params.latitude * (Math.PI / 180);
    var longitudeRad = req.params.longitude * (Math.PI / 180);
    console.log("Lat in radians: " + latitudeRad);
    console.log("Long in radians: " + longitudeRad);

    client.query('SELECT * FROM safetydetail WHERE acos((sin($1) * sin(radians(latitude))) + (cos($1) * cos(radians(latitude)) * cos(($2) - radians(longitude)))) * 6371 <= $3', 
      [latitudeRad, longitudeRad, req.params.radius], function(err, result) {
      if (err) {
        return console.error('Error running query', err);
      }
      console.log("Query was successful");
      console.log(result.rows);
      res.type('text/plain');
      res.json(result.rows);
      res.end();
    });
  });
});

app.listen(5000);