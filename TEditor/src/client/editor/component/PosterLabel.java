package client.editor.component;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.Border;

import client.editor.*;
import client.editor.cache.entity.*;
import client.editor.cache.entity.Image;
import client.net.*;
import server.protocol2.*;
import server.protocol2.editor.ImageObj;

import static client.editor.Env.cache;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 17.06.15
 */
public class PosterLabel extends JLabel implements NetListener<Request, Response> {
  private static final Dimension normalSize = new Dimension(207, 217);
  private static final Dimension compactSize = new Dimension(124, 130);
  private static final String loadingText = "Загрузка...";
  private static final String errorText = "Ошибка";
  private PosterData posterData;
  private String savedText = null;
  private Long loadingId;

  public PosterLabel() {
    setSize(false);
  }

  public void setSize(boolean compact) {
    if (compact) {
      setPreferredSize(new Dimension(compactSize));
      setMinimumSize(new Dimension(compactSize));
    } else {
      setPreferredSize(new Dimension(normalSize));
      setMinimumSize(new Dimension(normalSize));
    }
  }

  public void setPosterData(PosterData posterData) {
    this.posterData = posterData;
  }

  @Override
  public void setText(String text) {
    super.setText(text);
    savedText = text;
  }

  public void setPoster(byte[] img) {
    loadingId = null;
    if (img != null) super.setText(null);
    else super.setText(savedText);

    if (img == null) super.setIcon(null);
    else {
      Dimension innerSize = getInnerSize();
      ImageIcon scaledIcon = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(innerSize.width, innerSize.height, BufferedImage.SCALE_SMOOTH));
      super.setIcon(scaledIcon);
    }
  }

  public void setPoster(ImageObj poster, long id) {
    if (poster == null) setPoster(null);
    else if (poster.getImg().length > 0) setPoster(poster.getImg());
    else {
      Image cachedImage = null;
      switch (posterData) {
        case VENUE_BIG:
          cachedImage = cache.getVenueBigImage(id);
          break;
        case ACTION_BIG:
          cachedImage = cache.getActionBigImage(id);
          break;
        case ACTION_SMALL:
          cachedImage = cache.getActionSmallImage(id);
          break;
      }
      if (cachedImage != null && cachedImage.getHash().equals(poster.getHash())) {
        setPoster(cachedImage.getData());
      } else {
        setPoster(null);
        super.setText(loadingText);
        loadingId = id;
        Env.net.create(posterData.getCommand(), new Request(id), this, 10000).start();
      }
    }
  }

  private Dimension getInnerSize() {
    Border border = getBorder();
    if (border == null) return new Dimension(getWidth(), getHeight());
    Insets insets = border.getBorderInsets(this);
    int dx = insets.left + insets.right;
    int dy = insets.top + insets.bottom;
    return new Dimension(getWidth() - dx, getHeight() - dy);
  }

  @Override
  public void netState(NetEvent<Request, Response> event, Network.State state) {
  }

  @Override
  public void netResult(NetResultEvent<Request, Response> result) {
    Long id = (Long) result.getRequest().getData();
    if (!result.getResponse().isSuccess()) {
      if (id.equals(loadingId)) super.setText(errorText);
      return;
    }
    ImageObj image = (ImageObj) result.getResponse().getData();
    if (id.equals(loadingId)) setPoster(image.getImg());
    switch (posterData) {
      case VENUE_BIG:
        cache.put(new VenueBigImage(id, image.getHash(), image.getImg()));
        break;
      case ACTION_BIG:
        cache.put(new ActionBigImage(id, image.getHash(), image.getImg()));
        break;
      case ACTION_SMALL:
        cache.put(new ActionSmallImage(id, image.getHash(), image.getImg()));
        break;
    }
  }

  @Override
  public void netError(NetErrorEvent<Request, Response> error) {
    Long id = (Long) error.getRequest().getData();
    if (id.equals(loadingId)) super.setText(errorText);
  }
}
