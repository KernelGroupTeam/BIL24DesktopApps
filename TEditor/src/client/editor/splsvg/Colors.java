package client.editor.splsvg;

/**
 * Provide information about colors
 */
public enum Colors {
  FIRST("Первая", "ff0000"),
  SECOND("Вторая", "ffa000"),
  THIRD("Третья", "ffff00"),
  FOURTH("Четвертая", "00ff00"),
  FIFTH("Пятая", "00ffff"),
  SIXTH("Шестая", "0000ff"),
  SEVENTH("Седьмая", "800080"),
  EIGHTH("Восьмая", "008000"),
  NINTH("Девятая", "a9ff2d"),
  TENTH("Десятая", "aa2800"),
  ELEVENTH("Одиннадцатая", "0096ff"),
  TWELFTH("Двенадцатая", "808000"),
  THIRTEENTH("Тринадцатая", "3caa00"),
  FOURTEENTH("Четырнадцатая", "d4aa00"),
  FIFTEENTH("Пятнадцатая", "00d455"),
  SIXTEENTH("Шестнадцатая", "ff8080"),
  SEVENTEENTH("Семнадцатая", "008080"),
  EIGHTEENTH("Восемнадцатая", "cd87de"),
  NINETEENTH("Девятнадцатая", "784420"),
  TWENTIETH("Двадцатая", "91b272"),
  TWENTY_FIRST("Двадцать первая", "ff6600"),
  TWENTY_SECOND("Двадцать вторая", "ff002e"),
  TWENTY_THIRD("Двадцать третья", "00ff83"),
  TWENTY_FOURTH("Двадцать четвертая", "5500d4"),
  TWENTY_FIFTH("Двадцать пятая", "aa0044"),
  TWENTY_SIXTH("Двадцать шестая", "6c5367"),
  TWENTY_SEVENTH("Двадцать седьмая", "320080"),
  TWENTY_EIGHTH("Двадцать восьмая", "0080ff"),
  TWENTY_NINTH("Двадцать девятая", "ff3b2d"),
  THIRTIETH("Тридцатая", "5c221a");

  public final String hex;
  public final String order;

  Colors(String order, String hex) {
    this.order = order;
    this.hex = hex;
  }
}
