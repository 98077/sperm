#!/usr/bin/env python
#coding:utf-8
#Copyright (C) dirlt

import message_pb2
import time
import math

import urllib2
def raiseHTTPRequest(url,data=None,timeout=3):
    # if we do post, we have to provide data.
    f=urllib2.urlopen(url,data,timeout)
    return f.read()

def queryColumn():
    print '----------queryColumn----------'
    request = message_pb2.ReadRequest()

    request.table_name='appbenchmark'
    request.row_key='2012-04-08_YULE'
    request.column_family='stat'
    request.qualifiers.append('14_day_active_count_avg')

    data = request.SerializeToString()
    data2 = raiseHTTPRequest('http://dp5:12345/read',data,timeout=20)

    response = message_pb2.ReadResponse()
    response.ParseFromString(data2)
    print response

def queryColumnFamily():
    print '----------queryColumnFamily----------'
    request = message_pb2.ReadRequest()

    request.table_name='appbenchmark'
    request.row_key='2012-04-08_YULE'
    request.column_family='stat'

    data = request.SerializeToString()
    data2 = raiseHTTPRequest('http://dp5:12345/read',data,timeout=20)

    response = message_pb2.ReadResponse()
    response.ParseFromString(data2)
    print response

def multiQuery():
    print '----------multiQuery----------'
    mRequest = message_pb2.MultiReadRequest()

    request = message_pb2.ReadRequest()
    request.table_name='appbenchmark'
    request.row_key='2012-04-08_YULE'
    request.column_family='stat'
    request.qualifiers.append('14_day_active_count_avg')
    mRequest.requests.extend([request])

    request = message_pb2.ReadRequest()
    request.table_name='appbenchmark'
    request.row_key='2012-04-08_YULE'
    request.column_family='stat'
    mRequest.requests.extend([request])

    data = mRequest.SerializeToString()
    data2 = raiseHTTPRequest('http://dp5:12345/multi-read',data,timeout=20)
    
    mResponse = message_pb2.MultiReadResponse()
    mResponse.ParseFromString(data2)
    print mResponse

def queryColumnLarge():
    print '----------queryColumnLarge----------'
    request = message_pb2.ReadRequest()

    request.table_name='appuserstat'
    request.row_key='2013-03-08_4d707f5e112cf75410007470'
    request.column_family='stat'
    request.qualifiers.append('models_1_lanCnt')

    data = request.SerializeToString()
    s = time.time()
    data2 = raiseHTTPRequest('http://dp5:12345/read',data,timeout=20)
    print "data size = %d bytes"%(len(data2))
    e = time.time()
    print 'time spent %.2lfs'%((e-s))

    response = message_pb2.ReadResponse()
    response.ParseFromString(data2)

    x = response.kvs[0].content
    xs = x.split("\f")
    print "models size = %d"%(len(xs))

def queryModels():
    print '----------Models----------'
    request = message_pb2.ReadRequest()

    request.table_name = 'appuserstat'
    request.row_key = '2013-03-21_4d707f5e112cf75410007470'
    request.column_family = 'stat'
    request.qualifiers.append('models_1_day_installCnt_values')

    data = request.SerializeToString()
    data2 = raiseHTTPRequest('http://dp5:12345/read',data,timeout=20)

    response = message_pb2.ReadResponse()
    response.ParseFromString(data2)

    xs = response.kvs[0].content.split("\0")
    for x in xs[:100]:
        ss = x.split("\f")
        print ss
        
def doUVEstimator():
    print '----------UVEstimator----------'
    mRequest = message_pb2.MultiReadRequest()

    dates = [
        '20130701',
        # '20130702',
        # '20130703',
        # '20130704',
        # '20130705',
        # '20130706',
        # '20130707'
        ]
    appkey = '4d707f5e112cf75410007470'
    
    for date in dates:
        rowkey = date + '_' + appkey
        print rowkey
        request = message_pb2.ReadRequest()
        request.table_name='test_uvestimator'
        request.row_key=rowkey
        request.column_family='stat'
        request.qualifiers.append('uv_estimate')
        mRequest.requests.extend([request])

    data = mRequest.SerializeToString()
    data2 = raiseHTTPRequest('http://dp5:12345/multi-read',data,timeout=20)
    
    mResponse = message_pb2.MultiReadResponse()
    mResponse.ParseFromString(data2)

    ok = True
    bucket = [0] * (1 << 8)
    for response in mResponse.responses:
        if response.error :
            print "Error : ", response.message
            ok = False
            break
        vs  = response.kvs[0].content.split(',')
        for i in range(0,len(vs)):
            v = int(vs[i])            
            bucket[i] = max(bucket[i],v)

    if ok:
        print bucket            
        print 2 ** (float(sum(bucket)) / len(bucket)) * len(bucket) * 0.79402
    
if __name__=='__main__':
    # queryColumn()
    queryColumnFamily()
    # multiQuery()
    # queryColumnLarge()
    # queryModels()
    # doUVEstimator()
    
    
