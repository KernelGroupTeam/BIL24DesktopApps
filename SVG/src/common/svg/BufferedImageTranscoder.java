package common.svg;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 29.08.15
 */
class BufferedImageTranscoder extends ImageTranscoder {
  private BufferedImage image = null;

  @Override
  public BufferedImage createImage(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    return image;
  }

  @Override
  public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
    //empty
  }

  public BufferedImage getImage() {
    return image;
  }
}
