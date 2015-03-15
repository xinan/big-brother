$(document).ready(function() {

  $('#offer1').on('click', function() {
    console.log('Offer 1 sent');
    $.ajax({
      method: 'GET',
      url: '/sendoffer1'
    });
  });

  $('#offer2').on('click', function() {
    console.log('Offer 2 sent');
    $.ajax({
      method: 'GET',
      url: '/sendoffer2'
    });
  });

  $('#reminder').on('click', function() {
    console.log('Reminder sent');
    $.ajax({
      method: 'GET',
      url: '/reminder'
    });
  });

});