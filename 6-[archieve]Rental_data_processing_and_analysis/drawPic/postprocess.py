import pandas as pd
import numpy as np
import json


# csv转json
def csv2json(data):
    result = []
    num = data.shape[0]
    for i in range(num):
        result.append(np.array(data.iloc[i, :]).tolist())
    result = json.dumps(result)
    return result


def csv2dic(data):
    data.sort_values(by=1, inplace=True, ascending=False) 
    print("[")
    region_num = data.shape[0]
    for i in range(region_num):
        print("{value: "+str(data.iloc[i, 1]) + ", name: \'"+ data.iloc[i,0]+"\'},")
    print("]")


# total_price
total_price = pd.read_csv("data/total_price.csv", header=None, index_col=False)
print(np.array(total_price.iloc[:, 0]).tolist())
print(np.array(total_price.iloc[:, 1]).tolist())
# total_region
total_region = pd.read_csv("data/total_region.csv", header=None, index_col=False)
total_region.sort_values(by=1, inplace=True, ascending=False) 
print("[")
region_num = total_region.shape[0]
for i in range(region_num):
    print("{value: "+str(total_region.iloc[i, 1]) + ", name: \'"+ total_region.iloc[i,0]+"\'},")
print("]")
# total_coord
total_coord = pd.read_csv("data/total_coord.csv", header=None, index_col=False)
coord_json = []
room_num = total_coord.shape[0]
for i in range(room_num):
    coord_json.append([total_coord.iloc[i,1], total_coord.iloc[i,0], 1])
coord_json = json.dumps(coord_json)
f = open('data/total_coord.json', 'w')
f.write(coord_json)
f.close()
# total_min_nights
total_min_nights = pd.read_csv("data/total_min_nights.csv", header=None, index_col=False)
total_min_nights.sort_values(by=0, inplace=True, ascending=True)
min_nights_json = csv2json(total_min_nights)
f = open('data/total_min_nights.json', 'w')
f.write(min_nights_json)
f.close()
# total_availability
total_availability = pd.read_csv("data/total_availability.csv", header=None, index_col=False)
total_availability.sort_values(by=0, inplace=True, ascending=True)
availability_json = csv2json(total_availability)
f = open('data/total_availability.json', 'w')
f.write(availability_json)
f.close()

# total_type
total_type = pd.read_csv("data/total_type.csv", header=None, index_col=False)
csv2dic(total_type)

# region_price
region_price = pd.read_csv("data/region_price.csv", header=None, index_col=False)
region_price.sort_values(by=3, inplace=True, ascending=False)
region = '[\"'+'\", \"'.join(np.array(region_price.iloc[:,0]))+'\"]'
max = json.dumps(np.array(region_price.iloc[:,1]).tolist())
min = json.dumps(np.array(region_price.iloc[:,2]).tolist())
avg = json.dumps(np.array(region_price.iloc[:,3]).tolist())
f = open('data/region_price.json', 'w', encoding='utf-8')
f.write('var region = '+region+';\n')
f.write('var min = '+min+';\n')
f.write('var max = '+max+';\n')
f.write('var avg = '+avg+';\n')
f.close()
