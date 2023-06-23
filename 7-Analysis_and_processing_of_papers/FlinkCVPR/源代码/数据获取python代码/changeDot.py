import pandas as pd
import re
import numpy as np

data = pd.read_csv('CVPR_cat.csv')

data[u'authors'] = data[u'authors'].astype(str)
data[u'authors'] = data[u'authors'].apply(lambda x:re.sub(',.\"\s+',';',x))
# data[u'authors'] = data[u'authors'].apply(lambda x:re.sub(',\s+',';',x))

data[u'title'] = data[u'title'].astype(str)
d = data[u'title'].apply(lambda x:re.sub(',',' ',x))
data[u'title'] = d.apply(lambda x:re.sub('\"\(\)',' ',x))


data = data.iloc[:,1:3]

data.to_csv('CVPR_cat2.csv', index=True, encoding='utf-8')