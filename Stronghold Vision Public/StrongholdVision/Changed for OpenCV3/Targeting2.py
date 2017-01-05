import cv2
#import cv2.cv as cv
import numpy as np
import math as m
lowVal = (0, 90, 112)
highVal = (20, 201, 255)
camera = cv2.VideoCapture(0)
global lowHue
global highHue
global lowSat
global highSat
global lowIntensity
global highIntensity

lowHue = 0 # 126
highHue = 122 # 361
lowSat = 40 # 70
highSat = 100 # 255
lowIntensity = 0 # 0
highIntensity = 204 # 204

cv2.namedWindow("Trackbars",0)
def callback(value):
    global lowHue
    lowHue = value
def callback2(value):
    global highHue
    highHue = value
def callback3(value):
    global lowSat
    lowSat = value
def callback4(value):
    global highSat
    highSat = value
def callback5(value):
    global lowIntensity
    lowIntensity = value
def callback6(value):
    global highIntensity
    highIntensity = value
cv2.createTrackbar("lowHue","Trackbars",lowHue,360,callback)
cv2.createTrackbar("highHue","Trackbars",highHue,360,callback2)
cv2.createTrackbar("lowSat","Trackbars",lowSat,256,callback3)
cv2.createTrackbar("highSat","Trackbars",highSat,256,callback4)
cv2.createTrackbar("lowIntensity","Trackbars",lowIntensity,256,callback5)
cv2.createTrackbar("highIntensity","Trackbars",highIntensity,256,callback6)
while True:
	(grabbed, frame) = camera.read()
	#cv2.imshow("testing",frame)
	mask = cv2.inRange(frame, (lowHue,lowSat,lowIntensity), (highHue,highSat,highIntensity))
	mask = cv2.erode(mask, None, iterations=2)
	mask = cv2.dilate(mask, None, iterations=2)
	
	cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]
	lines = frame.copy()

	"""
	for i in lineMatrix[0]:
		rho = i[0]
		theta = i[1]
		a = m.cos(float(theta))
		b = m.sin(float(theta))
		x0 = float(a)*rho
		y0 = float(b)*rho
		point1 = (int(x0+1000*float(-b)),int(x0+1000*float(a)))
		point2 = (int(x0-1000*float(-b)),int(x0-1000*float(a)))
		cv2.line(lines,point1,point2,(255,255,255))
	"""

	
	#cv2.imshow("lines",lines)
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
			box = cv2.boxPoints(rect)
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
				line1Len = m.sqrt(line1Len)
				line2Len = (line2[0]*line2[0]) + (line2[1]*line2[1])
				line2Len = m.sqrt(line2Len)
				line3Len = (line3[0]*line3[0]) + (line3[1]*line3[1])
				line3Len = m.sqrt(line3Len)
				tempNum = (line1Len*line1Len+line2Len*line2Len-line3Len*line3Len)/(2*line1Len*line2Len)
				angle = m.degrees(m.acos(tempNum))
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
				target = (int((q1[0]+q2[0])/2),int((q1[1]+q2[1])/2))
				cv2.line(frame, target, target, (255, 0, 0), 20)
				xAngle = (((target[0])* 52.4)/640) - 26.2
				yAngle = (((target[1])* -43.4)/480) + 22.7
				print "x: " + str(xAngle)
				print "y: " + str(yAngle)
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
				xAngle = (((target[0])* 52.4)/640) - 26.2
				yAngle = (((target[1])* -43.4)/480) + 22.7
				print "x: " + str(xAngle)
				print "y: " + str(yAngle)
				
		

	cv2.drawContours(frame, cnts, -1, (0,0,255), 3)
	frame = cv2.resize(frame,(320,240))
	mask= cv2.resize(mask,(320,240))
	cv2.imshow("Frame",frame)
	cv2.imshow("Mask",mask)
	key = cv2.waitKey(1) & 0xFF
	if key == ord("q"):
		break
camera.release()


