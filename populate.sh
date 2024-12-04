#!/bin/bash

# Base URLs for leagues and metro areas
BASE_URL_LEAGUES="http://localhost:8080/api/v1/leagues"
BASE_URL_METROS="http://localhost:8080/api/v1/metros"

# Leagues data to populate
declare -a leagues=(
  '{"name": "MLB", "sport": "Baseball", "label": "mlb"}'
  '{"name": "NFL", "sport": "Football", "label": "nfl"}'
  '{"name": "NBA", "sport": "Basketball", "label": "nba"}'
  '{"name": "NHL", "sport": "Hockey", "label": "nhl"}'
  '{"name": "WNBA", "sport": "Basketball", "label": "wnba"}'
  '{"name": "MLS", "sport": "Soccer", "label": "mls"}'
)

# Metro areas data to populate
declare -a metros=(
  '{"name": "Anderson", "label": "anderson"}'
  '{"name": "Atlanta", "label": "atlanta"}'
  '{"name": "Austin", "label": "austin"}'
  '{"name": "Baltimore", "label": "baltimore"}'
  '{"name": "Calgary", "label": "calgary"}'
  '{"name": "Charlotte", "label": "charlotte"}'
  '{"name": "Chicago", "label": "chicago"}'
  '{"name": "Cincinnati", "label": "cincinnati"}'
  '{"name": "Cleveland", "label": "cleveland"}'
  '{"name": "Columbus", "label": "columbus"}'
  '{"name": "Dallas", "label": "dallas"}'
  '{"name": "Decatur", "label": "decatur"}'
  '{"name": "Denver", "label": "denver"}'
  '{"name": "Detroit", "label": "detroit"}'
  '{"name": "Edmonton", "label": "edmonton"}'
  '{"name": "Fort Wayne", "label": "fort-wayne"}'
  '{"name": "Grand Rapids", "label": "grand-rapids"}'
  '{"name": "Green Bay", "label": "green-bay"}'
  '{"name": "Hartford", "label": "hartford"}'
  '{"name": "Houston", "label": "houston"}'
  '{"name": "Indianapolis", "label": "ind"}'
  '{"name": "Jacksonville", "label": "jacksonville"}'
  '{"name": "Kansas City", "label": "kansas-city"}'
  '{"name": "Las Vegas", "label": "las-vegas"}'
  '{"name": "Los Angeles", "label": "la"}'
  '{"name": "Memphis", "label": "memphis"}'
  '{"name": "Miami", "label": "miami"}'
  '{"name": "Milwaukee", "label": "milwaukee"}'
  '{"name": "Minneapolis", "label": "minn"}'
  '{"name": "Montreal", "label": "montreal"}'
  '{"name": "Nashville", "label": "nashville"}'
  '{"name": "New Orleans", "label": "new-orleans"}'
  '{"name": "New York", "label": "ny"}'
  '{"name": "Oklahoma City", "label": "okc"}'
  '{"name": "Orlando", "label": "orlando"}'
  '{"name": "Ottawa", "label": "ottawa"}'
  '{"name": "Philadelphia", "label": "phil"}'
  '{"name": "Phoenix", "label": "phoenix"}'
  '{"name": "Pittsburgh", "label": "pitt"}'
  '{"name": "Portland", "label": "pdx"}'
  '{"name": "Portsmouth", "label": "portsmouth"}'
  '{"name": "Providence", "label": "providence"}'
  '{"name": "Quebec", "label": "quebec"}'
  '{"name": "Raleigh", "label": "raleigh"}'
  '{"name": "Rochester", "label": "rochester"}'
  '{"name": "Sacramento", "label": "sacramento"}'
  '{"name": "Salt Lake City", "label": "slc"}'
  '{"name": "San Antonio", "label": "san-antonio"}'
  '{"name": "San Diego", "label": "san-diego"}'
  '{"name": "San Francisco Bay", "label": "sf"}'
  '{"name": "Seattle", "label": "seattle"}'
  '{"name": "Sheboygan", "label": "sheboygan"}'
  '{"name": "Sioux City", "label": "sioux-city"}'
  '{"name": "St. Louis", "label": "stl"}'
  '{"name": "Syracuse", "label": "syracuse"}'
  '{"name": "Tampa Bay", "label": "tb"}'
  '{"name": "Toronto", "label": "toronto"}'
  '{"name": "Tri-Cities", "label": "tri-cities"}'
  '{"name": "Tulsa", "label": "tulsa"}'
  '{"name": "Vancouver", "label": "vancouver"}'
  '{"name": "Washington", "label": "dc"}'
  '{"name": "Winnipeg", "label": "winnipeg"}'
)

# Function to populate data for any given URL and array of JSON objects
populate_data() {
  local url=$1
  local data_array=("${!2}")

  for data in "${data_array[@]}"; do
    echo "Populating: $data"
    response=$(curl -X POST -H "Content-Type: application/json" -d "$data" -w "%{http_code}" -s -o /dev/null "$url")
    echo "Response Code: $response"
    echo -e "\n"
  done
}

# Populate Leagues
echo "Populating Leagues..."
populate_data "$BASE_URL_LEAGUES" leagues[@]

# Populate Metro Areas
echo "Populating Metro Areas..."
populate_data "$BASE_URL_METROS" metros[@]

echo "Leagues and Metro Areas populated successfully!"