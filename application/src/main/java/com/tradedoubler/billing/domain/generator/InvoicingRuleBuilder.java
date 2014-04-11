package com.tradedoubler.billing.domain.generator;

import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.domain.xml.XmlAttributeWrapper;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.util.AxDbConstantsUtil;
import com.tradedoubler.testutil.FileUtil;
import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Thomas Rambrant (thore)
 */

public class InvoicingRuleBuilder
{
    private static String invoicingRuleJson;
    private static String agreementJson;
    private static String clientJson;
    private static String marketJson;
    private static String recipientJson;

    private XmlAttributeWrapper xmlWrapper;
    private IdGenerator         idGenerator;
    private String              scenarioName;

    public InvoicingRuleBuilder(
        XmlAttributeWrapper xmlAttributeWrapper,
        IdGenerator         idGenerator,
        String              scenarioName)
    {
        this.xmlWrapper   = xmlAttributeWrapper;
        this.idGenerator  = idGenerator;
        this.scenarioName = scenarioName;

        invoicingRuleJson = FileUtil.readFileFromClasspath( "json-examples/invoicing-rule-updated-example.json");
        agreementJson     = FileUtil.readFileFromClasspath( "json-examples/agreement-updated-example.json");
        clientJson        = FileUtil.readFileFromClasspath( "json-examples/client-updated-example.json");
        marketJson        = FileUtil.readFileFromClasspath( "json-examples/market-message-updated-example.json");
        recipientJson     = FileUtil.readFileFromClasspath( "json-examples/invoice-recipient-example.json");
    }

    public InvoicingRuleCreated buildInvoicingRuleCreated( ProducerEventType event, long timestamp)
    {
        //
        // Build all parts of the invoice rule
        //
        String invoiceRuleId = idGenerator.getInvoiceRuleId( true);

        Client        client        = buildClient( ProducerEventType.NONE, true);
        Agreement     agreement     = buildAgreement( invoiceRuleId, client.getClientId(), ProducerEventType.NONE, true );
        InvoicingRule invoicingRule = buildInvoicingRule( invoiceRuleId, agreement.getSourceSystemAgreementId(), event, true );

        return new
            InvoicingRuleCreated(
                buildMetadata( timestamp, getInvoiceRuleCreatedMessageType()),
                invoicingRule,
                agreement,
                client);
    }

    public InvoicingRuleUpdated buildInvoicingRuleUpdated( ProducerEventType event, long timestamp)
    {
        //
        // Create the invoice rule
        //
        String invoiceRuleId = idGenerator.getInvoiceRuleId( false);
        String agreementId   = idGenerator.getAgreementId( invoiceRuleId, false);

        InvoicingRule invoicingRule = buildInvoicingRule( invoiceRuleId, agreementId, event, false);

        return new
            InvoicingRuleUpdated(
                buildMetadata( timestamp, getInvoicingRuleMessageUpdatedType()),
                invoicingRule);
    }

    public AgreementUpdated buildAgreementUpdated( long timestamp, ProducerEventType event, boolean create)
    {
        Agreement agreement = buildAgreement(
            idGenerator.getInvoiceRuleId( false),
            idGenerator.getClientId( false),
            event,
            create);

        return new
            AgreementUpdated(
                buildMetadata( timestamp, getAgreementMessageType()),
                agreement);
    }

    public ClientUpdated buildClientUpdated( long timestamp, ProducerEventType event, boolean create)
    {
        return new
            ClientUpdated(
                buildMetadata( timestamp, getClientMessageType()),
                buildClient( event, create));
    }

    public MarketMessageUpdated buildMarketMessageUpdated( long timestamp, ProducerEventType event, boolean create)
    {
        return new
            MarketMessageUpdated(
                buildMetadata( timestamp, getMarketMessageMessageType()),
                buildMarketMessage( event, create));
    }

    public SplittingRuleDeleted buildSplittingRuleDeleted( long timestamp)
    {
//        SplittingRule splittingRule = buildSplittingRule( idGenerator.getInvoiceRuleId( false));
//
//        return new
//            SplittingRuleDeleted(
//                buildMetadata( timestamp, getClientMessageType()),
//                splittingRule.getSplittingRuleId(),
//                splittingRule.getInvoiceRecipientId(),
//                splittingRule.get);

        return null; //TODO
    }

    public MetaData buildMetadata( long timestamp, String messageType)
    {
        java.sql.Timestamp timeStamp  = new java.sql.Timestamp( timestamp);
        SimpleDateFormat   dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        return new
            MetaData(
                messageType,                                    // messageType
                new Guid( UUID.randomUUID().toString()),        // messageId
                new Timestamp( dateFormat.format( timeStamp)),  // creationTime
                "1.0",                                          // version
                "CRM");                                         // sourceSystem
    }

    private InvoicingRule buildInvoicingRule( String invoiceRuleId, String agreementId, ProducerEventType event, boolean create )
    {
        InvoicingRule rule = GsonFactory.getGson().fromJson( invoicingRuleJson, InvoicingRuleUpdated.class).getInvoicingRule();

        //
        // Update the invoicing rule
        //
        if( event == ProducerEventType.SYNTAX)
        {
            rule.setInvoicingRuleId( "Broken Guid");
        }
        else
        {
            rule.setInvoicingRuleId( invoiceRuleId);

            //
            // Update the MessageRules
            //
            Assert.assertTrue( "cant handle more than one message rule", rule.getInvoiceMessageRules().size() == 1);

            for( InvoicingRuleMessageRule message : rule.getInvoiceMessageRules())
            {
                message.setInvoiceMessageRuleId( idGenerator.getInvoiceMessageRuleForInvoicingRuleId( invoiceRuleId, create));
                message.setInvoicingRuleId( invoiceRuleId);

                message.setMessageText( "Text-" + idGenerator.getCounter());

                xmlWrapper.patchMessageForInvoicingRule( message, scenarioName);
            }

            //
            // Update the Invoice Recipients
            //
            List< InvoiceRecipient> recipients = rule.getInvoiceRecipients();

            for( int i = recipients.size(); i > 1; --i)
            {
                recipients.remove( i - 1);
            }

            Assert.assertTrue( "cant handle more than one invoice recipient", rule.getInvoiceRecipients().size() == 1);

            for( InvoiceRecipient recipient : rule.getInvoiceRecipients())
            {
                recipient.setInvoiceRecipientId( idGenerator.getInvoiceRecipientIds( invoiceRuleId, create));
                recipient.setInvoicingRuleId( invoiceRuleId);

                recipient.setAttentionRow2( "Att-" + idGenerator.getCounter());
                recipient.getRegisteredAddress().setLine1( "Street-" + idGenerator.getCounter());

                xmlWrapper.patchInvoiceRecipient( recipient, scenarioName);
            }

            //
            // Update the Purchase orders
            //
            List< PurchaseOrder> orders = rule.getPurchaseOrders();

            for( int i = orders.size(); i > 1; --i)
            {
                orders.remove( i - 1);
            }

            Assert.assertTrue( "cant handle more than one purchase order", rule.getPurchaseOrders().size() == 1);

            for( PurchaseOrder order : rule.getPurchaseOrders())
            {
                order.setPurchaseOrderId( idGenerator.getPurchaseOrderIds( invoiceRuleId, create));
                order.setInvoicingRuleId( invoiceRuleId);

                order.setPoNumber( "Po-" + idGenerator.getCounter());

                xmlWrapper.patchPurchaseOrder( order, scenarioName);
            }
        }

        if( event == ProducerEventType.ILLEGAL)
        {
            rule.setMarketId( 99999999);
        }

        //
        // Update the list of agreements. This has to be done now since there is a circular dependency
        //
        rule.setSourceSystemAgreementIds( Arrays.asList( agreementId));

        //
        // Set the new values from the XML file
        //
        rule.setDescription( "Description-" + idGenerator.getCounter());

        xmlWrapper.patchInvoicingRule( rule, scenarioName);
        xmlWrapper.patchInvoicingRuleAgreements( rule, scenarioName);

        //
        // Update the pointers back to the invoicing rule
        //
        if( invoiceRuleId != null)
        {
            for( InvoiceRecipient recipient: rule.getInvoiceRecipients())
            {
                recipient.setInvoicingRuleId( invoiceRuleId);
            }

            for( PurchaseOrder order: rule.getPurchaseOrders())
            {
                order.setInvoicingRuleId( invoiceRuleId);
            }

            for( InvoicingRuleMessageRule message: rule.getInvoiceMessageRules())
            {
                message.setInvoicingRuleId( invoiceRuleId);
            }
        }

        return rule;
    }

    private Agreement buildAgreement( String invoiceRuleId, String clientId, ProducerEventType event, boolean create)
    {
        Agreement agreement = GsonFactory.getGson().fromJson( agreementJson, AgreementUpdated.class).getAgreement();

        //
        // Update the agreement
        //
        agreement.setInvoicingRuleId( invoiceRuleId);
        agreement.setClientId( clientId);

        if( event == ProducerEventType.SYNTAX)
        {
            agreement.setSourceSystemAgreementId( "Broken Guid");
        }
        else
        {
            agreement.setSourceSystemAgreementId( idGenerator.getAgreementId( invoiceRuleId, create));
        }

        if( event == ProducerEventType.ILLEGAL)
        {
            agreement.setInvoicingRuleId( AxDbConstantsUtil.generateInvoiceRuleId());
        }

        //
        // Set the new values from the XML file
        //
        agreement.setDescription( "Description-" + idGenerator.getCounter());

        xmlWrapper.patchAgreement( agreement, scenarioName);

        return agreement;
    }

    private Client buildClient( ProducerEventType event, boolean create)
    {
        Client client = GsonFactory.getGson().fromJson( clientJson, ClientUpdated.class).getClient();

        //
        // Update the client part
        //
        if( event == ProducerEventType.SYNTAX)
        {
            client.setClientId( "Broken Guid");
        }
        else
        {
            client.setClientId( idGenerator.getClientId( create));

            //
            // Set the name
            //
            client.setRegisteredCompanyName( idGenerator.getClientCompanyName( client.getClientId(), create));

            //
            // Update the Banks
            //
            Assert.assertTrue( "cant handle more than one bank account", client.getBankAccounts().size() == 1);

            for( Bank bank : client.getBankAccounts())
            {
                bank.setClientId( client.getClientId());

                bank.setAccountOwner( "Owner-" + idGenerator.getCounter());

                xmlWrapper.patchBank( bank, scenarioName);
            }

            //
            // Update the InvoiceMessageRules
            //
            Assert.assertTrue( "cant handle more than one message", client.getInvoiceMessageRules().size() == 1);

            for( ClientMessageRule messageRule: client.getInvoiceMessageRules())
            {
                messageRule.setInvoiceMessageRuleId( idGenerator.getInvoiceMessageRuleForClientsId( client.getClientId(), create));
                messageRule.setClientId( client.getClientId());

                messageRule.setMessageText( "Text-" + idGenerator.getCounter());

                xmlWrapper.patchMessageForClient( messageRule, scenarioName);
            }
        }

        //
        // Set the new values from the XML file
        //
        client.setRegisteredCompanyName( "ACME-" + idGenerator.getCounter());
        client.getRegisteredAddress().setLine1( "Street-" + idGenerator.getCounter());

        xmlWrapper.patchClient( client, scenarioName);

        //
        // take care of the market
        //
        Assert.assertTrue( "cant handle more than one market", client.getMarketIds().size() == 1);

        if( event == ProducerEventType.ILLEGAL)
        {
            for( Market market: client.getMarketIds())
            {
                market.setOrganizationId( 999999999);
            }
        }

        return client;
    }

    public InvoiceIssuerMessageRule buildMarketMessage( ProducerEventType event, boolean create)
    {
        InvoiceIssuerMessageRule message =
            GsonFactory.getGson().fromJson( marketJson, MarketMessageUpdated.class).getInvoiceMessageRule();

        //
        // There must be a market id
        //
        Assert.assertNotNull( message.getMarketId());

        //
        // Update the message part
        //
        if( event == ProducerEventType.SYNTAX)
        {
            message.setInvoiceMessageRuleId( "Broken Guid");
        }
        else
        {
            message.setInvoiceMessageRuleId(
                idGenerator.getInvoiceMessageRuleForMarketsId(
                    String.valueOf( message.getMarketId().getOrganizationId()),
                    create));
        }

        if( event == ProducerEventType.ILLEGAL)
        {
            message.setMarketId( 999999);
        }

        //
        // Patch with new values from the XML file
        //
        message.setMessageText( "Text-" + idGenerator.getCounter());

        xmlWrapper.patchMessageForMarket( message, scenarioName);

        return message;
    }

    private SplittingRule buildSplittingRule( String invoiceRecipientId)
    {
        InvoiceRecipient recipient = GsonFactory.getGson().fromJson( recipientJson, InvoiceRecipient.class);


        SplittingRule splittingRule = new SplittingRule(
            "4b751705-8c35-163d-6e62-562a647f5901",  // splittingRuleId  //TODO Dynamic
            invoiceRecipientId,                      // invoiceRecipientId
            "Splitter");                             // splitter

//            list.add(  splittingRule);
//
//        return list;

        return null;
    }


    public String getInvoiceRuleCreatedMessageType()
    {
        return "CreateInvoicingRule";
    }

    public String getInvoiceRuleUpdatedMessageType()
    {
        return "UpdateInvoicingRule";
    }

    public String getInvoicingRuleMessageUpdatedType()
    {
        return "UpdateInvoicingRule";
    }

    public String getAgreementMessageType()
    {
        return "UpdateAgreement";
    }

    public String getClientMessageType()
    {
        return "UpdateClient";
    }

    public String getSplittingRuleMessageType()
    {
        return "DeleteSplittingRule";
    }

    public String getInvoiceRecipientMessageType()
    {
        return "DeleteInvoiceRecipient";
    }

    public String getMarketMessageMessageType()
    {
        return "UpdateMarketMessage";
    }
}
