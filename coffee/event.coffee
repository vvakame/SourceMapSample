$ ->
  console.log "event"

  button = $("button")
  button.click ->
    console.log "button pressed!"
