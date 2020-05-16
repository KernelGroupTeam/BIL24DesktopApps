package client.net;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 30.11.15
 */
public class NetTelemetry {
  private static final DecimalFormat format = new DecimalFormat();
  private final String command;
  private volatile boolean zipOutput = false;
  private volatile boolean zipInput = false;
  private volatile boolean dataSent = false;
  private volatile long sentBytes = -1L;       //объем отправленных данных
  private volatile long sentBytesZip = -1L;    //объем отправленных данных после сжатия
  private volatile long receivedBytes = -1L;   //объем принятых данных
  private volatile long receivedBytesZip = -1L;//объем принятых данных перед разархивацией
  private volatile long processingTime = -1L;

  public NetTelemetry(String command) {
    this.command = command;
  }

  public String getCommand() {
    return command;
  }

  public boolean isZipOutput() {
    return zipOutput;
  }

  void setZipOutput(boolean zipOutput) {
    this.zipOutput = zipOutput;
  }

  public boolean isZipInput() {
    return zipInput;
  }

  void setZipInput(boolean zipInput) {
    this.zipInput = zipInput;
  }

  public boolean isDataSent() {
    return dataSent;
  }

  void setDataSent(boolean dataSent) {
    this.dataSent = dataSent;
  }

  public long getSentBytes() {
    return sentBytes;
  }

  void setSentBytes(long sentBytes) {
    this.sentBytes = sentBytes;
  }

  public long getSentBytesZip() {
    return sentBytesZip;
  }

  void setSentBytesZip(long sentBytesZip) {
    this.sentBytesZip = sentBytesZip;
  }

  public long getReceivedBytes() {
    return receivedBytes;
  }

  void setReceivedBytes(long receivedBytes) {
    this.receivedBytes = receivedBytes;
  }

  public long getReceivedBytesZip() {
    return receivedBytesZip;
  }

  void setReceivedBytesZip(long receivedBytesZip) {
    this.receivedBytesZip = receivedBytesZip;
  }

  public long getProcessingTime() {
    return processingTime;
  }

  void setProcessingTime(long processingTime) {
    this.processingTime = processingTime;
  }

  @Override
  public synchronized String toString() {
    String result = "NetTelemetry{Command: " + command + ", sent: " + format.format(sentBytes) + " b";
    if (zipOutput) result += " (" + format.format(sentBytesZip) + " b)";
    result += ", received: " + format.format(receivedBytes) + " b";
    if (zipInput) result += " (" + format.format(receivedBytesZip) + " b)";
    result += ", time: " + format.format(processingTime) + " ms}";
    return result;
  }
}
