package server.protocol2.cassa;

import java.io.Serializable;

/**
 * Created by Inventor on 29.07.2019
 * VatId:
 * 0 - НДС не облагается
 * 1 - 0%
 * 2 - 10%
 * 3 - 20%
 * 4 - 10/110
 * 5 - 20/120
 */
public class CassaFiscal implements Serializable {
  private static final long serialVersionUID = -4693494531090893216L;
  private int ticketVatId;//Идентификатор НДС билета
  private int serviceChargeVatId;//Идентификатор НДС сервисного сбора

  public CassaFiscal(int ticketVatId, int serviceChargeVatId) {
    this.ticketVatId = ticketVatId;
    this.serviceChargeVatId = serviceChargeVatId;
  }

  public int getTicketVatId() {
    return ticketVatId;
  }

  public int getServiceChargeVatId() {
    return serviceChargeVatId;
  }

  @Override
  public String toString() {
    return "CassaFiscal{ticketVatId=" + ticketVatId + ", serviceChargeVatId=" + serviceChargeVatId + '}';
  }
}
