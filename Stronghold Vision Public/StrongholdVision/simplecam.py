import cv2
from time import sleep

#ALL IMPORTS STATEMENTS SHOULD BE ABOVE THIS LINE
#BEGIN CODE

def cameraCall(windowName, cameraNumber, imageWidth, imageHeight, makeMidLine):
	running = True
	cv2.namedWindow(windowName)
	camera = cv2.VideoCapture(cameraNumber)
	while running:
		if True:
			keyValue = cv2.waitKey(5) & 0xFF
			
			if keyValue == ord('q'):
				camera.release()
				print "quiting " + windowName
				running = False
				sleep(0.2)
				break
			elif keyValue == ord('r'):
				print "redoing camera " + windowName
				camera.release()
				sleep(0.3)
				camera = cv2.VideoCapture(cameraNumber)
			ret, frame = camera.read()
			display = cv2.resize(frame,(imageWidth,imageHeight))
			if makeMidLine:
				cv2.line(display,(int(imageWidth/2)-1,0),(int(imageWidth/2)-1,imageHeight-1), (0,255,0),1)
			cv2.imshow(windowName, display)
	camera.release()
	running = False
	
cameraCall("feed", 0, 320, 240, False)




