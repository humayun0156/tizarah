
var app = angular.module('myApp', ['ui.bootstrap']);

app.controller('accountHeadCtrl', function ($scope, $timeout, AccHeadService) {
    $scope.myHeader = "Hello world!";
    $timeout(function(){
        $scope.myHeader = "How are you today?"
    }, 2000);

    $scope.accountHeads = [];
    $scope.subAccounts = [];

    function getAllAccountHeads() {
        AccHeadService.getAllHeadAccounts().then(function (res) {
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


app.controller('journalCtrl', function ($scope, $timeout, $filter, JournalService) {

    $scope.date = $filter('date')(new Date(), "yyyy-MM-dd");

    function getTodayJournal(date) {
        JournalService.todayJournal(date).then(function (res) {
            $scope.debitTranList = res.data.debit;
            $scope.creditTranList = res.data.credit;
            $scope.debitTotal = Number(res.data.debitTotal).toLocaleString('bn');
            $scope.creditTotal = Number(res.data.creditTotal).toLocaleString('bn');

            console.log(res.data);
            console.log($scope.debitTranList);
            console.log($scope.creditTranList);
        }, function (err) {
            //error
        });
    }

    getTodayJournal(new Date().getTime());

    $scope.change = function (dateValue) {
        console.log("Dateeee: " + dateValue); //    dd/MM/yyyy
        var splitDate = dateValue.split("/");
        console.log("Changed data: " + dateValue);
        //var d = dateValue.replace(/\//g, '-');
        var day = splitDate[0];
        var month = splitDate[1];
        var year = splitDate[2];
        var d = new Date(year+"-"+month+"-"+day).getTime();
        console.log("D: " + d);
        getTodayJournal(d);
    }
});


app.controller('ledgerCtrl', function ($scope, $timeout, $filter, LedgerService) {
    $scope.firstName = "Humayun";

    function getLedgerIndex() {
        LedgerService.ledgerIndex().then(function (res) {
            $scope.journalIndexData = res.data;
            console.log(res.data[0].headId);
            console.log(res.data);
        }, function (err) {
            //error
        });
    }
    getLedgerIndex();
});

app.controller('ledgerAccountCtrl', function ($scope, $timeout, $filter, $location, LedgerService) {
    $scope.firstName = "Humayun";
    var x = window.location.pathname.split("/");
    var accId = x[x.length-1];
    console.log("accId: " + accId);

    function getLedgerAccountData(id) {
        LedgerService.ledgerAccData(id).then(function (res) {
            $scope.ledgerTransactions = res.data.transactions;
            $scope.debitTotal = res.data.debitTotal;
            $scope.creditTotal = res.data.creditTotal;

            console.log(res.data);
        }, function (err) {
            //error
        });
    }
    getLedgerAccountData(accId);
});


app.filter('dateFormat', function() {
    return function(input) {
        var date = new Date(input);
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDay();
        return day + "/" + month + "/" + year;
    };
});

app.filter('amountInBangla', function() {
    return function (input) {
        return Number(input).toLocaleString('bn');
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

    task.getAllHeadAccounts = function () {
      var defer = $q.defer();
      $http.get('/api/v1/head_accounts')
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
        $http.get('/api/v1/head/' + headId + '/account')
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
        $http.post('/api/v1/account/head/create', data)
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
        $http.post('/api/v1/account/create', data)
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
        $http.post('/api/v1/transaction/create', data)
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


app.service("JournalService", function ($http, $q) {
    var task = this;
    task.taskList = {};

    task.todayJournal = function (date) {
        var defer = $q.defer();
        var url = '/api/v1/journal/today?date=' + date;
        console.log("Request: " + url);
        $http.get(url)
            .success(function(res) {
                task.taskList = res;
                defer.resolve(res);
            })
            .error(function (err, status) {
                defer.reject(err);
            });
        return defer.promise;
    };

    return task;
});


app.service("LedgerService", function ($http, $q) {
    var task = this;
    task.taskList = {};

    task.ledgerIndex = function () {
        var defer = $q.defer();
        var url = '/api/v1/ledger/index' ;
        console.log("Request: " + url);
        $http.get(url)
            .success(function(res) {
                task.taskList = res;
                defer.resolve(res);
            })
            .error(function (err, status) {
                defer.reject(err);
            });
        return defer.promise;
    };

    task.ledgerAccData = function (id) {
        var defer = $q.defer();
        var url = '/api/v1/ledger/account/' + id ;
        console.log("Request: " + url);
        $http.get(url)
            .success(function(res) {
                task.taskList = res;
                defer.resolve(res);
            })
            .error(function (err, status) {
                defer.reject(err);
            });
        return defer.promise;
    };

    return task;
});