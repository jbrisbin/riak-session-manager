package com.jbrisbin.vpc.riak.session

import org.apache.catalina.util.CustomObjectInputStream
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter

/**
 *
 * @author J. Brisbin <jon@jbrisbin.com>
 */
class CustomObjectMessageConverter extends AbstractHttpMessageConverter {

  protected RiakManager manager
  protected List<MediaType> supportedTypes = [MediaType.APPLICATION_OCTET_STREAM]

  CustomObjectMessageConverter(RiakManager manager) {
    this.manager = manager
  }

  List<MediaType> getSupportedMediaTypes() {
    return supportedTypes
  }

  protected boolean supports(Class clazz) {
    return clazz instanceof Serializable
  }

  protected Object readInternal(Class clazz, HttpInputMessage inputMessage) {
    ObjectInputStream oin = new CustomObjectInputStream(inputMessage.getBody(), manager.classLoader)
    return oin.readObject()
  }

  protected void writeInternal(Object t, HttpOutputMessage outputMessage) {
    ObjectOutputStream oout = new ObjectOutputStream(outputMessage.getBody())
    oout.writeObject(t)
  }

}
