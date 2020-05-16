package server.protocol2;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.12.15
 * <p/>
 * contract:
 * success == true -> errorForUser == null; exceptionStackTrace == null;
 * success == false -> errorForUser != null;
 */
public class Response implements Serializable {
  private static final long serialVersionUID = 1894047362542925757L;
  private final Object data;
  private final boolean success;
  private final String errorForUser;
  private final String exceptionStackTrace;
  private long executionTime;

  public Response(Object data) {
    this.data = data;
    this.success = true;
    errorForUser = null;
    exceptionStackTrace = null;
  }

  public Response(String errorForUser, Throwable serverException) {
    if (errorForUser == null) throw new IllegalArgumentException("errorForUser is null");
    this.errorForUser = errorForUser;
    if (serverException != null) {
      StringWriter stringWriter = new StringWriter();
      serverException.printStackTrace(new PrintWriter(stringWriter));
      exceptionStackTrace = stringWriter.toString();
    } else {
      exceptionStackTrace = null;
    }
    data = null;
    success = false;
  }

  public Object getData() {
    return data;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getErrorForUser() {
    return errorForUser;
  }

  public String getExceptionStackTrace() {
    return exceptionStackTrace;
  }

  public long getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(long executionTime) {
    this.executionTime = executionTime;
  }
}
