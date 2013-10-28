from BaseHTTPServer import HTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler
import json

dataLoadHigh = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"1"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}

dataLoadLow = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"0"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}
    
dataLoadMedium = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"0.5"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}   
     
REQ_GROUP_STATUS =  "monitoring/grouplasthealthstatus"
REQ_SET_LOAD_HIGH = "test/load/high"
REQ_SET_LOAD_LOW = "test/load/low"
REQ_SET_LOAD_MEDIUM = "test/load/medium"

data = dataLoadMedium
 
class MockRRdServer (BaseHTTPRequestHandler) :
    def do_GET(self) :
        global loadValue, data
        
        if REQ_GROUP_STATUS in self.path:
            self.send_response(200)
            self.send_header("Content-type:", "text/html")
            self.wfile.write("\n")
            json.dump(data, self.wfile)
            
        elif REQ_SET_LOAD_HIGH in self.path:
            data = dataLoadHigh
            print ("high")
            self.send_response(200)
            self.send_header("Content-type:", "text/html")
                
        elif REQ_SET_LOAD_LOW in self.path:
            data = dataLoadLow
            print ("low")

            self.send_response(200)
            self.send_header("Content-type:", "text/html")
            
        elif REQ_SET_LOAD_MEDIUM in self.path:
            print ("med")

            data = dataLoadMedium
            self.send_response(200)
            self.send_header("Content-type:", "text/html")
        
        else:
            self.send_response(400)
            self.send_header("Content-type:", "text/html")   
                
server = HTTPServer(("localhost", 8181), MockRRdServer)
server.serve_forever()
