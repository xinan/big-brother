var socket = io('http://localhost:3000');
  socket.on('CONNECTION_START', function (data) {
    console.log(data);
    socket.emit('CONNECTION_CONFIRM', "Client connected successfully.");
  });