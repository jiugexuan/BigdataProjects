import os
import matplotlib.pyplot as plt
import mpl_toolkits.basemap

def file_name(path):
    for root,dirs,files in os.walk(path):
        pass
    return files

def readData(file_path,latitudes,longitudes,magnitudes):
    f=open(file_path,'r')
    for term in f:
        #print(term)
        term=term.strip('\n')
        temp=term.split(',')
        latitudes.append(float(temp[0]))
        longitudes.append(float(temp[1]))
        magnitudes.append(float(temp[2]))
    f.close()

path="data/result/location"
files=file_name(path)

latitudes=[]
longitudes=[]
magnitudes=[]

#读取文件夹下所有文件
for file in files:
    file_path = path + "/" + file
    #对每个文件, 读出其中数据
    readData(file_path,latitudes,longitudes,magnitudes)

#世界地图
basemap = mpl_toolkits.basemap.Basemap()
basemap.drawcoastlines()
#画图
plt.title('Location')
plt.xlabel('Longitude')
plt.ylabel('Latitude')
plt.scatter(longitudes, latitudes, alpha=0.1, s=magnitudes)
plt.show()