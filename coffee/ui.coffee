$ ->
  console.log "ui"

  body = $("body")

  button = $("<button/>").appendTo body
  button.text "button"
