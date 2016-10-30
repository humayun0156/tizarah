
var app = angular.module('myApp', []);

app.controller('accountHeadCtrl', function ($scope, $timeout, AccHeadService) {
    $scope.firstName = "Humayun";
    $scope.lastName = "Kabir";
    $scope.myHeader = "Hello world!";
    $timeout(function(){
        $scope.myHeader = "How are you today?"
    }, 2000);

    $scope.accountHeads = [];
    $scope.subAccounts = [];

    function getAllAccountHeads() {
        AccHeadService.getAll().then(function (res) {
            $scope.accountHeads = res.data;
            console.log($scope.accountHeads)
        }, function (err) {
            //error
        });
    }

    $scope.getAccountsByHeadId = function(headId) {
        AccHeadService.getSubAccountByHeadId(headId).then(function (res) {
            $scope.subAccounts = res.data;
            console.log($scope.subAccounts)
        }, function (err) {
            //error
        });
    };

    $scope.newAccountHead = {};
    $scope.addAccountHead = function() {
      AccHeadService.addAccountHead($scope.newAccountHead).then(function (res) {
          //$('.modal').modal('hide');
          var newId = res.data.id;
          $scope.newAccountHead["id"] = newId
          $scope.accountHeads.push($scope.newAccountHead);
          $scope.newAccountHead = {};
          showAlertMessage(res.status,res.msg)
      })
    };

    $scope.newSubAccount = {};
    $scope.addSubAccount = function () {
      AccHeadService.addSubAccount($scope.newSubAccount).then(function (res) {
          //var newId = res.data.id;
          //$scope.newSubAccount["id"] = newId;
          //$scope.subAccounts.push($scope.newSubAccount);
          $scope.newSubAccount = {};
          showAlertMessage(res.status, res.msg)
      })
    };

    $scope.newTransaction = {};
    $scope.addTransaction = function () {
        AccHeadService.addTransaction($scope.newTransaction).then(function (res) {
            $scope.newTransaction = {};
            showAlertMessage(res.status, res.msg)
        })
    };

    getAllAccountHeads();

    $scope.alerts = [];
    function showAlertMessage(status, message) {
        if (status == "success") {
            $scope.alerts.push({type: "alert-success", title: "SUCCESS", content: message})
        } else if (status == "error") {
            $scope.alerts.push({type: "alert-danger", title: "ERROR", content: message})
        }
    }

});


/**
 * Directive for alert notification. You can also use angular ui-bootstrap for better alert notifications
 */
app.directive('notification', function($timeout){
    return {
        restrict: 'E',
        replace: true,
        scope: {
            ngModel: '='
        },
        template: '<div ng-class="ngModel.type" class="alert alert-box">{{ngModel.content}}</div>',
        link: function(scope, element, attrs) {
            $timeout(function(){
                element.hide();
            }, 3000);
        }
    }
});


app.service("AccHeadService", function ($http, $q) {
    var task = this;
    task.taskList = {};

    task.getAll = function () {
      var defer = $q.defer();
      $http.get('/admin/head/all')
          .success(function(res) {
            task.taskList = res;
            defer.resolve(res);
          })
          .error(function (err, status) {
            defer.reject(err);
          });
        return defer.promise;
    };

    task.getAllSubAccount = function () {
        var defer = $q.defer();
        $http.get('/admin/subaccount/all')
            .success(function(res) {
                task.taskList = res;
                defer.resolve(res);
            })
            .error(function (err, status) {
                defer.reject(err);
            });
        return defer.promise;
    };

    task.getSubAccountByHeadId = function (headId) {
        var defer = $q.defer();
        $http.get('/admin/acc/sub/' + headId)
            .success(function (res) {
                task.taskList = res;
                defer.resolve(res)
            }).error(function (err, status) {
            defer.reject(err);
        });
        return defer.promise;
    };

    task.addAccountHead = function (data) {
        var defer = $q.defer();
        $http.post('/create/account/xyz', data)
            .success(function (res) {
                task.taskList = res;
                defer.resolve(res)
            }).error(function (err, status) {
                defer.reject(err);
            });
        return defer.promise;
    };

    task.addSubAccount = function (data) {
        var defer = $q.defer();
        $http.post('/create/account/abc', data)
            .success(function (res) {
                task.taskList = res;
                defer.resolve(res)
            }).error(function (err, status) {
            defer.reject(err);
        });
        return defer.promise;
    };

    task.addTransaction = function (data) {
        var defer = $q.defer();
        $http.post('/create/account/transaction', data)
            .success(function (res) {
                task.taskList = res;
                defer.resolve(res)
            }).error(function (err, status) {
            defer.reject(err);
        });
        return defer.promise;
    };

    return task;
});