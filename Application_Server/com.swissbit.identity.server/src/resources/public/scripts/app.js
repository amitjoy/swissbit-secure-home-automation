var app = angular.module('identity-server', [
    'ngCookies',
    'ngResource',
    'ngSanitize',
    'ngRoute'
]);


// Global transform function to convert JSON to query parameters for $http.post
app.config(function ($httpProvider) {
    $httpProvider.defaults.transformRequest = function(data){
	$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
        if (data === undefined) {
            return data;
        }
        return $.param(data);
    }
});


app.directive('editText', function(){
	return {
    	restrict: 'E',
        scope: {
            value : '='
        },
        require: 'value',
        controller: ['$scope', function($scope){
            $scope.editing = false;
            $scope.toggleEdit = function(save) {
                if (!$scope.editing) {
                    $scope.editing = true;
                } else {
                    if (save) {
                        $scope.$parent.updateField();
                    }
                    $scope.editing = false;
                }
            }
        }],
        templateUrl: 'views/edit_text.html'
    };
});

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
	$scope.notification= "Now you could view and add Customers and Raspberry Pi details"
});

app.controller('viewCustomersCtrl', function ($scope, $http, $location, $route) {
		$scope.newCustomerForm = false;
		$scope.newCustomerDefaults = {};

        $http.get('/users').success(function (data) {
        	$scope.customers = data;
        })

		$scope.addCustomer = function () {
			$scope.newCustomerForm = true;
        }

        $scope.updateCustomer = function (newCustomerInfo) {
			var data = {
			    email: $scope.newCustomerInfo.email,
				name: $scope.newCustomerInfo.name,
    			password: $scope.newCustomerInfo.password,
				pin: $scope.newCustomerInfo.pin,
				username: $scope.newCustomerInfo.username
			};
			$http.post('/user', data).success(function (data) {
			    console.log('status changed');
				$scope.newCustomerInfo = angular.copy($scope.newCustomerDefaults);
				$scope.newCustomerForm = false;
				$location.path('/viewCustomers');
				$route.reload();
			}).error(function (data, status) {
    			console.log('Error ' + data);
			})
        }

        $scope.updateField = function () {
        }

        $scope.removeForm = function () {
			$scope.newCustomerInfo = angular.copy($scope.newCustomerDefaults);
            $scope.newCustomerForm = false;
        }

        $scope.resetForm = function () {
			$scope.newCustomerInfo = angular.copy($scope.newCustomerDefaults);
        }
}); 

app.controller('viewRaspPisCtrl', function ($scope, $http, $location, $route) {
        $scope.newRaspPiForm = false;
        $scope.newRaspPiDefaults = {};

        $http.get('/pis').success(function (data) {
        	$scope.rpis = data;
        })
        $scope.addRaspPi = function () {
        	$scope.newRaspPiForm = true;
		}

        $scope.updateRaspPi = function (newRaspPiInfo) {
            var data = {
                customer: $scope.newRaspPiInfo.customer,
                id: $scope.newRaspPiInfo.id,
                macaddr: $scope.newRaspPiInfo.macaddr,
				name: $scope.newRaspPiInfo.name,
                pin: $scope.newRaspPiInfo.pin
            };
            $http.post('/pi', data).success(function (data) {
                console.log('status changed');
                $scope.newRaspPiInfo = angular.copy($scope.newRaspPiDefaults);
                $scope.newRaspPiForm = false;
                $location.path('/viewRaspPis');
                $route.reload();
            }).error(function (data, status) {
                console.log('Error ' + data);
            })
        }

        $scope.removeForm = function () {
            $scope.newRaspPiInfo = angular.copy($scope.newRaspPiDefaults);
            $scope.newRaspPiForm = false;
        }

        $scope.resetForm = function () {
            $scope.newRaspPiInfo = angular.copy($scope.newRaspPiDefaults);
        }
}); 
