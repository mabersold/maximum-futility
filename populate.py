import json
import http.client
import os

# Leagues data to populate
leagues = [
    {"name": "MLB", "sport": "Baseball", "label": "mlb"},
    {"name": "NFL", "sport": "Football", "label": "nfl"},
    {"name": "NBA", "sport": "Basketball", "label": "nba"},
    {"name": "NHL", "sport": "Hockey", "label": "nhl"},
    {"name": "WNBA", "sport": "Basketball", "label": "wnba"},
    {"name": "MLS", "sport": "Soccer", "label": "mls"}
]

# Metro areas data to populate
metros = [
    {"name": "Anderson", "label": "anderson"},
    {"name": "Atlanta", "label": "atlanta"},
    {"name": "Austin", "label": "austin"},
    {"name": "Boston", "label": "boston"},
    {"name": "Baltimore", "label": "baltimore"},
    {"name": "Calgary", "label": "calgary"},
    {"name": "Charlotte", "label": "charlotte"},
    {"name": "Chicago", "label": "chicago"},
    {"name": "Cincinnati", "label": "cincinnati"},
    {"name": "Cleveland", "label": "cleveland"},
    {"name": "Columbus", "label": "columbus"},
    {"name": "Dallas", "label": "dallas"},
    {"name": "Decatur", "label": "decatur"},
    {"name": "Denver", "label": "denver"},
    {"name": "Detroit", "label": "detroit"},
    {"name": "Edmonton", "label": "edmonton"},
    {"name": "Fort Wayne", "label": "fort-wayne"},
    {"name": "Grand Rapids", "label": "grand-rapids"},
    {"name": "Green Bay", "label": "green-bay"},
    {"name": "Hartford", "label": "hartford"},
    {"name": "Houston", "label": "houston"},
    {"name": "Indianapolis", "label": "ind"},
    {"name": "Jacksonville", "label": "jacksonville"},
    {"name": "Kansas City", "label": "kansas-city"},
    {"name": "Las Vegas", "label": "las-vegas"},
    {"name": "Los Angeles", "label": "la"},
    {"name": "Memphis", "label": "memphis"},
    {"name": "Miami", "label": "miami"},
    {"name": "Milwaukee", "label": "milwaukee"},
    {"name": "Minneapolis", "label": "minn"},
    {"name": "Montreal", "label": "montreal"},
    {"name": "Nashville", "label": "nashville"},
    {"name": "New Orleans", "label": "new-orleans"},
    {"name": "New York", "label": "ny"},
    {"name": "Oklahoma City", "label": "okc"},
    {"name": "Orlando", "label": "orlando"},
    {"name": "Ottawa", "label": "ottawa"},
    {"name": "Philadelphia", "label": "philadelphia"},
    {"name": "Phoenix", "label": "phoenix"},
    {"name": "Pittsburgh", "label": "pittsburgh"},
    {"name": "Portland", "label": "pdx"},
    {"name": "Portsmouth", "label": "portsmouth"},
    {"name": "Providence", "label": "providence"},
    {"name": "Quebec", "label": "quebec"},
    {"name": "Raleigh", "label": "raleigh"},
    {"name": "Rochester", "label": "rochester"},
    {"name": "Sacramento", "label": "sacramento"},
    {"name": "Salt Lake City", "label": "slc"},
    {"name": "San Antonio", "label": "san-antonio"},
    {"name": "San Diego", "label": "san-diego"},
    {"name": "San Francisco Bay", "label": "sf"},
    {"name": "Seattle", "label": "seattle"},
    {"name": "Sheboygan", "label": "sheboygan"},
    {"name": "Sioux City", "label": "sioux-city"},
    {"name": "St. Louis", "label": "stl"},
    {"name": "Syracuse", "label": "syracuse"},
    {"name": "Tampa Bay", "label": "tb"},
    {"name": "Toronto", "label": "toronto"},
    {"name": "Tri-Cities", "label": "tri-cities"},
    {"name": "Tulsa", "label": "tulsa"},
    {"name": "Vancouver", "label": "vancouver"},
    {"name": "Washington", "label": "dc"},
    {"name": "Winnipeg", "label": "winnipeg"}
]

# Populate Leagues
print("Populating Leagues...")

BASE_URL = 'localhost:8080'

league_labels_to_ids = {}

print("Populating leagues")
for league in leagues:
    conn = http.client.HTTPConnection(BASE_URL)
    conn.request("POST", "/api/v1/leagues", body=json.dumps(league), headers={'Content-Type': 'application/json'})
    response = conn.getresponse()

    if response.status != 200:
        print(f"Failed to create league {league['name']}")
        continue

    response_body = json.loads(response.read())
    league_id = response_body['id']
    label = response_body['label']
    league_labels_to_ids[label] = league_id

metro_labels_to_ids = {}
print("Populating metros")
for metro in metros:
    conn = http.client.HTTPConnection(BASE_URL)
    conn.request("POST", "/api/v1/metros", body=json.dumps(metro), headers={'Content-Type': 'application/json'})

    response = conn.getresponse()

    if response.status != 200:
        print(f"Failed to create metro {metro['name']}")
        continue

    response_body = json.loads(response.read())
    metro_id = response_body['id']
    label = response_body['label']
    metro_labels_to_ids[label] = metro_id

print("Populating franchises")

franchise_labels_to_ids = {}
franchises_directory = "seed_data/franchises"
for league in leagues:
    path = f"{franchises_directory}/{league['label']}"

    print(f"Populating franchises from {path}")

    if not os.path.isdir(path):
        continue

    files = os.listdir(path)

    for file in files:
        print(f"Filename is {file}")

        with open(f"{path}/{file}", 'r') as f:
            content = f.read()

        request_dict = json.loads(content)

        # Replace the league_id
        league_label = request_dict['league_id']
        request_dict['league_id'] = league_labels_to_ids[league_label]

        # For each chapter, replace the league_id and metro_id
        for chapter in request_dict['chapters']:
            chapter['league_id'] = league_labels_to_ids[league_label]
            chapter['metro_id'] = metro_labels_to_ids[chapter['metro_id']]

        request_payload = json.dumps(request_dict)

        conn = http.client.HTTPConnection(BASE_URL)
        conn.request("POST", "/api/v1/franchises", body=request_payload, headers={'Content-Type': 'application/json'})

        response = conn.getresponse()

        if response.status != 200:
            print(f"Failed to create franchise {request_dict['name']}")
            continue

        response_body = json.loads(response.read())
        franchise_id = response_body['id']
        label = response_body['label']
        franchise_labels_to_ids[label] = franchise_id

print("Done!")