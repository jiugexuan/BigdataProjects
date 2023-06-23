import requests
import json
import csv


class Spider():
    def get(self, url):
        # 请求url
        r = requests.get(url)
        print(r.status_code)
        data = json.loads(r.text) # r.text是字符串，利用json转换为列表
        return data
    
    def write_to_csv(self, data):
        # province, city_number, city_name, time, temperature, humidity, pressure
        dct = {}
        dct['province'] = None
        dct['city_number'] = None
        dct['city'] = None
        dct['time'] = None
        dct['temperature'] = None
        dct['humidity'] = None
        dct['pressure'] = None
        file_path = r'data.csv'
        with open(file_path, 'w') as f:
            w = csv.writer(f)
            w.writerow(dct.keys())
        
        city_number = 0
        wrong_city = ['58357', '58151', '58981', '59989', '92024', '59554', '59362', '58964', '59354', '58965', '59152', '59162']

        for province in data:
            print("正在写入省份：{}".format(province['name']))

            with open(file_path, 'a+') as f:
                w = csv.writer(f)

                province_name = province['name'] # 省份名
                province_code = province['code'] # 省份编码
                url_city = 'http://www.nmc.cn/f/rest/province/' + province_code # 获取省份城市的url

                dct['province'] = province_name

                r = requests.get(url_city)
                data_city = json.loads(r.text)

                for city in data_city:
                    city_number += 1
                    print("正在写入第{}个城市：{}".format(city_number, city['city']))
                    city_name = city['city']
                    city_code = city['code']
                    url_temperature = 'http://www.nmc.cn/f/rest/passed/' + city_code

                    dct['city_number'] = city_number
                    dct['city'] = city_name

                    rr = requests.get(url_temperature)
                    try:
                        data_temperature = json.loads(rr.text) # 访问出错时，把出错城市的编码记下来，然后跳过这个城市
                    except json.decoder.JSONDecodeError:
                        wrong_city.append(city_code)
                        city_number -= 1
                        continue
                    for temperature in data_temperature:

                        dct['time'] = temperature['time'][-5:]
                        dct['temperature'] = temperature['temperature']
                        dct['humidity'] = temperature['humidity']
                        dct['pressure'] = temperature['pressure']
                        w.writerow(dct.values())
        print(wrong_city)



if __name__ == '__main__':
    url_province = 'http://www.nmc.cn/f/rest/province' # 省份
    # url_city = 'http://www.nmc.cn/f/rest/province/AFJ' # 省份的城市
    # url_temperature = 'http://www.nmc.cn/f/rest/passed/59134' # 城市的24小时天气， temperature， humidity， pressure
    spider = Spider()
    data = spider.get(url_province)
    spider.write_to_csv(data)
