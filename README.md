# gotcha
GotCha! is an Online Team Collaboration application

![alt tag](https://github.com/AhmadHakroosh/gotcha/blob/master/devices.png)

**Course Instructor**
Dr. Haggai Roitman, IBM Research - Haifa

**Publication date:** 1/1/2017

**Scope**
during the project we explore, design and implement a web application that is built on top of the various client-side and server-side technologies that were tought during the semester.

**Project general description**
This project aims to make us implement a simple, but powerful Online Collaboration system ([https://en.wikipedia.org/wiki/Computer-supported_collaboration](https://en.wikipedia.org/wiki/Computer-supported_collaboration)) – Computer-supported collaboration (CSC) research focuses on technology that affects groups, organizations, communities and societies, e.g., voice mail and text chat. It grew from cooperative work study of supporting people's work activities and working relationships. As net technology increasingly supported a wide range of recreational and social activities, consumer markets expanded the user base, enabling more and more people to connect online to create what researchers have called a computer supported cooperative work, which includes "all contexts in which technology is used to mediate human activities such as communication, coordination, cooperation, competition, entertainment, games, art, and music" (from CSCW 2004).

----------


#Deployment and Environment 

- Java 7 JRE: [http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html](http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html)
- EclipseIDE (Mars) for J2EE: [64-bit](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-jee-mars-2-win32-x86_64.zip)/[32-bit](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/mars/2/eclipse-jee-mars-2-win32.zip)
- Apache Tomcat v.7: [http://tomcat.apache.org/download-70.cgi](http://tomcat.apache.org/download-70.cgi)
- Bootstrap: [http://getbootstrap.com/getting-started/#download](http://getbootstrap.com/getting-started/#download)
- jQuery: [http://code.jquery.com/jquery-1.11.3.min.js](http://code.jquery.com/jquery-1.11.3.min.js)
- AngularJS: [https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0-beta.2/angular.min.js](https://ajax.googleapis.com/ajax/libs/angularjs/1.5.0-beta.2/angular.min.js)
- Apache Derby: [http://db.apache.org/derby/releases/release-10.12.1.1.cgi](http://db.apache.org/derby/releases/release-10.12.1.1.cgi)
- Apache Lucene: [http://archive.apache.org/dist/lucene/java/5.5.3/lucene-5.5.3.zip](http://archive.apache.org/dist/lucene/java/5.5.3/lucene-5.5.3.zip)


----------

#HOW TO: Use Derby Database as a Web Application data source

1. Obtain the latest Apache Derby realease:
	http://db.apache.org/derby/releases/release-10.12.1.1.cgi

	1.1 Unzip db-derby-10.12.1.1-bin.zip and obtain the files:
	derby.jar and derbyclient.jar

2. Copy derby.jar and derbyclient.jar files into WEB-INF/lib into your project folder.

3. Let TOMCAT_DIR be the directory where you unzipped the Tomcat
package.

	3.1 Open the file TOMCAT_DIR/conf/context.xml.

	3.2 Add a new element `<Resource>...</Resource>` as a child element of the `<Context>...</Context>` element in context.xml as follows:

  ```
   <Context>
     <!--
     gotchaDBOpen: The name of the datasource that represents Derby database opening connection.
     gotchaDB: The Derby database name
  	-->
     <Resource auth="Container" driverClassName="org.apache.derby.jdbc.EmbeddedDriver" 
     maxActive="20" maxIdle="10" maxWait="10" 
     name="jdbc/gotchaDBOpen" type="javax.sql.DataSource" 
     url="jdbc:derby:gotchaDB;create=true"/>
     <!--
     gotchaDBClose: The name of the datasource that represents Derby database closing connection.
     gotchaDB: The Derby database name
  	-->
     <Resource auth="Container" driverClassName="org.apache.derby.jdbc.EmbeddedDriver" 
     maxActive="20" maxIdle="10" maxWait="10" 
     name="jdbc/gotchaDBClose" type="javax.sql.DataSource" 
     url="jdbc:derby:gotchaDB;shutdown=true"/>
  </Context>
  ```
 4. In your application, manually add the following resource-ref definition to your web.xml file:

  ```
  <web-app>
    <resource-ref>
      <description>GotCha! Database Connection Open</description>
      <res-ref-name>jdbc/gotchaDBOpen</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth>
    </resource-ref>
    <resource-ref>
      <description>GotCha! Database Connection Close</description>
      <res-ref-name>jdbc/gotchaDBClose</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth>
    </resource-ref>
  </web-app>
  ```

 5. Now, you should be able to obtain a connection to your database using Tomcat’s connection pool as follows:

```
import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
//obtain GotCha data source from Tomcat's context
Context context = new InitialContext();
BasicDataSource gotchaDB = (BasicDataSource)context.lookup(“java:comp/env/jdbc/gotchaDB);
Connection conn = gotchaDB.getConnection();
//use connection as you wish...but close after usage!
//It is important for correct connection pool management
//within Tomcat
```