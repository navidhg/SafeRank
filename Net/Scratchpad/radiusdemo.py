import requests

# Lat/long in degrees, radius in km
latitude = 51.533157
longitude = -0.141392
radius = 0.3

requestUrl = "http://localhost:5000/data/" + str(latitude) + "/" + str(longitude) + "/" + str(radius)
r = requests.get(requestUrl)

print r.text