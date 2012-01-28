(function() {

  $(function() {
    var body, button;
    console.log("ui");
    body = $("body");
    button = $("<button/>").appendTo(body);
    return button.text("button");
  });

}).call(this);
