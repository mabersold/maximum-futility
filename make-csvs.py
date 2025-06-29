import csv
import json
import os

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
            # metros.append({"name": row[1], "label": row[2]})
            metros_reverse_lookup[row[2]] = {'id': int(row[0]), 'name': row[1]}
    
    return metros_reverse_lookup

def get_franchises(leagues: dict, metros: dict):
    franchises = []
    for league in leagues:
        franchises_path = f"{franchises_directory}/{league}"

        if not os.path.isdir(franchises_path):
            continue

        files = os.listdir(franchises_path)

        for file in files:
            with open(f"{franchises_path}/{file}", 'r') as f:
                content = f.read()

            franchise_dict = json.loads(content)
            franchise_dict['league_id'] = leagues[league]
            for c in franchise_dict.get('chapters', []):
                league = c['league_id']
                c['league_id'] = leagues.get(league)
                c['league_name'] = league.upper()
                metro = metros.get(c['metro_id'])
                c['metro_id'] = metro['id']
                c['metro_name'] = metro['name']
            franchises.append(franchise_dict)
    
    return franchises

def get_seasons(leagues: dict):
    seasons = []
    season_id = 1
    for league in leagues:
        seasons_path = f"{seasons_directory}/{league}"
        if not os.path.isdir(seasons_path):
            continue

        season_files = os.listdir(seasons_path)

        for file in season_files:
            with open(f"{seasons_path}/{file}", 'r') as f:
                content = f.read()
                season = json.loads(content)
                season['postseason_rounds'] = len(season.get('postseason', {}).get('rounds', []))
                season['id'] = season_id
                season['league_id'] = leagues[season.get('league')]
                seasons.append(season)
                season_id += 1

    return seasons

def get_franchise_seasons(seasons: list, franchises: list):
    total_franchise_seasons = []

    for season in seasons:
        franchise_seasons = []
        max_values = {}
        min_values = {}
        groupings = {}

        max_depth = 0
        
        has_divisions = season.get('total_minor_divisions', 0) > 0
        has_conferences = season.get('total_major_divisions', 0) > 0
        if has_divisions:
            max_depth = 2
        elif has_conferences:
            max_depth = 1

        get_mins_and_maxes(season.get('standings'), min_values, max_values, max_depth)
        get_flattened_franchise_list(season.get('standings'), max_depth, franchise_seasons, groupings)

        # Special cases for three NFL seasons where teams merged
        if season['start_year'] == 1943 and season['league'] == 'nfl':
            steagles = [fs for fs in franchise_seasons if fs.get('label') == 'steagles'][0]
            eagles = steagles.copy()
            eagles['label'] = 'eagles'
            steagles['label'] = 'steelers'
            franchise_seasons.append(eagles)

        if season['start_year'] == 1944 and season['league'] == 'nfl':
            cardpitt = [fs for fs in franchise_seasons if fs.get('label') == 'card-pitt'][0]
            cards = cardpitt.copy()
            cards['label'] = 'cardinals'
            cardpitt['label'] = 'steelers'
            franchise_seasons.append(cards)

        if season['start_year'] == 1945 and season['league'] == 'nfl':
            yankstigers = [fs for fs in franchise_seasons if fs.get('label') == 'yanks'][0]
            tigers = yankstigers.copy()
            tigers['label'] = 'tigers'
            franchise_seasons.append(tigers)

        league = season['league']
        year = season['start_year']
        postseason_results = get_postseason_results(season.get('postseason', {}), league, year)

        for fs in franchise_seasons:
            label = fs.get('label', '')
            franchise_id = get_franchise_id(label, league, year)
            
            franchise = franchises[franchise_id - 1]
            chapter = find_chapter(franchise.get('chapters', []), season.get('start_year'))
            
            if chapter == None:
                print(f"Could not find a chapter for franchise {franchise['name']}")
            
            fs['label'] = franchise['label']
            fs['season_id'] = season['id']
            fs['start_year'] = season['start_year']
            fs['franchise_id'] = franchise_id
            fs['metro_id'] = chapter['metro_id']
            fs['metro_name'] = chapter['metro_name']
            fs['league_id'] = chapter['league_id']
            fs['league_name'] = chapter['league_name']
            fs['team_name'] = chapter['team_name']
            
            # Overall results
            has_tie = True if sum(1 for r in franchise_seasons if r['metric'] == fs['metric']) > 1 else 0
            if fs['metric'] == max_values['Overall']:
                fs['league_position'] = 'FIRST_TIED' if has_tie else 'FIRST'
            elif fs['metric'] == min_values['Overall']:
                fs['league_position'] = 'LAST_TIED' if has_tie else 'LAST'

            # Conference results
            has_tie = True if has_conferences and sum(1 for r in groupings[fs['conference_name']] if r['metric'] == fs['metric']) > 1 else 0
            if has_conferences and fs['metric'] == max_values[fs['conference_name']]:
                fs['conference_position'] = 'FIRST_TIED' if has_tie else 'FIRST'
            elif has_conferences and fs['metric'] == min_values[fs['conference_name']]:
                fs['conference_position'] = 'LAST_TIED' if has_tie else 'LAST'

            # Division results
            has_tie = True if has_divisions and fs.get('division_name') and sum(1 for r in groupings[fs['division_name']] if r['metric'] == fs['metric']) > 1 else 0
            if has_divisions and fs.get('division_name') and fs['metric'] == max_values[fs['division_name']]:
                fs['division_position'] = 'FIRST_TIED' if has_tie else 'FIRST'
            elif has_divisions and fs.get('division_name') and fs['metric'] == min_values[fs['division_name']]:
                fs['division_position'] = 'LAST_TIED' if has_tie else 'LAST'

            # Postseason results
            ps_result = postseason_results.get(franchise_id, None)

            if ps_result:
                fs['qualified_for_postseason'] = True
                fs['rounds_won'] = ps_result.get('rounds_won', 0)
                fs['appeared_in_championship'] = ps_result.get('appeared_in_championship', False)
                fs['won_championship'] = ps_result.get('won_championship', False)
            elif len(postseason_results) > 0:
                fs['qualified_for_postseason'] = False
        
        total_franchise_seasons.extend(franchise_seasons)
    
    return total_franchise_seasons

def get_franchise_id(label: str, league: str, year: int):
    label = label.lower()

    matches = [team for team in franchise_lookups if team["name"].lower() == label and team["league"].lower() == league.lower()]

    if len(matches) == 1:
        return matches[0]["id"]
    
    for team in matches:
        if team["start_year"] <= year and (team["end_year"] is None or team["end_year"] >= year):
            return team["id"]

    return None
    

def write_franchises(leagues: dict):
    with open(f'{csv_base_directory}/franchises.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['id', 'name', 'label', 'is_defunct', 'league_id'])
        for franchise in franchises:
            writer.writerow([franchise['id'], franchise['name'], franchise['label'], franchise['is_defunct'], franchise['league_id']])

def write_seasons(leagues: dict):
    with open(f'{csv_base_directory}/seasons.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['id', 'name', 'start_year', 'end_year', 'league_id', 'total_major_divisions', 'total_minor_divisions', 'postseason_rounds'])

        for season in seasons:
            total_major_divisions = season.get('total_major_divisions', 0)
            total_minor_divisions = season.get('total_minor_divisions', 0)
            postseason_rounds = season.get('postseason_rounds', 0)

            writer.writerow([season['id'], season['name'], season['start_year'], season['end_year'], season['league_id'], total_major_divisions, total_minor_divisions, postseason_rounds])

def write_franchise_seasons(franchise_seasons: list):
    with open(f'{csv_base_directory}/franchise_seasons.csv', 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['season_id', 'metro_id', 'team_name', 'league_id', 'conference', 'division', 'league_position', 'conference_position', 'division_position', 'qualified_for_postseason', 'rounds_won', 'appeared_in_championship', 'won_championship'])

        for fs in franchise_seasons:
            writer.writerow([
                fs['season_id'],
                fs['metro_id'],
                fs['team_name'],
                fs['league_id'],
                fs.get('conference_name', None),
                fs.get('division_name', None),
                fs.get('league_position', None),
                fs.get('conference_position', None),
                fs.get('division_position', None),
                fs.get('qualified_for_postseason', None),
                fs.get('rounds_won', None),
                fs.get('appeared_in_championship', None),
                fs.get('won_championship', None)
            ])

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

def write_individual_seasons(leagues: dict, seasons: list):
    # Temporary function, to be removed when I revamp which CSVs are used
    for league in leagues:
        league_id = leagues.get(league)
        filtered_seasons = [s for s in seasons if s['league_id'] == league_id]

        with open(f'{csv_base_directory}/{league}/{league}-seasons.csv', 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file, lineterminator='\n')
            writer.writerow(['name', 'start_year', 'end_year', 'league', 'total_major_divisions', 'total_minor_divisions', 'postseason_rounds'])
            rows = [
                [
                    s['name'],
                    s['start_year'],
                    s['end_year'],
                    str(s.get('league', '')).upper(),
                    s['total_major_divisions'],
                    s['total_minor_divisions'],
                    s['postseason_rounds']
                ]
                for s in filtered_seasons
            ]
            writer.writerows(rows)

def write_individual_franchises(franchises: list, franchise_seasons: list):
    # Temporary function, to be removed when I revamp which CSVs are used
    for f in franchises:
        seasons = [x for x in franchise_seasons if x['label'] == f['label']]
        if len(seasons) == 0:
            continue
        identifier = f['name'].lower().replace(' ', '-').replace('.', '').replace('(', '').replace(')', '')
        league = seasons[-1]['league_name'].lower()

        seasons_directory = 'franchises' if league in ['wnba', 'mls'] else 'seasons'
        with open(f'{csv_base_directory}/{league}/{seasons_directory}/{identifier}.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(['season', 'metro', 'team_name', 'league', 'conference', 'division', 'league_position', 'conference_position', 'division_position', 'qualified_for_postseason', 'rounds_won', 'appeared_in_championship', 'won_championship'])
            rows = [
                [
                    fs['start_year'],
                    fs['metro_name'],
                    fs['team_name'],
                    fs['league_name'],
                    fs.get('conference_name', ''),
                    fs.get('division_name', ''),
                    fs.get('league_position', ''),
                    fs.get('conference_position', ''),
                    fs.get('division_position', ''),
                    str(fs.get('qualified_for_postseason', '')).lower(),
                    str(fs.get('rounds_won', '')).lower(),
                    str(fs.get('appeared_in_championship', '')).lower(),
                    str(fs.get('won_championship', '')).lower()
                ]
                for fs in seasons    
            ]
    
            writer.writerows(rows)

leagues = get_leagues()
metros = get_metros()
franchises = sorted(get_franchises(leagues, metros), key=lambda d: d['id'])

for f in franchises:
    for c in f.get('chapters', []):
        franchise_lookups.append({"name": c.get("label", f.get("label")), "league": c.get("league_name"), "start_year": c.get("start_year"), "end_year": c.get("end_year", None), "id": f["id"]})

seasons = get_seasons(leagues)
franchise_seasons = get_franchise_seasons(seasons, franchises)
write_franchises(leagues)
write_seasons(leagues)
write_individual_seasons(leagues, seasons)
write_franchise_seasons(franchise_seasons)
write_individual_franchises(franchises, franchise_seasons)

print("Done.")