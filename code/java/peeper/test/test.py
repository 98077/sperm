#!/usr/bin/env python
#coding:utf-8
#Copyright (C) dirlt

import urllib2
def raiseHTTPRequest(url,data=None,timeout=3):
    # if we do post, we have to provide data.
    f=urllib2.urlopen(url,data,timeout)
    return f.read()

import json
def jsonToString(dict):
    return json.dumps(dict)
def jsonFromString(s):
    return json.loads(s)

URL = 'http://localhost:12346/peep'
def test1():
    json = {"reqid":"3",
            "account":'dirlt',
            'timeout':1000,
            'reqtype':'geographic',
            'device':{
                'imei':'123'
                }
            }
    data = raiseHTTPRequest(URL,jsonToString(json))
    print data

if __name__ == '__main__':
    test1()
    
            
            
