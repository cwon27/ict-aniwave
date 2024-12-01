# -*- coding: utf-8 -*-
"""
Created on Sat Oct 12 19:25:26 2024

@author: user
"""

# encoding = UTF-8

from selenium.webdriver.common.by import By
from selenium import webdriver
import time
#import pymysql
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import pymysql
import schedule
from selenium.webdriver.chrome.options import Options

def run():
    

    op = Options()
    op.add_argument("--headless")
    op.add_argument("--disable-gpu")

    driver = webdriver.Chrome(options=op)
    conn = pymysql.connect(host='192.168.1.158', port=3306, user='final', password='tiger1234', db='finalproject', charset='utf8')
    cur = conn.cursor()
    
    target_list_sql = """SELECT T.trackingNum, T.idx
                         FROM T_order AS T
                         JOIN T_orderList AS TOL
                         ON T.idx = TOL.order_idx
                         WHERE TOL.orderState IN (3, 4)"""
    cur.execute(target_list_sql)
    target_code = cur.fetchall()
    
    target_xpath = '//*[@id="statusDetail"]/tr'
    
    for target in target_code:
        url = 'https://trace.cjlogistics.com/next/tracking.html?wblNo={}'.format(target[0])
        driver.get(url)
        
        tracking_status = []
        time.sleep(3)
        
        tracking_list = driver.find_elements(By.XPATH, target_xpath)
        for tracking in tracking_list:
            tracking_status.append(tracking.text)
        
        complete_check = False
    
        if '배송완료' in tracking_status[-1]:
            complete_check = True
            query = """
            UPDATE T_orderList
            SET orderState = 5
            WHERE order_idx = {}
            """.format(target[1])
            print(target[0] + ":: 배송완료 업데이트")
    
        elif len(tracking_status) != 0 and not complete_check:
            query = """
            UPDATE T_orderList
            SET orderState = 4
            WHERE order_idx = {}
            """.format(target[1])
            print(target[0] + ":: 배송중 업데이트")
        
        if 'query' in locals():
            cur.execute(query)
            conn.commit()
    
    cur.close()
    conn.close()
    driver.quit()
    
schedule.every(1).minutes.do(run)
#schedule.every(10).seconds.do(run)

while True:
    schedule.run_pending()
    time.sleep(1)