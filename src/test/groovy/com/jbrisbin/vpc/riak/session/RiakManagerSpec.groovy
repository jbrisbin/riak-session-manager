package com.jbrisbin.vpc.riak.session

import org.apache.catalina.core.StandardContext
import org.springframework.data.keyvalue.riak.core.RiakTemplate
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author J. Brisbin <jon@jbrisbin.com>
 */
class RiakManagerSpec extends Specification {

  @Shared RiakTemplate riak = new RiakTemplate()
  @Shared RiakManager manager
  @Shared String id

  def setupSpec() {
    riak.afterPropertiesSet()
    manager = new RiakManager()
    manager.container = new StandardContext()
  }

  def cleanupSpec() {
    riak.getBucketSchema(id, true).keys.each {
      riak.delete(id, it)
    }
    riak.getBucketSchema(manager.name, true).keys.each {
      riak.delete(manager.name, it)
    }
  }

  def "Test create session"() {

    given:
    def session = manager.createSession(null)

    when:
    manager.add(session)
    id = session.id
    def lastAccessedTime = manager.findSession(session.id).lastAccessedTime

    then:
    null != lastAccessedTime
    lastAccessedTime < System.currentTimeMillis()

  }

  def "Test load session"() {

    given:
    def session = manager.findSession(id)

    when:
    def lastAccessedTime = session.lastAccessedTime

    then:
    null != lastAccessedTime
    lastAccessedTime < System.currentTimeMillis()

  }

  def "Test get/set primitive attribute"() {

    given:
    def session = manager.findSession(id)
    session.setAttribute("test", 12)

    when:
    def value = session.getAttribute("test")

    then:
    null != value
    12 == value

  }

  def "Test get/set complex attribute"() {

    given:
    def session = manager.findSession(id)
    def obj = new TestObject(name: "test", child: new TestObject(name: "child"))
    session.setAttribute("test", obj)

    when:
    def value = session.getAttribute("test")

    then:
    null != value
    "child" == value.child.name

  }

}
