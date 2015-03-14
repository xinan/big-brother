var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res) {
  res.render('index', { title: 'Express' });
});

router.get('/idlers', function(req, res) {
  res.send({
    users: [
      {
        deviceId: 1,
        name: "Lu Bili",
        flight: {
          flightNo: "SQ01",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.244515300668223,
          long: 55.37045352788945,
          floor: 1
        }
      },
      {
        deviceId: 2,
        name: "Adola Fazli",
        flight: {
          flightNo: "SQ02",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.24282729711983,
          long: 55.37245461207174,
          floor: 1
        }
      },
      {
        deviceId: 3,
        name: "Mrunal Kumar",
        flight: {
          flightNo: "SQ03",
          flightTime: "2015-04-23T20:00:00.000+08:00"
        },
        location: {
          lat: 25.243092514556807,
          long: 55.37219699963714,
          floor: 1
        }
      }
    ],
    requestTime: "2015-04-23T20:00:00.000+08:00"
  });
});

module.exports = router;
