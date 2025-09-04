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
franchise_lookups = []

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

def lookup_franchise_id(label: str, league: str, year: int):
    label = label.lower()

    matches = [team for team in franchise_lookups if team["name"].lower() == label and team["league"].lower() == league.lower()]

    if len(matches) == 1:
        return matches[0]["id"]
    
    for team in matches:
        if team["start_year"] <= year and (team["end_year"] is None or team["end_year"] >= year):
            return team["id"]

    return None

def replace_labels_in_standings(group: dict, league: str, year: int):
    if group.get('sub_groups'):
        for sub_group in group.get('sub_groups'):
            replace_labels_in_standings(sub_group, league, year)
    if group.get('results'):
        for result in group.get('results'):
            # Special cases: temporarily merged franchises in the NFL
            if league == 'nfl' and year == 1943 and result['label'] == 'steagles':
                result['franchise_id'] = franchise_labels_to_ids['eagles']
                result['merged_franchise_id'] = franchise_labels_to_ids['steelers']
            elif league == 'nfl' and year == 1944 and result['label'] == 'card-pitt':
                result['franchise_id'] = franchise_labels_to_ids['cardinals-nfl']
                result['merged_franchise_id'] = franchise_labels_to_ids['steelers']
            elif league == 'nfl' and result['label'] == 'yanks' and year == 1945:
                result['franchise_id'] = franchise_labels_to_ids['yanks']
                result['merged_franchise_id'] = franchise_labels_to_ids['dodgers-nfl']
            else:
                # Almost all results will go here
                franchise_id = lookup_franchise_id(result.get('label'), league, year)
                result['franchise_id'] = franchise_id

            result.pop('label')

def replace_labels_in_postseason(postseason: dict, league: str, year: int):
    if not postseason:
        return

    for round in postseason.get('rounds'):
        for matchup in round.get('matchups'):
            winner_id = lookup_franchise_id(matchup.get('winner').get('label'), league, year)
            loser_id = lookup_franchise_id(matchup.get('loser').get('label'), league, year)
            matchup.get('winner')['franchise_id'] = winner_id
            matchup.get('winner').pop('label')
            matchup.get('loser')['franchise_id'] = loser_id
            matchup.get('loser').pop('label')

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

            chapter_labels = []

            if franchise_labels_to_ids.get(request_dict.get('label')):
                # Store in franchise_lookups so we can convert labels to ids in seasons later on
                for c in request_dict.get('chapters'):
                    franchise_lookups.append({
                        "name": c.get("label", request_dict.get("label")),
                        "league": c.get("league_id"),
                        "start_year": c.get("start_year"),
                        "end_year": c.get("end_year", None),
                        "id": franchise_labels_to_ids[request_dict.get('label')]
                    })

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
                chapter_labels.append(chapter.get("label", request_dict.get("label")))
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
            for c in request_dict.get('chapters'):
                franchise_lookups.append({
                    "name": chapter_labels.pop(0),
                    "league": league_label,
                    "start_year": c.get("start_year"),
                    "end_year": c.get("end_year", None),
                    "id": franchise_labels_to_ids[request_dict.get('label')]
                })

        if skipped > 0:
            print(f'Skipped creating {skipped} franchises in league {league.get("name")} that already existed')

def populate_seasons():
    print("Populating seasons")

    seasons_directory = "seed_data/seasons"
    season_lookups = {}

    for league in leagues:
        skipped = 0
        
        # Get existing seasons for this league
        league_id = league_labels_to_ids.get(league.get('label'))
        conn.request("GET", f"/api/v1/seasons?league_id={league_id}")
        response = conn.getresponse()
        if response.status != 200:
            print(f"Failed to load {object} from API")
            return

        seasons_response = json.loads(response.read())
        
        # Populate season_lookups so we can quickly validate whether a season already exists
        for season in seasons_response:
            season_lookups[f"{season['league_id']}-{season['start_year']}"] = season['name']
            pass

        path = f"{seasons_directory}/{league['label']}"
        print(f"Populating seasons from {path}")
        if not os.path.isdir(path):
            continue

        files = os.listdir(path)

        # Iterate through all files in this league's directory
        for file in files:
            with open(f"{path}/{file}", 'r') as f:
                # TODO: Temporarily merged franchises need a special case
                # TODO: Find a solution for AFL/NFL seasons
                
                content = f.read()
                request_dict = json.loads(content)
                
                # Iterate through the standings, replace labels with a franchise ID
                league = request_dict.get('league')
                league_id = league_labels_to_ids.get(league)
                start_year = request_dict.get('start_year')

                # Skip if the season has already been created
                key = f"{league_id}-{request_dict['start_year']}"
                if season_lookups.get(key):
                    skipped += 1
                    continue

                # Prepare request by replacing labels with their proper ids
                request_dict["league_id"] = league_id
                request_dict.pop("league")
                replace_labels_in_standings(request_dict.get('standings'), league, start_year)
                replace_labels_in_postseason(request_dict.get('play_in'), league, start_year)
                replace_labels_in_postseason(request_dict.get('postseason'), league, start_year)
                
                # Make the actual request
                request_payload = json.dumps(request_dict)
                conn.request('POST', '/api/v1/seasons', body=request_payload, headers={'Content-Type': 'application/json'})
                
                response = conn.getresponse()

                if response.status != 200:
                    response.read()
                    print(f"Failed to create season {request_dict['name']}")
                    continue
                else:
                    print(f"Successfully created seasons {request_dict['name']}")

                response.read()
                pass
        
        if skipped > 0:
            print(f'Skipped creating {skipped} seasons in league {league} that already existed')

populate_leagues()
populate_metros()
populate_franchises()
populate_seasons()
print("Done!")