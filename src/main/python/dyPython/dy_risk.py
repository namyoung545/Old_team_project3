import json
import pandas as pd

# Load JSON data
with open('resources/data/dy_risk.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Convert to pandas DataFrame
df = pd.DataFrame(data)

# Analyze data
print("Top 5 High Risk Regions by Risk Score:")
print(df.nlargest(5, 'riskScore')[['region', 'year', 'riskScore']])

# Save to CSV
df.to_csv('dy_risk.csv', index=False, encoding='utf-8')
