# signalk-core-java
Signalk core
============

This project contains the  major SignalK related functionality from the Signalk-server-java project without the reliance on Apache Camel and the servers network IO capabilities. It should make reuse of the core functionality easier in other projects.

This project was ripped from the server project with minimal thought to the ideal api etc due to my time constraints. There is currently a lot of legacy method names and patterns that suited the server, and the code is fairly rough as a result. It needs refactoring and work to make it better. Pull requests with improvements are welcome.

Using
=====
The project is not in any public maven repository yet. So for now you will have to build the project locally on your dev system, and do a maven install to install into your local m2 repository. 

Then add the following to the pom of your project.
```
<!--<properties>-->
<signalk.core.version>0.0.1-SNAPSHOT</signalk.core.version>

<!-- <dependencies>-->
    <dependency>
			<groupId>nz.co.fortytwo.signalk.core</groupId>
			<artifactId>signalk-core-java</artifactId>
			<version>${signalk.core.version}</version>
		</dependency>

```
Alternatively you can take the jar file from target/ and include manually in your project.

Development
===========

The project is a typical maven/eclipse java project. Clone the repository, import into eclipse and go.


