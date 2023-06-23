import pandas as pd

f1 = pd.read_csv('CVPR2016.csv')
f2 = pd.read_csv('CVPR2017.csv')
f3 = pd.read_csv('CVPR2018.csv')
f4 = pd.read_csv('CVPR2019.csv')
f5 = pd.read_csv('CVPR2020.csv')


f1 = f1.iloc[:,-2:]
f2 = f2.iloc[:,-2:]
f3 = f3.iloc[:,-2:]
f4 = f4.iloc[:,-2:]
f5 = f5.iloc[:,-2:]

file = [f1,f2,f3,f4,f5]

final = pd.concat(file,ignore_index=True)
final.to_csv('CVPR_cat.csv')