from numpy.random import randint
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import numpy as np


def draw_temperature_max():
    font = FontProperties(fname='/home/fanghan/bigdata/getdata/chinese.msyh.ttf', size=9)
    temperature_max20 = pd.read_csv("/home/fanghan/bigdata/getdata/temperature_max20.csv", header=None)
    temperature_max20.columns = ['province', 'city_number', 'city', 'time', 'temperature']
    colors = ['r', 'g', 'b', 'c', 'm', 'y', 'k']
    color = []
    for i in range(20):
        color.append(colors[np.random.randint(7)])
    plt.figure(figsize=(10,8))
    plt.bar([i for i in range(1, 41, 2)], temperature_max20['temperature'], width=0.8, color=color)
    plt.xticks([i for i in range(1, 41, 2)], temperature_max20['city'], fontproperties = font, rotation=-30)
    plt.xlabel("城市", fontproperties = font)
    plt.ylabel("平均气温",fontproperties = font)
    plt.title("全国平均气温前20名", fontproperties = font)
    for a,b in zip([i for i in range(1, 41, 2)], temperature_max20['temperature']):
        plt.text(a, b+0.05, '%.1f' % b, ha='center', va= 'bottom',fontsize=9)
    plt.show()
    plt.savefig("/home/fanghan/bigdata/getdata/temperature_max20.jpg")


def draw_temperature_min():
    font = FontProperties(fname='/home/fanghan/bigdata/getdata/chinese.msyh.ttf', size=9)
    temperature_max20 = pd.read_csv("/home/fanghan/bigdata/getdata/temperature_min20.csv", header=None)
    temperature_max20.columns = ['province', 'city_number', 'city', 'time', 'temperature']
    colors = ['r', 'g', 'b', 'c', 'm', 'y', 'k']
    color = []
    for i in range(20):
        color.append(colors[np.random.randint(7)])
    plt.figure(figsize=(10,8))
    plt.bar([i for i in range(1, 41, 2)], temperature_max20['temperature'], width=0.8, color=color)
    plt.xticks([i for i in range(1, 41, 2)], temperature_max20['city'], fontproperties = font, rotation=-30)
    plt.xlabel("城市", fontproperties = font)
    plt.ylabel("平均气温",fontproperties = font)
    plt.title("全国平均气温后20名", fontproperties = font)
    for a,b in zip([i for i in range(1, 41, 2)], temperature_max20['temperature']):
        plt.text(a, b+0.05, '%.1f' % b, ha='center', va= 'bottom',fontsize=9)
    plt.show()
    plt.savefig("/home/fanghan/bigdata/getdata/temperature_min20.jpg")


def draw_humidity_min20():
    font = FontProperties(fname='/home/fanghan/bigdata/getdata/chinese.msyh.ttf', size=9)
    temperature_max20 = pd.read_csv("/home/fanghan/bigdata/getdata/humidity_min20.csv", header=None)
    temperature_max20.columns = ['province', 'city_number', 'city', 'time', 'temperature']
    colors = ['r', 'g', 'b', 'c', 'm', 'y', 'k']
    color = []
    for i in range(20):
        color.append(colors[np.random.randint(7)])
    plt.figure(figsize=(10,8))
    plt.bar([i for i in range(1, 41, 2)], temperature_max20['temperature'], width=0.8, color=color)
    plt.xticks([i for i in range(1, 41, 2)], temperature_max20['city'], fontproperties = font, rotation=-30)
    plt.xlabel("城市", fontproperties = font)
    plt.ylabel("平均湿度",fontproperties = font)
    plt.title("全国平均湿度后20名", fontproperties = font)
    for a,b in zip([i for i in range(1, 41, 2)], temperature_max20['temperature']):
        plt.text(a, b+0.05, '%.1f' % b, ha='center', va= 'bottom',fontsize=9)
    plt.show()
    plt.savefig("/home/fanghan/bigdata/getdata/humidity_min20.jpg")


def draw_pressure_max20():
    font = FontProperties(fname='/home/fanghan/bigdata/getdata/chinese.msyh.ttf', size=9)
    temperature_max20 = pd.read_csv("/home/fanghan/bigdata/getdata/pressure_max20.csv", header=None)
    temperature_max20.columns = ['province', 'city_number', 'city', 'time', 'temperature']
    colors = ['r', 'g', 'b', 'c', 'm', 'y', 'k']
    color = []
    for i in range(20):
        color.append(colors[np.random.randint(7)])
    plt.figure(figsize=(10,8))
    plt.bar([i for i in range(1, 41, 2)], temperature_max20['temperature'], width=0.8, color=color)
    plt.xticks([i for i in range(1, 41, 2)], temperature_max20['city'], fontproperties = font)
    plt.xlabel("城市", fontproperties = font)
    plt.ylabel("平均气压",fontproperties = font)
    plt.title("全国平均气压前20名", fontproperties = font)
    for a,b in zip([i for i in range(1, 41, 2)], temperature_max20['temperature']):
        plt.text(a, b+0.05, '%.1f' % b, ha='center', va= 'bottom',fontsize=7)
    plt.show()
    plt.savefig("/home/fanghan/bigdata/getdata/pressure_max20.jpg")


if __name__ == "__main__":
    draw_temperature_max()
    draw_temperature_min()
    draw_humidity_min20()
    draw_pressure_max20()