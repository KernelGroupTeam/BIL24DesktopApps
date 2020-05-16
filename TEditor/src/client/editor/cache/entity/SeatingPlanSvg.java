package client.editor.cache.entity;

import com.sleepycat.persist.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 07.06.16
 */
@Entity
public class SeatingPlanSvg {
  @PrimaryKey
  @NotNull
  private Long id = 0L;
  @NotNull
  private byte[] svgZip = Image.EMPTY_BYTES;

  @SuppressWarnings("unused")
  SeatingPlanSvg() {
  }

  public SeatingPlanSvg(long id, @NotNull byte[] svgZip) {
    this.id = id;
    this.svgZip = svgZip;
  }

  public long getId() {
    return id;
  }

  @NotNull
  public byte[] getSvgZip() {
    return svgZip;
  }
}
