var socket = io('http://localhost:3000');
  socket.on('CONNECTION_STARTED', function (data) {
    console.log(data);
    socket.emit('CONNECTION_CONFIRMED', "Client connected successfully.");
  });