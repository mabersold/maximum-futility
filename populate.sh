#!/bin/bash

# Base URL of the API
BASE_URL="http://localhost:8080/api/v1/leagues"

# Leagues data to populate
declare -a leagues=(
  '{"name": "MLB", "sport": "Baseball", "label": "mlb"}'
  '{"name": "NFL", "sport": "Football", "label": "nfl"}'
  '{"name": "NBA", "sport": "Basketball", "label": "nba"}'
  '{"name": "NHL", "sport": "Hockey", "label": "nhl"}'
  '{"name": "WNBA", "sport": "Basketball", "label": "wnba"}'
  '{"name": "MLS", "sport": "Soccer", "label": "mls"}'
)

# Loop through each league and send a POST request
for league in "${leagues[@]}"; do
  echo "Populating league: $league"
  response=$(curl -X POST -H "Content-Type: application/json" -d "$league" -w "%{http_code}" -s -o /dev/null "$BASE_URL")
  echo "Response Code: $response"
done

echo "Leagues populated successfully!"