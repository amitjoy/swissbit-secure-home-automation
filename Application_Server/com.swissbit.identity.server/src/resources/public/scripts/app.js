var app = angular.module('identity-server', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute'
]);

app.config(function ($routeProvider, $locationProvider) {
    // use the HTML5 History API
    $locationProvider.html5Mode(true);

    $routeProvider.when('/', {
        templateUrl: 'views/displayHome.html',
        controller: 'defaultCtrl'
    }).when('/viewCustomers', {
        templateUrl: 'views/viewCustomers.html',
        controller: 'viewCustomersCtrl'
    }).when('/viewRaspPis', {
        templateUrl: 'views/viewRaspPis.html',
        controller: 'viewRaspPisCtrl'
    }).otherwise({
        redirectTo: '/'
    })
});

app.controller('defaultCtrl', function ($scope) {
	$scope.welcome = "Welcome to Swissbit Home Automation Systems"
	$scope.notification= "Now you could view Customers and Raspberry Pi details"
});

app.controller('viewCustomersCtrl', function ($scope, $http) {
        $http.get('/users').success(function (data) {
        $scope.customers = data;
        })
	$scope.addCustomer = function () {
		$scope.notification= "Function yet to be implemented"
        }
}); 

app.controller('viewRaspPisCtrl', function ($scope, $http) {
        $http.get('/pis').success(function (data) {
        $scope.rpis = data;
        })
        $scope.addRaspPi = function () {
                $scope.notification= "Function yet to be implemented"
        }
}); 
