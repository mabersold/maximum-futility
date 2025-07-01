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
    {"name": "Baltimore", "label": "baltimore"},
    {"name": "Boston", "label": "boston"},
    {"name": "Buffalo", "label": "buffalo"},
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
    {"name": "Indianapolis", "label": "indianapolis"},
    {"name": "Jacksonville", "label": "jacksonville"},
    {"name": "Kansas City", "label": "kansas-city"},
    {"name": "Las Vegas", "label": "las-vegas"},
    {"name": "Los Angeles", "label": "los-angeles"},
    {"name": "Memphis", "label": "memphis"},
    {"name": "Miami", "label": "miami"},
    {"name": "Milwaukee", "label": "milwaukee"},
    {"name": "Minneapolis", "label": "minneapolis"},
    {"name": "Montreal", "label": "montreal"},
    {"name": "Nashville", "label": "nashville"},
    {"name": "New Orleans", "label": "new-orleans"},
    {"name": "New York", "label": "new-york"},
    {"name": "Oklahoma City", "label": "oklahoma-city"},
    {"name": "Orlando", "label": "orlando"},
    {"name": "Ottawa", "label": "ottawa"},
    {"name": "Philadelphia", "label": "philadelphia"},
    {"name": "Phoenix", "label": "phoenix"},
    {"name": "Pittsburgh", "label": "pittsburgh"},
    {"name": "Portland", "label": "portland"},
    {"name": "Portsmouth", "label": "portsmouth"},
    {"name": "Providence", "label": "providence"},
    {"name": "Quebec", "label": "quebec"},
    {"name": "Raleigh", "label": "raleigh"},
    {"name": "Rochester", "label": "rochester"},
    {"name": "Sacramento", "label": "sacramento"},
    {"name": "Salt Lake City", "label": "salt-lake-city"},
    {"name": "San Antonio", "label": "san-antonio"},
    {"name": "San Diego", "label": "san-diego"},
    {"name": "San Francisco Bay", "label": "san-francisco-bay"},
    {"name": "Seattle", "label": "seattle"},
    {"name": "Sheboygan", "label": "sheboygan"},
    {"name": "Sioux City", "label": "sioux-city"},
    {"name": "St. Louis", "label": "st-louis"},
    {"name": "Syracuse", "label": "syracuse"},
    {"name": "Tampa Bay", "label": "tampa-bay"},
    {"name": "Toronto", "label": "toronto"},
    {"name": "Tri-Cities", "label": "tri-cities"},
    {"name": "Tulsa", "label": "tulsa"},
    {"name": "Vancouver", "label": "vancouver"},
    {"name": "Washington", "label": "washington"},
    {"name": "Waterloo", "label": "waterloo"},
    {"name": "Winnipeg", "label": "winnipeg"}
]

BASE_URL = 'localhost:8080'

league_labels_to_ids = {}
metro_labels_to_ids = {}
franchise_labels_to_ids = {}

conn = http.client.HTTPConnection(BASE_URL)

def populate_dict_with_existing(object: str, dictionary_to_update: dict, query_params: str = None):
    conn.request("GET", f"/api/v1/{object}?{query_params}")
    response = conn.getresponse()
    if response.status != 200:
        print(f"Failed to load {object} from API")
        return

    response_body = json.loads(response.read())    

    for item in response_body:
        id = item['id']
        label = item['label']
        dictionary_to_update[label] = id

def populate_leagues():
    print("Populating Leagues...")
    
    # Find already existing leagues and populate dictionary
    populate_dict_with_existing("leagues", league_labels_to_ids)

    # Iterate through our dictionary to create any leagues that haven't already been created
    skipped = 0
    for league in leagues:
        if league_labels_to_ids.get(league.get('label')):
            skipped += 1
            league['id'] = league_labels_to_ids.get(league.get('label'))
            continue

        conn.request("POST", "/api/v1/leagues", body=json.dumps(league), headers={'Content-Type': 'application/json'})
        response = conn.getresponse()

        if response.status != 200:
            print(f"Failed to create league {league['name']}")
            continue

        response_body = json.loads(response.read())
        league_id = response_body['id']
        label = response_body['label']
        league_labels_to_ids[label] = league_id
        league['id'] = league_id
    if skipped > 0:
        print(f'Skipped creating {skipped} metros that already existed')

def populate_metros():
    print("Populating metros")
    
    # Find already existing metros and populate dict
    populate_dict_with_existing('metros', metro_labels_to_ids)

    # Create any new metros that aren't already created
    skipped = 0
    for metro in metros:
        if metro_labels_to_ids.get(metro.get('label')):
            skipped += 1
            continue

        conn.request("POST", "/api/v1/metros", body=json.dumps(metro), headers={'Content-Type': 'application/json'})

        response = conn.getresponse()

        if response.status != 200:
            print(f"Failed to create metro {metro['name']}")
            continue

        response_body = json.loads(response.read())
        metro_id = response_body['id']
        label = response_body['label']
        metro_labels_to_ids[label] = metro_id
    if skipped > 0:
        print(f'Skipped creating {skipped} metros that already existed')

def populate_franchises():
    print("Populating franchises")

    franchises_directory = "seed_data/franchises"
    for league in leagues:
        # Get existing franchises for this league from the API
        populate_dict_with_existing('franchises', franchise_labels_to_ids, f"league_id={league.get('id')}")

        path = f"{franchises_directory}/{league['label']}"

        print(f"Populating franchises from {path}")

        if not os.path.isdir(path):
            continue

        files = os.listdir(path)

        skipped = 0
        for file in files:
            with open(f"{path}/{file}", 'r') as f:
                content = f.read()

            request_dict = json.loads(content)

            if franchise_labels_to_ids.get(request_dict.get('label')):
                skipped += 1
                continue

            # Replace the league_id from a label to the actual league ID
            league_label = request_dict['league_id']
            request_dict['league_id'] = league_labels_to_ids[league_label]

            # Remove the id from the request (API will reject otherwise)
            if request_dict.get('id'):
                request_dict.pop('id')

            # For each chapter, replace the league_id and metro_id strings with ints
            for chapter in request_dict['chapters']:
                chapter['league_id'] = league_labels_to_ids[league_label]
                if chapter['team_name'] is None:
                    chapter['team_name'] = request_dict['name']
                chapter['metro_id'] = metro_labels_to_ids[chapter['metro_id']]
                # Some chapters have labels in them, remove them for now
                if chapter.get('label'):
                    chapter.pop('label')

            request_payload = json.dumps(request_dict)

            conn.request("POST", "/api/v1/franchises", body=request_payload, headers={'Content-Type': 'application/json'})

            response = conn.getresponse()

            if response.status != 200:
                print(f"Failed to create franchise {request_dict['name']}")
                continue
            else:
                print(f"Successfully created franchise {request_dict['name']}")

            response_body = json.loads(response.read())
            franchise_id = response_body['id']
            label = response_body['label']
            franchise_labels_to_ids[label] = franchise_id
        if skipped > 0:
            print(f'Skipped creating {skipped} franchises in league {league.get("name")} that already existed')

def populate_seasons():
    pass

populate_leagues()
populate_metros()
populate_franchises()
print("Done!")