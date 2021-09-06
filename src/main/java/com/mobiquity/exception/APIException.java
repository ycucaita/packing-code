package com.mobiquity.exception;

public class APIException extends Exception {

  public APIException(String message, Exception e) {
    super(message, e);
  }

  public APIException(String message) {
    super(message,null,false,false);
  }
}
