package excel.interfaces;

import excel.enums.EDataFormat;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Inventor on 23.11.2017
 */
public interface IDataFormat {
  @NotNull
  Short getDataFormat(@NotNull EDataFormat edataFormat);
}
