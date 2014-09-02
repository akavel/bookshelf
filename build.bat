@echo off
setlocal

set ANDROID_HOME=C:\dnload\komrka~1\adt-bundle-windows-x86_64-20130729\sdk
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%
:: android sdk v. 2.1
set ANDROID_PLATFORM=android-7

::set JAVA_HOME=c:\dnload\java-etc\openjdk_1.6.0_b28_64
::set JAVA_HOME=c:\Program Files\Java\jdk1.7.0_25
set JAVA_HOME=c:\Program Files\Java\jdk1.6.0_38

::cmd /k

::call ant.bat clean
call ant.bat debug

endlocal