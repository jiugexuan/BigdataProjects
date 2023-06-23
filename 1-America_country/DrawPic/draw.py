#导入需要使用的模块
from pyecharts.charts import Geo
from pyecharts.datasets import register_url
from pyecharts import options as opts
from pyecharts.globals import ChartType, SymbolType
import pandas as pd
import matplotlib.pyplot as plt
import wordcloud
import jieba


def drawTop10PopulationState(inUrl):
    #解决中文乱码
    plt.rcParams['font.sans-serif']=['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    #从文件中读取数据
    df = pd.read_csv(inUrl,names=['population','state'])
    population = df['population'].tolist()
    state = df['state'].tolist()
    #画图
    # 设置x,y轴标签
    plt.xlabel("人口数量")
    plt.ylabel("州名")
    plt.barh(state,population,facecolor='tan',height=0.5,edgecolor='r',alpha=0.6)
    plt.title("美国人口数量top10的州")
    plt.show()

def drawUsDensityMap(inUrl,outUrl):
    #各州对应缩写的字典
    state_dict = {"Alabama":"AL","Alaska":"AK","Arizona":"AZ","Arkansas":"AR","California":"CA",
                  "Colorado":"CO","Connecticut":"CT","Delaware":"DE","Florida":"FL","Georgia":"GA",
                  "Hawaii":"HI","Idaho":"ID","Illinois":"IL","Indiana":"IN","Iowa":"IA",
                  "Kansas":"KS","Kentucky":"KY","Louisiana":"LA","Maine":"ME","Maryland":"MD",
                  "Massachusetts":"MA","Michigan":"MI","Minnesota":"MN","Mississippi":"MS","Missouri":"MO",
                  "Montana":"MT","Nebraska":"NE","Nevada":"NV","New Hampshire":"NH","New Jersey":"NJ",
                  "New Mexico":"NM","New York":"NY","North Carolina":"NC","North Dakota":"ND","Ohio":"OH",
                  "Oklahoma":"OK","Oregon":"OR","Pennsylvania":"PA","Rhode Island":"R","South Carolina":"SC",
                  "South Dakota":"SD","Tennessee":"TN","Texas":"TX","Utah":"UT","Vermont":"VT",
                  "Virginia":"VA","Washington":"WA","West Virginia":"WV","Wisconsin":"WI","Wyoming":"WY"
                  }
    try:
        register_url("https://echarts-maps.github.io/echarts-countries-js/")
    except Exception:
        import ssl
        ssl._create_default_https_context = ssl._create_unverified_context
        register_url("https://echarts-maps.github.io/echarts-countries-js/")
    df = pd.read_csv(inUrl,names=['population','area','density','state'])#从文件中读取数据
    density = df['density'].tolist()                             
    state = df['state'].tolist()
    #全称和缩写的映射
    for i in range(len(state)):
        state[i] = state_dict[state[i]]
    list = [[state[i],density[i]] for i in range(len(state))]  # 合并两个list为一个list
    maxDensity = max(density)                                         # 计算最大密度，用作图例的上限

    geo = (                                                          # 添加坐标点
        Geo(init_opts=opts.InitOpts(width = "1200px", height = "600px", bg_color = '#EEEEE8'))
            .add_schema(maptype="美国",itemstyle_opts=opts.ItemStyleOpts(color="#323c48", border_color="#111"))
            .add_coordinate('WA',-120.04,47.56).add_coordinate('OR',-120.37,43.77).add_coordinate('CA',-120.44,36.44).add_coordinate('AK',-122.00,28.46)
            .add_coordinate('ID',-114.08,43.80).add_coordinate('NV',-116.44,39.61).add_coordinate('MT',-109.42,47.13).add_coordinate('WY',-107.29,42.96)
            .add_coordinate('UT',-111.19,39.35).add_coordinate('AZ',-111.70,34.45).add_coordinate('HI',-105.25,28.72).add_coordinate('CO',-105.52,38.89)
            .add_coordinate('NM',-106.11,34.45).add_coordinate('ND',-100.22,47.53).add_coordinate('SD',-100.52,44.72).add_coordinate('NE',-99.64,41.65)
            .add_coordinate('KS',-98.53,38.43).add_coordinate('OK',-97.13,35.42).add_coordinate('TX',-98.16,31.03).add_coordinate('MN',-94.26,46.02)
            .add_coordinate('IA',-93.60,42.09).add_coordinate('MO',-92.57,38.48).add_coordinate('AR',-92.43,34.69).add_coordinate('LA',-92.49,31.22)
            .add_coordinate('WI',-89.55,44.25).add_coordinate('MI',-84.62,43.98).add_coordinate('IL',-89.11,40.20).add_coordinate('IN',-86.17,40.08)
            .add_coordinate('OH',-82.71,40.31).add_coordinate('KY',-84.92,37.44).add_coordinate('TN',-86.32,35.78).add_coordinate('MS',-89.63,32.66)
            .add_coordinate('AL',-86.68,32.53).add_coordinate('FL',-81.68,28.07).add_coordinate('GA',-83.22,32.59).add_coordinate('SC',-80.65,33.78)
            .add_coordinate('NC',-78.88,35.48).add_coordinate('VA',-78.24,37.48).add_coordinate('WV',-80.63,38.62).add_coordinate('PA',-77.57,40.78)
            .add_coordinate('NY',-75.22,43.06).add_coordinate('MD',-76.29,39.09).add_coordinate('DE',-75.55,39.09).add_coordinate('NJ',-74.47,40.03)
            .add_coordinate('VT',-72.70,44.13).add_coordinate('NH',-71.64,43.59).add_coordinate('MA',-72.09,42.33).add_coordinate('CT',-72.63,41.67)
            .add_coordinate('RI',-71.49,41.64).add_coordinate('ME',-69.06,45.16).add_coordinate('PR',-75.37,26.42).add_coordinate('DC',-77.04,38.90)
            .add("Density", list,symbol_size = 10,itemstyle_opts = opts.ItemStyleOpts(color="red"))
            .set_series_opts(label_opts=opts.LabelOpts(is_show=False),type='effectScatter')
            .set_global_opts(
            title_opts=opts.TitleOpts(title="美国人口密度图"),
            visualmap_opts=opts.VisualMapOpts(max_ = maxDensity,is_piecewise=True),
        )
            .render(outUrl)
    )

def drawAreaWordCloud(inUrl):
    #1从文件中读取数据
    df = pd.read_csv(inUrl,names=['population','area','density','state'])
    area = df['area'].tolist()
    state = df['state'].tolist()
    #2.将面积和州名建立成字典
    dict = {}
    for i in range(len(state)):
        dict[state[i]]=area[i]

    #3.生成词云
    wc = wordcloud.WordCloud(
        background_color='white',  # 背景颜色
        width=800,
        height=600,
        max_font_size=50,  # 字体大小
        min_font_size=10,
        #mask=plt.imread('C:\\Users\\91541\\Pictures\\Saved Pictures\\map.jpg'),  # 背景图片
        max_words=1000
    )
    wc.generate_from_frequencies(dict)
    wc.to_file('Area.jpg')  # 4.图片保存

# def drawCloud():
#     words = pd.read_csv("data/frequencyOfTitle.csv", header=None, names=['word', 'count'])
#     data = [(i, j) for i, j in zip(words['word'], words['count'])]
#     cloud = (
#         WordCloud(init_opts=opts.InitOpts(width="2000px", height="800px"))
#             .add("", data, word_size_range=[20, 100], shape=SymbolType.ROUND_RECT)
#             .set_global_opts(title_opts=opts.TitleOpts(title="2000年-2019年所有音乐专辑名称词汇统计", pos_left="center"),
#                              legend_opts=opts.LegendOpts(pos_top="30px"),
#                              tooltip_opts=opts.TooltipOpts(is_show=True))
#     )
#     cloud.render("wordCloud.html")

def drawDensityPie(inUrl):
    #解决中文乱码
    plt.rcParams['font.sans-serif']=['SimHei']
    plt.rcParams['axes.unicode_minus'] = False
    #从文件中读取数据
    df = pd.read_csv(inUrl,names=['density','state'])
    density = df['density'].tolist()
    state = df['state'].tolist()
    #画图
    plt.axes(aspect=1)
    plt.pie(x=density,labels=state,autopct="%0f%%",shadow=True)
    plt.title("美国人口密度前10的州饼状图")
    plt.show()

if __name__ == '__main__':
    drawUsDensityMap('data/state.csv',"US-Density.html")
    drawTop10PopulationState('data/populationTop10.csv')
    drawAreaWordCloud('data/state.csv')
    drawDensityPie('data/densityTop10.csv')
    # import matplotlib
    # matplotlib.matplotlib_fname()