import requests
import pandas as pd
import json

# Spring API URL
url = "http://localhost:8080/api/electric-fires"

def fetch_data():
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        return data
    else:
        return {"error": "Failed to fetch data from API"}

def generate_graph_data():
    data = fetch_data()
    df = pd.DataFrame(data)
    graph_data = {
        "regions": df["region"].tolist(),
        "totalIncidents": df["totalIncidents"].tolist(),
        "totalDamage": df["totalDamage"].tolist(),
    }
    return json.dumps(graph_data)  # JSON 형식으로 반환

if __name__ == "__main__":
    print(generate_graph_data())
