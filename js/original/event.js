(function() {

  $(function() {
    var button;
    console.log("event");
    button = $("button");
    return button.click(function() {
      return console.log("button pressed!");
    });
  });

}).call(this);
