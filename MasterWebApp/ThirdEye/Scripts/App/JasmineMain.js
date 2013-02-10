require([
  "jasmineamd", "jasminehtml","PlayerSpec"
],
  
function (exported,exportedHtml) {
    var jasmine = exported.jasmine;

    var jasmineEnv = jasmine.getEnv();
    jasmineEnv.updateInterval = 1000;

    var htmlReporter = new exportedHtml.HtmlReporter();

    jasmineEnv.addReporter(htmlReporter);

    jasmineEnv.specFilter = function (spec) {
        return htmlReporter.specFilter(spec);
    };
    jasmineEnv.execute();

});