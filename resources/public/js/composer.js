angular.module('composer', [])
    .config(function ($routeProvider, $httpProvider) {
        $routeProvider
	    .when("/about", {templateUrl: "/partial/about", controller: AboutCtrl})
	    .when("/message", {templateUrl: "/partial/message", controller: MessageCtrl})
	    .otherwise({redirectTo: "/about"});
    });

angular.bootstrap(document.body, ['composer']);

