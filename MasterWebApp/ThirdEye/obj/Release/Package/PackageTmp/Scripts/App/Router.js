define([
  // Application.
  "App",
  'backbone'
],

function (app, Backbone) {
    var Router = Backbone.Router.extend({
        routes: {
            "/": "index",
            "": "index",
            "#": "index",
        },
        index: function () {
            var self = this;
            //$.ajax({
            //    url: "/api/commands",
            //    data: {
            //        Id: null,
            //        Value: "Haha",
            //    },
            //    type: "POST",
            //});
            self._showOperationMenu(51);
            //$.ajax({
            //    url: "/api/flowers",
            //    type: "POST",
            //    data: {
            //        Name: "Flower Power",
            //    }
            //}).success(function () {
            //    $.ajax({
            //        url: "/api/flowers",
            //        type: "GET"
            //    }).success(function (data) {
            //        console.log(data);

            //    });
            //});

            self._bindEvents("button.default");
        },
        _bindEvents: function (buttons) {
            var self = this;
            $(buttons).live("click", function () {
                var commandValue = $(this).text().replace(" ", "").substring(0, 1).toUpperCase();
                console.log(commandValue);
                var commandText = $(this).text().toUpperCase();
                $.ajax({
                    data: {
                        Value: commandValue
                    },
                    url: "/api/commands",
                    type: "POST"
                }).success(function () {
                    $(".app-bar").prepend("<div style='float:left;width:100%;position:relative'>" + "> " + commandText + "</div>");
                })
            });
        },
        _showOperationMenu: function (offset) {
            var menuOperation = "#menu_operation";
            $(menuOperation).show();
            $(menuOperation).css("margin-top", offset + "px");
        },
        _showPlaceholder: function (placeholder) {
            var self = this;
            $(placeholder).parent().children().hide();
            $(placeholder).show();
        }
    });

    return Router;

});