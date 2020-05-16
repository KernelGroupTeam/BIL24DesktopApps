package client.editor.component.listener;

import java.util.EventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public interface ResizeListener extends EventListener {

  void needResize(ResizeEvent event);
}
