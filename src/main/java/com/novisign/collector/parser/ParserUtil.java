package com.novisign.collector.parser;

import org.apache.commons.codec.binary.Base64;

import com.novisign.collector.exception.BadRequestException;

class ParserUtil {
  
  public static String[] split(String csv) {
    return csv.trim().split(";", -1);
  }
  
  public static int getVersion(String[] a, int max) {
    int version;
    try {
      version = Integer.parseInt(a[1]);
    } catch (RuntimeException e) {
      throw new BadRequestException("Incorrect version: " + a[1]);
    }
    if (version < 1 || version > max) {
      throw new BadRequestException("Unsupported version");
    }
    return version;
  }

  public static int parseInt(String s) {
    if (s.isEmpty()) {
      return 0;
    }
    return Integer.parseInt(s);
  }

  public static long parseLong(String s) {
    if (s.isEmpty()) {
      return 0L;
    }
    return Long.parseLong(s);
  }

  public static Boolean parseBoolean(String s) {
    if (s.isEmpty()) {
      return null;
    }
    return s.equals("1");
  }

  public static String decodeBase64(String s) {
    if (s != null) {
      return new String(Base64.decodeBase64(s));
    }
    return null;
  }
}
