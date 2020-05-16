package client.editor.splsvg;


/**
 * Keeps information about target sector: names, size, location in output model
 */
class SvgSector {
  // size constants
  static final double spaceBetweenSectors = 35;
  static final double spaceAfterRowNumbers = 20;
  static final double spaceBetweenSeats = 2;
  static final double symbolWidth = 2;
  static final double seatRadius = 3.5;
  static final double seatDiameter = seatRadius * 2;
  static final double spaceBetweenRows = seatDiameter;
  static final double titleHeight = 2;
  static final double rowNumberYOffset = 3.8;

  // seats information
  private final Named.Sector sector;

  // helps to determine final width and height
  private final int maxSeats;
  private final int rows;

  // sectors are distributed in rectangle, this coordinates point to place at that rectangle
  private final int cellX;
  private final int cellY;

  // coordinates in output .svg
  private double xOffset;
  private double yOffset;
  private double height;
  private double width;

  SvgSector(int cellX, int cellY, Named.Sector sector) {
    this.sector = sector;
    this.cellX = cellX;
    this.cellY = cellY;
    this.rows = sector.getRows().size();
    this.maxSeats = calculateMaxSeats(sector);
    this.height = calculateHeight();
    this.width = calculateWidth();
  }

  Named.Sector getSector() {
    return sector;
  }

  double getWidth() {
    return width;
  }

  double getHeight() {
    return height;
  }

  int getMaxSeats() {
    return maxSeats;
  }

  int getRows() {
    return rows;
  }

  int getCellX() {
    return cellX;
  }

  int getCellY() {
    return cellY;
  }

  double getxOffset() {
    return xOffset;
  }

  SvgSector setxOffset(double xOffset) {
    this.xOffset = xOffset;
    return this;
  }

  double getyOffset() {
    return yOffset;
  }

  SvgSector setyOffset(double yOffset) {
    this.yOffset = yOffset;
    return this;
  }

  SvgSector setHeight(double height) {
    this.height = height;
    return this;
  }

  SvgSector setWidth(double width) {
    this.width = width;
    return this;
  }

  private int calculateMaxSeats(Named.Sector sector) {
    int max = -1;
    for (Named.Row row : sector.getRows().values()) {
      int size = row.getSeats().size();
      if (size > max) max = size;
    }

    return max;
  }

  private double calculateWidth() {
    return
        calculateRowNumbersWidth() +
            spaceAfterRowNumbers +
            maxSeats * (seatDiameter + spaceBetweenSeats);
  }

  private double calculateRowNumbersWidth() {
    int maxSymbols = -1;
    for (Named.Row row : sector.getRows().values()) {
      int length = row.getName().length();
      if (length > maxSymbols) maxSymbols = length;
    }

    // max symbols can't be equal -1, such sector should not be parsed before
    return maxSymbols * symbolWidth;
  }

  private double calculateHeight() {
    return titleHeight + (rows * (seatDiameter + spaceBetweenRows));
  }
}
