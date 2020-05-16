package server.protocol2.manager;

import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim Ponomarev
 * Date: 21.04.16
 */
public class OperatorObj extends AuthorityObj {
  private static final long serialVersionUID = -4084567272983788958L;

  public OperatorObj(long id, @NotNull Entity entity) {
    super(id, AuthTypeObj.OPERATOR, entity);
  }
}
