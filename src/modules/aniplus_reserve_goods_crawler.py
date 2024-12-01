# -*- coding: utf-8 -*-
"""
Created on Sat Oct 12 19:25:26 2024

@author: user
"""

# encoding = UTF-8
import time
import sys
import os
import sqlite3
import pandas as pd
import json
import requests
from urllib import parse
#from selenium import webdriver
#from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from datetime import datetime
from urllib.request import urlretrieve
from seleniumwire import webdriver

driver = webdriver.Chrome()

driver.get('https://shop.aniplustv.com/reserve')

time.sleep(4)

driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")

time.sleep(2)

next_page_button = '//*[@id="no_container"]/div[2]/div[2]/div[3]/ul/li[2]'
driver.find_element(By.XPATH, next_page_button).click()

def url_preprocess(url):
    parsed = parse.unquote(url, encoding="EUC-KR")
    return parsed.replace('/_next/image?url=', '').replace('&w=1200&q=75', '')
    """
    example = 'https://ah9szoaj9w.ecn.cdn.ofs.kr%2Fimages%2Ftvee-admin%2Fshopping%2Fshop_20241010161203.png&w=1200&q=75'
    ex = '/_next/image?url=https%3A%2F%2Fah9szoaj9w.ecn.cdn.ofs.kr%2Fimages%2Ftvee-admin%2Fshopping%2Fshop_20241010161203.png&w=1200&q=75'
    """

for request in driver.requests:
    if request.response and 'application/json' in request.response.headers.get('Content-Type', ''):
        if 'goods?page=1&perPage=48&sort=RECENT&filter[reserveFlag]=true&filter[saleFlag]=true' in request.url:
            json_data = request.response.body.decode('utf-8')
            # JSON 파싱
            data = json.loads(json_data)
            
for request in driver.requests:
    if request.response and 'application/json' in request.response.headers.get('Content-Type', ''):
        if 'goods?page=2&perPage=48&sort=RECENT&filter[reserveFlag]=true&filter[saleFlag]=true' in request.url:
            json_data = request.response.body.decode('utf-8')
            # JSON 파싱
            data2 = json.loads(json_data)

data_df = pd.DataFrame(data['list'])
data_df2 = pd.DataFrame(data2['list'])

data_df = pd.concat([data_df, data_df2])

drop_list = ['goodCode', 'goodsType', 'dcPrice', 'dcRate',
             'saleType', 'saleStart', 'saleEnd', 'saleActive',
             'active', 'soldout', 'groupCode', 'groupName', 'groupImg',
             'optionName', 'groupMinPrice', 'groupMaxPrice',
             'isNew', 'isLast', 'saleCount', 'seriesTags', 'characterTags']

for col in drop_list:
    data_df = data_df.drop(col, axis = 1)

data_df.columns = ['goodSerial', 'title', 'price', 'relDT',
                   'thumImg', 'ani_title', 'tmpcategory', 'brand']

serials = data_df.goodSerial.to_list()
thumbnail_name = data_df.thumImg.to_list()

thumbnail_xpath = '//*[@id="no_container"]/div[1]/div[1]/div[1]/div/img'

detail_img_data = []
detail_info_data = []

for item, thumb in zip(serials, thumbnail_name):
    url = 'https://shop.aniplustv.com/item/' + str(item)
    driver.get(url)
    time.sleep(0.5)
    ####################
    # Download Thumbnail
    thumbnail_src = driver.find_element(By.XPATH, thumbnail_xpath).get_attribute('src')
    urlretrieve(thumbnail_src, 'D:/data/{}'.format(thumb))
    # Update thumbnail info in Dataframe
    
    ####################
    time.sleep(0.25)
    start = time.time()
    driver.find_element(By.XPATH, '//*[@id="no_container"]/div[1]/div[1]/div[1]/div/button').click()
    
    info2 = '//*[@id="detail_menu01"]/div'
    info2_imgs = driver.find_elements(By.XPATH, f"{info2}//img")
    image_srcs = [img.get_attribute('src') for img in info2_imgs]
    
    ####################
    # Append product detail data
    product_detail_info = driver.find_element(By.XPATH, '//*[@id="detail_menu01"]/ul/li[9]/span[2]/div').text
    detail_info_data.append({'goodSerial':item,
                     'pro_detail':product_detail_info})
    ####################
    count = 0
    
    for image_src in image_srcs:
        file_name = str(item) + "_" + str(count)
        urlretrieve(image_src, 'D:/data/{}.png'.format(file_name))
        
        detail_img_data.append({'goodSerial':item,
                     'detailImg':file_name})
        count += 1
    
    print("이미지 다운로드 완료 :: " + str(item) + " :: 소요시간 " + str(time.time() - start))
    time.sleep(0.5)

detail_img_df = pd.DataFrame(detail_img_data)
detail_info_df = pd.DataFrame(detail_info_data)

combined_df = pd.merge(data_df, detail_info_df, on='goodSerial', how='inner')

combined_df.to_csv('goods.csv', index=False, encoding='utf-8-sig')
detail_img_df.to_csv('goods_detail_img.csv', index=False, encoding='utf-8-sig')