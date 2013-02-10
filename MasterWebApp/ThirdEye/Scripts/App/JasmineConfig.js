// Set the require.js configuration for your application.
require.config({
    deps: ["JasmineMain"],
    paths: {
        // Use the underscore build of Lo-Dash to minimize incompatibilities.
        jquery: '../libs/jquery/jquery',
        underscore: '../libs/underscore/underscore-amd',
        jasmine: '../libs/jasmine/jasmine',
        jasmineamd: '../libs/jasmine/jasmine-amd',
        jasminehtml: '../libs/jasmine/jasmine-html',
        backbone: '../libs/backbone/backbone-amd',
        Song: '../jasmine-samples/Song',
        Player: '../jasmine-samples/Player',
        PlayerSpec: '../jasmine-samples/PlayerSpec',
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