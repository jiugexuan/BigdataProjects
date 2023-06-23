import os
import matplotlib.pyplot as plt

def file_name(path):
    for root,dirs,files in os.walk(path):
        pass
    return files

def readData(file_path,yearcounts):
    f=open(file_path,'r')
    for term in f:
        #print(term)
        term=term.strip('\n')
        temp=term.split(',')
        yearcounts.append((int(temp[0]),int(temp[1])))
    f.close()

path="data/result/yearCount"
files=file_name(path)

years=[]
counts=[]
yearcounts=[]

#读取文件夹下所有文件
for file in files:
    file_path = path + "/" + file
    #对每个文件, 读出其中数据
    readData(file_path,yearcounts)
#按年份进行排序 (升序)
yearcounts = sorted(yearcounts)

#画图
for year,count in yearcounts:
    years.append(year)
    counts.append(count)

plt.title('Earthquakes Per Year')
plt.xlabel('Year')
plt.ylabel('Count')
plt.plot(years,counts)
plt.show()