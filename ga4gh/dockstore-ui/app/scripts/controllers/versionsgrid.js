'use strict';

/**
 * @ngdoc function
 * @name dockstore.ui.controller:VersionsGridCtrl
 * @description
 * # VersionsGridCtrl
 * Controller of the dockstore.ui
 */
angular.module('dockstore.ui')
  .controller('VersionsGridCtrl', [
    '$scope',
    '$q',
    'ContainerService',
    'FormattingService',
    'NotificationService',
    function ($scope, $q, ContainerService, FrmttSrvc, NtfnService) {

      $scope.containers = [];
      $scope.sortColumn = 'name';
      $scope.sortReverse = false;

      $scope.getHRSize = FrmttSrvc.getHRSize;
      $scope.getDateModified = FrmttSrvc.getDateModified;

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

      $scope.deleteTag = function(tagId) {
        $scope.setError(null);
        return ContainerService.deleteContainerTag($scope.containerObj.id, tagId)
          .then(
            function(response) {
              $scope.removeVersionTag(tagId);
              $scope.$emit('tagEditorRefreshContainer', $scope.containerObj.id);
            },
            function(response) {
              $scope.setError(
                'The webservice encountered an error trying to delete this ' +
                'tag, please ensure that the container and the tag both exist.',
                '[HTTP ' + response.status + '] ' + response.statusText + ': ' +
                response.data
              );
              return $q.reject(response);
            }
          );
      };

      $scope.addVersionTag = function(tagObj) {
        $scope.versionTags.push(tagObj);
      };

      $scope.removeVersionTag = function(tagId) {
        for (var i = 0; i < $scope.versionTags.length; i++) {
          if ($scope.versionTags[i].id === tagId) {
            $scope.versionTags.splice(i, 1);
            break;
          }
        }
      };

      $scope.getDockerPullCmd = function(path, tagName) {
        return FrmttSrvc.getFilteredDockerPullCmd(path, tagName);
      };

      $scope.getCreateTagObj = function() {
        return {
          create: true,
          name: '',
          reference: '',
          image_id: '',
          dockerfile_path: $scope.containerObj.default_dockerfile_path,
          cwl_path: $scope.containerObj.default_cwl_path,
          wdl_path: $scope.containerObj.default_wdl_path,
          hidden: true,
          automated: false
        };
      };

  }]);
