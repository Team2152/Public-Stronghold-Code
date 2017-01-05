# 02-21 update:
# added a 2d projection to 3d position ( x and z)
# also sends theta values
# program runs as startup by adding to /etc/rc.local before exit 0
# added a logfile, function now requires 2 arguments (ex: python dthread.py <path/logfile>

import socket, sys, os, traceback
from multiprocessing import Queue as pQueue
from multiprocessing import Process as pProcess
import threading
from Queue import Queue as tQueue
from time import sleep
import time
import math
import cv2.cv as cv
import numpy as np
import util
#import trackLoop
import cv2
import imutils
from collections import namedtuple
debugOn = False
loggingOn = False
xAngle = 9999
yAngle = 9999
shootAngle = 9999

lowHue = 93  #0 # 126
highHue = 241  #122 # 361
lowSat = 141 #40 # 70
highSat = 256 #100 # 255
lowIntensity = 0 #0 # 0
highIntensity = 100 #204 # 204

roborio = "10.21.52.2"

def goalTrack(frame):
  #(grabbed, frame) = camera.read()
  shooterXAngle = 9999.0
  shooterYAngle = 9999.0
  mask = cv2.inRange(frame, (lowHue,lowSat,lowIntensity), (highHue,highSat,highIntensity))
  mask = cv2.erode(mask, None, iterations=2)
  mask = cv2.dilate(mask, None, iterations=2)
  cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]

  if (len(cnts) > 0):
     cnts = sorted(cnts,key = cv2.contourArea, reverse = False)[:10]
     cnt = None
     for e in cnts:

        M = cv2.moments(e)
        cx = int(M['m10']/M['m00'])
        cy = int(M['m01']/M['m00'])
        cv2.line(frame, (cx,cy), (cx,cy), (255, 0, 255), 20)
        tempX,tempY,tempW,tempH = cv2.boundingRect(e)
        x,y,w,h = cv2.boundingRect(e)
        if (cv2.contourArea(e) < tempW * tempH / 2 and cv2.contourArea(e) > 400):
           cnt = e
          

     if (cnt != None):
        x,y,w,h = cv2.boundingRect(cnt)
        rect = cv2.minAreaRect(cnt)
        box = cv.BoxPoints(rect)
        box = np.int0(box)
        #cv2.drawContours(frame,[box],0,(255,0,0),2)
        hull = cv2.convexHull(cnt)
        cv2.drawContours(frame,[hull],0,(255,255,0),4)

        kNum = 0
        corners = []
        corners2 = []
        for k in hull:
           cv2.drawContours(frame,[k],0,(0,128,255),5)
           if (kNum == 0):
              kBef = len(hull)-1
           else:
              kBef = kNum-1
           if (kNum == len(hull)-1):
              kAft = 0
           else:
              kAft = kNum+1
           line1 = (hull[kBef][0][0]-k[0][0], hull[kBef][0][1]-k[0][1])
           line2 = (hull[kAft][0][0]-k[0][0], hull[kAft][0][1]-k[0][1])
           line3 = (hull[kAft][0][0]-hull[kBef][0][0], hull[kAft][0][1]-hull[kBef][0][1])
           line1Len = (line1[0]*line1[0]) + (line1[1]*line1[1])
           line1Len = math.sqrt(line1Len)
           line2Len = (line2[0]*line2[0]) + (line2[1]*line2[1])
           line2Len = math.sqrt(line2Len)
           line3Len = (line3[0]*line3[0]) + (line3[1]*line3[1])
           line3Len = math.sqrt(line3Len)
           tempNum = (line1Len*line1Len+line2Len*line2Len-line3Len*line3Len)/(2*line1Len*line2Len)
           angle = math.degrees(math.acos(tempNum))
           if (angle < 150):
              corners.append(k)
           kNum=kNum+1
          
        for an in corners:
           corners2.append(an[0])
           cv2.line(frame, (an[0][0],an[0][1]), (an[0][0],an[0][1]), (255,255,0), 20)
       
        testBox = sorted(corners2,key = lambda point: point[0])
        if (len(corners2) == 4):
           if (testBox[0][1] > testBox[1][1]):
              q1 = testBox[1]
           else:
              q1 = testBox[0]
           if (testBox[2][1] > testBox[3][1]):
              q2 = testBox[3]
           else:
              q2 = testBox[2]
           M = cv2.moments(e)
           cx = int(M['m10']/M['m00'])
           cy = int(M['m01']/M['m00'])
           cv2.line(frame, (cx,cy), (cx,cy), (255, 0, 255), 20)
           if (q2[1] > cy and q1[1] > cy):
              target = (int((q1[0]+q2[0])/2),int((q1[1]+q2[1])/2))
              cv2.line(frame, target, target, (255, 0, 0), 20)
              shooterXAngle = (((target[0])* 52.4)/640) - 26.2
              shooterYAngle = (((target[1])* -43.4)/480) + 22.7
              print "x: " + str(shooterXAngle)
              print "y: " + str(shooterYAngle)
           else:
              testBox = sorted(box,key = lambda corner:corner[0])
              if (testBox[0][1] > testBox[1][1]):
                 q1 = testBox[1]
              else:
                 q1 = testBox[0]
              if (testBox[2][1] > testBox[3][1]):
                 q2 = testBox[3]
              else:
                 q2 = testBox[2]
              target = (int((q1[0]+q2[0])/2),int((q1[1]+q2[1])/2))
              cv2.line(frame, target, target, (255, 255, 128), 20)
              shooterXAngle = (((target[0])* 52.4)/640) - 26.2
              shooterYAngle = (((target[1])* -43.4)/480) + 22.7
              print "x: " + str(shooterXAngle)
              print "y: " + str(shooterYAngle)
        else:
           testBox = sorted(box,key = lambda corner:corner[0])
           if (testBox[0][1] > testBox[1][1]):
              q1 = testBox[1]
           else:
              q1 = testBox[0]
           if (testBox[2][1] > testBox[3][1]):
              q2 = testBox[3]
           else:
              q2 = testBox[2]
           target = (int((q1[0]+q2[0])/2),int((q1[1]+q2[1])/2))
           cv2.line(frame, target, target, (255, 255, 128), 20)
           shooterXAngle = (((target[0])* 52.4)/640) - 26.2
           shooterYAngle = (((target[1])* -43.4)/480) + 22.7
           print "x: " + str(shooterXAngle)
           print "y: " + str(shooterYAngle)
          
    

  cv2.drawContours(frame, cnts, -1, (0,0,255), 3)
  frame = cv2.resize(frame,(320,240))
  mask= cv2.resize(mask,(320,240))
  cv2.line(frame,(int(320/2)-1,0),(int(320/2)-1,240-1), (0,255,0),1)
  cv2.imshow("Frame",frame)
  #cv2.imshow("Mask",mask)
  #cv2.waitKey(1)
  
     
  return shooterXAngle,shooterYAngle

def runProcessShooterCamera(r2mQ,rCommQ):
	cameraNumber = 0
	localStop = False
	try:
		camera = cv2.VideoCapture(cameraNumber)
	except:
		camera.release()
		print('camera error 34 in rproc') 
	while not localStop:
		shooterXAngle = 9999
		shooterYAngle = 9999
		retShooter, frameShooter = camera.read()
		(shooterXAngle, shooterYAngle) = goalTrack(frameShooter)
		keyValue = cv2.waitKey(5) & 0xFF
		if keyValue == ord('q'):
			camera.release()
			print "quiting shooter"
			running = False
			sleep(0.2)
			break
		elif keyValue == ord('r'):
			print "redoing Shooter camera "
			camera.release()
			sleep(0.3)
			camera = cv2.VideoCapture(cameraNumber)
		if shooterXAngle != 9999 or shooterYAngle != 9999:
			r2mQ.put("S,A" + str(shooterXAngle) + ",B" + str(shooterYAngle) + ",C" + str(0) + "@")
			#print("message got")
			util.specPrint(debugOn,(shooterXAngle,shooterYAngle)) 
		rCommQ.qsize()
		if not rCommQ.empty():
			util.specPrint(debugOn,"msg in rCommQ 57")
			cmd = rCommQ.get()
			if cmd == "stop":
				localStop = True
				util.specPrint(debugOn,"localstop = True 38")
				break
	cv2.destroyAllWindows()
	camera.release()

def runProcessFrontCamera(r2mQ,rCommQ):
  localStop = False
  global xAngle
  global yAngle
  global shootAngle
  cameraNumber = 1
  try:
     cameraWide = cv2.VideoCapture(cameraNumber)
     print "camera Captured"
  except:
     cameraWide.release()
     print('camera error 34 in rproc')
  try:
     cv2.namedWindow('back')
     cv2.setMouseCallback('back',findMouse)
  except:
	print('error 48qqqqq')
  while not localStop:
	retL, frameL = cameraWide.read()
	displayL = cv2.resize(frameL,(320,240))
	cv2.line(displayL,(int(320/2)-1,0),(int(320/2)-1,240-1), (0,255,0),1)
	cv2.imshow('back', displayL)
	keyValue = cv2.waitKey(1) & 0xFF
	if keyValue == ord('q'):
		cameraWide.release()
		print "quiting back feed"
		running = False
		sleep(0.2)
		break
	elif keyValue == ord('r'):
		print "redoing FEED camera "
		cameraWide.release()
		sleep(0.3)
		cameraWide = cv2.VideoCapture(cameraNumber)
	if xAngle != 9999 or yAngle != 9999 or shootAngle != 9999:
		r2mQ.put("G,A" + str(xAngle) + ",B" + str(yAngle) + ",C" + str(shootAngle) + "@")
		#print("message got")
		util.specPrint(debugOn,(xAngle,yAngle,shootAngle))
		xAngle = 9999
		yAngle = 9999
		shootAngle = 9999
	rCommQ.qsize()
	if not rCommQ.empty():
		util.specPrint(debugOn,"msg in rCommQ 57")
		cmd = rCommQ.get()
		if cmd == "stop":
			localStop = True
			util.specPrint(debugOn,"localstop = True 38")
			break
  cv2.destroyAllWindows()
  cameraWide.release()


global _tcpConnected
global progDone
_tcpConnected = True
progDone = False
progActuallyDone = False

#receives requests from RIO and pushes them to cmdQ
class TCPServerThread(threading.Thread):
  def __init__(self, port, cmdQ, btQ, sharedQ, logHandle):
     threading.Thread.__init__(self)
     self.logHandle = logHandle
     self.host = ''
     self.port = port
     self.cmdQ = cmdQ
     self.btQ = btQ
     self.lock = threading.Lock()
     #global _tcpConnected
     _tcpConnected = False
     self.sharedQ = sharedQ
     self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
     util.specPrint(debugOn,'Socket created')
    
  def run(self):
     util.specPrint(debugOn,'Beginning TCP Comms')
     self.bindSocket()
     self.seekConnection()
     while not progDone:
        try:
           if(_tcpConnected):
              self.recvData()
        except socket.error:
           util.specPrint(debugOn,'##Send failed')
           traceback.print_exc()
           break
     self.s.close()
     util.specPrint(debugOn,"Closing TCP socket (72)")
     util.logEntry(loggingOn,self.logHandle, "TCP was closed (93)")
     util.specPrint(debugOn,"dbg TCP thread ended")
    
  def recvData(self):
     #try:
     msg = ""
    
     if(_tcpConnected):
        #util.specPrint(debugOn,"Receiving data")
        msg = self.conn.recv(64)
        util.specPrint(debugOn,"TCP Msg: [" + msg + "]")
    
     if(msg == "" or msg == "Q" or msg == "kill"):
        self.lock.acquire()
        util.specPrint(debugOn,'terminate!')
        #self.conn.shutdown(socket.SHUT_RDWR)
        #self.conn.close()
        #global _tcpConnected
        _tcpConnected = False
        #global progDone
        progDone = True
        #self.btQ.put("stop")
        #self.btQ.put("kill")
        self.sharedQ.put("kill")
        util.specPrint(debugOn,"Connection terminated.")
        self.lock.release()
        #elif msg != "":
        #self.cmprint(msg)
     else:
        util.specPrint(debugOn,"Unknown command from rio with non-empty btqueue")
        util.specPrint(debugOn,str(self.btQ.qsize()))
        #util.specPrint(debugOn,self.btQ.get())
       
  def seekConnection(self):
     self.lock.acquire()
     self.s.listen(.1)
     util.specPrint(debugOn,"Socket listening (for Rio comms)")   
     self.conn, self.addr = self.s.accept()
     util.specPrint(debugOn,'TCP connection complete! Connected to ' + self.addr[0] + ':' + str(self.addr[1]))
     global _tcpConnected
     _tcpConnected = True
     self.lock.release()
    
  def bindSocket(self):
     try:
        self.s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.s.bind((self.host, self.port))
     except socket.error:
        util.specPrint(debugOn,'Bind failed (TCP). Try again? (y/n). Showing stack trace (127)')
        traceback.print_exc()
        yesno = raw_input()
        if yesno == 'y':
           self.bindSocket()
        else:
           os._exit(1)
     util.specPrint(debugOn,'Socket bind complete (TCP)')
    
#sends everything in sharedQ to RIO
class ClientUDPThread(threading.Thread):
  def __init__(self, host, port, msginit, cmdQ, logHandle, b2mQ):
     threading.Thread.__init__(self)
     self.logHandle = logHandle
     self.host = host
     self.port = port
     self.msginit = msginit
     self.cmdQ = cmdQ
     self.b2mQ = b2mQ
     self.s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
     util.specPrint(debugOn,"UDP init complete")

  def run(self):
     self.bindSocket()
     while not progDone:
        (self.msginit, addr) = self.s.recvfrom(1024)
        util.specPrint(debugOn,"any msginit received")
        if (self.msginit == "roborio on"):
           self.cmdQ.put(self.msginit)
           util.specPrint(debugOn,"correct msginit received")
           util.logEntry(loggingOn,self.logHandle, "UDP was connected (161)")
           break
     try:
        util.specPrint(debugOn,"Beginning UDP comms; sending initial message to rio")
        self.s.sendto("begin", (roborio, self.port))
        while not progDone:
           qLen = self.b2mQ.qsize()
          
           if not b2mQ.empty():
              #print("Sending data to UDP reciever")
              while not self.b2mQ.empty():
                 msg = self.b2mQ.get()
                 print(msg)
                 self.s.sendto(msg, (self.host, self.port))
           else:
              sleep(.01)
        #self.s.shutdown(socket.SHUT_RDWR)
        util.specPrint(debugOn,"Closing UDP socket (181)")
        self.s.close()
        util.logEntry(loggingOn,self.logHandle, "UDP was closed (179)")
     except socket.error:  
        util.specPrint(debugOn,"Send failed (176)")
        traceback.print_exc()
        #self.s.close()
        sys.exit()
     util.specPrint(debugOn,"dbg UDP thread ended")
       
  def bindSocket(self):
     try:
        self.s.bind(('', self.port))
     except socket.error:
        util.specPrint(debugOn,'Bind failed (UDP). Try again? (y/n) (185)')
        util.specPrint(debugOn,'Showing stack trace (186)')
        traceback.print_exc()
        yesno = raw_input()
        if yesno == 'y':
           self.bindSocket()
        else:
           os._exit(1)
     util.specPrint(debugOn,'Socket bind complete (UDP)')

class EndThread(threading.Thread):
  def __init__(self, logHandle):
     threading.Thread.__init__(self)
     self.logHandle = logHandle
  def run(self):
     global progActuallyDone
     global progDone
     while not progDone:
        raw_input()
        progDone = True
        progActuallyDone = True
       
def putCommQs(x):
  for q in commQs:
     q.put(x)
    
def findMouse(event,x,y,flags,param):
  global xAngle, yAngle, shootAngle
  if event == cv2.EVENT_LBUTTONDBLCLK:
     mouseLoc = (x,y)
     temp1 = 0.0000000849*float(x)*float(x)*float(x)
     temp2 = -0.0000840492*float(x)*float(x)
     temp3 = 0.0589577385*float(x)
     temp4 = (temp1+temp2+temp3-13.0915828993)/(0.252*55)
     xAngle = math.degrees(math.atan(temp4))
     temp5 = 0.0000000552*float(y)*float(y)*float(y)
     temp6 = -0.0000351767*float(y)*float(y)
     temp7 = 0.0377110880*float(y)
     temp8 = (temp5+temp6+temp7-7.82491214)/(0.252*52.5)
     yAngle = math.degrees(math.atan(temp8))
     distance = (1.764*29/12)/(math.tan(math.radians(yAngle)))
     shootAngle = math.degrees(math.atan(distance*(math.tan(math.radians(xAngle)))/(distance+(14.75)*0.252)))
     print("mouse 1 found:")

    
logFile = ""
       
numArgs = len(sys.argv)
util.specPrint(debugOn,numArgs)
util.specPrint(debugOn, sys.argv[0])
if (numArgs != 2):
  util.specPrint(debugOn,"python dthread.py requires a logfile name")
  util.specPrint(debugOn,"python dthread.py <path/logfile>")
  os._exit(1)
else:
  util.specPrint(debugOn,sys.argv[1])
  logFile = sys.argv[1]
logHandle = open(logFile, "a")
util.logEntry(loggingOn,logHandle, "PROGRAM INIT; NEW LOG ENTRY/USE (246)")

#destHost = "roborio-2152-frc.local"
tcpPort = 5801
udpPort = 5801
msginit = "waiting for roborio comms"

debugQ = pQueue()
#debugQ.put("got debug")

rCommQ = pQueue()
shooterCommQ = pQueue()
commQs = [rCommQ,shooterCommQ]
#imgQs = [p2bQ, p2vQ]
b2mQ = pQueue()
#testQ = tQueue();

if __name__ == '__main__':
  pass
  '''
  p = pProcess(target=processWorker, args=(p2bQ, pCommQ, p2vQ, debugQ))
  p.start()
 
  b = pProcess(target=ballTrackProcess, args=(p2bQ, bCommQ, b2mQ, debugQ))
  b.start()
 
  v = pProcess(target=viewFeed, args=(p2vQ,vCommQ, debugQ))
  v.start()
  '''
  shooterProcess = pProcess(target=runProcessShooterCamera, args=(b2mQ, shooterCommQ))
  shooterProcess.start()
  
  frontProcess = pProcess(target=runProcessFrontCamera, args=(b2mQ, rCommQ))
  frontProcess.start()


while not progActuallyDone:
  util.logEntry(loggingOn,logHandle, "Program Start/Restart (256)")
  #global progDone
  progDone = False
  sharedQ = tQueue() #used to communicate between threads
  cmdQ = tQueue() #used to receive requests for data from the RIO
  btQ = tQueue() #used to control ball track thread
 
  endThread = EndThread(logHandle)
  endThread.start()

  #sends everything in sharedQ to RIO
  udpClient = ClientUDPThread(roborio, udpPort, msginit, cmdQ, logHandle, b2mQ)
  udpClient.start()

  while not progDone:
     #print (msginit)
     if(not cmdQ.empty()):
        msginit = cmdQ.get()
     if (msginit == "roborio on"):
        util.specPrint(debugOn,"TCP Initializing")
        util.logEntry(loggingOn,logHandle, "TCP was connected (278)")
        break
     else:
        #util.specPrint(debugOn,(str(cmdQ.qsize())))
        pass
  #pushes data to sharedQ depending on requests in cmdQ
  tcpServer = TCPServerThread(tcpPort, cmdQ, btQ, sharedQ, logHandle)
  tcpServer.start()
  #misc = MiscThread(sharedQ, cmdQ)

  #trackBall = BallTrackThread(sharedQ, btQ, logHandle)

  #receives requests from RIO and pushes them to cmdQ

  #misc.start()
  #trackBall.start()
  while not progDone:
     pass
  if progActuallyDone:
     #pCommQ.put("stop")
     putCommQs("stop")
     util.specPrint(debugOn,"Manually stopping program (269)")
     util.logEntry(loggingOn,logHandle, "Program manually stopped (298)")
     logHandle.close()
     try:           
        if __name__ == '__main__':
           shooterProcess.join(1)
           frontProcess.join(1)
     except Exception:
        util.specPrint(debugOn, ("Line 504 Unexpected error:", sys.exc_info()[0]))
     while not debugQ.empty():
        util.specPrint(debugOn,"dbg " + debugQ.get())
     util.specPrint(debugOn,"lastline")
     os._exit(1)
  else:
     util.specPrint(debugOn,"Program Ended! Restarting in 1 seconds... (262)")
     sleep(1)
     if progActuallyDone: #just in case
        util.specPrint(debugOn,"Manually stopping program (275)")
        util.logEntry(loggingOn,logHandle, "Program manually stopped (303)")
        logHandle.close()
        try:
           util.specPrint(debugOn,"before join 375")
           frontProcess.join(3)
           frontProcess.terminate()
           shooterProcess.join(3)
           shooterProcess.terminate()
           util.specPrint(debugOn,"after join 377")
        except Exception:
           util.specPrint(debugOn,"error 379")
        #while not debugQ.empty():
        #  util.specPrint(debugOn,"dbg " + debugQ.get())
        util.specPrint(debugOn,"last line")
        os._exit(1)




