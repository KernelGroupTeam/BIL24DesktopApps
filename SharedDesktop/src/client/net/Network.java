package client.net;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.11.15
 */
public class Network<Req, Res> {
  private static final int TCP_BUFFER_SIZE = 128 * 1024;
  private static final Charset charset = StandardCharsets.UTF_8;
  private static final ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
    final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(Runnable r) {
      Thread thread = defaultFactory.newThread(r);
      thread.setName("Network-" + thread.getName());
      return thread;
    }
  });

  private final InetSocketAddress serverAddress;
  private final String command;
  private final Req request;
  private final NetListener<Req, Res> listener;
  private final NetPoolJointListener poolListener;
  private final NetTelemetry telemetry;

  private SocketFactory socketFactory;
  private boolean zip;
  private byte[] macSecurity;
  private int sendingBufSize;
  private int receivingBufSize;
  private int timeout;
  private EventMode eventMode;

  private volatile boolean fireStartFinish = true;
  private volatile boolean fireState = true;
  private volatile boolean started = false;
  private volatile String answer = null;
  private volatile Res response = null;

  private NetRequestProcessor<Req, Res> requestProcessor;
  private NetPoolRequestProcessor poolRequestProcessor;
  private NetResponseProcessor<Req, Res> responseProcessor;
  private NetPoolResponseProcessor poolResponseProcessor;

  public Network(InetSocketAddress serverAddress, String command, Req request, NetListener<Req, Res> listener) {
    this(serverAddress, command, request, listener, SocketFactory.getDefault());
  }

  public Network(InetSocketAddress serverAddress, String command, Req request, NetListener<Req, Res> listener, SocketFactory socketFactory) {
    this(serverAddress, command, request, listener, null, socketFactory, false, 8192, 8192, 20000, EventMode.STANDARD);
  }

  Network(InetSocketAddress serverAddress, String command, Req request, NetListener<Req, Res> listener, NetPoolJointListener poolListener, SocketFactory socketFactory, boolean zip, int sendingBufSize, int receivingBufSize, int timeout, EventMode eventMode) {
    if (listener == null) listener = new NetAdapter<Req, Res>() {
    };
    this.serverAddress = serverAddress;
    this.command = command;
    this.request = request;
    this.listener = listener;
    this.poolListener = poolListener;
    this.telemetry = new NetTelemetry(command);
    this.socketFactory = socketFactory;
    this.zip = zip;
    this.sendingBufSize = sendingBufSize;
    this.receivingBufSize = receivingBufSize;
    this.timeout = timeout;
    this.eventMode = eventMode;
  }

  public <Req2, Res2> Network<Req2, Res2> clone(String command, Req2 request, NetListener<Req2, Res2> listener) {
    return new Network<>(serverAddress, command, request, listener, null, socketFactory, zip, sendingBufSize, receivingBufSize, timeout, eventMode);
  }

  public InetSocketAddress getServerAddress() {
    return serverAddress;
  }

  public String getCommand() {
    return command;
  }

  public Req getRequest() {
    return request;
  }

  public NetListener<Req, Res> getListener() {
    return listener;
  }

  public NetTelemetry getTelemetry() {
    return telemetry;
  }

  public SocketFactory getSocketFactory() {
    return socketFactory;
  }

  public void setSocketFactory(SocketFactory socketFactory) {
    if (started) throw new IllegalStateException("started");
    if (socketFactory == null) throw new IllegalArgumentException("socketFactory is null");
    this.socketFactory = socketFactory;
  }

  public boolean isZip() {
    return zip;
  }

  public void setZip(boolean zip) {
    if (started) throw new IllegalStateException("started");
    this.zip = zip;
  }

  public void setMacSecurity(byte[] macSecurity) {
    this.macSecurity = macSecurity;
  }

  public int getSendingBufSize() {
    return sendingBufSize;
  }

  public void setSendingBufSize(int sendingBufSize) {
    if (started) throw new IllegalStateException("started");
    if (sendingBufSize < 512) sendingBufSize = 512;
    this.sendingBufSize = sendingBufSize;
  }

  public int getReceivingBufSize() {
    return receivingBufSize;
  }

  public void setReceivingBufSize(int receivingBufSize) {
    if (started) throw new IllegalStateException("started");
    if (receivingBufSize < 512) receivingBufSize = 512;
    this.receivingBufSize = receivingBufSize;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    if (started) throw new IllegalStateException("started");
    this.timeout = timeout;
  }

  public EventMode getEventMode() {
    return eventMode;
  }

  public void setEventMode(EventMode eventMode) {
    if (started) throw new IllegalStateException("started");
    this.eventMode = eventMode;
  }

  public boolean isFireStartFinish() {
    return fireStartFinish;
  }

  public void setFireStartFinish(boolean fireStartFinish) {
    this.fireStartFinish = fireStartFinish;
  }

  public boolean isFireState() {
    return fireState;
  }

  public void setFireState(boolean fireState) {
    this.fireState = fireState;
  }

  public boolean isStarted() {
    return started;
  }

  public synchronized void start() {
    if (started) throw new IllegalStateException("started");
    started = true;
    pool.execute(new NetRunnable());
  }

  public String getAnswer() {
    return answer;
  }

  public Res getResponse() {
    return response;
  }

  public NetRequestProcessor<Req, Res> getRequestProcessor() {
    return requestProcessor;
  }

  public void setRequestProcessor(NetRequestProcessor<Req, Res> requestProcessor) {
    this.requestProcessor = requestProcessor;
  }

  void setPoolRequestProcessor(NetPoolRequestProcessor poolRequestProcessor) {
    this.poolRequestProcessor = poolRequestProcessor;
  }

  public NetResponseProcessor<Req, Res> getResponseProcessor() {
    return responseProcessor;
  }

  public void setResponseProcessor(NetResponseProcessor<Req, Res> responseProcessor) {
    this.responseProcessor = responseProcessor;
  }

  void setPoolResponseProcessor(NetPoolResponseProcessor poolResponseProcessor) {
    this.poolResponseProcessor = poolResponseProcessor;
  }

  private class NetRunnable implements Runnable {
    private final NetListener<Req, Res> listener;

    public NetRunnable() {
      if (eventMode == EventMode.EDT_INVOKE_LATER) listener = new EDTLaterNetListener<>(Network.this.listener);
      else if (eventMode == EventMode.EDT_INVOKE_AND_WAIT) listener = new EDTWaitNetListener<>(Network.this.listener);
      else listener = Network.this.listener;
    }

    @SuppressWarnings({"unchecked", "UnnecessaryFinalOnLocalVariableOrParameter"})
    @Override
    public void run() {
      final NetEvent<Req, Res> event = new NetEvent<>(Network.this, command, request);

      NetEvent<Req, Res> resultEvent;
      try {
        if (poolListener != null) poolListener.netPoolStarted();
        if (fireStartFinish) listener.netState(event, State.STARTED);
        if (poolListener != null) poolListener.netState(Network.this, event, State.STARTED);
        long startTime = System.nanoTime();
        preProcessing(event);
        fireState(event, State.ESTABLISHING_CONNECTION);
        try (Socket socket = socketFactory.createSocket(serverAddress.getAddress(), serverAddress.getPort())) {
          socket.setSendBufferSize(TCP_BUFFER_SIZE);
          socket.setReceiveBufferSize(TCP_BUFFER_SIZE);
          socket.setSoTimeout(timeout);
          if (socket instanceof SSLSocket) ((SSLSocket) socket).startHandshake();
          fireState(event, State.CONNECTED);
          CountingOutputStream out = new CountingOutputStream(new BufferedOutputStream(socket.getOutputStream(), sendingBufSize));
          out.write((command + "\n").getBytes(charset));
          String outFormat = "OBJ";
          if (zip) outFormat = "GZIP";
          if (macSecurity != null) outFormat += ",MAC";
          out.write((outFormat + "\n").getBytes(charset));
          fireState(event, State.HEADER_SENT);
          OutputStream outputStream = out;
          MessageDigest outDigest = null;
          if (macSecurity != null) {
            outDigest = MessageDigest.getInstance("SHA-256");
            outputStream = new DigestOutputStream(out, outDigest);
          }
          long outDeltaZip = 0;//сэкономлено байт за счет сжатия
          if (zip) {
            telemetry.setZipOutput(true);
            long bytes = out.getByteCount();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream, 8192);
            CountingOutputStream zipCountingOutputStream = new CountingOutputStream(gzipOutputStream, bytes);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(zipCountingOutputStream);
            objectOutputStream.writeObject(request);//writeObject() очищает внутренний буфер методом drain(), flush() не нужен
            gzipOutputStream.finish();//очищает внутренний буфер, flush() не нужен
            outDeltaZip = zipCountingOutputStream.getByteCount() - out.getByteCount() - 1;//"GZIP".length()-"OBJ".length() == 1
          } else {
            telemetry.setZipOutput(false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(request);//writeObject() очищает внутренний буфер методом drain(), flush() не нужен
          }
          if (macSecurity != null && outDigest != null) {
            outDigest.update(macSecurity);
            byte[] mac = outDigest.digest();
            out.write(mac);
          }
          out.flush();
          telemetry.setDataSent(true);
          telemetry.setSentBytes(out.getByteCount() + outDeltaZip);
          telemetry.setSentBytesZip(out.getByteCount());
          fireState(event, State.DATA_SENT);

          CountingInputStream in = new CountingInputStream(new BufferedInputStream(socket.getInputStream(), receivingBufSize));
          answer = readLine(in, charset);
          String inFormat = readLine(in, charset);
          if (answer == null || inFormat == null) throw new ProtocolException("protocol format error");
          fireState(event, State.HEADER_RECEIVED);
          List<String> formatList = Arrays.asList(inFormat.split(","));
          long inDeltaZip = 0;//сэкономлено байт за счет сжатия
          if (formatList.contains("GZIP")) {
            telemetry.setZipInput(true);
            long bytes = in.getByteCount();
            CountingInputStream zipCountingInputStream = new CountingInputStream(new GZIPInputStream(in), bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(zipCountingInputStream);
            response = (Res) objectInputStream.readObject();
            inDeltaZip = zipCountingInputStream.getByteCount() - in.getByteCount() - 1;//"GZIP".length()-"OBJ".length() == 1
          } else {
            telemetry.setZipInput(false);
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            response = (Res) objectInputStream.readObject();
          }
          if (formatList.contains("MAC")) {//not implemented
            readMac(in);
          }
          telemetry.setReceivedBytes(in.getByteCount() + inDeltaZip);
          telemetry.setReceivedBytesZip(in.getByteCount());
          fireState(event, State.DATA_RECEIVED);
        }
        postProcessing(event);
        telemetry.setProcessingTime(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
        resultEvent = new NetResultEvent<>(Network.this, command, request, answer, response, telemetry);
      } catch (Exception ex) {
        resultEvent = new NetErrorEvent<>(Network.this, command, request, telemetry.isDataSent(), ex);
      } finally {
        if (fireStartFinish) listener.netState(event, State.FINISHED);
        if (poolListener != null) poolListener.netState(Network.this, event, State.FINISHED);
        if (poolListener != null) poolListener.netPoolFinished();
      }
      if (resultEvent instanceof NetResultEvent) {
        listener.netResult((NetResultEvent<Req, Res>) resultEvent);
        if (poolListener != null) poolListener.netResult(Network.this, (NetResultEvent<?, ?>) resultEvent);
      } else {
        listener.netError((NetErrorEvent<Req, Res>) resultEvent);
        if (poolListener != null) poolListener.netError(Network.this, (NetErrorEvent<?, ?>) resultEvent);
      }
    }

    private void fireState(NetEvent<Req, Res> event, State state) {
      if (fireState) listener.netState(event, state);
      if (poolListener != null) poolListener.netState(Network.this, event, state);
    }

    private void preProcessing(NetEvent<Req, Res> event) {
      if (requestProcessor != null || poolRequestProcessor != null) {
        fireState(event, State.REQUEST_PROCESSING);
        if (requestProcessor != null) requestProcessor.processRequest(Network.this, command, request);
        if (poolRequestProcessor != null) poolRequestProcessor.processRequest(Network.this, command, request);
        fireState(event, State.REQUEST_PROCESSED);
      }
    }

    private void postProcessing(NetEvent<Req, Res> event) {
      if (responseProcessor != null || poolResponseProcessor != null) {
        fireState(event, State.RESPONSE_PROCESSING);
        if (responseProcessor != null) responseProcessor.processResponse(Network.this, answer, response);
        if (poolResponseProcessor != null) poolResponseProcessor.processResponse(Network.this, answer, response);
        fireState(event, State.RESPONSE_PROCESSED);
      }
    }

    private String readLine(InputStream in, Charset charset) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int c;
      for (c = in.read(); c != '\n' && c != -1; c = in.read()) {
        if (c != '\r') byteArrayOutputStream.write(c);
      }
      if (c == -1 && byteArrayOutputStream.size() == 0) {
        return null;
      }
      return new String(byteArrayOutputStream.toByteArray(), charset);
    }

    private byte[] readMac(InputStream in) throws IOException {
      byte[] data = new byte[32];
      int pos = 0;
      int nRead;
      while ((nRead = in.read(data, pos, data.length - pos)) != -1) {
        pos += nRead;
        if (pos == data.length) break;
      }
      if (pos != data.length) throw new IOException("MAC length error");
      return data;
    }
  }

  public enum State {
    STARTED, REQUEST_PROCESSING, REQUEST_PROCESSED, ESTABLISHING_CONNECTION, CONNECTED, HEADER_SENT, DATA_SENT,
    HEADER_RECEIVED, DATA_RECEIVED, RESPONSE_PROCESSING, RESPONSE_PROCESSED, FINISHED
  }

  public enum EventMode {STANDARD, EDT_INVOKE_LATER, EDT_INVOKE_AND_WAIT}
}
