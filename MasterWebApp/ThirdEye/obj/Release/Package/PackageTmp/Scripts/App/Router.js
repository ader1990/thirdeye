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
            var self = this; $.ajax({
                url: "/api/commands",
                data: {
                    Id: null,
                    Value: "Haha",
                },
                type: "POST",
            });
            self._showOperationMenu(51);
            var placeholder = "#flowers";
            self._showPlaceholder(placeholder);
            $(".flower-menu a").css("z-index", 0);
            $(".link").removeClass("selected");
            $(".flower-menu a").addClass("selected");
            $(".flower-menu a").css("z-index", 100);
            $.ajax({
                url: "/api/flowers",
                type: "POST",
                data: {
                    Name: "Flower Power",
                }
            }).success(function () {
                $.ajax({
                    url: "/api/flowers",
                    type: "GET"
                }).success(function (data) {
                    console.log(data);

                    for (var i in data) {
                        $.ajax({
                            url: "/api/flowers/" + data[i].Id,
                            type: "DELETE"
                        });
                        $("#flowers").append("<p>" + data[i].Name + "</p>");
                    }
                });
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