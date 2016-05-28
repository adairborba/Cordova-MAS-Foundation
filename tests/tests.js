cordova.define("com.ca.apim-tests.tests", function(require, exports, module) {


/**
 *Below are the manual tests for the MASPlugin.
 *
 *Note: Once the start is successful, please make sure that device is deregistered and the app is clean and relaunched before running another
 * test. As we found some issues related to time out to numerous async calls.
 */


exports.defineManualTests = function(contentEl, createActionButton) {

    createActionButton('MAS.start()', function() {
        var name = 'MAS.start()';
        var mas = new MASPlugin.MAS();
        mas.start(startSuccessHandler, errorHandler);
        function startSuccessHandler(result) {
            console.log(name + ' Success: ' + result);
        }
        function errorHandler(error) {
            console.log(name + ' Failed: ' + result);
        }
    });


    createActionButton('MASUser.loginWithUsernameAndPassword()', function() {
        var name = 'MASUser.loginWithUsernameAndPassword()';
        var masUser = new MASPlugin.MASUser();
        masUser.loginWithUsernameAndPassword(startSuccessHandler, errorHandler, 'sarek', '7layer');
        function startSuccessHandler(result) {
            console.log(name + ' Success: ' + result);
        }
        function errorHandler(error) {
            console.log(name + ' Failed: ' + result);
        }
    });

    createActionButton('MASDevice.deregister()', function() {
        var name = 'MASDevice.deregister()';
        var masDevice = new MASPlugin.MASDevice();
        masDevice.deregister(startSuccessHandler, errorHandler);
        function startSuccessHandler(result) {
            console.log(name + ' - Success: ' + result);
        }
        function errorHandler(error) {
            console.log(name + ' - Failed: ' + error);
        }
    });

    createActionButton('MAS.getFromPath()', function() {
        var name = 'MAS.getFromPath()';
        var mas = new MASPlugin.MAS();
        var parameters = {"operation": "listProducts"};
        var headers = {"headerName" : "headerValue"};
        mas.getFromPath(startSuccessHandler, errorHandler, '/protected/resource/products'
            , parameters, headers, 0,  0);
        function startSuccessHandler(result) {
            console.log(name + ' - Success: ' + JSON.stringify(result));
        }
        function errorHandler(error) {
            console.log(name + ' - Failed: ' + error.errorInfo);
        }
    });




    createActionButton('Valid Registration Type', function() {
                       
                       var MAS = new MASPlugin.MAS();
                       MAS.deviceRegistrationType(null,null,'0');
                       MAS.start(startSuccessHandler,errorHandler);
                       
                       function startSuccessHandler(result)
                       {
                       console.log("Valid Registration Type Test Pass");
                       
                       }
                       function errorHandler(error)
                       {
                       console.log("Valid Registration Type Test Fail");
                       
                       }
                       });
    
    createActionButton('Deregister Device', function() {
                       
                       var MASDevice = new MASPlugin.MASDevice();
                       MASDevice.deregister(deregisterSuccessHandler,deregisterErrorHandler);
                       function deregisterSuccessHandler(result)
                       {
                       console.log("Deregister Device Success");
                       
                       }
                       function deregisterErrorHandler(error)
                       {
                       console.log("Deregister Device Failure");
                       
                       }
                       });
    
    createActionButton('Invalid Registration Type', function() {
                       
                       var MAS = new MASPlugin.MAS();
                       MAS.deviceRegistrationType(null,null,'6');
                       MAS.start(startSuccessHandler,errorHandler);
                       
                       function startSuccessHandler(result)
                       {
                       console.log("Invalid Registration Type Fail");
                       
                       }
                       function errorHandler(error)
                       {
                       console.log("Invalid Registration Type Pass");
                       
                       }
                       });
    
    createActionButton('Valid config filename', function() {
                       
                       var MAS = new MASPlugin.MAS();
                       MAS.configFileName(null,null,"msso_config");
                       MAS.start(startSuccessHandler,errorHandler);
                       
                       function startSuccessHandler(result)
                       {
                       console.log("Valid config filename Pass");
                       
                       }
                       function errorHandler(error)
                       {
                       console.log("Valid config filename Fail");
                       
                       }
                       });
    
    
    createActionButton('Invalid config filename', function() {
                       
                       var MAS = new MASPlugin.MAS();
                       MAS.configFileName(null,null,"msso_config1");
                       MAS.start(startSuccessHandler,errorHandler);
                       
                       function startSuccessHandler(result)
                       {
                       console.log("Invalid config filename Fail");
                       
                       }
                       function errorHandler(error)
                       {
                       console.log("Invalid config filename Pass");
                       
                       }
                       });
    
};

});
