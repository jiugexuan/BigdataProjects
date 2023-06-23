import pandas as pd

data = pd.read_csv('../Data/listings-preprocess.csv', usecols=[9], index_col=False, header=0)
Q3 = data.quantile(0.75)
Q1 = data.quantile(0.25)
IQR = Q3-Q1
lower_bound = Q1 - 1.5*IQR
highest_bound = Q3 + 1.5*IQR
# -512
print(lower_bound)
# 1568
print(highest_bound)
