package client.editor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;

import client.component.*;
import client.editor.cache.LocalCache;
import client.editor.model.ManualQuotaData;
import client.net.NetPool;
import org.jetbrains.annotations.*;
import server.protocol2.common.*;
import server.protocol2.editor.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 13.04.16
 */
public class Env {
  public static final String ver = "2.0";
  public static final List<Image> frameIcons;

  static {
    List<Image> icons = new ArrayList<>(6);
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon16.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon32.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon48.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon64.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon128.png")).getImage());
    icons.add(new ImageIcon(Env.class.getResource("/resources/icon256.png")).getImage());
    frameIcons = Collections.unmodifiableList(icons);
  }

  public static final int MAX_CAT_NUMBER_SEATS = 50000;
  public static final int MAX_PLAN_NUMBER_SEATS = 50000;
  public static final int MAX_PLAN_NUMBER_SEATS2 = 80000;
  public static final int MAX_ADD_EVENTS_NUMBER_SEATS = 50000;
  public static final int GATEWAY_TIMEOUT = 60000;
  public static final Set<Long> loadedArchivalActionIdSet = new HashSet<>();
  public static final Map<Integer, Set<Object>> gatewayEventMap = new HashMap<>();
  public static final Map<Long, ManualQuotaData> planQuotaDataMap = new HashMap<>();

  public static boolean testZone;
  public static Preferences pref;
  public static NetPool net;
  public static LocalCache cache;
  public static ConsoleFrame consoleFrame;
  public static LoginUser user;

  public static String planUrl = "";
  public static String bookletUrl = "";

  public static List<GatewayObj> gatewayList = Collections.emptyList();
  public static List<EOrganizer> organizerList = Collections.emptyList();
  public static List<BarcodeFormat> barcodeFormatList = Collections.emptyList();
  public static List<QuotaFormatObj> quotaFormatList = Collections.emptyList();
  public static List<BookletType> bookletTypeList = Collections.emptyList();
  public static List<GenreObj> genreList = Collections.emptyList();

  private Env() {
  }

  public static void addGatewayEvent(ActionEventObj actionEventObj) {
    GatewayEventObj gatewayEventObj = actionEventObj.getGatewayEvent();
    GatewayObj gateway = gatewayEventObj.getGateway();
    if (gateway.getId() == 0) return;
    Set<Object> gatewayIdSet = gatewayEventMap.get(gateway.getId());
    if (gatewayIdSet == null) gatewayEventMap.put(gateway.getId(), gatewayIdSet = new HashSet<>());
    gatewayIdSet.add(gatewayEventObj.getEventId());
  }

  public static void updateCategoryPrices(@NotNull ActionEventData actionEventData, @Nullable ActionEventObj actionEvent,
                                          @NotNull OperationComboBox<ActionEventObj> actionEventComboBox) {
    if (actionEvent == null) {
      for (ActionEventObj element : actionEventComboBox.getElementList()) {
        if (element.getId() == actionEventData.getId()) {
          actionEvent = element;
          break;
        }
      }
    }
    if (actionEvent == null) return;
    for (CategoryPriceObj currentPrice : actionEvent.getPriceList()) {
      for (CategoryPriceObj actualPrice : actionEventData.getCategoryPriceList()) {
        if (currentPrice.getId() == actualPrice.getId()) {
          currentPrice.setPrice(actualPrice.getPrice());
          currentPrice.setName(actualPrice.getName());
          currentPrice.setAvailability(actualPrice.getAvailability());
          break;
        }
      }
    }
    if (actionEvent.equals(actionEventComboBox.getSelectedElement())) actionEventComboBox.reload();
  }
}
