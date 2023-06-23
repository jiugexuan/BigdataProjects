# 替换字符串中双引号之间的逗号为间隔号
def trim_comma(str1):
    if str1.find('\"') == -1:
        return str1
    lid = str1.find('\"')
    rid = str1.rfind('\"')
    result = str1[:lid]+str1[lid:rid].replace(",", "-")+str1[rid:]
    return result


f = open("data/listings.csv", "r", encoding='utf-8')
data = f.readlines()
newdata = []
tempdata = ""
f.close()
# 是否复制该行数据
flag = True
for i in range(len(data)):
    if flag:
        # 若一行只有1个双引号，就说明数据被分割到多行了，进行合并处理
        if data[i].count("\"") == 1:
            tempdata = tempdata+data[i][:-1]+"-"
            flag = False
        else:
            tempdata = trim_comma(data[i])
            newdata.append(tempdata)
            tempdata = ""
    else:
        if data[i].count("\"") == 1:
            tempdata = tempdata+data[i]
            flag = True
            tempdata = trim_comma(tempdata)
            newdata.append(tempdata)
            tempdata = ""
        else:
            tempdata = tempdata+data[i][:-1]+"-"

f = open("data/listings-preprocess.csv", "w", encoding='utf-8')
f.writelines(newdata)
f.close()
