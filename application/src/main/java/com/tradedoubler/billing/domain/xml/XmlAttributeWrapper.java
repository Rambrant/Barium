package com.tradedoubler.billing.domain.xml;

import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.service.pan.BariumOrderLine;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Rambrant (thore)
 */

public class XmlAttributeWrapper
{
    private Map< String, NamedNodeMap> invoicingRuleAttributes           = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> agreementAttributes               = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> clientAttributes                  = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> purchaseOrderAttributes           = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> invoiceRecipientsAttributes       = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> messageForInvoicingRuleAttributes = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> messageForClientAttributes        = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> messageForMarketAttributes        = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> bankAttributes                    = new HashMap< String, NamedNodeMap>();
    private Map< String, NamedNodeMap> panOrderLineAttributes            = new HashMap< String, NamedNodeMap>();

    public void setInvoicingRuleAttributes( Map< String, NamedNodeMap> invoicingRuleAttributes)
    {
        this.invoicingRuleAttributes = invoicingRuleAttributes;
    }

    public InvoicingRule patchInvoicingRule( InvoicingRule rule, String scenarioName)
    {
        return XmlValueMapper.patchObject( rule, invoicingRuleAttributes.get( scenarioName));
    }

    public void setAgreementAttributes( Map< String, NamedNodeMap> agreementAttributes)
    {
        this.agreementAttributes = agreementAttributes;
    }

    public Agreement patchAgreement( Agreement agreement, String scenarioName)
    {
        return XmlValueMapper.patchObject( agreement, agreementAttributes.get( scenarioName));
    }

    public void setClientAttributes( Map< String, NamedNodeMap> clientAttributes)
    {
        this.clientAttributes = clientAttributes;
    }

    public Client patchClient( Client client, String scenarioName)
    {
        return XmlValueMapper.patchObject( client, clientAttributes.get( scenarioName));
    }

    public void setBankAttributes( Map< String, NamedNodeMap> bankAttributes)
    {
        this.bankAttributes = bankAttributes;
    }

    public Bank patchBank( Bank bank, String scenarioName)
    {
        return XmlValueMapper.patchObject( bank, bankAttributes.get( scenarioName));
    }

    public void setMessageForInvoicingRuleAttributes( Map< String, NamedNodeMap> messageAttributes)
    {
        this.messageForInvoicingRuleAttributes = messageAttributes;
    }

    public InvoicingRuleMessageRule patchMessageForInvoicingRule( InvoicingRuleMessageRule message, String scenarioName)
    {
        return XmlValueMapper.patchObject( message, messageForInvoicingRuleAttributes.get( scenarioName));
    }

    public void setMessageForClientAttributes( Map< String, NamedNodeMap> messageAttributes)
    {
        this.messageForClientAttributes = messageAttributes;
    }

    public ClientMessageRule patchMessageForClient( ClientMessageRule message, String scenarioName)
    {
        return XmlValueMapper.patchObject( message, messageForClientAttributes.get( scenarioName));
    }

    public void setMessageForMarketAttributes( Map< String, NamedNodeMap> messageAttributes)
    {
        this.messageForMarketAttributes = messageAttributes;
    }

    public InvoiceMessageRule patchMessageForMarket( InvoiceMessageRule message, String scenarioName)
    {
        return XmlValueMapper.patchObject( message, messageForMarketAttributes.get( scenarioName));
    }

    public void setInvoiceRecipientAttributes( Map< String, NamedNodeMap> invoiceRecipientsAttributes)
    {
        this.invoiceRecipientsAttributes = invoiceRecipientsAttributes;
    }

    public InvoiceRecipient patchInvoiceRecipient( InvoiceRecipient recipient, String scenarioName)
    {
        return XmlValueMapper.patchObject( recipient, invoiceRecipientsAttributes.get( scenarioName));
    }

    public void setPurchaseOrderAttributes( Map< String, NamedNodeMap> purchaseOrderAttributes)
    {
        this.purchaseOrderAttributes = purchaseOrderAttributes;
    }

    public PurchaseOrder patchPurchaseOrder( PurchaseOrder order, String scenarioName)
    {
        return XmlValueMapper.patchObject( order, purchaseOrderAttributes.get( scenarioName));
    }

    public void setOrderLineAttributes( Map<String, NamedNodeMap> panOrderLineAttributes)
    {
        this.panOrderLineAttributes = panOrderLineAttributes;
    }

    public BariumOrderLine patchOrderLine( BariumOrderLine orderLine, String scenarioName)
    {
        return XmlValueMapper.patchObject( orderLine, panOrderLineAttributes.get( scenarioName));
    }

    public void patchInvoicingRuleAgreements( InvoicingRule rule, String scenarioName)
    {
        if( agreementAttributes.size() == 0 || agreementAttributes.get( scenarioName) == null)
        {
            return;
        }

        Node node = agreementAttributes.get( scenarioName).getNamedItem( "sourceSystemAgreementId");

        if( node != null)
        {
            String agreementId = node.getNodeValue();

            rule.setSourceSystemAgreementIds( Arrays.asList( agreementId));
        }
    }
}
