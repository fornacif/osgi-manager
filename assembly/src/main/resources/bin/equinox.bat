@echo off

SET DIR=%~dp0
set JDK_HOME=${jdk.home}

SET CLASSPATH="%JDK_HOME%/jre/lib/jfxrt.jar"
SET CLASSPATH=%CLASSPATH%;"%JDK_HOME%/lib/tools.jar"
SET CLASSPATH=%CLASSPATH%;"%DIR%/../org.eclipse.osgi-${equinox.version}.jar"

start "OSGi Manager" "%JDK_HOME%/bin/java.exe" -Dlogback.configurationFile="%DIR%/../configuration/logback.xml" -cp %CLASSPATH% org.eclipse.core.runtime.adaptor.EclipseStarter -console