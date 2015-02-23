# Post client to make requests to demo server
import datetime
import json
import requests
import time
 
while 1:
	data = {"userid": 1, "sampletime": str(datetime.datetime.now()), "rating": 3, "longitude": -0.144267, "latitude": 51.540772, "brightness": 314.0}
	# data = {"userid":1,"sampletime":"2015-02-05 14:47:14","rating":4,"longitude":"-0.1323937","latitude":"51.5231853","brightness":"13.0"}
	headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
	url = "http://localhost:5000/upload"
	# url = "http://178.62.32.221:5000/upload"

	r = requests.post(url, data=json.dumps(data), headers=headers)
	print r.text
	time.sleep(3)
