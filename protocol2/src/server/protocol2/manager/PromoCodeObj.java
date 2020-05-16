package server.protocol2.manager;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 28.03.19
 */
public class PromoCodeObj implements Serializable {
  private static final long serialVersionUID = -2728255795050429083L;
  @NotNull
  private String code;
  @NotNull
  private String codeOrView;
  private int reusability;
  private boolean reusabilityChanged;
  private int used;

  public PromoCodeObj(@NotNull String code, @NotNull String codeOrView, int reusability, int used) {
    this.code = code;
    this.codeOrView = codeOrView;
    this.reusability = reusability;
    this.reusabilityChanged = false;
    this.used = used;
  }

  @NotNull
  public String getCode() {
    return code;
  }

  @NotNull
  public String getCodeOrView() {
    return codeOrView;
  }

  public int getReusability() {
    return reusability;
  }

  public void setReusability(int reusability) {
    this.reusability = reusability;
    reusabilityChanged = true;
  }

  public boolean isDisposable() {
    return reusability == 1;
  }

  public boolean isReusabilityChanged() {
    return reusabilityChanged;
  }

  public int getUsed() {
    return used;
  }

  public boolean isSpent() {
    return used >= reusability;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PromoCodeObj)) return false;
    PromoCodeObj that = (PromoCodeObj) o;
    return code.equals(that.code);
  }

  @Override
  public int hashCode() {
    return code.hashCode();
  }
}
