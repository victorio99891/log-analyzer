# Log Analayzer
A simple CLI-base tool which can help you with your log analysis.

## Short description: 
**Base features:**

- Parsing *.log files.
- Parsing *.log files rolled in *.zip, but only in specific format (described below).
- Logs for analysis comes from base directory for provided path and subdirectory called "archive".
- Logs for analysis should contain *SysMonError* in file name.
- Storage history of analysed *.log files as JSON file.
- Generation of Excel document based on JSON history file.

<br/>

## Single log format:
Format of analysed logs (sections are cut by pipe separator): 

>[LOG|ERROR|2020 January 15, 06:35:30 (584)|TestServer|/var/log/Server_141012.log]
> 
>Example message
>
>[END]
>
>com.marketdata.MarketDataException: No Currency Default Set up for null
>	at com.util.CurrencyUtil.getFamilyCurrencyPair(CurrencyUtil.java:7)
>	at com.util.CurrencyUtil.getCcyFamily(CurrencyUtil.java:10)
<br/>

## Prerequisites
- JDK 1.7
- Maven 3.6.3
<br/>

## Setup project:
- settings.xml for Maven is included in ``/mvn_settings_runConfigs/settings.xml`` directory
- run configurations (supported by IDEA) are included in ``/mvn_settings_runConfigs/runConfigurations/*``
- Maven command to make fat-jar under JDK7: ``mvn -Dhttps.protocols=TLSv1.2 install clean package`` or 
an alternative *runConfiguration* named ``log_analyzer___Dhttps_protocols_TLSv1_2____.xml`` under ``/mvn_settings_runConfigs/``

<br/><br/>

## Step-by-step setup:
1. Clone git/svn repository from **release/0.1** branch for latest stable version
2. Open IntelliJ and import project as Maven project - **Cancel dependency resolution if you're using custom Maven settings.xml!**
3. Open Settings and go to Maven Settings and replace/override: 
*Maven home directory to apache-maven-3.6.3* 
and *settings.xml from mvn_settings_runConfigs subdirectory*
4. Copy **runConfigurations** directory under your **.idea** directory and restart IntelliJ
5. Now you're able to run Maven goal by choosing it from run configuration list in the upper area of the IntelliJ window
6. Now your dependencies will be resolved to those defined in pom.xml and project would be packaged into target/ directory.
<br/>

## How to use:
**Available commands:**

| Action | Flag | Description |
| --- | --- | --- |
|Help |              --help |                         Shows available commands as also their description and briefly information about software version. |
|Directory Path |    --path [argument]|               Specify the path to the directory to scan. In case of any whitespaces in path use double quotes. |
|Date From |         --datefrom [argument]|           Specify the date with time where log analysis should be STARTED. Format: yyyyMMddHHmmss Example: 20190125121544 for 25-01-2019 12:15:44.|
|Date To |           --dateto [argument]|             Specify the date with time where log analysis should be FINISHED. Format: yyyyMMddHHmmss Example: 20190125121544 for 25-01-2019 12:15:44.|
|Generate report |   --report|                        Generates Excel report file.|

<br/>

**Examples of allowed flag combinations:** <br/><br/>
Show help:
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --help

Analyse logs under provided path:
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --path "/var/logs" 

<br/><br/><br/>
Analyse logs under provided path where logs dates are after '--datefrom':
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --path "/var/logs" --datefrom 20101001120000

Analyse logs under provided path where logs dates are before '--dateto':
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --path "/var/logs" --dateto 20101201120000

Analyse logs under provided path where logs dates are between time interval '--datefrom' and '--dateto':
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --path "/var/logs" --datefrom 20101001120000 --dateto 20101201120000

Create Excel report file based on LogHistory.json file:
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --report

Analyse logs under provided path where logs dates are before '--dateto' and create Excel report based on LogHistory.json and current analysis:
> java -jar target/log-analyzer-0.1-SNAPSHOT-STANDALONE.jar --path "/var/logs" --dateto 20101201120000


## GlobalConfiguration.json:
This configuration can be found under *'resources/'* directory inside project or in JAR archive under *'BOOT-INF/classes/'*
```
{
  "logDelimiterPattern": "|######|",
  "regexFilterList": [
    "(?<=\\[Error-ID: \\d\\d\\d-\\d\\d\\d\\d\\]).*",
    "([a-z]+[0-9]|[0-9]+[a-z])[a-z0-9]*",
    "(?s)\\{.*\\}(?=.*(\\[END\\]|$))"
  ],
  "historyFileName" : "LogHistory.json"
}
```

**logDelimiterPattern** - this pattern is responsible for exchange pipe operator in cut log files

**regexFilterList** - array of regular expressions responsible for filtering logs in every analysis

**historyFileName** - string which describes name of history file after analysis

