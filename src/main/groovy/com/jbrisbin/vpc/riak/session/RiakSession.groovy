package com.jbrisbin.vpc.riak.session

import javax.servlet.http.HttpSession
import org.apache.catalina.session.StandardSession
import org.springframework.data.keyvalue.riak.core.RiakTemplate
import org.springframework.web.client.ResourceAccessException

/**
 *
 * @author J. Brisbin <jon@jbrisbin.com>
 */
class RiakSession extends StandardSession {

  RiakSession(manager) {
    super(manager);
  }

  void setIdInternal(String id) {
    this.id = id
  }

  void setCreationTime(long time) {
    def metadata = getRiakTemplate().get(manager.name, id)
    metadata.creationTime = time
    getRiakTemplate().set(manager.name, id, metadata)
  }

  void setLastAccessedTime(long time) {
    def metadata = getRiakTemplate().get(manager.name, id)
    metadata.lastAccessedTime = time
    getRiakTemplate().set(manager.name, id, metadata)
  }

  long getLastAccessedTime() {
    def metadata = getRiakTemplate().get(manager.name, id)
    return metadata.lastAccessedTime
  }

  long getLastAccessedTimeInternal() {
    return getLastAccessedTime()
  }

  HttpSession getSession() {
    return new RiakSessionFacade(this)
  }

  boolean isValid() {
    if (!this.isValid) {
      return false
    }

    if (maxInactiveInterval >= 0) {
      if (maxInactiveInterval >= 0) {
        long timeNow = System.currentTimeMillis();
        int timeIdle = (int) ((timeNow - getLastAccessedTime()) / 1000L);
        if (timeIdle >= maxInactiveInterval) {
          expire(true);
        }
      }
    }

    return this.isValid
  }

  long getCreationTime() {
    try {
      def metadata = getRiakTemplate().get(manager.name, id)
      return metadata.creationTime
    } catch (ResourceAccessException notFound) {
      return null
    }
  }

  void removeAttribute(String name) {
    getRiakTemplate().delete(getId(), name)
  }

  void removeAttribute(String name, boolean notify) {
    removeAttribute(getId(), name)
  }

  void setAttribute(String name, Object value) {
    getRiakTemplate().set(getId(), name, value)
  }

  void setAttribute(String name, Object value, boolean notify) {
    setAttribute(name, value)
  }

  def Object getAttribute(String name) {
    getRiakTemplate().get(getId(), name)
  }

  def Enumeration getAttributeNames() {
    def schema = getRiakTemplate().getBucketSchema(getId(), true)
    return Collections.enumeration(schema.keys)
  }

  protected String[] keys() {
    Map<String, Object> schema = getRiakTemplate().getBucketSchema(manager.name, id, true)
    return schema.keys
  }

  RiakTemplate getRiakTemplate() {
    return ((RiakManager) manager).getRiakTemplate()
  }

}
