# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import io
import base64
from PIL import Image
import os
import json
import random
import requests

import urllib.request
import urllib.parse
import json
import requests  # pip install requests
# import execjs  # 安装指令：pip install PyExecJS
import random
import hashlib
import re

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.

# 百度翻译方法
def baidu_translate(content):
    print(content)
    if len(content) > 4891:
        return '输入请不要超过4891个字符！'
    salt = str(random.randint(0, 50))
    # 申请网站 http://api.fanyi.baidu.com/api/trans
    appid = '20240402002012912'  # 这里写你自己申请的
    secretKey = 'cBsDikgLttqSxaBkZeOU'  # 这里写你自己申请的
    sign = appid + content + salt + secretKey
    sign = hashlib.md5(sign.encode(encoding='UTF-8')).hexdigest()
    head = {'q': f'{content}',
            'from': 'zh',
            'to': 'en',
            'appid': f'{appid}',
            'salt': f'{salt}',
            'sign': f'{sign}'}
    j = requests.get('http://api.fanyi.baidu.com/api/trans/vip/translate', head)
    print(j.json())
    res = j.json()['trans_result'][0]['dst']
    res = re.compile('[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]').sub(' ', res)
    #print(res)
    return res


# def world2pic(des):
#     if __name__ == '__main__':
#         print_hi('Stable Diffusion')
#     url = "http://10.0.2.2:7860" #使用Android模拟器的特殊地址
#     # url = ""
#     # des = input("请输入背景描述：")
#     print("请输入背景描述：")
#     print(des)
#     des_en = baidu_translate(des)
#     print(des_en)
#     # re = input("是否继续生成 y or n:")
#     # if re == "n":
#     #     sys.exit(0)
#     print("继续生成")
#     payload = {
#         "prompt": des_en,
#         "steps": 5,
#         "width": 1080,
#         "height": 512,
#     }
#     response = requests.post(url=f'{url}/sdapi/v1/txt2img', json=payload)
#     print("正在生成中，请耐心等待~")
#     r = response.json()
#     #print(r)
#     for i in r['images']:
#         image = Image.open(io.BytesIO(base64.b64decode(i.split(",", 1)[0])))
#         # 构建保存路径，保存到应用的私有存储目录
#         file_dir = os.path.dirname(os.path.abspath(__file__))
#         file_name = "output.png"
#         # 将路径转换为字符串
#         file_dir_str = str(file_dir)
#         file_name_str = str(file_name)
#         # 使用 os.path.join 构建路径
#         # save_path = os.path.join(file_dir_str, file_name_str)
#         save_path = str(os.path.join(file_dir_str, file_name_str))
#         image.save(save_path)
#     print(f"生成完毕，保存路径：{save_path}")
#     return save_path
def world2pic(des):
    if __name__ == '__main__':
        print_hi('Stable Diffusion')
    # url = "http://10.0.2.2:7860" #使用Android模拟器的特殊地址
    url = "http://139.224.214.59:7860"
    # des = input("请输入背景描述：")
    print("请输入背景描述：")
    print(des)
    des_en = baidu_translate(des)
    print(des_en)
    # re = input("是否继续生成 y or n:")
    # if re == "n":
    #     sys.exit(0)
    print("继续生成")
    payload = {
        "prompt": des_en,
        "steps": 50,
        "width": 1024,
        "height": 576,
    }
    #response = requests.post(url=f'{url}/sdapi/v1/txt2img', json=payload)
    response = requests.post(url=f'{url}/sdapi/v1/txt2img', json=payload)
    print("正在生成中，请耐心等待~")
    print(response)
    r = response.json()
    print(r)
    #print(r)
    for i in r['images']:
        image = Image.open(io.BytesIO(base64.b64decode(i.split(",", 1)[0])))
        # 构建保存路径，保存到应用的私有存储目录
        file_dir = os.path.dirname(os.path.abspath(__file__))
        file_name = "output.png"
        # 将路径转换为字符串
        file_dir_str = str(file_dir)
        file_name_str = str(file_name)
        # 使用 os.path.join 构建路径
        # save_path = os.path.join(file_dir_str, file_name_str)
        save_path = str(os.path.join(file_dir_str, file_name_str))
        image.save(save_path)
    print(f"生成完毕，保存路径：{save_path}")
    return save_path


