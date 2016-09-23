'use strict';

/**
 * @ngdoc function
 * @name dockstore.ui.controller:WorkflowsGridCtrl
 * @description
 * # WorkflowsGridCtrl
 * Controller of the dockstore.ui
 */
angular.module('dockstore.ui')
  .controller('WorkflowsGridCtrl', [
    '$scope',
    '$rootScope',
    'FormattingService',
    function ($scope, $rootScope, FrmttSrvc) {

      $scope.workflows = [];
      $scope.sortColumn = 'name';
      $scope.sortReverse = false;
      $scope.numContsPage = 10;
      $scope.currPage = 1;
      $scope.contLimit = $scope.previewMode ? 5 : undefined;

      $scope.getGitReposProvider = FrmttSrvc.getGitReposProvider;
      $scope.getGitReposProviderName = FrmttSrvc.getGitReposProviderName;
      $scope.getGitReposWebUrlFromPath = FrmttSrvc.getGitReposWebUrlFromPath;

      /* Column Sorting */
      $scope.clickSortColumn = function(columnName) {
        if ($scope.sortColumn === columnName) {
          $scope.sortReverse = !$scope.sortReverse;
        } else {
          $scope.sortColumn = columnName;
          $scope.sortReverse = false;
        }
      };

      $scope.getIconClass = function(columnName) {
        if ($scope.sortColumn === columnName) {
          return !$scope.sortReverse ?
            'glyphicon-sort-by-alphabet' : 'glyphicon-sort-by-alphabet-alt';
        } else {
          return 'glyphicon-sort';
        }
      };

      /* Pagination */
      $scope.getFirstPage = function() {
        return 1;
      };

      $scope.getLastPage = function() {
        return Math.ceil($scope.filteredWorkflows.length / $scope.numContsPage);
      };

      $scope.changePage = function(nextPage) {
        if (nextPage) {
          /* Next Page*/
          if ($scope.currPage === $scope.getLastPage) return;
          $scope.currPage++;
        } else {
          /* Previous Page*/
          if ($scope.currPage === $scope.getFirstPage) return;
          $scope.currPage--;
        }
      };

      $scope.getListRange = function() {
        return {
          start: Math.min($scope.numContsPage * ($scope.currPage - 1),
                          $scope.filteredWorkflows.length),
          end: Math.min($scope.numContsPage * $scope.currPage - 1,
                        $scope.filteredWorkflows.length)
        };
      };

      $scope.getListRangeString = function() {
        var start = Math.min($scope.numContsPage * ($scope.currPage - 1) + 1,
                              $scope.filteredWorkflows.length).toString();
        var end = Math.min($scope.numContsPage * $scope.currPage,
                              $scope.filteredWorkflows.length).toString();

        var padLength = Math.max(start.length, end.length);

        for (var i = start.length; i < padLength; i++) {
          start = '0' + start;
        }
        for (var j = end.length; j < padLength; j++) {
          end = '0' + end;
        }

        return start + ' to ' + end + ' of ' + $scope.filteredWorkflows.length;
      };

      $scope.getHumanReadableDescriptor = function(descriptor) {
        switch(descriptor) {
          case 'DOCKSTORE_CWL':
            return 'CWL';
          case 'DOCKSTORE_WDL' :
            return 'WDL';
          default :
            return 'n/a';
        }
      };

  }]);
