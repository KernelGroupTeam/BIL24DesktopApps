package common.svg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.*;

import org.apache.batik.transcoder.*;
import org.jetbrains.annotations.*;
import org.w3c.dom.svg.SVGDocument;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 26.08.15
 */
public class SVGTranscoder {

  private SVGTranscoder() {
  }

  @NotNull
  public static BufferedImage svgToImage(File svgFile) throws IOException, TranscoderException {
    TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));
    return svgToImage(input, null, null);
  }

  @NotNull
  public static BufferedImage svgToImage(@NotNull byte[] svgData) throws TranscoderException {
    TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgData));
    return svgToImage(input, null, null);
  }

  @NotNull
  public static BufferedImage svgToImage(@NotNull SVGDocument svgDocument) throws TranscoderException {
    TranscoderInput input = new TranscoderInput(svgDocument);
    return svgToImage(input, null, null);
  }

  @NotNull
  public static BufferedImage svgToScaledImageFast(File svgFile, int maxWidth, int maxHeight) throws IOException, TranscoderException {
    TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));
    return svgToImage(input, maxWidth, maxHeight);
  }

  @NotNull
  public static BufferedImage svgToScaledImageFast(@NotNull byte[] svgData, int maxWidth, int maxHeight) throws TranscoderException {
    TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svgData));
    return svgToImage(input, maxWidth, maxHeight);
  }

  @NotNull
  private static BufferedImage svgToImage(@NotNull TranscoderInput input, @Nullable Integer maxWidth, @Nullable Integer maxHeight) throws TranscoderException {
    BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
    if (maxWidth != null) transcoder.addTranscodingHint(BufferedImageTranscoder.KEY_MAX_WIDTH, maxWidth.floatValue());
    if (maxHeight != null) transcoder.addTranscodingHint(BufferedImageTranscoder.KEY_MAX_HEIGHT, maxHeight.floatValue());
    transcoder.transcode(input, null);
    return transcoder.getImage();
  }

  @NotNull
  public static Image svgToScaledImage(File svgFile, int maxWidth, int maxHeight) throws IOException, TranscoderException {
    BufferedImage image = svgToImage(svgFile);
    return scaleImage(image, maxWidth, maxHeight);
  }

  @NotNull
  public static Image svgToScaledImage(@NotNull byte[] svgData, int maxWidth, int maxHeight) throws TranscoderException {
    BufferedImage image = svgToImage(svgData);
    return scaleImage(image, maxWidth, maxHeight);
  }

  @NotNull
  public static Image scaleImage(@NotNull BufferedImage image, int maxWidth, int maxHeight) {
    int imageWidth = image.getWidth();
    int imageHeight = image.getHeight();
    int minWidth = Math.min(maxWidth, imageWidth);
    int minHeight = Math.min(maxHeight, imageHeight);
    double aspect1 = minWidth / (double) imageWidth;
    double aspect2 = minHeight / (double) imageHeight;
    double aspect = Math.min(aspect1, aspect2);
    int scaledWidth = (int) (imageWidth * aspect);
    int scaledHeight = (int) (imageHeight * aspect);
    return image.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH);
  }

  @NotNull
  public static byte[] toGzip(@NotNull byte[] svgData) {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(result)) {
      gzipOutputStream.write(svgData);
    } catch (IOException e) {//оборачиваем в RuntimeException, т.к. тут его быть не должно
      throw new RuntimeException(e);
    }
    return result.toByteArray();
  }

  @NotNull
  public static byte[] fromGzip(byte[] svgData) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    ByteArrayInputStream srcStream = new ByteArrayInputStream(svgData);
    try (GZIPInputStream gzipInputStream = new GZIPInputStream(srcStream)) {
      byte[] buffer = new byte[1024];
      int read;
      while ((read = gzipInputStream.read(buffer, 0, buffer.length)) != -1) {
        result.write(buffer, 0, read);
      }
    }
    return result.toByteArray();
  }
}
