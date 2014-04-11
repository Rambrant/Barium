package com.tradedoubler.billing.domain.xml;

import com.tradedoubler.billing.domain.*;
import com.tradedoubler.billing.domain.parse.*;
import com.tradedoubler.billing.service.pan.BariumOrderLine;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.testutil.FileUtil;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * @author Thomas Rambrant (thore)
 */

public class XmlAttributeWrapperTest
{
    @Test
    public void testPatchInvoicingRule() throws Exception
    {
        String xml =
            "<invoicingRules>" +
            "    <invoicingRule scenarioName = \"Simple\"" +
            "        marketId = \"1234\""                  +
            "        displayTDcommission = \"Y\""          +
            "        invoicingRuleName = \"Pelles Rule\""  +
            "    />"                                       +
            "</invoicingRules>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        InvoicingRuleParser parser = new InvoicingRuleParser();

        wrapper.setInvoicingRuleAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty invoicing rule and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/invoicing-rule-updated-example.json");

        InvoicingRule rule = GsonFactory.getGson().fromJson( json, InvoicingRuleUpdated.class).getInvoicingRule();

        InvoicingRule result = wrapper.patchInvoicingRule( rule, "Simple");

        //
        // Verify the result
        //
        Assert.assertEquals( 1234, result.getMarketId().getOrganizationId());
        Assert.assertEquals( true, result.isDisplayTradeDoublerCommission());
        Assert.assertEquals( "Pelles Rule", result.getInvoicingRuleName());
    }

    @Test
    public void testPatchAgreement() throws Exception
    {
        String xml =
            "<agreements>" +
            "    <agreement scenarioName = \"Simple\""             +
            "        clientId = \"Nisse\""                         +
            "        marketId = \"1234\""                          +
            "        validFrom =\"2012-06-25T15:30:01.999+02:00\"" +
            "    />"                                               +
            "</agreements>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        AgreementParser parser = new AgreementParser();

        wrapper.setAgreementAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty Agreement and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/agreement-updated-example.json");

        Agreement agreement = GsonFactory.getGson().fromJson( json, AgreementUpdated.class).getAgreement();

        Agreement result = wrapper.patchAgreement( agreement, "Simple");

        //
        // Verify the result
        //
        Assert.assertEquals( "Nisse", result.getClientId());
        Assert.assertEquals( 1234, result.getMarketId().getOrganizationId());
        Assert.assertEquals( "2012-06-25T15:30:01.999+02:00", result.getValidFrom().toString());
    }

    @Test
    public void testPatchClient() throws Exception
    {
        String xml =
            "<clients>" +
            "    <client scenarioName = \"Simple\""     +
            "        vatNumber        = \"1234567890\"" +
            "        businessFormCode = \"HejSvejs\""   +
            "    />"                                    +
            "</clients>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        ClientParser parser = new ClientParser();

        wrapper.setClientAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty Client and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/client-updated-example.json");

        Client client = GsonFactory.getGson().fromJson( json, ClientUpdated.class).getClient();

        Client result = wrapper.patchClient( client, "Simple");

        //
        // Verify the result
        //
        Assert.assertEquals( "1234567890", result.getVatNumber());
        Assert.assertEquals( "HejSvejs",   result.getBusinessFormCode());
    }

    @Test
    public void testPatchInvoiceRecipient() throws Exception
    {
        String xml =
            "<invoiceRecipients>" +
            "    <invoiceRecipient scenarioName = \"Simple\"" +
            "        emailAddress = \"tom@dummy.com\""        +
            "        attentionRow1 = \"HejSvejs\""            +
            "    />"                                          +
            "</invoiceRecipients>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        InvoiceRecipientParser parser = new InvoiceRecipientParser();

        wrapper.setInvoiceRecipientAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty Client and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/invoicing-rule-updated-example.json");

        InvoicingRule rule = GsonFactory.getGson().fromJson( json, InvoicingRuleUpdated.class).getInvoicingRule();

        boolean patched = false;

        for( InvoiceRecipient recipient: rule.getInvoiceRecipients())
        {
            patched = true;

            InvoiceRecipient result = wrapper.patchInvoiceRecipient( recipient, "Simple");

            //
            // Verify the result
            //
            Assert.assertEquals( "tom@dummy.com", result.getEmailAddress());
            Assert.assertEquals( "HejSvejs",   result.getAttentionRow1());
        }

        Assert.assertTrue( "No Invoice Recipient to patch", patched);
    }

    @Test
    public void testPatchPurchaseOrder() throws Exception
    {
        String xml =
            "<purchaseOrders>" +
            "    <purchaseOrder scenarioName = \"Simple\"" +
            "        poNumber = \"2345\""                  +
            "    />"                                       +
            "</purchaseOrders>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        PurchaseOrderParser parser = new PurchaseOrderParser();

        wrapper.setPurchaseOrderAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty Client and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/invoicing-rule-updated-example.json");

        InvoicingRule rule = GsonFactory.getGson().fromJson( json, InvoicingRuleUpdated.class).getInvoicingRule();

        boolean patched = false;

        for( PurchaseOrder order: rule.getPurchaseOrders())
        {
            patched = true;

            PurchaseOrder result = wrapper.patchPurchaseOrder( order, "Simple");

            //
            // Verify the result
            //
            Assert.assertEquals( "2345", result.getPoNumber());
        }

        Assert.assertTrue( "No Purchase Order to patch", patched);
    }

    @Test
    public void testPatchBank() throws Exception
    {
        String xml =
            "<banks>" +
            "    <bank scenarioName = \"Simple\""       +
            "        bankAccount    = \"1234567890\""   +
            "        accountOwner   = \"SvenneStalar\"" +
            "    />"                                    +
            "</banks>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        BankParser parser = new BankParser();

        wrapper.setBankAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty Agreement and patch some values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/client-updated-example.json");

        ClientUpdated client = GsonFactory.getGson().fromJson( json, ClientUpdated.class);

        boolean patched = false;

        for( Bank bank : client.getClient().getBankAccounts())
        {
            patched = true;

            Bank result = wrapper.patchBank( bank, "Simple");

            //
            // Verify the result
            //
            Assert.assertEquals( "1234567890", result.getBankAccount());
            Assert.assertEquals( "SvenneStalar", result.getAccountOwner());
        }

        Assert.assertTrue( "No Banks to patch", patched);
    }

    @Test
    public void testPatchMessageForInvoicingRule() throws Exception
    {
        String xml =
            "<messageForInvoicingRules>" +
            "    <messageForInvoicingRule scenarioName = \"Simple\"" +
            "        messageText = \"God Jul\""                      +
            "    />"                                                 +
            "</messageForInvoicingRules>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        InvoiceMessageForInvoicingRuleParser parser = new InvoiceMessageForInvoicingRuleParser();

        wrapper.setMessageForInvoicingRuleAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create a client and patch the messages according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/invoicing-rule-updated-example.json");

        InvoicingRule rule = GsonFactory.getGson().fromJson( json, InvoicingRuleUpdated.class).getInvoicingRule();

        boolean patched = false;

        for( InvoicingRuleMessageRule message : rule.getInvoiceMessageRules())
        {
            patched = true;

            InvoicingRuleMessageRule result = wrapper.patchMessageForInvoicingRule( message, "Simple");

            //
            // Verify the result
            //
            Assert.assertEquals( "God Jul", result.getMessageText());
        }

        Assert.assertTrue( "No InvoiceMessageRules to patch", patched);
    }

    @Test
    public void testPatchMessageForClient() throws Exception
    {
        String xml =
            "<messageForClients>" +
            "    <messageForClient scenarioName = \"Simple\"" +
            "        messageText     = \"God Jul\""           +
            "    />"                                          +
            "</messageForClients>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        InvoiceMessageForClientParser parser = new InvoiceMessageForClientParser();

        wrapper.setMessageForClientAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create a client and patch the messages according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/client-updated-example.json");

        ClientUpdated client = GsonFactory.getGson().fromJson( json, ClientUpdated.class);

        boolean patched = false;

        for( ClientMessageRule message : client.getClient().getInvoiceMessageRules())
        {
            patched = true;

            ClientMessageRule result = wrapper.patchMessageForClient( message, "Simple");

            //
            // Verify the result
            //
            Assert.assertEquals( "God Jul", result.getMessageText());
        }

        Assert.assertTrue( "No InvoiceMessageRules to patch", patched);
    }

    @Test
    public void testPatchMessageForMarket() throws Exception
    {
        String xml =
            "<messageForMarkets>" +
            "    <messageForMarket scenarioName = \"Simple\"" +
            "        messageText     = \"God Jul\""           +
            "    />"                                          +
            "</messageForMarkets>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        InvoiceMessageForMarketParser parser = new InvoiceMessageForMarketParser();

        wrapper.setMessageForMarketAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty message and patch the values according to the xml
        //
        String json = FileUtil.readFileFromClasspath( "json-examples/market-message-updated-example.json");

        InvoiceIssuerMessageRule message =
            GsonFactory.getGson().fromJson( json, MarketMessageUpdated.class).getInvoiceMessageRule();

        InvoiceMessageRule result = wrapper.patchMessageForMarket( message, "Simple");

        Assert.assertEquals( "God Jul", result.getMessageText());
    }

    @Test
    public void testPatchOrderLine() throws Exception
    {
        String xml =
            "<orderLines>" +
            "    <orderLine scenarioName = \"Simple\""  +
            "        total         = \"12.56\""         +
            "        debitOrCredit = \"CREDIT\""        +
            "    />"                                    +
            "</orderLines>";

        //
        // Parse the XML document and store the attribute in the XmlAttributeWrapper
        //
        XmlAttributeWrapper wrapper = new XmlAttributeWrapper();

        OrderLineParser parser = new OrderLineParser();

        wrapper.setOrderLineAttributes( parser.parse( getXmlDocument( xml)));

        //
        // Create an empty order line and patch some values according to the xml
        //
        BariumOrderLine orderLine = new BariumOrderLine(
            "Batch_id_1",                           // batch id
            "dummy",                                // systemAgreementId
            "dummy",                                // orderLineId
            true,                                   // invoiceable
            "110",                                  // salesPart
            "Made by Barium",                       // description
            2,                                      // quantity
            BigDecimal.valueOf( 1390.43),           // total
            BigDecimal.valueOf( 695.21),            // cost
            "1012",                                 // period
            new Timestamp( "2012-10-26T21:32:52"),  // dateOfPrepayment
            DebitOrCredit.DEBIT,                    // debitOrCredit
            "11111",                                // credited invoice Id
            "1365489");                             // splitter

        BariumOrderLine result = wrapper.patchOrderLine( orderLine, "Simple");

        //
        // Verify the result
        //
        Assert.assertEquals( new BigDecimal( "12.56"), result.getTotal().getValue());
        Assert.assertEquals( DebitOrCredit.CREDIT, result.getDebitOrCredit());
    }

    private Document getXmlDocument( String xml) throws Exception
    {
        //
        // convert String into InputStream
        //
        InputStream is = new ByteArrayInputStream( xml.getBytes());

        //
        // Create the XML document
        //
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        return dbf.newDocumentBuilder().parse( is);
    }
}
