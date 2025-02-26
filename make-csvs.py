import csv
import json
import os

csv_base_directory = 'src/main/resources/data'

leagues = []
metros = []

with open(f'{csv_base_directory}/leagues.csv', mode='r') as file:
    csv_reader = csv.reader(file)
    for row in csv_reader:
        leagues.append({"name": row[1], "sport": row[2], "label": row[3]})

with open(f'{csv_base_directory}/metros.csv', mode='r') as file:
    csv_reader = csv.reader(file)
    for row in csv_reader:
        metros.append({"name": row[1], "label": row[2]})

franchises_directory = "seed_data/franchises"

franchises = []

for league in leagues:
    path = f"{franchises_directory}/{league['label']}"

    if not os.path.isdir(path):
        continue

    files = os.listdir(path)

    for file in files:
        print(f"Filename is {file}")

        with open(f"{path}/{file}", 'r') as f:
            content = f.read()

        franchise_dict = json.loads(content)
        franchises.append(franchise_dict)

# with open(f'{csv_base_directory}/franchises.csv', 'w', newline='') as file:
#     writer = csv.writer(file)

print("Hi")