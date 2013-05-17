@echo off

SET DIR=%~dp0
set JDK_HOME=D:/Program Files/Java/${jdk.version}

SET CLASSPATH="%JDK_HOME%/jre/lib/jfxrt.jar"
SET CLASSPATH=%CLASSPATH%;"%JDK_HOME%/lib/tools.jar"
SET CLASSPATH=%CLASSPATH%;"%DIR%/../org.eclipse.osgi-${equinox.version}.jar"

"%JDK_HOME%/bin/java.exe" -Dlogback.configurationFile="%DIR%/../configuration/logback.xml" -cp %CLASSPATH% org.eclipse.core.runtime.adaptor.EclipseStarter -console