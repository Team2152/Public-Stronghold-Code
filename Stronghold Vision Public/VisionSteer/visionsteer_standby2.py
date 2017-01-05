# USAGE
# NOT to be used on its own
# Meant to be called from udpsender.py


# import the necessary packages
from collections import deque
import numpy as np
import argparse
import imutils
import cv2 as cv
import cv2
import math as m
import util
from time import sleep
import matplotlib


# construct the argument parse and parse the arguments
global minSize
global maxSize
global defaultSize
global mSide
mSide = 0
minSize = 200
maxSize = 300
defaultSize = 255


def calibrate(event, x, y, flags, param):
    # grab references to the global variables
    global maxSize
    global minSize
    global defaultSize
    global mSide
    if event == cv2.EVENT_LBUTTONDBLCLK:
        if (y > 300):
            maxSize = int(mSide)
            print "maxSide: " + str(maxSize)
        elif (y < 180):
            minSize = int(mSide)
            print "minSide: " + str(minSize)
        else:
            defaultSize = int(mSide)
            print "defaultSize: " + str(defaultSize)

def visionSteer():
    global maxSize
    global minSize
    global defaultSize
    global mSide
    leftDefault = 200
    rightDefault = 420
    woah = np.zeros((480,640,3), np.uint8)
    cv2.imshow("Arrows", woah)
    cv2.setMouseCallback("Arrows", calibrate)
    greenLower = (45, 86, 99)
    greenUpper = (70, 255, 255)
    # "Gray"
    # greenLower = (0, 0, 170)
    # greenUpper = (63, 55, 235)

    pts = deque(maxlen=64)

    # if a video path was not supplied, grab the reference
    # to the web cam
    camera = cv2.VideoCapture(0)

    # keep looping
    while True:
        # grab the current frame
        lowMidPoint = (defaultSize + minSize) / 2
        highMidPoint = (defaultSize + maxSize) / 2
        (grabbed, frame) = camera.read()
        if not grabbed:
			print "Not Grabbed"
			break
        # resize the frame and convert it to the HSV
        frame = cv2.flip(frame, 1)
        woah = np.zeros((480,640,3), np.uint8)
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
        x = 0
        while x < len(cnts):
            if cv2.contourArea(cnts[x]) < 200:
                cnts[x] = np.array([[-40, -40]])
            # cnts[x] = None
            x += 1
        #  only proceed if at least one contour was found
        if len(cnts) > 0:
            # find the largest contour in the mask, then use
            cnts = sorted(cnts, key=cv2.contourArea, reverse=False)
            cnt = cnts[len(cnts) - 1]
            if (cnt != None and cnt.all() != np.array([[-40, -40]])).all():
                rect = cv2.minAreaRect(cnt)
                box = np.int0(cv.boxPoints(rect))
                cv2.drawContours(frame, [box], 0, (255, 0, 0), 2)
                center = (int((box[1][0] + box[2][0] + box[0][0] + box[3][0]) / 4),
                          int((box[1][1] + box[2][1] + box[0][1] + box[3][1]) / 4))
                cv2.line(frame, center, center, (255, 0, 0), 20)
                sides = [0, 0, 0, 0, 0]
                sides[0] = m.sqrt((box[0][0] - box[1][0]) * (box[0][0] - box[1][0]) + (box[1][1] - box[0][1]) * (
                box[1][1] - box[0][1]))
                sides[1] = m.sqrt((box[1][0] - box[2][0]) * (box[1][0] - box[2][0]) + (box[2][1] - box[1][1]) * (
                box[2][1] - box[1][1]))
                sides[2] = m.sqrt((box[2][0] - box[3][0]) * (box[2][0] - box[3][0]) + (box[3][1] - box[2][1]) * (
                box[3][1] - box[2][1]))
                sides[3] = m.sqrt((box[3][0] - box[0][0]) * (box[3][0] - box[0][0]) + (box[0][1] - box[3][1]) * (
                box[0][1] - box[3][1]))
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
                    j = i + 1
                if box[i][0] < box[j][0]:
                    point1 = box[i]
                    point2 = box[j]
                else:
                    point1 = box[j]
                    point2 = box[i]
                #cv2.line(frame, tuple(point1), tuple(point2), (0, 0, 255), 2)
                adj = point2[0] - point1[0]
                opp = point2[1] - point1[1]
                if adj != 0:
                    angle = m.degrees(m.atan(float(opp) / float(adj)))
                else:
                    angle = 0
                distance = mSide - defaultSize
                horizontal = center[0]
                if all((p[0] > 10 and p[0] < 630 and p[1] > 10 and p[1] < 470) for p in box):
					forward = mSide > highMidPoint
					backward = mSide < lowMidPoint and distance != 0
					turnLeft = angle < -20 and forward == False and backward == False
					turnRight = angle > 20 and forward == False and backward == False
					strafeLeft = horizontal < leftDefault and forward == False and backward == False and turnLeft == False and turnRight == False
					strafeRight = horizontal > rightDefault and forward == False and backward == False and turnLeft == False and turnRight == False
                else:
					forward = False
					backward = False
					turnLeft = False
					turnRight = False
					strafeLeft = False
					strafeRight = False
                #dataQ.put(str(forward) + ";" + str(backward) + ";" + str(turnLeft) + ";" + str(turnRight) + ";" + str(
                #    strafeLeft) + ";" + str(strafeRight) + ";end")

                if (forward):
                    cv2.fillConvexPoly(woah, np.array([(280, 180), (320, 80), (360, 180)]), (0, 0, 255))
                elif (backward):
                    cv2.fillConvexPoly(woah, np.array([(280, 300), (320, 400), (360, 300)]), (0, 0, 255))
                elif (turnLeft):
                    cv2.fillConvexPoly(woah, np.array([(260, 200), (160, 240), (260, 280)]), (0, 0, 255))
                elif (turnRight):
                    cv2.fillConvexPoly(woah, np.array([(380, 200), (480, 240), (380, 280)]), (0, 0, 255))
                elif (strafeLeft):
                    cv2.circle(woah, (320, 240), 20, (255, 0, 0), -1)
                elif (strafeRight):
                    cv2.circle(woah, (320, 240), 20, (0, 255, 0), -1)
                #print "Forward: " + str(forward) + "; Backward: " + str(backward) + "; TurnLeft: " + str(
                #    turnLeft) + "; TurnRight: " + str(turnRight) + "; StrafeLeft: " + str(
                #    strafeLeft) + "; StrafeRight: " + str(strafeRight) + ";end"
        # show the frame to our screen
        cv2.imshow("Frame", frame)
        cv2.imshow("Arrows", woah)

        key = cv2.waitKey(1) & 0xFF
        # if the 'q' key is pressed, stop the loop
        if key == ord("q"):
			print "q quit"
			break
        # stop if we are asked to by the main process / caller

    # clean up the camera and close any open windows
    camera.release()
    cv2.destroyAllWindows()

# end of function
visionSteer();