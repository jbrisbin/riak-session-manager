package com.jbrisbin.vpc.riak.session

import javax.servlet.ServletContext
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionContext

/**
 *
 * @author J. Brisbin <jon@jbrisbin.com>
 */
class RiakSessionFacade implements HttpSession {

  private RiakSession session

  RiakSessionFacade(session) {
    this.session = session;
  }

  long getCreationTime() {
    return session.creationTime
  }

  String getId() {
    return session.id
  }

  long getLastAccessedTime() {
    return session.lastAccessedTime
  }

  ServletContext getServletContext() {
    return session.servletContext
  }

  void setMaxInactiveInterval(int i) {
    session.maxInactiveInterval = i
  }

  int getMaxInactiveInterval() {
    return session.maxInactiveInterval
  }

  HttpSessionContext getSessionContext() {
    return session.sessionContext
  }

  Object getAttribute(String s) {
    return session.getAttribute(s)
  }

  Object getValue(String s) {
    return session.getAttribute(s)
  }

  Enumeration getAttributeNames() {
    return session.getAttributeNames()
  }

  String[] getValueNames() {
    return session.getValueNames()
  }

  void setAttribute(String s, Object o) {
    session.setAttribute(s, o)
  }

  void putValue(String s, Object o) {
    session.putValue(s, o)
  }

  void removeAttribute(String s) {
    session.removeAttribute(s)
  }

  void removeValue(String s) {
    session.removeAttribute(s)
  }

  void invalidate() {
    session.invalidate()
  }

  boolean isNew() {
    return session.isNew()
  }

}
