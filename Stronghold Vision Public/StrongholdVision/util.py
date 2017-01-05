import time
import math
import datetime
from Queue import Queue as tQueue
from multiprocessing import Queue as pQueue

'''
Converts a queue to a string with delimiters
Example: {("a", 4), ("b", 3)} -> "a4;b4@"
'''

		
def shrinkQs(qList):
	while qList.qsize() > 1:
		qList.get()

def getTime():
	ts = datetime.datetime.fromtimestamp(time.time()).strftime('%Y-%m-%d %H:%M:%S')
	return ts
	
def logEntry(debug, fileHandle, msg):
	if debug == True:
		fileHandle.write(getTime() + ": " + msg + "\n")

def specPrint(debug, statement):
	if debug == True:
		print(statement)
	
def queueToString(q):
	result = ""
	for i in range(0, q.qsize()):
		(k,v) = q.get()
		if(i < q.qsize()):
			endDelimiter = ";"
		else:
			endDelimiter = "@"	
		result += str(k) + str(v) + endDelimiter
	return result

'''
Converts a dict{str : dict{str : int}} into a string.
For example, the following:

objDict = { 'b' : { 'x' : 43,
                    'y' : 76,
                    'd' : 10
                  },
            'w' : { 'x' : 7,
                    'y' : 5,
                    'd' : 4
                  }
		  }
		  
would result in "b,y76,x43,d10;w,y5,x7@"
'''
def dictToString(theDict):
	result = ""
	for (n,d) in theDict.iteritems():
		result += n + ','
		for (k,v) in d.iteritems():
			result += k + str(v) + ','
		result = result[:-1] + ';'
	result = result[:-1] + '@'
	return result

#test
'''
q = Queue()
data = [("i", 4), ("n", 32)] 
for kv in data:
	self.outQ.put(kv)
print(queueToString(q))
'''
def virttoRealTrig(imgx,dist):
	fl = .13878
	camX = pxltoDist(imgx)
	theta = (math.atan(camX/fl))
	x = dist * math.sin(theta)
	z = dist * math.cos(theta)
	return (x, z, math.degrees(theta))

def virttoReal(imgx,dist):
	fl = .13878
	realx = (pxltoDist(imgx) * dist/(fl**2 + pxltoDist(imgx)**2)**.5)
	#print realx
	realz = ((dist**2 - realx**2)**.5)
	#print realz
	theta = (recttoPolar(pxltoDist(imgx), fl))
	#print theta
	return (realx, realz, theta) 
	
def pxltoDist(pixels):
	realDist = pixels*2*.00019676
	return realDist

def recttoPolar(imgx, focal):
	return math.atan(imgx/focal)/3.14*180
