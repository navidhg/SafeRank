// Imports
var config = require("./config");
var pg = require("pg");
var express = require("express");
var bodyParser = require("body-parser")

// Set up express
var app = express();
app.use(bodyParser.json());

// Set up postgres details
var conString = config.getPostgresConnectionString();
var client = new pg.Client(conString);

// POST route
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

app.listen(5000);