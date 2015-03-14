var airportDatabase;
var airport;
var building;
var floor;
var map;
var floorView;

var navPoint;
var epsilon = 0.000001;

function getInactiveCustomers(callback) {
    $.ajax({
      url: 'http://private-0cf82-xbili.apiary-mock.com/users',
      type: 'GET',
      crossDomain: true,
      dataType: 'json',
      success: function (result) {
        var users = result.users;
        callback(users);
      },
      error: function (xhr, status) {
        window.alert(status);
      }
    });
}

function example_ready() {
    getInactiveCustomers(function(users) {
        $.each(users, function(i, user) {
            var navPoint = new locuslabs.maps.NavPoint();
            var latLng = new locuslabs.maps.LatLng(user.location.lat, user.location.long);
            var floorView = map.getView().getFloorView('Level_5');
            navPoint.setFloor(floorView);
            navPoint.set('fillColor', 'rgb(255, 0, 0)');
            navPoint.setPosition(latLng);
            console.log(navPoint);
            $(navPoint._view._circleView._circle).attr('z-index', 100);
            $(navPoint._view._circleView._circle).on('click', function(e) {
                alert('Floor: ' + user.location.floor + "\n"
                 + 'Customer name: ' + user.name);
            });
        });
    });
}

function generatePopup(navPoint) {
    return 'test';
}

function map_loaded(_map) {
    map = _map;
    floorView = map.getView().getFloorView(map.getFloorId());

    // Pan and zoom the map to a more interesting part of the airport.
    map.setCenter(new locuslabs.maps.LatLng(25.243092514556807, 55.37219699963714));
    map.setRadius(300);

    example_ready();
}

function show_floor(_floor)
{
    floor = _floor;

    // Render the map of the floor into the #map-canvas div.
    floor.loadMap(document.getElementById('map-canvas'),map_loaded);
}

function show_building(_building)
{
    building = _building;

    // Get the list available floors for this building, then load and show the first one.
    var floors = building.listFloors();
    show_floor( building.loadFloor(floors[0].floorId ) );
}

function airport_loaded(_airport)
{
    airport = _airport;

    // Get the list available buildings for this airport, then load the first one.
    var buildings = airport.listBuildings();
    show_building(airport.loadBuilding(buildings[0].buildingId));
}

$(document).ready(function () {
    // Initialize the Account Id
    locuslabs.setup( { accountId: "A1VPGMX49NA3QQ" }, function () {
        // Create an AirportDatabase object then load an airport.
        airportDatabase = new locuslabs.maps.AirportDatabase();
        airportDatabase.loadAirport('dxb',airport_loaded);
    });
});

