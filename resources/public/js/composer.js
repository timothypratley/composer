angular.module('composer')
    .config(function ($routeProvider, $httpProvider) {
        $routeProvider
	    .when("/message", {templateUrl: "partials/message", controller: MessageCtrl})
	    .otherwise({redirectTo: "/about"});
    });

angular.bootstrap(document.body, ['composer']);

