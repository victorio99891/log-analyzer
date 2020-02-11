# Log Analayzer
A simple CLI-base tool which can help you with your log analysis. 

## Short description: 

Base features:
- parsing *.log files
- parsing *.log files rolled in *.zip, but only in specific format (described below)
- logs for analysis comes from base directory for provided path and subdirectory called "archive"
- logs for analysis should contain *SysMonError* in file name
- storage history of analyzed *.log files as JSON file
- generation of Excel document based on JSON history file

Format of analyzed logs (sections are cut by pipe separator): 

>[LOG|ERROR|2020 January 15, 06:35:30 (584)|TestServer|/var/log/Server_141012.log]
>
>[END]
> 
>com.marketdata.MarketDataException: No Currency Default Set up for null
>	at com.util.CurrencyUtil.getFamilyCurrencyPair(CurrencyUtil.java:7)
>	at com.util.CurrencyUtil.getCcyFamily(CurrencyUtil.java:10)


## Prerequisites
- JDK 1.7
- Maven 3.6.3


## Setup project:
- settings.xml for Maven is included in ``/mvn_settings_runConfigs/settings.xml`` directory
- run configurations (supported by IDEA) are included in ``/mvn_settings_runConfigs/runConfigurations/*``
- Maven command to make fat-jar under JDK7: ``mvn -Dhttps.protocols=TLSv1.2 install clean package`` or 
an alternative *runConfiguration* named ``log_analyzer___Dhttps_protocols_TLSv1_2____.xml`` under ``/mvn_settings_runConfigs/``

## Step-by-step setup:
1. Clone git/svn repository from **release/0.1** branch for latest stable version
2. Open IntelliJ and import project as Maven project - **Cancel dependency resolution if you're using custom Maven settings.xml!**
3. Open Settings and go to Maven Settings and replace/override: 
*Maven home directory to apache-maven-3.6.3* 
and *settings.xml from mvn_settings_runConfigs subdirectory*
4. Copy **runConfigurations** directory under your **.idea** directory and restart IntelliJ
5. Now you're able to run Maven goal by choosing it from run configuration list in the upper area of the IntelliJ window
6. Now your dependencies will be resolved to those defined in pom.xml and project would be packaged into target/ directory.

## How to use:
