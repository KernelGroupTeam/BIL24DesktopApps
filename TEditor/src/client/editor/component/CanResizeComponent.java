package client.editor.component;

import client.editor.component.listener.ResizeListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public interface CanResizeComponent {

  void addResizeListener(ResizeListener l);

  void removeResizeListener(ResizeListener l);

  ResizeListener[] getResizeListeners();
}
