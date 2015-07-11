//Angular module declaration
var app = angular.module('identity-server', [
	'ngAnimate',
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

//Directive for password confirmation text field
app.directive('validPasswordC', function () {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function (viewValue, $scope) {
                var noMatch = viewValue != scope.newAdmin.password.$viewValue
                ctrl.$setValidity('noMatch', !noMatch)
            })
        }
    }
})

// Directive for edit text field
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

// Route declaration
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
    }).when('/signUp', {
        templateUrl: 'views/signUp.html',
        controller: 'viewSignUpCtrl'
    }).when('/logIn', {
        templateUrl: 'views/logIn.html',
        controller: 'viewLogInCtrl'
    }).when('/home', {
        templateUrl: 'views/displayUserHome.html',
        controller: 'viewDisplayUserHomeCtrl'
    }).when('/logOut', {
        controller: 'viewLogOutCtrl',
		templateUrl: 'views/logIn.html'});
//    }).otherwise ( { redirectTo: "/home" });
		
	}).
	run(function($rootScope, $location) {
    	$rootScope.$on( "$routeChangeStart", function(event, next, current) {
	  		if ($rootScope.loggedInUser == null) {
        			// no logged user, redirect to /login
				$rootScope.loggedIn = false;
        		if ( next.templateUrl === "views/logIn.html") {
        		} 
				else if ( next.templateUrl === "views/signUp.html") {
				}
				else if ( next.templateUrl === "views/displayUserHome.html") {
				}
				else {
          			$location.path("/logIn");
       			}
      		}
    	});
  	});

app.controller('defaultCtrl', function ($scope) {
	$scope.welcome = "Welcome to Swissbit Home Automation Systems"
	$scope.notification= "Please Login to Continue"
});


app.controller('viewDisplayUserHomeCtrl', function ($scope) {
    $scope.welcome = "Welcome to your home page"
       	$scope.slides = [
            {image: 'images/cust1.png', description: 'Image 00'},
            {image: 'images/cust2.png', description: 'Image 01'},
            {image: 'images/rpi1.png', description: 'Image 02'},
            {image: 'images/rpi2.png', description: 'Image 03'},
            {image: 'images/rpi3.png', description: 'Image 04'}
        ];

		$scope.direction = 'left';
        $scope.currentIndex = 0;

        $scope.setCurrentSlideIndex = function (index) {
			$scope.direction = (index > $scope.currentIndex) ? 'left' : 'right';
            $scope.currentIndex = index;
        };

        $scope.isCurrentSlideIndex = function (index) {
            return $scope.currentIndex === index;
        };
		$scope.prevSlide = function () {
			 $scope.direction = 'left';
            $scope.currentIndex = ($scope.currentIndex < $scope.slides.length - 1) ? ++$scope.currentIndex : 0;
        };

        $scope.nextSlide = function () {
			 $scope.direction = 'right';
            $scope.currentIndex = ($scope.currentIndex > 0) ? --$scope.currentIndex : $scope.slides.length - 1;
        };
});

app.animation('.slide-animation', function () {
	return {
            beforeAddClass: function (element, className, done) {
                var scope = element.scope();

                if (className == 'ng-hide') {
                    var finishPoint = element.parent().width();
                    if(scope.direction !== 'right') {
                        finishPoint = -finishPoint;
                    }
                    TweenMax.to(element, 0.5, {left: finishPoint, onComplete: done });
                }
                else {
                    done();
                }
            },
            removeClass: function (element, className, done) {
                var scope = element.scope();

                if (className == 'ng-hide') {
                    element.removeClass('ng-hide');

                    var startPoint = element.parent().width();
                    if(scope.direction === 'right') {
                        startPoint = -startPoint;
                    }

                    TweenMax.fromTo(element, 0.5, { left: startPoint }, {left: 0, onComplete: done });
                }
                else {
                    done();
                }
            }
        };
    });

app.controller('viewSignUpCtrl', function ($scope, $http, $location, $route) {

	$scope.newAdminDefaults = {};
	$scope.updateAdmin = function (newAdminInfo) {
    	var data = {
            email: $scope.newAdminInfo.email,
			id: $scope.newAdminInfo.id,
			fname: $scope.newAdminInfo.fname,
            lname: $scope.newAdminInfo.lname,
            password: $scope.newAdminInfo.password,
        };
        $http.post('/admin', data).success(function (data) {
            console.log('status changed');
            $scope.newAdminInfo = angular.copy($scope.newAdminDefaults);
            $location.path('views/displayHome.html');
            $route.reload();
        }).error(function (data, status) {
		    $scope.notification= "Oops.. Something went wrong. Please try after sometime"; 	
            console.log('Error ' + data);
        })
    }

    $scope.removeForm = function () {
        $scope.newAdminInfo = angular.copy($scope.newAdminDefaults);
        $location.path('views/displayHome.html');
		$route.reload();
    }

    $scope.resetForm = function () {
        $scope.newAdminInfo = angular.copy($scope.newAdminDefaults);
    }
});

app.controller('viewLogInCtrl', function ($scope, $rootScope, $http, $location, $route) { 
    $scope.existingAdminDefaults = {};
    $scope.validateAdmin = function (existingAdminInfo) {
		$rootScope.userNotification = ""
		var data = {
            email: $scope.existingAdminInfo.email,
            password: $scope.existingAdminInfo.password,
    	};
		$http.post('authenticate', data).success(function (data) {
			console.log('Login Successful');
			$scope.userNotification = "Login Successful"
			$rootScope.loggedInUser = JSON.parse(data);
			console.log($rootScope.loggedInUser);
			$rootScope.loggedIn = true;
			$location.path('/home');
		}).error(function (data, status) {
    		$rootScope.userNotification = data.message; 
    		console.log('Error ' + data);
		})
    }

    $scope.removeForm = function () {
        $scope.existingAdminInfo = angular.copy($scope.existingAdminDefaults);
        $location.path('views/displayHome.html');
        $route.reload();
    }

    $scope.resetForm = function () {
        $scope.existingAdminInfo = angular.copy($scope.existingAdminDefaults);
    }
});


app.controller('viewLogOutCtrl', function ($scope, $rootScope, $location, $route) {
	$rootScope.loggedInUser = null;
	$rootScope.loggedIn = false;
	$location.path("/logIn");
	$route.reload();
	$rootScope.userNotification = "You have now been logged Out. Please login again to continue";
});



app.controller('viewCustomersCtrl', function ($scope, $http, $location, $route) {
		$scope.newCustomerForm = false;
		$scope.updateCustomerForm = false;
		$scope.newCustomerDefaults = {};
		$scope.currentCustomerDefaults = {};
		$scope.updateCustomerInfo = {};
        $http.get('/users').success(function (data) {
        	$scope.customers = data;
        })

		$scope.addCustomer = function () {
			$scope.newCustomerForm = true;
        }

        $scope.addnewCustomer = function (newCustomerInfo) {
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

        $scope.editCustomer = function (customer) {
            $scope.updateCustomerForm = true;
            $scope.currentCustomerDefaults = angular.copy(customer);
            $scope.updateCustomerInfo = angular.copy(customer);
        }

        $scope.updateCustomer = function (updateCustomerInfo) {
            var data = {
                email: $scope.updateCustomerInfo.email,
                id: $scope.updateCustomerInfo.id,
                name: $scope.updateCustomerInfo.name,
                pin: $scope.updateCustomerInfo.pin,
                username: $scope.updateCustomerInfo.username
            };
            $http.post('/user/' + $scope.updateCustomerInfo.id, data).success(function (data) {
                console.log('status changed');
                $scope.updateCustomerForm = false;
                $location.path('/viewCustomers');
                $route.reload();
            }).error(function (data, status) {
                console.log('Error ' + data);
            })
        }

		$scope.removeCustomer = function (customer) {
			$http.delete('/deluser/' + customer.id).success(function (data) {
				console.log('User has been removed');
				$location.path('/viewCustomers');
				$route.reload();
            }).error(function (data, status) {
                console.log('Error ' + data);
            })
        }

        $scope.removeForm = function () {
			$scope.newCustomerInfo = angular.copy($scope.newCustomerDefaults);
            $scope.updateCustomerInfo = angular.copy($scope.currentCustomerDefaults);
            $scope.newCustomerForm = false;
            $scope.updateCustomerForm = false;
        }

        $scope.resetForm = function () {
			$scope.newCustomerInfo = angular.copy($scope.newCustomerDefaults);
			$scope.updateCustomerInfo = angular.copy($scope.currentCustomerDefaults);
        }
}); 

app.controller('viewRaspPisCtrl', function ($scope, $http, $location, $route) {
        $scope.newRaspPiForm = false;
		$scope.updateRaspPiForm = false;
		console.log($scope.updateRaspPiForm);
        $scope.newRaspPiDefaults = {};
		$scope.currentRaspPiDefaults = {};
		$scope.updateRaspPiInfo = {};
        $http.get('/pis').success(function (data) {
        	$scope.rpis = data;
        })
        $scope.addRaspPi = function () {
        	$scope.newRaspPiForm = true;
		}

        $scope.addnewRaspPi = function (newRaspPiInfo) {
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

        $scope.editRaspPi = function (rpi) {
			console.log(rpi);
			console.log($scope.updateRaspPiForm);
            $scope.updateRaspPiForm = true;
			console.log($scope.updateRaspPiForm);
			$scope.currentRaspPiDefaults = angular.copy(rpi);
			$scope.updateRaspPiInfo = angular.copy(rpi);
			console.log($scope.currentRaspPiDefaults);
        }
		
		$scope.updateRaspPi = function (updateRaspPiInfo) {
            var data = {
            	customer: $scope.updateRaspPiInfo.customer,
                id: $scope.updateRaspPiInfo.id,
                macaddr: $scope.updateRaspPiInfo.macaddr,
                name: $scope.updateRaspPiInfo.name,
                pin: $scope.updateRaspPiInfo.pin
           	};
			console.log(data);
			$http.post('/pi/' + $scope.updateRaspPiInfo.id, data).success(function (data) {
                console.log('status changed');
                $scope.updateRaspPiForm = false;
                $location.path('/viewRaspPis');
                $route.reload();
            }).error(function (data, status) {
                console.log('Error ' + data);
            })	
		}
		
        $scope.removeRaspPi = function (rpi) {
            $http.delete('/delpi/' + rpi.id).success(function (data) {
                console.log('Raspberry Pi has been removed');
                $location.path('/viewRaspPis');
                $route.reload();
            }).error(function (data, status) {
                console.log('Error ' + data);
            })
        }
	
		$scope.viewCodeLog = function(rpimacaddr) {
			$http.get('/logs/' + rpimacaddr + '/code').success(function (data) {
				var blob = new Blob([data], { type:"text/html;charset=utf-8;" });
				var downloadLink = angular.element('<a></a>');
				downloadLink.attr('href',window.URL.createObjectURL(blob));
				downloadLink.attr('download', 'raspberrypi_code.log');
				downloadLink[0].click();
	
			})
		}

        $scope.viewAppLog = function(rpimacaddr) {
            $http.get('/logs/' + rpimacaddr + '/app').success(function (data) {
                console.log(data);
                var blob = new Blob([data], { type:"application/xml;charset=utf-8;" });
                var downloadLink = angular.element('<a></a>');
                downloadLink.attr('href',window.URL.createObjectURL(blob));
                downloadLink.attr('download', 'raspberrypi_app.xml');
                downloadLink[0].click();

            })
        }

        $scope.removeForm = function () {
            $scope.newRaspPiInfo = angular.copy($scope.newRaspPiDefaults);
			$scope.updateRaspPiInfo = angular.copy($scope.currentRaspPiDefaults);
            $scope.newRaspPiForm = false;
			$scope.updateRaspPiForm = false;
        }

        $scope.resetForm = function () {
            $scope.RaspPiInfo = angular.copy($scope.newRaspPiDefaults);
			$scope.updateRaspPiInfo = angular.copy($scope.currentRaspPiDefaults);
        }
}); 
