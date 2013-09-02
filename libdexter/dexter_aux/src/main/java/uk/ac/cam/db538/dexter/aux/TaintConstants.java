package uk.ac.cam.db538.dexter.aux;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class TaintConstants {

  public static final int TAINT_EMPTY = 0;
	
  public static final int TAINT_SOURCE_CONTACTS = 	1 << 0;
  public static final int TAINT_SOURCE_SMS = 		1 << 1;
  public static final int TAINT_SOURCE_CALL_LOG = 	1 << 2;
  public static final int TAINT_SOURCE_LOCATION = 	1 << 3;
  public static final int TAINT_SOURCE_BROWSER = 	1 << 4;
  public static final int TAINT_SOURCE_DEVICE_ID = 	1 << 5;

  public static final int TAINT_SINK_FILE = 		1 << 29;
  public static final int TAINT_SINK_SOCKET = 		1 << 30;
  public static final int TAINT_SINK_OUT = 			1 << 31;

  public static final int TAINT_SOURCE =
    TAINT_SOURCE_CONTACTS |
    TAINT_SOURCE_SMS |
    TAINT_SOURCE_CALL_LOG |
    TAINT_SOURCE_LOCATION |
    TAINT_SOURCE_BROWSER |
    TAINT_SOURCE_DEVICE_ID;
  public static final int TAINT_SINK =
    TAINT_SINK_FILE |
    TAINT_SINK_SOCKET |
    TAINT_SINK_OUT;

  public static final int queryTaint(String query) {
    if (query.startsWith("content://com.android.contacts"))
      return TAINT_SOURCE_CONTACTS;
    else if (query.startsWith("content://sms"))
      return TAINT_SOURCE_SMS;
    else if (query.startsWith("content://call_log"))
      return TAINT_SOURCE_CALL_LOG;
    else
      return TAINT_EMPTY;
  }

  public static final int serviceTaint(String name) {
    if (name.equals("location"))
      return TAINT_SOURCE_LOCATION;
    else if (name.equals("phone"))
      return TAINT_SOURCE_DEVICE_ID;
    else
      return TAINT_EMPTY;
  }
  
  public static final int sinkTaint(Object obj, int taint) {
	  if (obj instanceof File)
		  taint |= TAINT_SINK_FILE;
	  else if (obj instanceof Socket)
		  taint |= TAINT_SINK_SOCKET;
	  return taint;
  }
  
  public static final boolean isSourceTaint(int taint) {
	  return (taint & TAINT_SOURCE) != 0;
  }
  
  public static final boolean isSinkTaint(int taint) {
	  return (taint & TAINT_SINK) != 0;
  }
  
  public static final boolean isImmutable(Object obj) {
	return obj == null || isImmutableType(obj.getClass());
  }
  
  public static final boolean isImmutableType(Class<?> cls) {
	return IMMUTABLES.contains(cls);
  }

  public static final List<Class<?>> IMMUTABLES = Arrays.asList(
	  String.class,
	  Integer.class,
	  Boolean.class,
	  Byte.class,
	  Character.class,
	  Double.class,
	  Float.class,
	  Long.class,
	  Short.class,
	  Void.class,
	  BigDecimal.class,
	  BigInteger.class,
	  java.security.Timestamp.class
  );
}
