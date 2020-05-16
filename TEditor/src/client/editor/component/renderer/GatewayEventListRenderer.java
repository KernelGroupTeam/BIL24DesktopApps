package client.editor.component.renderer;

import java.awt.*;
import java.util.Set;
import javax.swing.*;

import client.component.suggestion.ElementToStringConverter;
import client.editor.Env;
import org.jetbrains.annotations.NotNull;
import server.protocol2.editor.GatewayEventObj;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 24.07.15
 */
public class GatewayEventListRenderer implements ListCellRenderer<GatewayEventObj>, ElementToStringConverter<GatewayEventObj> {
  private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
  private int lastGatewayId;
  private Set<Object> lastGatewayIdSet;

  @Override
  public Component getListCellRendererComponent(JList<? extends GatewayEventObj> list, GatewayEventObj value, int index, boolean isSelected, boolean cellHasFocus) {
    String str = stringValue(value);
    return renderer.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus);
  }

  @Override
  public String stringValue(GatewayEventObj value) {
    if (value == null) return "";
    String str = value.toString();
    if (isExists(value)) str = "√ " + str;
    return str;
  }

  public boolean isExists(@NotNull GatewayEventObj value) {
    int gatewayId = value.getGateway().getId();
    if (gatewayId == 0) return false;
    Set<Object> gatewayIdSet;
    if (gatewayId == lastGatewayId) {
      gatewayIdSet = lastGatewayIdSet;
    } else {
      gatewayIdSet = Env.gatewayEventMap.get(gatewayId);
      lastGatewayId = gatewayId;
      lastGatewayIdSet = gatewayIdSet;
    }
    return gatewayIdSet != null && gatewayIdSet.contains(value.getEventUid());
  }
}
