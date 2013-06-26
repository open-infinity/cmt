from BaseHTTPServer import HTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler
import json

me = {"name": "ryanne"}

class MockRRdServer (BaseHTTPRequestHandler) :

    def do_GET(self) :

        if self.path == "/monitoring/groupmetrictypes/?groupName=cluster_1097" :
            #send response code:
            self.send_response(200)
            #send headers:
            self.send_header("Content-type:", "text/html")
            # send a blank line to end headers:
            self.wfile.write("\n")

            #send response:
            json.dump(me, self.wfile)

server = HTTPServer(("localhost", 8003), MockRRdServer)

server.serve_forever()
