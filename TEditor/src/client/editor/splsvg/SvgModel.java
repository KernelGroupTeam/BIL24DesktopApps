package client.editor.splsvg;

import java.util.*;

/**
 * Keeps information about sector names and sizes
 */
class SvgModel {
  private static final Comparator<Named.Sector> comparatorSector = new Comparator<Named.Sector>() {
    @Override
    public int compare(Named.Sector o1, Named.Sector o2) {
      return o1.getName().compareTo(o2.getName());
    }
  };
  private static final Comparator<SvgSector> comparatorSvgSector = new Comparator<SvgSector>() {
    @Override
    public int compare(SvgSector o1, SvgSector o2) {
      return o1.getRows() - o2.getRows();
    }
  };

  static final double maxWidth = 1999.0;
  static final double legendXOffset = 260;
  static final double leftOffset = 40;
  static final double topOffset = 40;

  private static final int maxColumns = 5;
  private static final int minColumns = 3;

  private final Map<Integer, List<SvgSector>> columns;
  private final Map<Integer, List<SvgSector>> rows;
  private final double height;
  private final double width;
  private final double scale;

  SvgModel(Map<String, Named.Sector> sectors) {
    double height1;
    // determine size of rectangle in sectors
    double squared = Math.ceil(Math.pow(sectors.size(), 0.5));
    int columns;
    int rows;
    if (squared < minColumns) {
      columns = minColumns;
      //максимальное количество колоннок 3, при этом секторов может быть 4, в этом случае один сектор не отрисовывается, т.к. rows == 1
      rows = 1;
      if (sectors.size() > 3) rows = 2;
    } else //noinspection FloatingPointEquality
      if (squared == maxColumns - 1) {
        columns = (int) (squared + 1);
        rows = (int) squared;
      } else {
        columns = maxColumns;
        rows = sectors.size() / columns;
        if (sectors.size() % columns != 0) rows++;
      }

    // determine order of sectors
    this.columns = new HashMap<>();
    this.rows = new HashMap<>();

    ArrayList<Named.Sector> list = new ArrayList<>(sectors.values());
    Collections.sort(list, comparatorSector);

    Iterator<Named.Sector> iterator = list.iterator();
    boolean isFinish = false;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        if (i * columns + j >= list.size()) {
          isFinish = true;
          break;
        }

        SvgSector svgSector = new SvgSector(j, i, iterator.next());

        // put to rows
        if (this.rows.get(i) == null) {
          this.rows.put(i, new ArrayList<SvgSector>());
        }
        this.rows.get(i).add(svgSector);

        // put to columns
        if (this.columns.get(j) == null) {
          this.columns.put(j, new ArrayList<SvgSector>());
        }
        this.columns.get(j).add(svgSector);
      }

      if (isFinish) break;
    }

    // after determining rows can determine desired offsets between sectors
    // calculate x offset
    for (int i = 1; i < this.columns.size(); i++) {
      // determine x offset
      List<SvgSector> previousColumnSectors = this.columns.get(i - 1);
      SvgSector firstInColumn = previousColumnSectors.get(0);
      double maxColumnWidth = determineMaxWidth(i - 1);
      double xOffset = maxColumnWidth + firstInColumn.getxOffset() + SvgSector.spaceBetweenSectors;

      // set x offset
      List<SvgSector> currentColumnSectors = this.columns.get(i);
      for (SvgSector currentColumnSector : currentColumnSectors) {
        currentColumnSector.setxOffset(xOffset);
      }
    }

    // determine total width
    List<SvgSector> lastColumn = this.columns.get(this.columns.size() - 1);
    double maxWidth = determineMaxWidth(this.columns.size() - 1);
    this.width = maxWidth + lastColumn.get(0).getxOffset() + leftOffset + legendXOffset;

    // calculate y offset
    for (int i = 1; i < this.rows.size(); i++) {
      // determine y offset
      List<SvgSector> previousRowSectors = this.rows.get(i - 1);
      SvgSector firstInRow = previousRowSectors.get(0);
      double maxHeight = determineMaxHeight(i - 1);
      double yOffset = maxHeight + firstInRow.getyOffset() + SvgSector.spaceBetweenSectors;

      // set y offset
      List<SvgSector> currentRowSectors = this.rows.get(i);
      for (SvgSector currentRowSector : currentRowSectors) {
        currentRowSector.setyOffset(yOffset);
      }
    }

    // determine total height
    List<SvgSector> lastRow = this.rows.get(this.rows.size() - 1);
    double maxHeight = determineMaxHeight(this.rows.size() - 1);
    int x = (int) (maxHeight + lastRow.get(0).getyOffset() + topOffset + 500);
    if (x > 750 && x < 1100) {
      height1 = x + 350;
    } else {
      height1 = (double) x;
    }
    this.height = height1;
    if (this.width > SvgModel.maxWidth) {
      this.scale = SvgModel.maxWidth / this.width;
    } else {
      scale = 1.0;
    }
  }


  double getHeight() {
    return height;
  }

  double getWidth() {
    return width;
  }

  double getScale() {
    return scale;
  }

  List<SvgSector> row(int row) {
    return rows.get(row);
  }

  int rows() {
    return rows.size();
  }

  // there are lines of sectors, every line have maximum lines of rows
  // such function determine absolute number of row between all rows of all
  // sector lines
  int absoluteSeatRow(int sectorRow, int row) {
    int totalRows = 0;
    for (int i = 0; i < sectorRow; i++) {
      List<SvgSector> sectorsRow = row(i);
      int maxRows = Collections.max(sectorsRow, comparatorSvgSector).getRows();
      totalRows += maxRows;
    }

    return totalRows + row;
  }

  int totalSeatRows() {
    int sum = 0;
    for (List<SvgSector> svgSectors : rows.values()) {
      SvgSector maxRowsSector = Collections.max(svgSectors, comparatorSvgSector);
      sum += maxRowsSector.getRows();
    }

    return sum;
  }

  private double determineMaxWidth(int column) {
    List<SvgSector> svgSectors = this.columns.get(column);
    double max = -1;
    for (SvgSector svgSector : svgSectors) {
      if (svgSector.getWidth() > max) {
        max = svgSector.getWidth();
      }
    }
    return max;
  }

  private double determineMaxHeight(int row) {
    List<SvgSector> svgSectors = this.rows.get(row);
    double max = -1;
    for (SvgSector svgSector : svgSectors) {
      if (svgSector.getHeight() > max) {
        max = svgSector.getHeight();
      }
    }
    return max;
  }
}
