var socket = io('http://big-brother-api.herokuapp.com');
  socket.on('CONNECTION_STARTED', function (data) {
    console.log(data);
    socket.emit('CONNECTION_CONFIRMED', "Client connected successfully.");
  });