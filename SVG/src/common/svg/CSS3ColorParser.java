package common.svg;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.11.16
 */
public class CSS3ColorParser {
  private static final Map<String, Integer> colorKeywords;

  static {
    Map<String, Integer> keywords = new ConcurrentHashMap<>(147, 0.75f, 1);
    keywords.put("aliceblue", 0xf0f8ff);
    keywords.put("antiquewhite", 0xfaebd7);
    keywords.put("aqua", 0x00ffff);
    keywords.put("aquamarine", 0x7fffd4);
    keywords.put("azure", 0xf0ffff);
    keywords.put("beige", 0xf5f5dc);
    keywords.put("bisque", 0xffe4c4);
    keywords.put("black", 0x000000);
    keywords.put("blanchedalmond", 0xffebcd);
    keywords.put("blue", 0x0000ff);
    keywords.put("blueviolet", 0x8a2be2);
    keywords.put("brown", 0xa52a2a);
    keywords.put("burlywood", 0xdeb887);
    keywords.put("cadetblue", 0x5f9ea0);
    keywords.put("chartreuse", 0x7fff00);
    keywords.put("chocolate", 0xd2691e);
    keywords.put("coral", 0xff7f50);
    keywords.put("cornflowerblue", 0x6495ed);
    keywords.put("cornsilk", 0xfff8dc);
    keywords.put("crimson", 0xdc143c);
    keywords.put("cyan", 0x00ffff);
    keywords.put("darkblue", 0x00008b);
    keywords.put("darkcyan", 0x008b8b);
    keywords.put("darkgoldenrod", 0xb8860b);
    keywords.put("darkgray", 0xa9a9a9);
    keywords.put("darkgreen", 0x006400);
    keywords.put("darkgrey", 0xa9a9a9);
    keywords.put("darkkhaki", 0xbdb76b);
    keywords.put("darkmagenta", 0x8b008b);
    keywords.put("darkolivegreen", 0x556b2f);
    keywords.put("darkorange", 0xff8c00);
    keywords.put("darkorchid", 0x9932cc);
    keywords.put("darkred", 0x8b0000);
    keywords.put("darksalmon", 0xe9967a);
    keywords.put("darkseagreen", 0x8fbc8f);
    keywords.put("darkslateblue", 0x483d8b);
    keywords.put("darkslategray", 0x2f4f4f);
    keywords.put("darkslategrey", 0x2f4f4f);
    keywords.put("darkturquoise", 0x00ced1);
    keywords.put("darkviolet", 0x9400d3);
    keywords.put("deeppink", 0xff1493);
    keywords.put("deepskyblue", 0x00bfff);
    keywords.put("dimgray", 0x696969);
    keywords.put("dimgrey", 0x696969);
    keywords.put("dodgerblue", 0x1e90ff);
    keywords.put("firebrick", 0xb22222);
    keywords.put("floralwhite", 0xfffaf0);
    keywords.put("forestgreen", 0x228b22);
    keywords.put("fuchsia", 0xff00ff);
    keywords.put("gainsboro", 0xdcdcdc);
    keywords.put("ghostwhite", 0xf8f8ff);
    keywords.put("gold", 0xffd700);
    keywords.put("goldenrod", 0xdaa520);
    keywords.put("gray", 0x808080);
    keywords.put("green", 0x008000);
    keywords.put("greenyellow", 0xadff2f);
    keywords.put("grey", 0x808080);
    keywords.put("honeydew", 0xf0fff0);
    keywords.put("hotpink", 0xff69b4);
    keywords.put("indianred", 0xcd5c5c);
    keywords.put("indigo", 0x4b0082);
    keywords.put("ivory", 0xfffff0);
    keywords.put("khaki", 0xf0e68c);
    keywords.put("lavender", 0xe6e6fa);
    keywords.put("lavenderblush", 0xfff0f5);
    keywords.put("lawngreen", 0x7cfc00);
    keywords.put("lemonchiffon", 0xfffacd);
    keywords.put("lightblue", 0xadd8e6);
    keywords.put("lightcoral", 0xf08080);
    keywords.put("lightcyan", 0xe0ffff);
    keywords.put("lightgoldenrodyellow", 0xfafad2);
    keywords.put("lightgray", 0xd3d3d3);
    keywords.put("lightgreen", 0x90ee90);
    keywords.put("lightgrey", 0xd3d3d3);
    keywords.put("lightpink", 0xffb6c1);
    keywords.put("lightsalmon", 0xffa07a);
    keywords.put("lightseagreen", 0x20b2aa);
    keywords.put("lightskyblue", 0x87cefa);
    keywords.put("lightslategray", 0x778899);
    keywords.put("lightslategrey", 0x778899);
    keywords.put("lightsteelblue", 0xb0c4de);
    keywords.put("lightyellow", 0xffffe0);
    keywords.put("lime", 0x00ff00);
    keywords.put("limegreen", 0x32cd32);
    keywords.put("linen", 0xfaf0e6);
    keywords.put("magenta", 0xff00ff);
    keywords.put("maroon", 0x800000);
    keywords.put("mediumaquamarine", 0x66cdaa);
    keywords.put("mediumblue", 0x0000cd);
    keywords.put("mediumorchid", 0xba55d3);
    keywords.put("mediumpurple", 0x9370db);
    keywords.put("mediumseagreen", 0x3cb371);
    keywords.put("mediumslateblue", 0x7b68ee);
    keywords.put("mediumspringgreen", 0x00fa9a);
    keywords.put("mediumturquoise", 0x48d1cc);
    keywords.put("mediumvioletred", 0xc71585);
    keywords.put("midnightblue", 0x191970);
    keywords.put("mintcream", 0xf5fffa);
    keywords.put("mistyrose", 0xffe4e1);
    keywords.put("moccasin", 0xffe4b5);
    keywords.put("navajowhite", 0xffdead);
    keywords.put("navy", 0x000080);
    keywords.put("oldlace", 0xfdf5e6);
    keywords.put("olive", 0x808000);
    keywords.put("olivedrab", 0x6b8e23);
    keywords.put("orange", 0xffa500);
    keywords.put("orangered", 0xff4500);
    keywords.put("orchid", 0xda70d6);
    keywords.put("palegoldenrod", 0xeee8aa);
    keywords.put("palegreen", 0x98fb98);
    keywords.put("paleturquoise", 0xafeeee);
    keywords.put("palevioletred", 0xdb7093);
    keywords.put("papayawhip", 0xffefd5);
    keywords.put("peachpuff", 0xffdab9);
    keywords.put("peru", 0xcd853f);
    keywords.put("pink", 0xffc0cb);
    keywords.put("plum", 0xdda0dd);
    keywords.put("powderblue", 0xb0e0e6);
    keywords.put("purple", 0x800080);
    keywords.put("red", 0xff0000);
    keywords.put("rosybrown", 0xbc8f8f);
    keywords.put("royalblue", 0x4169e1);
    keywords.put("saddlebrown", 0x8b4513);
    keywords.put("salmon", 0xfa8072);
    keywords.put("sandybrown", 0xf4a460);
    keywords.put("seagreen", 0x2e8b57);
    keywords.put("seashell", 0xfff5ee);
    keywords.put("sienna", 0xa0522d);
    keywords.put("silver", 0xc0c0c0);
    keywords.put("skyblue", 0x87ceeb);
    keywords.put("slateblue", 0x6a5acd);
    keywords.put("slategray", 0x708090);
    keywords.put("slategrey", 0x708090);
    keywords.put("snow", 0xfffafa);
    keywords.put("springgreen", 0x00ff7f);
    keywords.put("steelblue", 0x4682b4);
    keywords.put("tan", 0xd2b48c);
    keywords.put("teal", 0x008080);
    keywords.put("thistle", 0xd8bfd8);
    keywords.put("tomato", 0xff6347);
    keywords.put("turquoise", 0x40e0d0);
    keywords.put("violet", 0xee82ee);
    keywords.put("wheat", 0xf5deb3);
    keywords.put("white", 0xffffff);
    keywords.put("whitesmoke", 0xf5f5f5);
    keywords.put("yellow", 0xffff00);
    keywords.put("yellowgreen", 0x9acd32);
    colorKeywords = Collections.unmodifiableMap(keywords);
  }

  private static final Pattern hash3Pattern = Pattern.compile("^#([0-9a-f]{3})$");
  private static final Pattern hash6Pattern = Pattern.compile("^#([0-9a-f]{6})$");
  private static final Pattern rgbIntPattern = Pattern.compile("^rgb\\(\\s*(?<r>-?\\d+)\\s*,\\s*(?<g>-?\\d+)\\s*,\\s*(?<b>-?\\d+)\\s*\\)$");
  private static final Pattern rgbPercentPattern = Pattern.compile("^rgb\\(\\s*(?<r>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*,\\s*(?<g>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*,\\s*(?<b>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*\\)$");
  private static final Pattern rgbaIntPattern = Pattern.compile("^rgba\\(\\s*(?<r>-?\\d+)\\s*,\\s*(?<g>-?\\d+)\\s*,\\s*(?<b>-?\\d+)\\s*,\\s*(?<a>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))\\s*\\)$");
  private static final Pattern rgbaPercentPattern = Pattern.compile("^rgba\\(\\s*(?<r>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*,\\s*(?<g>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*,\\s*(?<b>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))%\\s*,\\s*(?<a>(-?\\d+(\\.\\d+)?)|(-?\\.\\d+))\\s*\\)$");

  private CSS3ColorParser() {
  }

  @NotNull
  public static Color parse(@NotNull String color) {//не поддерживает HSL color values
    String s = color.trim().toLowerCase();
    Matcher matcher;

    if (s.startsWith("#")) {
      matcher = hash3Pattern.matcher(s);
      if (matcher.matches()) {
        String hex = matcher.group(1);
        int r = Integer.parseInt(String.valueOf(hex.charAt(0)), 16) * 0x11;
        int g = Integer.parseInt(String.valueOf(hex.charAt(1)), 16) * 0x11;
        int b = Integer.parseInt(String.valueOf(hex.charAt(2)), 16) * 0x11;
        return new Color(r, g, b);
      }

      matcher = hash6Pattern.matcher(s);
      if (matcher.matches()) {
        String hex = matcher.group(1);
        int rgb = Integer.parseInt(hex, 16);
        return new Color(rgb);
      }
    } else if (s.startsWith("rgb(")) {
      matcher = rgbIntPattern.matcher(s);
      if (matcher.matches()) {
        int r = clipped(Integer.parseInt(matcher.group("r"), 10));
        int g = clipped(Integer.parseInt(matcher.group("g"), 10));
        int b = clipped(Integer.parseInt(matcher.group("b"), 10));
        return new Color(r, g, b);
      }

      matcher = rgbPercentPattern.matcher(s);
      if (matcher.matches()) {
        float r = clipped(Float.parseFloat(matcher.group("r")) / 100);
        float g = clipped(Float.parseFloat(matcher.group("g")) / 100);
        float b = clipped(Float.parseFloat(matcher.group("b")) / 100);
        return new Color(r, g, b);
      }
    } else if (s.startsWith("rgba(")) {
      matcher = rgbaIntPattern.matcher(s);
      if (matcher.matches()) {
        int r = clipped(Integer.parseInt(matcher.group("r"), 10));
        int g = clipped(Integer.parseInt(matcher.group("g"), 10));
        int b = clipped(Integer.parseInt(matcher.group("b"), 10));
        float a = clipped(Float.parseFloat(matcher.group("a")));
        int alpha = (int) (a * 255 + 0.5);
        return new Color(r, g, b, alpha);
      }

      matcher = rgbaPercentPattern.matcher(s);
      if (matcher.matches()) {
        float r = clipped(Float.parseFloat(matcher.group("r")) / 100);
        float g = clipped(Float.parseFloat(matcher.group("g")) / 100);
        float b = clipped(Float.parseFloat(matcher.group("b")) / 100);
        float a = clipped(Float.parseFloat(matcher.group("a")));
        return new Color(r, g, b, a);
      }
    } else if (s.equals("transparent")) {
      return new Color(0x00000000, true);
    } else {
      Integer keywordColor = colorKeywords.get(s);
      if (keywordColor != null) return new Color(keywordColor);
    }
    throw new IllegalArgumentException(color);
  }

  @SuppressWarnings("ManualMinMaxCalculation")
  private static int clipped(int value) {
    if (value < 0) return 0;
    if (value > 255) return 255;
    return value;
  }

  @SuppressWarnings("ManualMinMaxCalculation")
  private static float clipped(float value) {
    if (value < 0.0f) return 0.0f;
    if (value > 1.0f) return 1.0f;
    return value;
  }

  @NotNull
  public static String toHash6Format(@NotNull Color color) {
    StringBuilder sb = new StringBuilder(7);
    Formatter formatter = new Formatter(sb);
    formatter.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    return sb.toString();
  }
}
