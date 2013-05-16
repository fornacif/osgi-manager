@echo off

set JDK_HOME=D:/Program Files/Java/jdk1.7.0_21

set PATH="%JDK_HOME%/bin"

SET CLASSPATH="%JDK_HOME%/jre/lib/jfxrt.jar"
SET CLASSPATH=%CLASSPATH%;"%JDK_HOME%/lib/tools.jar"
SET CLASSPATH=%CLASSPATH%;../org.eclipse.osgi-${equinox.version}.jar

"%JDK_HOME%/bin/java.exe" -Dlogback.configurationFile=../configuration/logback.xml -cp %CLASSPATH% org.eclipse.core.runtime.adaptor.EclipseStarter -console