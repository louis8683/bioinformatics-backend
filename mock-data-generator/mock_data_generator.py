import random
import time
from datetime import datetime, timedelta, timezone
import json
import requests
from faker import Faker
from pprint import pprint


# Configurations
DATA_SERVICE_URL = "http://localhost:9000/api/data"
SESSION_DURATION_S = 3600
ENTRY_INTERVAL_S = 5
START_LATITUDE = 40.7128
START_LONGITUDE = -74.0060
# Generate current time in ISO 8601 format with a specific offset
offset = timedelta(hours=-5)  # EST, -05:00 offset
START_TIME = datetime.now(timezone(offset))

# Session configuration
USER_ID = Faker().uuid4()
DEVICE_MAC = Faker().mac_address()
GROUP_ID = 10
SESSION_DESC = "This is a mock session generated with Python."

# Random walk - 1% of range for air quality
random.seed(START_TIME.isoformat())
latitude_walk = 8.3e-5
longitude_walk = 6.3e-5
co_walk = 1 # 0-100 PPM
co_range = (0, 100)
pm2_5_walk = 5 # 0-500 ug/m3
pm2_5_range = (0, 500)
temperature_walk = 0.5 # -15 to 35 C
temperature_range = (-15, 35)
humidity_walk = 0.01 # 0-1 
humidity_range = (0, 1)

# Request configuration
# HEADERS = {"Content-Type": "application/json"}  # Ensure correct header


class Session:
    def __init__(self, sessionId, userId, groupId, sensorMac, startTimestamp, endTimestamp, description):
        self.sessionId = sessionId
        self.userId = userId
        self.groupId = groupId
        self.sensorMac = sensorMac
        self.startTimestamp = startTimestamp
        self.endTimestamp = endTimestamp
        self.description = description

    
    def __str__(self):
        return f"session[{self.sessionId}], user[{self.userId}], group[{self.groupId}], mac[{self.sensorMac}], startTime[{self.startTimestamp}], endTime[{self.endTimestamp}], desc[{self.description}]"


    @staticmethod
    def fromJson(data: dict):
        return Session( data["id"], data["userId"],data["groupId"], 
                       data["sensorMac"], data["startTimestamp"], 
                       data["endTimestamp"], data["description"])
        


class Entry:
    def __init__(self, userId, sessionId, timestamp, latitude, longitude, coLevel, pm2_5Level, temperature, humidity):
        self.userId = userId
        self.sessionId = sessionId
        self.timestamp = timestamp
        self.latitude = latitude
        self.longitude = longitude
        self.coLevel = coLevel
        self.pm2_5Level = pm2_5Level
        self.temperature = temperature
        self.humidity = humidity


    def toDict(self) -> dict:
        return {
            "userId": self.userId,
            "sessionId": self.sessionId,
            "timestamp": self.timestamp,
            "latitude": self.latitude, 
            "longitude": self.longitude,
            "coLevel": self.coLevel,
            "pm2_5Level": self.pm2_5Level,
            "temperature": self.temperature,
            "humidity": self.humidity 
        }
    

    def __str__(self):
        return f"Entry: user[{self.userId}], latlong[{self.latitude},{self.longitude}], CO[{self.coLevel}], PM2.5[{self.pm2_5Level}], temperature[{self.temperature}], humidity[{self.humidity}]"


def createSession(userId=USER_ID, sensorMac=DEVICE_MAC) -> Session:
    # create the session
    response = requests.post(f"{DATA_SERVICE_URL}/createSession", json={
        "userId": userId,
        "sensorMac": sensorMac
    })
    if response.status_code == 200:
        sessionId = int(response.text)
        print("Session created.")
    else:
        raise Exception(f"Failed to create a new session ({response}). Aborting...")
    
    # retrieve it through API
    response = requests.get(DATA_SERVICE_URL + "/session/" + str(sessionId))
    if response.status_code == 200:
        print("Session verified.")
        session = Session.fromJson(response.json())
    else:
        raise Exception("Failed to verify the existence of the new session. Aborting...")

    return session


def reflect(val, range):
    """
    A helper function to reflect a value back into its range.
    """
    if val < range[0]:
        val = range[0] + (range[0] - val)
    elif val > range[1]:
        val = range[1] - (val - range[0])
    
    # make sure the val in still in range
    if val < range[0] or val > range[1]:
        raise Exception("Value cannot be reflect since it's too far from the edges of the range.")

    return val


def generate_data(sessionId, userId=USER_ID) -> list[Entry]:
    data = []
    latitude = START_LATITUDE
    longitude = START_LONGITUDE
    coLevel = 0
    pm2_5 = 0
    temperature = 0
    humidity = 0
    for i in range(SESSION_DURATION_S // ENTRY_INTERVAL_S):
        # random walk
        latitude += latitude_walk * random.uniform(-1, 1)
        longitude += longitude_walk * random.uniform(-1, 1)
        coLevel += co_walk * random.uniform(-1, 1)
        pm2_5 += pm2_5_walk * random.uniform(-1, 1)
        temperature += temperature_walk * random.uniform(-1, 1)
        humidity += humidity_walk * random.uniform(-1, 1)

        # reflect into range
        coLevel = reflect(coLevel, co_range)
        pm2_5 = reflect(pm2_5, pm2_5_range)
        temperature = reflect(temperature, temperature_range)
        humidity = reflect(humidity, humidity_range)

        # add to dataset
        data.append(Entry(
            userId, sessionId, 
            (START_TIME + timedelta(seconds=ENTRY_INTERVAL_S * i)).isoformat(),
            latitude, longitude, coLevel, pm2_5, temperature, humidity
        ))

    return data


def assertEntryDictEqual(dict1: dict, dict2: dict, tolerance: float):
    """
    Compare dicts with tolerance to floats.
    """
    assert(set(dict1.keys()) == set(dict2.keys()))
    for key in dict1.keys():
        if type(dict1[key]) == float:
            assert(abs(dict1[key] - dict2[key]) <= tolerance)
        elif key == "timestamp":
            assert(datetime.fromisoformat(dict1[key]) == datetime.fromisoformat(dict1[key]))
        else:
            assert(dict1[key] == dict2[key])


def batchSave(data):
    # convert each element to a dict
    data_dict = [entry.toDict() for entry in data]

    # Send request
    response = requests.post(f"{DATA_SERVICE_URL}/entry/batchSave", json=data_dict)
    if response.status_code == 200:
        entry_ids = response.json()
        print("Batch save success.")
    else:
        raise Exception(f"Failed to batch save data ({response}).")

    # Verify request
    for i, entry_id in enumerate(entry_ids):
        response = requests.get(f"{DATA_SERVICE_URL}/entry/{entry_id}")
        if response.status_code == 200:
            response_dict = response.json()
            del response_dict["id"]
            try:
                assertEntryDictEqual(response_dict, data_dict[i], 1e-3)
            except AssertionError:
                raise Exception(f"Failed to verify entry id {entry_id}. Content mismatched: {response_dict} != {data_dict[i]}")
        else:
            raise Exception(f"Failed to verify entry id {entry_id} ({response}).")
    print("Batch save verified.")


def generate():

    # Create session
    session = createSession()
    print(session)

    # Generate data
    data = generate_data(sessionId=session.sessionId)
    print(f"data length: {len(data)}")
    print(f"first 10 entries: ")
    for entry in data[:10]:
        print(entry)

    # Batch save
    batchSave(data)
    

if __name__ == "__main__":
    generate()