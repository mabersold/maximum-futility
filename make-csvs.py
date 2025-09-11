import csv
import json
from pathlib import Path

csv_base_directory = 'src/main/resources/data'
franchises_directory = "seed_data/franchises"
seasons_directory = "seed_data/seasons"

franchise_lookups = []

def get_leagues():
    leagues = []
    league_reverse_lookup = {}
    with open(f'{csv_base_directory}/leagues.csv', mode='r') as file:
        csv_reader = csv.reader(file)
        next(csv_reader)
        for row in csv_reader:
            leagues.append({"name": row[1], "sport": row[2], "label": row[3]})
            league_reverse_lookup[row[3]] = int(row[0])
    
    return league_reverse_lookup

def get_metros():
    metros_reverse_lookup = {}
    with open(f'{csv_base_directory}/metros.csv', mode='r') as file:
        csv_reader = csv.reader(file)
        next(csv_reader)
        for row in csv_reader:
            metros_reverse_lookup[row[2]] = {'id': int(row[0]), 'name': row[1]}
    
    return metros_reverse_lookup

def get_franchises(leagues: dict, metros: dict):
    all_files = [p for p in Path(franchises_directory).glob('**/*') if p.is_file()]

    highest_id = 0

    franchises_by_league = {key: [] for key in leagues}
    chapters_by_league = {key: [] for key in leagues}

    for file in all_files:
        with open(file, 'r') as f:
            content = f.read()
        
        franchise_dict = json.loads(content)
        
        # Replace league_id (but store the value)
        league = franchise_dict['league_id']
        franchise_dict['league_id'] = leagues[league]

        highest_id = max(highest_id, franchise_dict['id'])

        # Replace league and metro info in chapters
        for c in franchise_dict.get('chapters', []):
            c_league = c['league_id']
            c['league_id'] = leagues.get(c_league)
            c['league_name'] = c_league.upper()
            metro = metros.get(c['metro_id'])
            c['metro_id'] = metro['id']
            c['metro_name'] = metro['name']
            chapters_by_league[league].append(c)
        
        franchises_by_league[league].append(franchise_dict)

    # Store CSVs for franchises
    for league in leagues:
        sorted_franchises = sorted(franchises_by_league[league], key=lambda d: d['id'])
        with open(f'{csv_base_directory}/{league}/{league}-franchises.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(['id', 'name', 'label', 'is_defunct', 'league_id'])
            for franchise in sorted_franchises:
                writer.writerow([franchise['id'], franchise['name'], franchise['label'], str(franchise['is_defunct']).lower(), franchise['league_id']])

    print(f"Highest franchise id is {highest_id}")
    return franchises_by_league

def get_seasons(leagues: dict):
    # Get files
    all_files = [p for p in Path(seasons_directory).glob('**/*') if p.is_file()]
    season_ids = {0}

    seasons_by_league = {key: [] for key in leagues}
    franchise_season_by_league = {key: [] for key in leagues}

    highest_id = 0

    # Load and annotate seasons
    for file in all_files:
        with open(file, 'r') as f:
            content = f.read()
        
        season_dict = json.loads(content)

        # Make sure ID is unique
        if not season_dict.get('id'):
            print(f"ERROR: {season_dict['name']} does not have an ID")
        else:
            id = season_dict.get('id')
            if id in season_ids:
                print(f"ERROR: {id} is a repeated season id")
            else:
                season_ids.add(id)
                highest_id = max(highest_id, id)
        
        # Add correct annotations for the season
        league_label = season_dict.get('league')
        season_dict['league_id'] = leagues[league_label]

        season_dict['total_postseason_rounds'] = len(season_dict.get('postseason', {}).get('rounds', []))

        seasons_by_league[league_label].append(season_dict)

        franchise_seasons = []
        max_values = {}
        min_values = {}
        groupings = {}
        max_depth = 0
        
        has_divisions = season_dict.get('total_minor_divisions', 0) > 0
        has_conferences = season_dict.get('total_major_divisions', 0) > 0
        if has_divisions:
            max_depth = 2
        elif has_conferences:
            max_depth = 1
        
        get_mins_and_maxes(season_dict.get('standings'), min_values, max_values, max_depth)
        get_flattened_franchise_list(season_dict.get('standings'), max_depth, franchise_seasons, groupings)
        postseason_results = get_postseason_results(season_dict.get('postseason', {}), league_label, season_dict.get('start_year'))

        # Special cases for three NFL seasons where teams merged
        if season_dict['start_year'] == 1943 and season_dict['league'] == 'nfl':
            steagles = [fs for fs in franchise_seasons if fs.get('label') == 'steagles'][0]
            eagles = steagles.copy()
            eagles['label'] = 'eagles'
            steagles['label'] = 'steelers'
            franchise_seasons.append(eagles)

        if season_dict['start_year'] == 1944 and season_dict['league'] == 'nfl':
            cardpitt = [fs for fs in franchise_seasons if fs.get('label') == 'card-pitt'][0]
            cards = cardpitt.copy()
            cards['label'] = 'cardinals'
            cardpitt['label'] = 'steelers'
            franchise_seasons.append(cards)

        if season_dict['start_year'] == 1945 and season_dict['league'] == 'nfl':
            yankstigers = [fs for fs in franchise_seasons if fs.get('label') == 'yanks'][0]
            tigers = yankstigers.copy()
            tigers['label'] = 'tigers'
            franchise_seasons.append(tigers)

        for fs in franchise_seasons:
            label = fs.get('label', '')
            franchise_id = get_franchise_id(label, league_label, season_dict.get('start_year'))
            
            franchise = franchises[franchise_id - 1]
            chapter = find_chapter(franchise.get('chapters', []), season_dict.get('start_year'))
            
            if chapter == None:
                print(f"Could not find a chapter for franchise {franchise['name']}")
            
            fs['label'] = franchise['label']
            fs['season_id'] = season_dict['id']
            fs['start_year'] = season_dict['start_year']
            fs['franchise_id'] = franchise_id
            fs['metro_id'] = chapter['metro_id']
            fs['metro_name'] = chapter['metro_name']
            fs['league_id'] = chapter['league_id']
            fs['league_name'] = chapter['league_name']
            fs['team_name'] = chapter['team_name']
            
            # Overall results
            has_tie = sum(1 for r in franchise_seasons if r['metric'] == fs['metric']) > 1
            fs['league_position'] = get_position(fs['metric'], 'Overall', max_values, min_values, True, has_tie)

            # Conference results
            if season_dict.get('conferences_are_divisions', False):
                # This block is for when we want to consider conferences as divisions instead (which is rare)
                fs['division_name'] = fs['conference_name']
                fs['conference_name'] = None
                has_tie = has_divisions and sum(1 for r in groupings[fs['division_name']] if r['metric'] == fs['metric']) > 1
                fs['division_position'] = get_position(fs['metric'], fs.get('division_name'), max_values, min_values, has_divisions, has_tie)
            else:
                has_tie = has_conferences and sum(1 for r in groupings[fs['conference_name']] if r['metric'] == fs['metric']) > 1
                fs['conference_position'] = get_position(fs['metric'], fs.get('conference_name'), max_values, min_values, has_conferences, has_tie)

            # Division results
            if not season_dict.get('conferences_are_divisions', False):
                has_tie = has_divisions and fs.get('division_name') and sum(1 for r in groupings[fs['division_name']] if r['metric'] == fs['metric']) > 1
                fs['division_position'] = get_position(fs['metric'], fs.get('division_name'), max_values, min_values, has_divisions, has_tie)

            # Postseason results
            ps_result = postseason_results.get(franchise_id, None)

            if ps_result:
                fs['qualified_for_postseason'] = True
                fs['rounds_won'] = ps_result.get('rounds_won', 0)
                fs['appeared_in_championship'] = ps_result.get('appeared_in_championship', False)
                fs['won_championship'] = ps_result.get('won_championship', False)
            elif len(postseason_results) > 0:
                fs['qualified_for_postseason'] = False

            franchise_season_by_league.get(league_label).append(fs)

    # Store CSVs for seasons
    for league in leagues:
        sorted_seasons = sorted(seasons_by_league[league], key=lambda d: d['id'])
        # Store seasons CSV
        with open(f'{csv_base_directory}/{league}/{league}-seasons.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(['id', 'name', 'start_year', 'end_year', 'league_id', 'total_major_divisions', 'total_minor_divisions', 'postseason_rounds'])
            for season in sorted_seasons:
                writer.writerow([season['id'], season['name'], season['start_year'], season['end_year'], season['league_id'], season['total_major_divisions'], season['total_minor_divisions'], season['total_postseason_rounds']])
        
        sorted_franchise_seasons = sorted(franchise_season_by_league[league], key=lambda d: d['season_id'])
        # Store franchise seasons CSV
        with open(f'{csv_base_directory}/{league}/{league}-franchise-seasons.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(['franchise_id', 'season_id', 'metro_id', 'team_name', 'league_id', 'conference', 'division', 'league_position', 'conference_position', 'division_position', 'qualified_for_postseason', 'rounds_won', 'appeared_in_championship', 'won_championship'])

            for fs in sorted_franchise_seasons:
                writer.writerow([
                    fs['franchise_id'],
                    fs['season_id'],
                    fs['metro_id'],
                    fs['team_name'],
                    fs['league_id'],
                    fs.get('conference_name', None),
                    fs.get('division_name', None),
                    fs.get('league_position', None),
                    fs.get('conference_position', None),
                    fs.get('division_position', None),
                    str(fs.get('qualified_for_postseason', '')).lower(),
                    fs.get('rounds_won', None),
                    str(fs.get('appeared_in_championship', '')).lower(),
                    str(fs.get('won_championship', '')).lower()
                ])
    print(f"Highest season id is {highest_id}")

def get_position(metric, unit_name, max_values, min_values, has_unit: bool, has_tie: bool):
    if has_unit and unit_name and metric == max_values[unit_name]:
        return 'FIRST_TIED' if has_tie else 'FIRST'
    elif has_unit and unit_name and metric == min_values[unit_name]:
        return 'LAST_TIED' if has_tie else 'LAST'

def get_franchise_id(label: str, league: str, year: int):
    label = label.lower()

    matches = [team for team in franchise_lookups if team["name"].lower() == label and team["league"].lower() == league.lower()]

    if len(matches) == 1:
        return matches[0]["id"]
    
    for team in matches:
        if team["start_year"] <= year and (team["end_year"] is None or team["end_year"] >= year):
            return team["id"]

    return None

def find_chapter(chapters, year):
    for i, chapter in enumerate(chapters):
        start_year = chapter["start_year"]
        end_year = chapter.get("end_year")

        if year < start_year:
            return None 
        if end_year is None or start_year <= year <= end_year:
            return chapter

    return chapters[-1] if chapters else None  # If past the last start_year, return the last chapter

def get_mins_and_maxes(standings: dict, mins: dict, maxes: dict, max_depth: int, level=0):
    name = standings.get('name', 'Overall')
    
    # if level == max_depth:
    if standings.get('sub_groups', None) == None:
        for r in standings.get('results', []):
            if 'points' in r:
                r['metric'] = r['points']
            else:
                total_wins = r['wins'] + r['losses']
                r['metric'] = r['wins'] / total_wins if total_wins > 0 else 0.0

        highest = max(standings.get('results'), key=lambda x: x['metric']).get('metric')
        lowest = min(standings.get('results'), key=lambda x: x['metric']).get('metric')
        mins[name] = lowest
        maxes[name] = highest
        return highest, lowest
    
    highest = float('-inf')
    lowest = float('inf')

    for sub_group in standings.get('sub_groups', []):
        r_highest, r_lowest = get_mins_and_maxes(sub_group, mins, maxes, max_depth, level + 1)

        if r_highest > highest:
            highest = r_highest
        if r_lowest < lowest:
            lowest = r_lowest

    mins[name] = lowest
    maxes[name] = highest

    return highest, lowest

def get_flattened_franchise_list(standings: dict, max_depth: int, franchises: list, groupings: dict, level=0, prev_name=''):
    if standings.get('results'):
        for f in standings.get('results'):
            extended_f = f.copy()
            if extended_f.get('metric'):
                extended_f['metric'] = float(extended_f.get('metric', None))
            if level == 2:
                extended_f['conference_name'] = prev_name
                division_name = standings.get('name', 'None')
                extended_f['division_name'] = division_name
                if prev_name in groupings:
                    groupings[prev_name].append(f)
                else:
                    groupings[prev_name] = [f]
                
                if division_name in groupings:
                    groupings[division_name].append(f)
                else:
                    groupings[division_name] = [f]
            elif level == 1:
                conference_name = standings.get('name', 'None')
                extended_f['conference_name'] = conference_name
                if conference_name in groupings:
                    groupings[conference_name].append(f)
                else:
                    groupings[conference_name] = [f]

            franchises.append(extended_f)
    else:
        name = standings.get('name', 'Overall')
        for sub_group in standings.get('sub_groups', []):
            get_flattened_franchise_list(sub_group, max_depth, franchises, groupings, level + 1, name)
    
def get_postseason_results(postseason: dict, league: str, year: int):
    results = {}

    for round in postseason.get('rounds', []):
        for matchup in round.get('matchups', []):
            winner_label = matchup.get('winner', {}).get('label')
            winner_id = get_franchise_id(winner_label, league, year)
            
            loser_label = matchup.get('loser', {}).get('label')
            loser_id = get_franchise_id(loser_label, league, year)

            winner_results = results.get(winner_id, {})
            winner_results['rounds_won'] = winner_results.get('rounds_won', 0) + 1
            if matchup.get('is_championship_round', False) == True:
                winner_results['appeared_in_championship'] = True
                winner_results['won_championship'] = True
            results[winner_id] = winner_results

            loser_results = results.get(loser_id, {'rounds_won': 0})
            if matchup.get('is_championship_round', False) == True:
                loser_results['appeared_in_championship'] = True
            results[loser_id] = loser_results

    return results

leagues = get_leagues()
metros = get_metros()
franchises_by_league = get_franchises(leagues, metros)
franchises = sorted([f for franchises in franchises_by_league.values() for f in franchises], key=lambda d: d['id'])

for f in franchises:
    for c in f.get('chapters', []):
        franchise_lookups.append({"name": c.get("label", f.get("label")), "league": c.get("league_name"), "start_year": c.get("start_year"), "end_year": c.get("end_year", None), "id": f["id"]})

get_seasons(leagues)
print("Done.")