# based on: http://stackoverflow.com/a/23231027/98528
# run with:
#  %ANDROID_HOME%\tools\monkeyrunner.bat %cd%\screencap.py \tmp\s07.png
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
import sys
device = MonkeyRunner.waitForConnection()
result = device.takeSnapshot()
result.writeToFile(sys.argv[1] if len(sys.argv)>1 else '/tmp/screenshot.png','png')