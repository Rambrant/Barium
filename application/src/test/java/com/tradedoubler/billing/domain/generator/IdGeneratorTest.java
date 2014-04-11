package com.tradedoubler.billing.domain.generator;

import com.tradedoubler.billing.util.AxDbConstantsUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class IdGeneratorTest
{
    @Test
    public void testGenerateInvoiceRuleId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test Default value
        //
        Assert.assertEquals(
            AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID,
            generator.getInvoiceRuleId( false));

        //
        // Test new id
        //
        String createdId = generator.getInvoiceRuleId( true);

        Assert.assertFalse( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID.equals( createdId));
        Assert.assertEquals( createdId, generator.getInvoiceRuleId( false));
    }

    @Test
    public void testGenerateAgreementId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test Default value on default invoice rule
        //
        Assert.assertEquals(
            AxDbConstantsUtil.EXISTING_AGREEMENT_ID,
            generator.getAgreementId( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, false));

        //
        // Test new id on default invoice rule
        //
        String createdId = generator.getAgreementId( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, true);

        Assert.assertFalse( AxDbConstantsUtil.EXISTING_AGREEMENT_ID.equals( createdId));
        Assert.assertEquals( createdId, generator.getAgreementId( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, false));

        //
        // Test default value on new invoice rule
        //
        String newInvoice = generator.getInvoiceRuleId( true);

        Assert.assertNull( generator.getAgreementId( newInvoice, false));

        //
        // Test new id on new
        //
        String newId = generator.getAgreementId( newInvoice, true);

        Assert.assertFalse( createdId.equals( newId));
        Assert.assertEquals( newId, generator.getAgreementId( newInvoice, false));
    }

    @Test
    public void testGenerateClientId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test Default value
        //
        Assert.assertEquals(
            AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM,
            generator.getClientId( false));

        //
        // Test new id
        //
        String createdId = generator.getClientId( true);

        Assert.assertFalse( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM.equals( createdId));
        Assert.assertEquals( createdId, generator.getClientId( false));
    }

    @Test
    public void testGenerateOrderIdId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test new id for default client
        //
        String firstId = generator.getOrderLineId( 1);

        Assert.assertTrue( firstId.length() == 10);

        //
        // Have we incremented correctly to the next id
        //
        long firstValue = Long.valueOf( firstId.substring( firstId.length() - 8, firstId.length()));

        String nextId    = generator.getOrderLineId( 10);
        long   nextValue = Long.valueOf( nextId.substring( nextId.length() - 8, nextId.length()));

        Assert.assertTrue( nextValue - firstValue == 1);

        //
        // Test the larger increment
        //
        String lastId    = generator.getOrderLineId( 1);
        long   lastValue = Long.valueOf( lastId.substring( lastId.length() - 8, lastId.length()));

        Assert.assertTrue( lastValue - nextValue == 10);
     }

    @Test
    public void testGenerateMessageForClientId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test Default value for default client
        //
        Assert.assertEquals(
            AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_CLIENT_ID,
            generator.getInvoiceMessageRuleForClientsId( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, false));

        //
        // Test new id for default client
        //
        String createdId = generator.getInvoiceMessageRuleForClientsId( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, true);

        Assert.assertFalse( AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_CLIENT_ID.equals( createdId));
        Assert.assertEquals( createdId, generator.getInvoiceMessageRuleForClientsId( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, false));

        //
        // Test default value on new client
        //
        String newClient = generator.getClientId( true);

        Assert.assertNull( generator.getInvoiceMessageRuleForClientsId( newClient, false));

        //
        // Test new id on new client
        //
        String newId = generator.getInvoiceMessageRuleForClientsId( newClient, true);

        Assert.assertFalse( createdId.equals( newId));
        Assert.assertEquals( newId, generator.getInvoiceMessageRuleForClientsId( newClient, false));
    }

    @Test
    public void testGenerateMessageForMarketId() throws Exception
    {
        IdGenerator generator = new IdGenerator();

        //
        // Test Default value for default client
        //
        Assert.assertEquals(
            AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_MARKET_ID,
            generator.getInvoiceMessageRuleForMarketsId( AxDbConstantsUtil.EXISTING_MARKET_ID, false));

        //
        // Test new id for default client
        //
        String createdId = generator.getInvoiceMessageRuleForMarketsId( AxDbConstantsUtil.EXISTING_MARKET_ID, true);

        Assert.assertFalse( AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_MARKET_ID.equals( createdId));
        Assert.assertEquals( createdId, generator.getInvoiceMessageRuleForMarketsId( AxDbConstantsUtil.EXISTING_MARKET_ID, false));

        //
        // Test default value on new client
        //
        String newMarket = "100";

        Assert.assertNull( generator.getInvoiceMessageRuleForMarketsId( newMarket, false));

        //
        // Test new id on new client
        //
        String newId = generator.getInvoiceMessageRuleForMarketsId( newMarket, true);

        Assert.assertFalse( createdId.equals( newId));
        Assert.assertEquals( newId, generator.getInvoiceMessageRuleForMarketsId( newMarket, false));
    }

    @Test
    public void testCompanyNameGenerator()
    {
        IdGenerator generator = new IdGenerator();

        Assert.assertEquals(
            "Telia AB",
            generator.getClientCompanyName( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, false));

        Assert.assertNull( generator.getClientCompanyName( "TestClient", false));

        String name = generator.getClientCompanyName( "TestClient", true);

        Assert.assertNotNull( name);
        Assert.assertTrue( name.contains( "Barium-"));
    }
}
