'use strict';

angular.module('jee-web-application',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Catalogs',{templateUrl:'views/Catalog/search.html',controller:'SearchCatalogController'})
      .when('/Catalogs/new',{templateUrl:'views/Catalog/detail.html',controller:'NewCatalogController'})
      .when('/Catalogs/edit/:CatalogId',{templateUrl:'views/Catalog/detail.html',controller:'EditCatalogController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
