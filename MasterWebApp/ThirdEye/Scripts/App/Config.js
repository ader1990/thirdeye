// Set the require.js configuration for your application.
require.config({
    deps: ["Main"],
    paths: {
        // Use the underscore build of Lo-Dash to minimize incompatibilities.
        jquery: '../libs/jquery/jquery',
        underscore: '../libs/underscore/underscore-amd',
        backbone: '../libs/backbone/backbone-amd'
        // Put additional paths here.
    },
    map: {
        // Ensure Lo-Dash is used instead of underscore.
        //"*": { "underscore": "lodash" }

        // Put additional maps here.
    },
    shim: {
        // Put shims here.
    }

});