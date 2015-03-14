var express = require('express');
var path = require('path');
var favicon = require('static-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var File = require('File');
var FileReader = require('FileReader');

var routes = require('./routes/index');
var users = require('./routes/users');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(favicon());
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);

/// catch 404 and forwarding to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

/// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});

var server = require('http').Server(app);
var io = require('socket.io')(server);

var userData = {
    name: "",
    flightNum: "",
    declineNum: 0,
    reports: []
};

io.on('connection', function(socket) {
    socket.emit('CONNECTION_STARTED', "Connection started successfully.");
    socket.on('CONNECTION_CONFIRMED', function (data) {
        userData.name = data.name;
        userData.flightNum = (data.flightNum === "") ? (socket.emit("NO_FLIGHT_NUM", "")) : data.flightNum;
    });
    socket.on('REPORT', function (report) {
        userData.reports.push(report);
        sendOffer(socket, report);
    });
    socket.on('OFFER_DECISION', function (offer) {
        if (offer.hasAccepted) {
            // voucher
            userData.declineNum = -1;
            sendVoucher(socket, offer.id);
        } else {
            userData.declineNum++;
            sendRejectConfirmation(socket);
        }
    });

    // socket.on('')
});

// yi wen's get requests
app.get('/alert/:id', function(req, res) {
    res.send({
        "status": "success"
    });
    socket.emit('WAKE_UP', "");
});

// Helper functions

function setAlarm(socket) {
    var flightTime = getFlightTime(userData.flightNum);
    socket.emit('SET_ALARM', flightTime);
}

function getFlightTime(flightNum) {
    // use api to find time of departure
    // var hour;
    // var minute;

    return {
        "hour": hour,
        "minute": minute
    }
}

function sendOffer(socket, report) {
    /***************
    * I BLAME VARUN.
    ****************
    *
    var reader = new FileReader();

    // Read the local file
    var imageFile = new File('burgerking.jpg');

    // Actual image
    var image;
    reader.onload = function (e) {
        image = e.target.result;
        // analyze the report and send the socket the offer
        var offer = {
            image: 'burgerking.jpg',
            title: 'Burger King Chicken Royale',
            tier: 2,
            description: 'You have won a free Chicken Royale in Burger King!'
        };
        console.log(image);
        socket.emit('SEND_OFFER', offer);
    };
    reader.readAsDataURL(imageFile);
    */
    var offer = {
        id: 1,
        image: 'burgerking',
        title: 'Burger King Chicken Royale',
        tier: 2,
        description: 'You have won a free Chicken Royale in Burger King!'
    };
    socket.emit('SEND_OFFER', offer);
}

function sendVoucher(socket, offerID) {
    var voucherID = getVoucher(offerID);
    socket.emit('OFFER_VOUCHER', voucherID);
}

function getVoucher(offerID) {
    // return the voucher code associated with the id of the offer
    // by looking up from a database of offers.
}

function sendRejectConfirmation(socket) {
    socket.emit('REJECT_CONFIRMED', "");
}

var port = Number(process.env.PORT || 5000);
server.listen(port, function() {
    console.log('Listening on port' + port + '...');
});

module.exports = app;
