// Localization completed
angular.module('headwind-kiosk')
    .controller('FilesTabController', function ($scope, $rootScope, $state, $modal, alertService, confirmModal, fileService,
                                                authService, $window, localization) {
        $scope.search = {};

        $scope.paging = {
            currentPage: 1,
            pageSize: 50
        };

        $scope.$watch('paging.currentPage', function () {
            $window.scrollTo(0, 0);
        });

        $scope.hasPermission = authService.hasPermission;

        $scope.url = document.URL.replace("/#/login", "").replace("/#/", "");

        $scope.init = function () {
            $rootScope.settingsTabActive = false;
            $rootScope.pluginsTabActive = false;
            $scope.paging.currentPage = 1;
            $scope.search();
        };

        $scope.search = function () {
            fileService.getAllFiles({value: $scope.search.searchValue},
                function (response) {
                    $scope.files = response.data;
                });
        };

        $scope.addFile = function () {
            var modalInstance = $modal.open({
                templateUrl: 'app/components/main/view/modal/file.html',
                controller: 'FileModalController'
            });

            modalInstance.result.then(function () {
                $scope.search();
            });
        };

        $scope.removeFile = function (file) {
            confirmModal.getUserConfirmation(localization.localize('question.delete.file').replace('${fileName}', file.name), function () {
                fileService.removeFile(file, function (response) {
                    if (response.status === 'OK') {
                        $scope.search();
                    } else {
                        alertService.showAlertMessage(localization.localize(response.message));
                    }
                });
            });
        };

        $scope.showApps = function (file) {
            fileService.getApps({value: encodeURIComponent(file.url)}, function (response) {
                if (response.status === 'OK') {
                    var modalInstance = $modal.open({
                        templateUrl: 'app/components/main/view/modal/fileApps.html',
                        controller: 'FileAppsModalController',
                        resolve: {
                            apps: function () {
                                return response.data;
                            }
                        }
                    });
                } else {
                    alertService.showAlertMessage(localization.localize(response.message));
                }
            })
        };

        $scope.init();
    })
    .controller('FileAppsModalController', function ($scope, $modalInstance, apps) {
        $scope.apps = apps;

        $scope.closeModal = function () {
            $modalInstance.dismiss();
        }
    })
    .controller('FileModalController', function ($scope, $modalInstance, fileService, alertService, localization) {
        $scope.file = {};

        $scope.save = function () {
            $scope.errorMessage = '';
            $scope.successMessage = '';

            if (!$scope.file.path) {
                $scope.errorMessage = localization.localize('error.file.empty');
            } else {
                var request = {
                    path: $scope.file.path,
                    localPath: $scope.file.localPath
                };

                fileService.moveFile(request, function (response) {
                    if (response.status === 'OK') {
                        $modalInstance.close();
                    } else {
                        $modalInstance.close();
                        alertService.showAlertMessage(localization.localize(response.message));
                    }
                });
            }
        };

        $scope.onStartedUpload = function () {
            $scope.loading = true;
        };

        $scope.fileUploaded = function (response) {
            $scope.errorMessage = '';
            $scope.successMessage = '';

            $scope.loading = false;

            if (response.data.status === 'OK') {
                $scope.file.path = response.data.message;
                $scope.successMessage = localization.localize('success.file.uploaded.need.save');
            }
        };

        $scope.closeModal = function () {
            $modalInstance.dismiss();
        }
    });