import requests
from bs4 import BeautifulSoup
import pandas as pd
import numpy as np
headers = {'user-agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36'}#创建头部信息
urls=[
    'https://openaccess.thecvf.com/CVPR2018?day=2018-06-19',
    'https://openaccess.thecvf.com/CVPR2018?day=2018-06-20',
    'https://openaccess.thecvf.com/CVPR2018?day=2018-06-21'
]
alllist=[]
dayCount = 0

for url in urls:
    r=requests.get(url,headers=headers)
    content=r.content.decode('utf-8')
    soup = BeautifulSoup(content, 'html.parser')
    dts=soup.find_all('dt',class_='ptitle')
    hts='http://openaccess.thecvf.com/'
    #数据爬取
    for i in range(len(dts)):
        title=dts[i].a.text.strip()
        print('这是第'+str(i+dayCount)+'篇文章:', title)
        href=hts+dts[i].a['href']
        r = requests.get(href, headers=headers)
        content = r.content.decode('utf-8')
        soup = BeautifulSoup(content, 'html.parser')
        #print(title,href)
        div_author=soup.find(name='div',attrs={"id":"authors"})
        authors = div_author.text.strip().split(';')[0]
        print('第'+str(i+dayCount)+'篇文章的作者：', authors)

        value=(title, authors)
        alllist.append(value)
    dayCount+=len(dts)
    print(dayCount)


name = ['title', 'authors']
papers = pd.DataFrame(columns=name, data=alllist)
print(papers.head())
papers.to_csv('CVPR2018.csv', encoding='utf-8')