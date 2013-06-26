from BaseHTTPServer import HTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler
import json

groupLoadDataHigh = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"1"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}

groupLoadDataLow = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"0"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}
    
groupLoadDataMedium = {"responseStatus":0,"metrics":[{"responseStatus":0,"name":"load.rrd","values":{
    "midterm":[{"date":1372243590000,"value":"0"}],
    "shortterm":[{"date":1372243590000,"value":"0.5"}],
    "longterm":[{"date":1372243590000,"value":"0"}]}}]}   
     
counter = 0

class RRdServerStub (BaseHTTPRequestHandler) :
    def do_GET(self) :
        global counter, shortTermLoad, groupLoadData
        counter += 1
        self.send_response(200)
        self.send_header("Content-type:", "text/html")
        self.wfile.write("\n")
        print("counter:" + str(counter))
        if counter == 1:
            json.dump(groupLoadDataHigh, self.wfile)
        elif counter > 5:
            json.dump(groupLoadDataLow, self.wfile)
        else:
            json.dump(groupLoadDataMedium, self.wfile)
        
    def create_reply(self, shortTermLoad):
        global groupLoadData
        
server = HTTPServer(("localhost", 8181), RRdServerStub)
server.serve_forever()
