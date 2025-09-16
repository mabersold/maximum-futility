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
    all_files = [p for p in Path(seasons_directory).glob('**/*') if p.is_file()]

    seasons_by_league = {key: [] for key in leagues}
    franchise_season_by_league = {key: [] for key in leagues}

    highest_id = 0

    # Load and annotate seasons
    for file in all_files:
        with open(file, 'r') as f:
            content = f.read()
        
        season_dict = json.loads(content)

        id = season_dict.get('id', 0)
        highest_id = max(highest_id, id)

        league_label = season_dict.get('league')
        season_dict['league_id'] = leagues[league_label]
        season_dict['total_postseason_rounds'] = len(season_dict.get('postseason', {}).get('rounds', []))
        seasons_by_league[league_label].append(season_dict)

        # Define the structure of the league
        level_types = ['league', 'conference', 'division']
        if season_dict.get('conferences_are_divisions'):
            level_types = ['league', 'division']

        # Retrieve annotated franchise seasons from the standings
        results = get_standings_info(season_dict.get('standings', {}), season_dict, level_types)
        if season_dict.get('additional_standings'):
            # Additional standings are present in the first 4 NFL seasons prior to the AFL-NFL merger
            additional_results = get_standings_info(season_dict.get('additional_standings'), season_dict, level_types)
            results.extend(additional_results)

        # Update results with postseason info
        annotate_with_postseason(results, season_dict.get('postseason', {}))
        franchise_season_by_league.get(league_label).extend(results)
    
    # Now, iterate through and store the results
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
                if not fs.get('qualified_for_postseason') and fs.get('has_postseason', False):
                    fs['qualified_for_postseason'] = False

                # If it has a merged franchise, we created an additional franchise season
                if fs.get('merged_team'):
                    additional_fs = fs.copy()
                    additional_fs['label'] = additional_fs['merged_team']
                    annotate_with_metro(additional_fs, league)
                    write_franchise_season(additional_fs, writer)
                    pass
                
                # A few final annotations to set franchise ID, metro ID and team name
                annotate_with_metro(fs, league)
                write_franchise_season(fs, writer)
    print(f"Highest season id is {highest_id}")

def get_standings_info(standings: dict, season_dict: dict, level_types, level: int = 0):
    has_postseason = bool(season_dict.get('postseason', {}).get('rounds'))
    
    # Basis case: there are results
    if 'results' in standings:
        results = standings['results']
        for r in results:
            r.update({
                'season_id': season_dict['id'],
                'league_id': season_dict['league_id'],
                'year': season_dict['start_year'],
                'has_postseason': has_postseason,
                'metric': r.get('points') or (
                    r['wins'] / (r['wins'] + r['losses']) if (r['wins'] + r['losses']) > 0 else 0.0
                )
            })

        annotate_with_positions(results, level_types[level], standings.get('name'))
        return results
    
    # Recursive case: there are sub_groups
    all_results = []
    for sg in standings.get('sub_groups', []):
        results = get_standings_info(sg, season_dict, level_types, level + 1)
        all_results.extend(results)

    annotate_with_positions(all_results, level_types[level], standings.get('name'))
    return all_results

def annotate_with_positions(results, level_type, group_name):
    if not results:
        return

    metrics = [r.get('metric', 0) for r in results]
    highest = max(metrics)
    lowest = min(metrics)
    highest_count = metrics.count(highest)
    lowest_count = metrics.count(lowest)

    for r in results:
        if group_name and level_type != 'league':
            r[f"{level_type}_name"] = group_name

        if r.get('metric', 0) == highest:
            r[f"{level_type}_position"] = 'FIRST_TIED' if highest_count > 1 else 'FIRST'
        elif r.get('metric', 0) == lowest:
            r[f"{level_type}_position"] = 'LAST_TIED' if lowest_count > 1 else 'LAST'

def annotate_with_postseason(results, postseason: dict):
    results_by_label = {team['label']: team for team in results}
    
    for round in postseason.get('rounds', []):
        for matchup in round.get('matchups', []):
            winner_label = matchup['winner']['label']
            loser_label = matchup['loser']['label']

            winning_team = results_by_label[winner_label]
            losing_team = results_by_label[loser_label]

            for team in (winning_team, losing_team):
                team.setdefault('qualified_for_postseason', True)
                team.setdefault('appeared_in_championship', False)
                team.setdefault('won_championship', False)
                team.setdefault('rounds_won', 0)

            winning_team['rounds_won'] += 1
            
            if matchup.get('is_championship_round') == True:
                winning_team['appeared_in_championship'] = True
                winning_team['won_championship'] = True
                losing_team['appeared_in_championship'] = True
                losing_team['won_championship'] = False

def annotate_with_metro(fs: dict, league_label: str):
    franchise_id = get_franchise_id(fs['label'], league_label, fs['year'])
    franchise = franchises[franchise_id - 1]
    chapter = find_chapter(franchise.get('chapters', []), fs['year'])
    fs['franchise_id'] = franchise_id
    fs['metro_id'] = chapter['metro_id']
    fs['team_name'] = chapter['team_name']

def write_franchise_season(fs: dict, writer):
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

leagues = get_leagues()
metros = get_metros()
franchises_by_league = get_franchises(leagues, metros)
franchises = sorted([f for franchises in franchises_by_league.values() for f in franchises], key=lambda d: d['id'])

for f in franchises:
    for c in f.get('chapters', []):
        franchise_lookups.append({"name": c.get("label", f.get("label")), "league": c.get("league_name"), "start_year": c.get("start_year"), "end_year": c.get("end_year", None), "id": f["id"]})

get_seasons(leagues)
print("Done.")