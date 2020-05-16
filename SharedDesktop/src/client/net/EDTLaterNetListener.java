package client.net;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 01.12.15
 */
class EDTLaterNetListener<Req, Res> implements NetListener<Req, Res> {
  private final NetListener<Req, Res> overriddenListener;

  public EDTLaterNetListener(NetListener<Req, Res> overriddenListener) {
    this.overriddenListener = overriddenListener;
  }

  @Override
  public void netState(final NetEvent<Req, Res> event, final Network.State state) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netState(event, state);
      }
    });
  }

  @Override
  public void netResult(final NetResultEvent<Req, Res> result) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netResult(result);
      }
    });
  }

  @Override
  public void netError(final NetErrorEvent<Req, Res> error) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        overriddenListener.netError(error);
      }
    });
  }
}
