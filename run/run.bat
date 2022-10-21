if "%1"=="hide" goto CmdBegin
start mshta vbscript:createobject("wscript.shell").run("""%~0"" hide",0)(window.close)&&exit
:CmdBegin
F:\jdk\jdk17\bin\java.exe -Dfile.encoding=GBK -jar F:\project\I2HS_Client\run\WhyPPSStart-1.0-SNAPSHOT.jar frameworkPath:WhyPPSFramework-1.0-SNAPSHOT.jar