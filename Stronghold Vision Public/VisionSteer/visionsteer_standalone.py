# USAGE
# python visionsteer.py


# import the necessary packages
from collections import deque
import numpy as np
import argparse
import imutils
import cv2.cv as cv
import cv2
import math as m
# construct the argument parse and parse the arguments

defaultSize = 250
minSize = 200
maxSize = 300
lowMidPoint = (defaultSize + minSize) / 2
highMidPoint = (defaultSize + maxSize) / 2
leftDefault = 200
rightDefault = 420


"Green"
greenLower = (45, 86, 99)
greenUpper = (70, 255, 255)

"Gray"
# greenLower = (0, 0, 170)
# greenUpper = (63, 55, 235)

pts = deque(maxlen=64)

# if a video path was not supplied, grab the reference
# to the web cam
camera = cv2.VideoCapture(0)

# keep looping
while True:
    # grab the current frame
    (grabbed, frame) = camera.read()
    if not grabbed:
        break
    # resize the frame, blur it, and convert it to the HSV
    # color space
    frame = imutils.resize(frame, width=600)
    frame = cv2.flip(frame, 1)
    woah = imutils.resize(frame, width=600)
    blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    # construct a mask for the color "green", then perform
    # a series of dilations and erosions to remove any small
    # blobs left in the mask
    mask = cv2.inRange(hsv, greenLower, greenUpper)
    mask = cv2.erode(mask, None, iterations=2)
    mask = cv2.dilate(mask, None, iterations=2)
    
    # find contours in the mask and initialize the current
    cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
                            cv2.CHAIN_APPROX_SIMPLE)[-2]
    x=0
    while x < len(cnts):
        if cv2.contourArea(cnts[x]) < 200:
            cnts[x] = np.array([[-40,-40]])
            # cnts[x] = None
        x+=1
    center = None
    # only proceed if at least one contour was found
    if len(cnts) > 0:
        # find the largest contour in the mask, then use
        cnts = sorted(cnts,key = cv2.contourArea, reverse = False)
        for e in cnts:
			cnt = e
        if (cnt != None and cnt.all() != np.array([[-40, -40]])).all():
            rect = cv2.minAreaRect(cnt)
            box = cv.BoxPoints(rect)
            box = np.int0(box)
            cv2.drawContours(frame, [box], 0, (255, 0, 0), 2)
            center = (int((box[1][0] + box[2][0] + box[0][0]+box[3][0]) / 4), int((box[1][1] + box[2][1] + box[0][1] + box[3][1]) / 4))
            cv2.line(frame, center, center, (255, 0, 0), 20)
            sides = [0,0,0,0,0]
            sides[0] = m.sqrt((box[0][0] - box[1][0])*(box[0][0] - box[1][0]) + (box[1][1] - box[0][1])*(box[1][1] - box[0][1]))
            sides[1] = m.sqrt((box[1][0] - box[2][0])*(box[1][0] - box[2][0]) + (box[2][1] - box[1][1])*(box[2][1] - box[1][1]))
            sides[2] = m.sqrt((box[2][0] - box[3][0])*(box[2][0] - box[3][0]) + (box[3][1] - box[2][1])*(box[3][1] - box[2][1]))
            sides[3] = m.sqrt((box[3][0] - box[0][0])*(box[3][0] - box[0][0]) + (box[0][1] - box[3][1])*(box[0][1] - box[3][1]))
            sides[4] = sides[0]
            mSide = np.amax(sides)
            i = 0
            while (i < 4):
                if (sides[i] == mSide):
                    break
                i += 1
            if (i == 3):
                j = 0
            else:
                j = i+1
            if box[i][0] < box[j][0]:
                point1 = box[i]
                point2 = box[j]
            else:
                point1 = box[j]
                point2 = box[i]
            cv2.line(frame, tuple(point1), tuple(point2), (0, 0, 255), 2)
            cv2.line(frame, tuple(point1), tuple(point1), (0, 255, 0), 6)
            adj = point2[0] - point1[0]
            if adj != 0:
                if (point1[1] < point2[1]):
                    opp = point1[1] - point2[1]
                else:
                    opp = point2[1] - point1[1]
                opp = point2[1] - point1[1]
                angle = m.degrees(m.atan(float(opp)/float(adj)))
                print angle
            else:
                angle = 0
            distance = mSide - defaultSize
            horizontal = center[0]
            turnLeft = angle < -30
            turnRight = angle > 30
            strafeLeft = horizontal < leftDefault
            strafeRight = horizontal > rightDefault
            forward = mSide > highMidPoint
            backward = mSide < lowMidPoint
            if (strafeLeft):
                cv2.circle(woah, (320,240), 20, (255,0,0), -1)
                forward = False
                backward = False
                turnLeft = False
                turnRight = False
            if (strafeRight):
                cv2.circle(woah, (320,240), 20, (0,255,0), -1)
                forward = False
                backward = False
                turnLeft = False
                turnRight = False
            cv2.rectangle(woah, (0,0),(640,480),(0,0,0),thickness=-1)
            if distance == 0:
                backward = False
            if (forward):
                cv2.fillConvexPoly(woah,np.array([(280,180),(320,80),(360,180)]),(0,0,255))
            if (backward):
                cv2.fillConvexPoly(woah,np.array([(280,300),(320,400),(360,300)]),(0,0,255))
            if (turnLeft):
                cv2.fillConvexPoly(woah,np.array([(260,200),(160,240),(260,280)]),(0,0,255))
            if (turnRight):
                cv2.fillConvexPoly(woah,np.array([(380,200),(480,240),(380,280)]),(0,0,255))
            #print mSide
            #print int(angle)
            print "F: " + str(forward) + " ####  D: " + str(distance) + " ####  B: " + str(backward) + " #### " + "A: " + str(mSide)
    # show the frame to our screen

    #cv2.imshow("Frame", frame)
    #cv2.imshow("Mask", mask)
    cv2.imshow("Arrows", woah)

    key = cv2.waitKey(1) & 0xFF

    # if the 'q' key is pressed, stop the loop
    if key == ord("q"):
        break

# cleanup the camera and close any open windows
camera.release()
cv2.destroyAllWindows()
