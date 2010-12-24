# Tomcat Session Manager for Riak

This is a rough draft of a Tomcat session manager implementation backed
by the Riak key/value data store. It should be useful for setting up
a cluster of Tomcat instances that share user session data, allowing you
to serve web apps without sticky sessions, as the user's session data will
be persisted to a central Riak server rather than reside in the local server's
memory.

To configure a web app for using this session manager, you'll need all the
dependencies of the session manager, which include Groovy and Spring Data for
Riak.

Here's a list of the third-party dependencies from my local test server:

    activation-1.1.1.jar
    groovy-all-1.7.5.jar
    jackson-core-asl-1.6.0.jar
    jackson-mapper-asl-1.6.0.jar
    jcl-over-slf4j-1.6.1.jar
    log4j-1.2.15.jar
    mail-1.4.1.jar
    riak-sessions.jar
    slf4j-api-1.6.1.jar
    slf4j-log4j12-1.6.1.jar
    spring-beans-3.0.5.RELEASE.jar
    spring-context-3.0.5.RELEASE.jar
    spring-core-3.0.5.RELEASE.jar
    spring-data-riak-1.0.0.M2-SNAPSHOT.jar
    spring-tx-3.0.5.RELEASE.jar
    spring-web-3.0.5.RELEASE.jar

For convenience, I've uploaded a zip file that contains all these dependencies
in binary form.

When these jars are available in your server's classpath, configure your
`META-INF/context.xml` like so:

    <?xml version="1.0" encoding="UTF-8"?>
    <Context>
      <Manager className="com.jbrisbin.vpc.riak.session.RiakManager"
               defaultUri="http://localhost:8098/riak/{bucket}/{key}"
               mapReduceUri="http://localhost:8098/mapred"
               maxInactiveInterval="1800"/>
    </Context>




