package com.jbrisbin.vpc.riak.session

import java.util.concurrent.atomic.AtomicInteger
import org.apache.catalina.Session
import org.apache.catalina.session.ManagerBase
import org.apache.catalina.session.StandardSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.keyvalue.riak.core.QosParameters
import org.springframework.data.keyvalue.riak.core.RiakTemplate

/**
 *
 * @author J. Brisbin <jon@jbrisbin.com>
 */
class RiakManager extends ManagerBase {

  private static final String info = RiakManager.name + "/1.0"

  protected Logger log = LoggerFactory.getLogger(getClass());
  protected RiakTemplate riak = new RiakTemplate()
  protected String name = "riak-manager"
  protected AtomicInteger rejected = new AtomicInteger(0)


  def RiakManager() {
    List converters = riak.getRestTemplate().getMessageConverters()
    converters.add(converters.size(), new CustomObjectMessageConverter())
  }

  void setName(String name) {
    this.name = name
  }

  String getName() {
    return this.name
  }

  def String getInfo() {
    return info
  }

  void setDefaultUri(String uri) {
    riak.setDefaultUri(uri)
  }

  String getDefaultUri() {
    return riak.getDefaultUri()
  }

  void setMapReduceUri(String uri) {
    riak.setMapReduceUri(uri)
  }

  String getMapReduceUri() {
    return riak.getMapReduceUri()
  }

  void setDefaultQosParameters(QosParameters qos) {
    riak.setDefaultQosParameters(qos)
  }

  QosParameters getDefaultQosParameters() {
    return riak.getDefaultQosParameters()
  }

  RiakTemplate getRiakTemplate() {
    return riak
  }

  ClassLoader getClassLoader() {
    return container?.loader?.classLoader ?: getClass().getClassLoader()
  }

  int getRejectedSessions() {
    return rejected.get()
  }

  void setRejectedSessions(int i) {
    rejected.set(i)
  }

  void load() {
    riak.getBucketSchema(name, true)?.keys?.each {
      if (log.debugEnabled) {
        log.debug "Loading session $it"
      }
      findSession(it)
    }
  }

  void unload() {
    // NO-OP
  }

  void add(Session session) {
    def metadata = [
        creationTime: System.currentTimeMillis(),
        lastAccessedTime: System.currentTimeMillis()
    ]
    if (log.debugEnabled) {
      log.debug "Saving session ${session.id}"
    }
    riak.set(name, session.id, metadata)
  }

  Session findSession(String id) {
    Session sess = super.findSession(id);
    if (null != sess) {
      if (log.debugEnabled) {
        log.debug "Returning cached session: $sess"
      }
      return sess;
    }

    sess = createSession(id)
    sess.idInternal = id
    sessions.put(id, sess)

    return sess
  }

  Session[] findSessions() {
    def s = []
    riak.getBucketSchema(name, true)?.keys?.each {
      s << findSession(it)
    }
    return s.toArray(new Session[s.size()])
  }

  void remove(Session session) {
    riak.delete(name, session.id)
  }

  protected StandardSession getNewSession() {
    return new RiakSession(this)
  }

  HashMap getSession(String sessionId) {
    return findSession(sessionId)
  }

  void expireSession(String sessionId) {
    findSession(sessionId)?.expire()
  }

  String getCreationTime(String sessionId) {
    return findSession(sessionId)?.getCreationTime()
  }

  long getCreationTimestamp(String sessionId) {
    return findSession(sessionId)?.getCreationTime()
  }

  long getLastAccessedTimestamp(String sessionId) {
    return findSession(sessionId)?.getLastAccessedTime()
  }

  String getLastAccessedTime(String sessionId) {
    return findSession(sessionId)?.getCreationTime()
  }

  String getSessionAttribute(String sessionId, String key) {
    return findSession(sessionId)?.getAttribute(key)
  }

  Session createSession(String sessionId) {
    Session session = createEmptySession();
    session.setNew(true)
    session.valid = true
    session.maxInactiveInterval = maxInactiveInterval
    if (!sessionId) {
      session.idInternal = generateSessionId()
      session.creationTime = System.currentTimeMillis()
      session.lastAccessedTime = System.currentTimeMillis()
      sessionCounter++
    }

    return session
  }

}
