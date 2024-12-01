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
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

driver = webdriver.Chrome()
#389, 388, 387, 378, 368, 358, 354, 343, 453, 451, 450,449, 438,
origin_code = [437, 436, 407]

def run(code):
    url = 'https://shop.aniplustv.com/exhibition/{}'.format(code)
    response_target = 'goods?page=1&perPage=48&sort=RECENT&filter[saleFlag]=true&filter[ogFlag]=false&filter[contentIdx]={}'.format(code)
    driver.get(url)
    
    WebDriverWait(driver, 10).until(lambda d: any(
    request.response and 'application/json' in request.response.headers.get('Content-Type', '')
    and response_target in request.url for request in driver.requests
))
    
    for request in driver.requests:
        if request.response and 'application/json' in request.response.headers.get('Content-Type', ''):
            if response_target in request.url:
                json_data = request.response.body.decode('utf-8')
                # JSON 파싱
                data = json.loads(json_data)
    
    
    data_df = pd.DataFrame(data['list'])
    
    
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
        product_detail_info = driver.find_element(By.CLASS_NAME, 'explain').text
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
    
    combined_df.to_csv('goods_origin_{}.csv'.format(code), index=False, encoding='utf-8-sig')
    detail_img_df.to_csv('goods_origin_{}_detail_img.csv'.format(code), index=False, encoding='utf-8-sig')
    
for code in origin_code:
    run(code)