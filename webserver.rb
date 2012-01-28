require 'webrick'

document_root = './'

server = WEBrick::HTTPServer.new({
  :DocumentRoot => document_root,
  :BindAddress => '127.0.0.1',
  :Port => 10080
})

server.mount_proc("/compiled.js") {|req, res|

  # res["X-SourceMap"] = "source-map.json"
  res["X-SourceMap"] = "http://localhost:10080/source-map.json"

  filename = File.join(document_root, req.path)
  open(filename) do |file|
    res.body = file.read
  end
  res.content_length = File.stat(filename).size
  res.content_type = "application/json"
}

['INT', 'TERM'].each {|signal|
  Signal.trap(signal){ server.shutdown }
}

server.start

