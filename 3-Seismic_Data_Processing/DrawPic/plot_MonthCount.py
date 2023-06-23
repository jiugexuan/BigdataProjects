import os
import matplotlib.pyplot as plt

def file_name(path):
    for root,dirs,files in os.walk(path):
        pass
    return files

def readData(file_path,x,y):
    f=open(file_path,'r')
    for term in f:
        term=term.strip('\n')
        temp=term.split(',')
        x.append(int(temp[0]))
        y.append(int(temp[2]))
    f.close()

path="data/result/monthCount"
files=file_name(path)

month=[]
count=[]

#读取文件夹下所有文件
for file in files:
    file_path = path + "/" + file
    #对每个文件, 读出其中数据
    readData(file_path,month,count)

#画图
plt.title('Earthquakes Per Month')
plt.xlabel('Month')
plt.ylabel('Count')
plt.scatter(month, count, alpha=0.2)
plt.show()