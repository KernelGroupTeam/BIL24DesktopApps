package client.net;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.SocketFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.12.15
 */
public class NetPool implements Cloneable {
  private final AtomicInteger activeThreads = new AtomicInteger();
  private final NetPoolJointListener poolJointListener = new NetPoolJointListenerImpl();
  private final List<NetPoolListenerOver> poolListenerList = new CopyOnWriteArrayList<>();
  private final List<NetPoolStateListenerOver> poolStateListenerList = new CopyOnWriteArrayList<>();
  private InetSocketAddress serverAddress;
  private SocketFactory socketFactory = SocketFactory.getDefault();
  private boolean zip = false;
  private byte[] macSecurity = null;
  private int sendingBufSize = 8192;
  private int receivingBufSize = 8192;
  private int timeout = 20000;
  private Network.EventMode eventMode = Network.EventMode.STANDARD;
  private NetPoolRequestProcessor poolRequestProcessor;
  private NetPoolResponseProcessor poolResponseProcessor;

  public NetPool(InetSocketAddress serverAddress) {
    if (serverAddress == null) throw new IllegalArgumentException("serverAddress is null");
    this.serverAddress = serverAddress;
  }

  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Override
  public NetPool clone() {
    NetPool netPool = new NetPool(serverAddress);
    netPool.setSocketFactory(socketFactory);
    netPool.setZip(zip);
    netPool.setMacSecurity(macSecurity);
    netPool.setSendingBufSize(sendingBufSize);
    netPool.setReceivingBufSize(receivingBufSize);
    netPool.setTimeout(timeout);
    netPool.setEventMode(eventMode);
    netPool.setPoolRequestProcessor(poolRequestProcessor);
    netPool.setPoolResponseProcessor(poolResponseProcessor);
    return netPool;
  }

  public NetPool clone(boolean copyPoolListeners) {
    NetPool netPool = clone();
    if (copyPoolListeners) netPool.poolListenerList.addAll(poolListenerList);
    return netPool;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener) {
    Network<Req, Res> network = new Network<>(serverAddress, command, request, listener, poolJointListener, socketFactory, zip, sendingBufSize, receivingBufSize, timeout, eventMode);
    network.setMacSecurity(macSecurity);
    network.setPoolRequestProcessor(poolRequestProcessor);
    network.setPoolResponseProcessor(poolResponseProcessor);
    return network;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener, boolean zip) {
    Network<Req, Res> network = create(command, request, listener);
    network.setZip(zip);
    return network;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener, int timeout) {
    Network<Req, Res> network = create(command, request, listener);
    network.setTimeout(timeout);
    return network;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener, boolean zip, int timeout) {
    Network<Req, Res> network = create(command, request, listener);
    network.setZip(zip);
    network.setTimeout(timeout);
    return network;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener, Integer sendingBufSize, Integer receivingBufSize) {
    Network<Req, Res> network = create(command, request, listener);
    if (sendingBufSize != null) network.setSendingBufSize(sendingBufSize);
    if (receivingBufSize != null) network.setReceivingBufSize(receivingBufSize);
    return network;
  }

  public <Req, Res> Network<Req, Res> create(String command, Req request, NetListener<Req, Res> listener, Byte sendingBufMultiplier, Byte receivingBufMultiplier) {
    Network<Req, Res> network = create(command, request, listener);
    if (sendingBufMultiplier != null) network.setSendingBufSize(sendingBufSize * sendingBufMultiplier);
    if (receivingBufMultiplier != null) network.setReceivingBufSize(receivingBufSize * receivingBufMultiplier);
    return network;
  }

  public int getActiveThreads() {
    return activeThreads.get();
  }

  public InetSocketAddress getServerAddress() {
    return serverAddress;
  }

  public SocketFactory getSocketFactory() {
    return socketFactory;
  }

  public void setServerAddress(InetSocketAddress serverAddress) {
    if (serverAddress == null) throw new IllegalArgumentException("serverAddress is null");
    this.serverAddress = serverAddress;
  }

  public void setSocketFactory(SocketFactory socketFactory) {
    if (socketFactory == null) throw new IllegalArgumentException("socketFactory is null");
    this.socketFactory = socketFactory;
  }

  public boolean isZip() {
    return zip;
  }

  public void setZip(boolean zip) {
    this.zip = zip;
  }

  public void setMacSecurity(byte[] macSecurity) {
    if (macSecurity == null) throw new IllegalArgumentException("use removeMacSecurity() instead");
    removeMacSecurity();
    this.macSecurity = macSecurity;
  }

  public void removeMacSecurity() {
    if (macSecurity != null) {
      Arrays.fill(macSecurity, (byte) 0);
      macSecurity = null;
    }
  }

  public int getSendingBufSize() {
    return sendingBufSize;
  }

  public void setSendingBufSize(int sendingBufSize) {
    if (sendingBufSize < 512) sendingBufSize = 512;
    this.sendingBufSize = sendingBufSize;
  }

  public int getReceivingBufSize() {
    return receivingBufSize;
  }

  public void setReceivingBufSize(int receivingBufSize) {
    if (receivingBufSize < 512) receivingBufSize = 512;
    this.receivingBufSize = receivingBufSize;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public Network.EventMode getEventMode() {
    return eventMode;
  }

  public void setEventMode(Network.EventMode eventMode) {
    this.eventMode = eventMode;
  }

  public void addPoolListener(NetPoolListener poolListener) {
    addPoolListener(poolListener, Network.EventMode.STANDARD);
  }

  public void addPoolListener(NetPoolListener poolListener, Network.EventMode eventMode) {
    if (poolListener == null) return;
    if (eventMode == Network.EventMode.STANDARD)
      poolListenerList.add(new EDTStdNetPoolListener(poolListener));
    else if (eventMode == Network.EventMode.EDT_INVOKE_LATER)
      poolListenerList.add(new EDTLaterNetPoolListener(poolListener));
    else if (eventMode == Network.EventMode.EDT_INVOKE_AND_WAIT)
      poolListenerList.add(new EDTWaitNetPoolListener(poolListener));
  }

  public List<NetPoolListener> getPoolListenerList() {
    List<NetPoolListener> result = new ArrayList<>(poolListenerList.size());
    for (NetPoolListenerOver poolListenerOver : poolListenerList) {
      result.add(poolListenerOver.getOverridden());
    }
    return result;
  }

  public boolean removePoolListener(NetPoolListener poolListener) {
    if (poolListener == null) return false;
    for (NetPoolListenerOver poolListenerOver : poolListenerList) {
      if (poolListenerOver.getOverridden().equals(poolListener)) {
        return poolListenerList.remove(poolListenerOver);
      }
    }
    return false;
  }

  public void addPoolStateListener(NetPoolStateListener poolStateListener) {
    addPoolStateListener(poolStateListener, Network.EventMode.STANDARD);
  }

  public void addPoolStateListener(NetPoolStateListener poolStateListener, Network.EventMode eventMode) {
    if (poolStateListener == null) return;
    if (eventMode == Network.EventMode.STANDARD)
      poolStateListenerList.add(new EDTStdNetPoolStateListener(poolStateListener));
    else if (eventMode == Network.EventMode.EDT_INVOKE_LATER)
      poolStateListenerList.add(new EDTLaterNetPoolStateListener(poolStateListener));
    else if (eventMode == Network.EventMode.EDT_INVOKE_AND_WAIT)
      poolStateListenerList.add(new EDTWaitNetPoolStateListener(poolStateListener));
  }

  public List<NetPoolStateListener> getPoolStateListenerList() {
    List<NetPoolStateListener> result = new ArrayList<>(poolStateListenerList.size());
    for (NetPoolStateListenerOver poolStateListenerOver : poolStateListenerList) {
      result.add(poolStateListenerOver.getOverridden());
    }
    return result;
  }

  public boolean removePoolStateListener(NetPoolStateListener poolStateListener) {
    if (poolStateListener == null) return false;
    for (NetPoolStateListenerOver poolStateListenerOver : poolStateListenerList) {
      if (poolStateListenerOver.getOverridden().equals(poolStateListener)) {
        return poolStateListenerList.remove(poolStateListenerOver);
      }
    }
    return false;
  }

  public NetPoolRequestProcessor getPoolRequestProcessor() {
    return poolRequestProcessor;
  }

  public void setPoolRequestProcessor(NetPoolRequestProcessor poolRequestProcessor) {
    this.poolRequestProcessor = poolRequestProcessor;
  }

  public NetPoolResponseProcessor getPoolResponseProcessor() {
    return poolResponseProcessor;
  }

  public void setPoolResponseProcessor(NetPoolResponseProcessor poolResponseProcessor) {
    this.poolResponseProcessor = poolResponseProcessor;
  }

  private class NetPoolJointListenerImpl implements NetPoolJointListener {
    @Override
    public void netState(Network<?, ?> network, NetEvent<?, ?> event, Network.State state) {
      for (NetPoolListenerOver poolListener : poolListenerList) {
        poolListener.netState(network, event, state);
      }
    }

    @Override
    public void netResult(Network<?, ?> network, NetResultEvent<?, ?> result) {
      for (NetPoolListenerOver poolListener : poolListenerList) {
        poolListener.netResult(network, result);
      }
    }

    @Override
    public void netError(Network<?, ?> network, NetErrorEvent<?, ?> error) {
      for (NetPoolListenerOver poolListener : poolListenerList) {
        poolListener.netError(network, error);
      }
    }

    @Override
    public void netPoolStarted() {
      if (activeThreads.getAndIncrement() == 0) {
        for (NetPoolStateListenerOver poolStateListener : poolStateListenerList) {
          poolStateListener.netPoolStarted();
        }
      }
    }

    @Override
    public void netPoolFinished() {
      if (activeThreads.decrementAndGet() == 0) {
        for (NetPoolStateListenerOver poolStateListener : poolStateListenerList) {
          poolStateListener.netPoolFinished();
        }
      }
    }
  }
}
