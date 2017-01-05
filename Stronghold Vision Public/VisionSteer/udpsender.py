

import socket, sys, os, traceback
from multiprocessing import Queue as pQueue
from multiprocessing import Process as pProcess
import threading
from Queue import Queue as tQueue
from time import sleep
import time
import util
#import trackLoop
import visionsteer
from collections import namedtuple
global debugOn
global loggingOn
global progDone
debugOn = True
loggingOn = True
progDone = False

#Create queues
queueData = pQueue()
queueTrackProcess = pQueue()
global dataQs
dataQs = [queueData]
global cmdQs
cmdQs = [queueTrackProcess]

#default to PizzaBot values, but you will have to enter them in the 
#invokation of this script
roborio = "10.99.11.21"
udpPort = 5807

########################################################################
    
#sends everything in sharedQ to RIO
class ClientUDPThread(threading.Thread):
	global debugOn
	def __init__(self, host, port, logHandle, queueToCheck):
		threading.Thread.__init__(self)
		self.logHandle = logHandle
		self.host = host
		self.port = port
		self.queueToCheck = queueToCheck
		self.s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		util.specPrint(debugOn,"ClientUDPThread: UDP init complete")

	def run(self):
		bindOk = self.bindSocket()
		if not bindOk:
			util.specPrint(debugOn,"ClientUDPThread: Unable to continue due to bind error")
			sys.exit()
		try:
			util.specPrint(debugOn,"ClientUDPThread: Beginning UDP comms")
			self.s.sendto("begin", (self.host, self.port))
			while not progDone:
				if not self.queueToCheck.empty():
					while not self.queueToCheck.empty():
						msg = self.queueToCheck.get()
						#print("UDPSender: " + msg)
						self.s.sendto(msg, (self.host, self.port))
				else:
					sleep(.01)        
			util.specPrint(debugOn,"ClientUDPThread: Closing UDP socket")
			self.s.close()
			util.logEntry(loggingOn,self.logHandle, "ClientUDPThread: UDP was closed")
		except socket.error:  
			util.specPrint(debugOn,"ClientUDPThread: Send failed")
			traceback.print_exc()
			sys.exit()
		util.specPrint(debugOn,"ClientUDPThread: UDP thread ended")

	def bindSocket(self):
		bindWorked = False
		try:
			self.s.bind(('', self.port))
			bindWorked = True
			util.specPrint(debugOn,'ClientUDPThread: Socket bind complete (UDP)')
		except socket.error:
			util.specPrint(debugOn,'ClientUDPThread: Bind failed (UDP).')
			util.specPrint(debugOn,'ClientUDPThread: Showing stack trace')
			traceback.print_exc()
		return bindWorked
		
########################################################################

class EndThread(threading.Thread):
	def __init__(self, logHandle):
		threading.Thread.__init__(self)
		self.logHandle = logHandle
	def run(self):
		global progDone
		global debugOn
		while not progDone:
			raw_input()
			util.specPrint(debugOn, 'EndThread: received shutdown command.')
			progDone = True
		util.specPrint(debugOn, 'EndThread: exiting.')
        
########################################################################        
       
def putInCmdQ(x):
  global cmdQs
  for q in cmdQs:
     q.put(x)

########################################################################        
       
def putInDataQ(x):
  global dataQs
  for q in dataQs:
     q.put(x)
    
## MAIN ################################################################

logFile = ""      
numArgs = len(sys.argv)
if (numArgs != 4):
	util.specPrint(debugOn, "Invalid number of parameters.")
	util.specPrint(debugOn, "python udpsender.py <logfile> <roborioipaddress> <roborioport>")
	util.specPrint(debugOn, "python udpsender.py log.txt   10.99.11.21        5807")
	os._exit(1)
else:
	util.specPrint(debugOn,sys.argv[1])
	logFile = sys.argv[1]
	roborio = sys.argv[2]
	udpPort = int(sys.argv[3])

#Open up the log file
logHandle = open(logFile, "a")
util.logEntry(loggingOn, logHandle, "PROGRAM INIT; NEW LOG ENTRY/USE")
util.logEntry(loggingOn, logHandle, "Roborio:  " + roborio)
util.logEntry(loggingOn, logHandle, "UDP Port: " + str(udpPort))

#Start Vision Steer process
if __name__ == '__main__':
  pass
  trackProcess = pProcess(target=visionsteer.visionSteer, args=(queueTrackProcess, queueData))
  trackProcess.start()

#Start EndThread, UDP and wait until done
util.logEntry(loggingOn,logHandle, "Program Start/Restart")

#Starts and waits for raw input (any input); ends program once received  
endThread = EndThread(logHandle)
endThread.start()

#Sends everything in data queue to roborio
udpClient = ClientUDPThread(roborio, udpPort, logHandle, queueData)
udpClient.start()

#Wait
while not progDone:
  pass

#stopping processes
putInCmdQ("stop")
sleep(1)
util.specPrint(debugOn,"Manually stopping program")
util.logEntry(loggingOn,logHandle, "Program manually stopped")
logHandle.close()
try:           
   if __name__ == '__main__':
      trackProcess.join(1)
except Exception:
   util.specPrint(debugOn, ("Unexpected error:", sys.exc_info()[0]))
util.specPrint(debugOn,"Exiting script")
os._exit(1)




