angular.module('composer', ['charts'])
    .config(function ($routeProvider, $httpProvider) {
        $routeProvider
	    .when("/about", {templateUrl: "/partial/about", controller: AboutCtrl})
	    .when("/schedule", {templateUrl: "/partial/schedule", controller: ScheduleCtrl})
	    .when("/message", {templateUrl: "/partial/message", controller: MessageCtrl})
	    .otherwise({redirectTo: "/about"});
    });

google.load('visualization', '1.0', {'packages':['corechart', 'table']});
google.setOnLoadCallback(function() {
   angular.bootstrap(document.body, ['composer']);
});

