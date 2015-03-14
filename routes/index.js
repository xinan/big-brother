var express = require('express');
var router = express.Router();



module.exports = function(io){
  /* GET home page. */
router.get('/', function(req, res) {
  res.render('index', { title: 'Big Brother APIs' });
});

//Alerts the user that he/she is late for the flight
router.get('/alert/:id', function(req, res) {
    res.send({
        status: true
    });
    io.emit('late person','');
});

// Returns the idlers 
router.get('/idlers', function(req, res) {
  res.send({
    users: [
      {
        deviceId: 1,
        name: "Lu Bili",
        flight: {
          flightNo: "EK01",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.244515300668223,
          long: 55.37045352788945,
          floor: 1
        },
        boardingGate: 'A1',
        status: 'agent',
        statusTime: 12
      },
      {
        deviceId: 2,
        name: "Adola Fazli",
        flight: {
          flightNo: "EK02",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.24282729711983,
          long: 55.37245461207174,
          floor: 1
        },
        boardingGate: 'A1',
        status: 'idle',
        statusTime: 23
      },
      {
        deviceId: 3,
        name: "Mrunal Kumar",
        flight: {
          flightNo: "EK03",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.243092514556807,
          long: 55.37219699963714,
          floor: 1
        },
        boardingGate: 'A1',
        status: 'alert',
        statusTime: 10
      }
    ],
    requestTime: "2015-04-23T20:00:00.000+08:00"
  });
});
  return router;
}
