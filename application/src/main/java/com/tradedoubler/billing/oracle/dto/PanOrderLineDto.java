package com.tradedoubler.billing.oracle.dto;

import com.tradedoubler.billing.annotaition.Column;
import com.tradedoubler.billing.annotaition.Id;

import java.sql.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanOrderLineDto
{
    //
    // The database row id
    //
    @Id
    private Long id;

    //
    // The program_id
    //
    @Column( name = "program_id")
    private String programId;

    //
    // The TD organization ID for the program
    //
    @Column( name = "invoice_td_org_id")
    private Long tdOrgId;

    //
    // The currency for the program
    //
    @Column( name = "currency_id")
    private String currencyId;

    //
    // The revenue type for the program
    //
    @Column( name = "rev_type")
    private String revenueType;

    //
    // The source system agreement id (the program id prepended with 1-)
    //
    @Column( name = "source_system_agreement_id")
    private String agreementId;

    @Column( name = "segment_tariff_id")
    private Long segmentTariffId;

    @Column( name = "sales_part")
    private Long salesPart;

    @Column( name = "quantity")
    private Long quantity;

    @Column( name = "total")
    private Double total;

    @Column( name = "cost")
    private Double cost;

    @Column( name = "debit")
    private String debit;

    @Column( name = "splitter")
    private Long splitter;

    @Column( name = "order_id")
    private Long orderId;

    @Column( name = "date_of_prepayment")
    private Date dateOfPrepayment;

    @Column( name = "credited_invoice_no")
    private Long creditedInvoiceNumber;
    //
    // The batch id of this "collection" of order lines
    //
    @Column( name = "order_line_batch_id")
    private String batchId;

    public Long getId()
    {
        return id;
    }

    public void setId( Long rowId)
    {
        this.id = rowId;
    }

    public String getProgramId()
    {
        return programId;
    }

    public void setProgramId( String programId)
    {
        this.programId = programId;
    }

    public String getAgreementId()
    {
        return agreementId;
    }

    public void setAgreementId( String agreementId)
    {
        this.agreementId = agreementId;
    }

    public String getBatchId()
    {
        return batchId;
    }

    public void setBatchId( String batchId)
    {
        this.batchId = batchId;
    }

    public Long getTDOrgId()
    {
        return tdOrgId;
    }

    public void setTdOrgId( Long tdOrgId)
    {
        this.tdOrgId = tdOrgId;
    }

    public String getCurrencyId()
    {
        return currencyId;
    }

    public void setCurrencyId( String currencyId)
    {
        this.currencyId = currencyId;
    }

    public String getRevenueType()
    {
        return revenueType;
    }

    public void setRevenueType( String revenueType)
    {
        this.revenueType = revenueType;
    }

    public Long getSegmentTariffId()
    {
        return segmentTariffId;
    }

    public void setSegmentTariffId( Long segmentTariffId)
    {
        this.segmentTariffId = segmentTariffId;
    }

    public Long getSalesPart()
    {
        return salesPart;
    }

    public void setSalesPart( Long salesPart)
    {
        this.salesPart = salesPart;
    }

    public Long getQuantity()
    {
        return quantity;
    }

    public void setQuantity( Long quantity)
    {
        this.quantity = quantity;
    }

    public Double getTotal()
    {
        return total;
    }

    public void setTotal( Double total)
    {
        this.total = total;
    }

    public Double getCost()
    {
        return cost;
    }

    public void setCost( Double cost)
    {
        this.cost = cost;
    }

    public String getDebit()
    {
        return debit;
    }

    public void setDebit( String debit)
    {
        this.debit = debit;
    }

    public Long getSplitter()
    {
        return splitter;
    }

    public void setSplitter( Long splitter)
    {
        this.splitter = splitter;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId( Long orderId)
    {
        this.orderId = orderId;
    }

    @Override
    public String toString()
    {
        return "OrderLineDto { " +
            "id = "                           + id                    +
            ", program_id = "                 + programId             +
            ", segmentTariffId = "            + segmentTariffId       +
            ", salesPart = "                  + salesPart             +
            ", quantity = "                   + quantity              +
            ", total = "                      + total                 +
            ", cost = "                       + cost                  +
            ", debit = "                      + debit                 +
            ", splitter = "                   + splitter              +
            ", orderId = "                    + orderId               +
            ", invoice_td_org_id = "          + tdOrgId               +
            ", currency_id = "                + currencyId            +
            ", revenue_type = "               + revenueType           +
            ", source_system_agreement_id = " + agreementId           +
            ", dateOfPrepayment = "           + dateOfPrepayment      +
            ", creditedInvoiceNumber = "      + creditedInvoiceNumber +
            ", batch_id = "                   + batchId               +
            '}';
    }
}
