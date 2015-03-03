import requests

# Lat/long in degrees, radius in km
latitude = 51.533157
longitude = -0.141392
radius = 0.3

# requestUrl = "http://localhost:5000/data/" + str(latitude) + "/" + str(longitude) + "/" + str(radius)

requestUrl = "http://localhost:5000/data/test/3"

r = requests.get(requestUrl)

print r.text