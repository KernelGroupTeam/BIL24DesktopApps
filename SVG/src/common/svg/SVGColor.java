package common.svg;

import java.awt.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.11.16
 */
class SVGColor {
  @NotNull
  private final String value;
  @NotNull
  private final Color color;

  public SVGColor(@NotNull String value) {
    this.value = value;
    this.color = CSS3ColorParser.parse(value);
  }

  @NotNull
  public String getValue() {
    return value;
  }

  @NotNull
  public Color getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SVGColor)) return false;
    SVGColor svgColor = (SVGColor) o;
    return color.equals(svgColor.color);

  }

  @Override
  public int hashCode() {
    return color.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
