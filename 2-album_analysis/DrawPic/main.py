from pyecharts.charts import *
from pyecharts import options as opts
from pyecharts.globals import SymbolType
import pandas as pd


# 绘制2000年至2019年每年各类型音乐专辑的发行数量
def drawReleaseNumOfYear():
    releaseNumOfYear = pd.read_csv("data/releaseNumOfYear.csv", header=None, names=['Year', 'Genre', 'ReleaseNum'])
    data = pd.pivot(releaseNumOfYear, index='Year', columns='Genre')
    timeline = Timeline(init_opts=opts.InitOpts(width="2000px", height="800px"))
    for index, year in zip(range(data['ReleaseNum'].shape[0]), data.index.tolist()):
        bar = (
            Bar()
                .add_xaxis(data['ReleaseNum'].columns.tolist())
                .add_yaxis("销量", data['ReleaseNum'].iloc[index,].tolist(), label_opts=opts.LabelOpts(position="right"))
                .reversal_axis()
                .set_global_opts(title_opts=opts.TitleOpts(title="%d年各类型音乐专辑发行数量" % year, pos_left="center"),
                                 legend_opts=opts.LegendOpts(pos_top="30px"),
                                 xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="发行数量"),
                                 yaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="音乐专辑类型")
                                 )
        )
        timeline.add(bar, year)
    timeline.render('releaseNumOfYear.html')


# 绘制总销量排名前50的音乐作者在不同评分体系中的得分
def drawSalesAndScoreOfArtist():
    salesAndScoreOfArtist = pd.read_csv("data/salesAndScoreOfAuthor.csv", header=None,
                                        names=['artist_id', 'mtv_score', 'rolling_stone_score', 'music_maniac_score',
                                               'sale'])
    index = [str(x) for x in salesAndScoreOfArtist['artist_id']]
    bar = (
        Bar(init_opts=opts.InitOpts(width="2000px", height="800px"))
            .add_xaxis(index)
            .add_yaxis("发行量", salesAndScoreOfArtist['sale'].tolist())

            .set_global_opts(title_opts=opts.TitleOpts(title="2000年-2019年音乐专辑销量前50的音乐作家专辑总销量", pos_left="center"),
                             legend_opts=opts.LegendOpts(pos_top="30px"),
                             xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=90, font_size=12), name="作家id"),
                             yaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="销售量"),
                             tooltip_opts=opts.TooltipOpts(trigger="axis", axis_pointer_type="cross")
                             )
            .set_series_opts(label_opts=opts.LabelOpts(is_show=False))
    )
    mult_bar = (
        Bar(init_opts=opts.InitOpts(width="2000px", height="800px"))
            .add_xaxis(index)
            .add_yaxis("mtv_score", salesAndScoreOfArtist['mtv_score'].tolist(), stack='stack1')
            .add_yaxis("rolling_stone_score", salesAndScoreOfArtist['rolling_stone_score'].tolist(), stack='stack1')
            .add_yaxis("music_maniac_score", salesAndScoreOfArtist['music_maniac_score'].tolist(), stack='stack1')
            .set_global_opts(
            xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=90, font_size=12), name="作家id"),
            yaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="评分"),
            title_opts=opts.TitleOpts(title="2000年-2019年音乐专辑销量前50的音乐作家评分数据", pos_left="center"),
            legend_opts=opts.LegendOpts(pos_top="30px"),
            tooltip_opts=opts.TooltipOpts(trigger="axis", axis_pointer_type="cross"))
            .set_series_opts(label_opts=opts.LabelOpts(is_show=False))
    )
    page = Page()
    page.add(bar)
    page.add(mult_bar)
    page.render('salesAndScoreOfArtist.html')


# 绘制2000年至2019年每年各类型音乐专辑的销售量
def drawSalesOfGenreAndYear():
    salesOfGenreAndYear = pd.read_csv("data/salesOfGenreAndYear.csv", header=None, names=['Year', 'Genre', 'Sale'])
    data = pd.pivot(salesOfGenreAndYear, index='Year', columns='Genre')
    timeline = Timeline(init_opts=opts.InitOpts(width="2000px", height="800px"))
    for index, year in zip(range(data['Sale'].shape[0]), data.index.tolist()):
        bar = (
            Bar()
                .add_xaxis(data['Sale'].columns.tolist())
                .add_yaxis("销量", data['Sale'].iloc[index,].tolist(), label_opts=opts.LabelOpts(position="right"))
                .reversal_axis()
                .set_global_opts(title_opts=opts.TitleOpts(title="%d年各类型音乐专辑销量" % year, pos_left="center"),
                                 legend_opts=opts.LegendOpts(pos_top="30px"),
                                 xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="销量"),
                                 yaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12), name="音乐专辑类型")
                                 )
        )
        timeline.add(bar, year)
    timeline.render('salesOfGenreAndYear.html')


# 对专辑名称中出现的词绘制词云
def drawCloud():
    words = pd.read_csv("data/frequencyOfTitle.csv", header=None, names=['word', 'count'])
    data = [(i, j) for i, j in zip(words['word'], words['count'])]
    cloud = (
        WordCloud(init_opts=opts.InitOpts(width="2000px", height="800px"))
            .add("", data, word_size_range=[20, 100], shape=SymbolType.ROUND_RECT)
            .set_global_opts(title_opts=opts.TitleOpts(title="2000年-2019年所有音乐专辑名称词汇统计", pos_left="center"),
                             legend_opts=opts.LegendOpts(pos_top="30px"),
                             tooltip_opts=opts.TooltipOpts(is_show=True))
    )
    cloud.render("wordCloud.html")


# 绘制2000年至2019年各类型的音乐专辑的发行数量和销量
def drawReleaseNumAndSalesOfGenre():
    releaseNumAndSalesOfGenre = pd.read_csv("data/releaseNumAndSalesOfGenre.csv", header=None,
                                            names=['Type', 'Sale', 'Num'])
    bar = (
        Bar(init_opts=opts.InitOpts(width="2000px", height="800px"))
            .add_xaxis(releaseNumAndSalesOfGenre['Type'].tolist())
            .add_yaxis("发行量", releaseNumAndSalesOfGenre['Num'].tolist(), label_opts=opts.LabelOpts(is_show=False))

            .set_global_opts(title_opts=opts.TitleOpts(title="2000年-2019年不同类型音乐专辑发行量与销量", pos_left="center"),
                             legend_opts=opts.LegendOpts(pos_top="30px"),
                             xaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(rotate=45, font_size=12), name="音乐专辑类型"),
                             yaxis_opts=opts.AxisOpts(axislabel_opts=opts.LabelOpts(font_size=12),
                                                      name="发行量"),
                             tooltip_opts=opts.TooltipOpts(trigger="axis", axis_pointer_type="cross")
                             )
            # 添加右侧y轴
            .extend_axis(
            yaxis=opts.AxisOpts(
                name="销量",
            )
        )
    )
    line = (
        Line()
            .add_xaxis(releaseNumAndSalesOfGenre['Type'].tolist())
            .add_yaxis("销量",
                       releaseNumAndSalesOfGenre['Sale'],
                       yaxis_index=1,
                       z=2,
                       label_opts=opts.LabelOpts(is_show=False), is_smooth=True)
    )
    bar.overlap(line).render("releaseNumAndSalesOfGenre.html")


if __name__ == '__main__':
    drawReleaseNumAndSalesOfGenre()
    drawCloud()
    drawSalesOfGenreAndYear()
    drawSalesAndScoreOfArtist()
    drawReleaseNumOfYear()
