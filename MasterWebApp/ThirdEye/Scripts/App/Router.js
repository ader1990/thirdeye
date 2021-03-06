﻿define([
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
            $(buttons).die();
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
    $("html").live("keydown", function (event) {
        var key = event.which;
        switch (key) {
            case 65: sendCommand("F", "FORWARD");
                event.preventDefault();
                break;
            case 66: sendCommand("B", "BACKWARD");
                event.preventDefault();
                break;
            case 67: sendCommand("L", "LEFT");
                event.preventDefault(); break;
            case 68: sendCommand("R", "RIGHT");
                event.preventDefault(); break;
            case 38: sendCommand("F", "FORWARD");
                event.preventDefault();
                break;
            case 40: sendCommand("B", "BACKWARD");
                event.preventDefault();
                break;
            case 37: sendCommand("L", "LEFT");
                event.preventDefault(); break;
            case 32: sendCommand("S", "STOP");
                event.preventDefault(); break;
            case 39: sendCommand("R", "RIGHT");
                event.preventDefault(); break;
            default: break;
        }

        console.log(key);
    });
    var sendCommand = function (data, commandText) {
        $.ajax({
            data: {
                Value: data
            },
            url: "/api/commands",
            type: "POST"
        }).success(function () {
            $(".app-bar").prepend("<div style='float:left;width:100%;position:relative'>" + "> " + commandText + "</div>");
        })
    }
    return Router;

});