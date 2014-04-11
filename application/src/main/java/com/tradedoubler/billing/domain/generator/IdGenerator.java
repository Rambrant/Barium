package com.tradedoubler.billing.domain.generator;

import com.tradedoubler.billing.util.AxDbConstantsUtil;

import java.util.*;

/**
 * @author Thomas Rambrant (thore)
 */

public class IdGenerator
{
    private static long nextOrderLineId = System.currentTimeMillis();
    private static long uniqueCounter   = 0L;

    private List< String>              invoiceRuleIds;
    private List< String>              clientIds;
    private Map< String, String>       clientNames;
    private Map< String, List<String>> agreementIds;
    private Map< String, List<String>> invoiceRecipientIds;
    private Map< String, List<String>> purchaseOrderIds;
    private Map< String, List<String>> messageForInvoicingRuleIds;
    private Map< String, List<String>> messageForClientIds;
    private Map< String, List<String>> messageForMarketIds;

    public IdGenerator()
    {
        invoiceRuleIds = new ArrayList< String>();
        invoiceRuleIds.add( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID);

        clientIds = new ArrayList< String>();
        clientIds.add( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM);

        clientNames = new HashMap<String, String>();
        clientNames.put( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, "Telia AB");

        agreementIds = new HashMap<String, List< String>>();
        agreementIds.put( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, new ArrayList<String>());
        agreementIds.get( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID).add( AxDbConstantsUtil.EXISTING_AGREEMENT_ID);

        invoiceRecipientIds = new HashMap<String, List< String>>();
        invoiceRecipientIds.put( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, new ArrayList<String>());
        invoiceRecipientIds.get( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID).add( AxDbConstantsUtil.EXISTING_INVOICE_RECIPIENT_ID);

        purchaseOrderIds = new HashMap<String, List< String>>();
        purchaseOrderIds.put( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, new ArrayList<String>());
        purchaseOrderIds.get( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID).add( AxDbConstantsUtil.EXISTING_PURCHASE_ORDER_ID);

        messageForInvoicingRuleIds = new HashMap<String, List< String>>();
        messageForInvoicingRuleIds.put( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID, new ArrayList<String>());
        messageForInvoicingRuleIds.get( AxDbConstantsUtil.EXISTING_INVOICE_RULE_ID).add( AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_ID);

        messageForClientIds = new HashMap<String, List< String>>();
        messageForClientIds.put( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM, new ArrayList<String>());
        messageForClientIds.get( AxDbConstantsUtil.EXISTING_CLIENT_ACCOUNT_NUM).add( AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_CLIENT_ID);

        messageForMarketIds = new HashMap<String, List< String>>();
        messageForMarketIds.put( AxDbConstantsUtil.EXISTING_MARKET_ID, new ArrayList<String>());
        messageForMarketIds.get( AxDbConstantsUtil.EXISTING_MARKET_ID).add( AxDbConstantsUtil.EXISTING_INVOICE_MESSAGE_RULE_MARKET_ID);
    }

    public String getInvoiceRuleId( boolean create)
    {
        if( create)
        {
            invoiceRuleIds.add( AxDbConstantsUtil.generateInvoiceRuleId());
        }

        return getLastId( invoiceRuleIds);
    }

    public List< String> getAllInvoiceRuleIds()
    {
        return invoiceRuleIds;
    }

    public String getClientId( boolean create)
    {
        if( create)
        {
            clientIds.add( AxDbConstantsUtil.generateClientId());

            sleepToNextMs();
        }

        return getLastId( clientIds);
    }

    public String getAgreementId( String invoiceRuleId, boolean create)
    {
        if( create)
        {
            if( ! agreementIds.containsKey( invoiceRuleId))
            {
                agreementIds.put( invoiceRuleId, new ArrayList<String>());
            }

            agreementIds.get( invoiceRuleId).add( AxDbConstantsUtil.generateAgreementId());

            sleepToNextMs();
        }

        return getLastId( invoiceRuleId, agreementIds);
    }

    public String getInvoiceRecipientIds( String invoiceRuleId, boolean create)
    {
        if( create)
        {
            if( ! invoiceRecipientIds.containsKey( invoiceRuleId))
            {
                invoiceRecipientIds.put( invoiceRuleId, new ArrayList<String>());
            }

            invoiceRecipientIds.get( invoiceRuleId).add( AxDbConstantsUtil.generateInvoiceRecipientId());

            sleepToNextMs();
        }

        return getLastId( invoiceRuleId, invoiceRecipientIds);
    }


    public String getPurchaseOrderIds( String invoiceRuleId, boolean create)
    {
        if( create)
        {
            if( ! purchaseOrderIds.containsKey( invoiceRuleId))
            {
                purchaseOrderIds.put( invoiceRuleId, new ArrayList<String>());
            }

            purchaseOrderIds.get( invoiceRuleId).add( AxDbConstantsUtil.generatePurchaseOrderId());

            sleepToNextMs();
        }

        return getLastId( invoiceRuleId, purchaseOrderIds);
    }

    public String getInvoiceMessageRuleForInvoicingRuleId( String invoiceRuleId, boolean create)
    {
        if( create)
        {
            if( ! messageForInvoicingRuleIds.containsKey( invoiceRuleId))
            {
                messageForInvoicingRuleIds.put( invoiceRuleId, new ArrayList<String>());
            }

            messageForInvoicingRuleIds.get( invoiceRuleId).add( AxDbConstantsUtil.generateInvoiceMessageRuleId() );
        }

        return getLastId( invoiceRuleId, messageForInvoicingRuleIds);
    }

    public String getInvoiceMessageRuleForClientsId( String clientId, boolean create)
    {
        if( create)
        {
            if( ! messageForClientIds.containsKey( clientId))
            {
                messageForClientIds.put( clientId, new ArrayList<String>());
            }

            messageForClientIds.get( clientId).add( AxDbConstantsUtil.generateInvoiceMessageRuleId());
        }

        return getLastId( clientId, messageForClientIds);
    }

    public String getInvoiceMessageRuleForMarketsId( String marketId, boolean create)
    {
        if( create)
        {
            if( ! messageForMarketIds.containsKey( marketId))
            {
                messageForMarketIds.put( marketId, new ArrayList<String>());
            }

            messageForMarketIds.get( marketId).add( AxDbConstantsUtil.generateInvoiceMessageRuleId());
        }

        return getLastId( marketId, messageForMarketIds);
    }

    public Map< String, List< String>> getAllInvoiceMessageRuleForMarketsIds()
    {
        return messageForMarketIds;
    }

    public String getOrderLineId( long numberOfLines)
    {
        long tmpReturn = nextOrderLineId;

        nextOrderLineId += numberOfLines;

        return getLastChars( String.valueOf( tmpReturn), 10);
    }

    public String getBatchId()
    {
        return replaceFront( "babe", UUID.randomUUID().toString());
    }

    private String getLastId( List< String> ids)
    {
        if( ids != null)
        {
            return ids.get( ids.size() - 1);
        }

        return null;
    }

    private String getLastId( String key, Map< String, List< String>> map)
    {
        List< String> ids = map.get( key);

        if( ids != null)
        {
            return ids.get( ids.size() - 1);
        }

        return null;
    }

    private void sleepToNextMs()
    {
        //
        // Sleep a ms to ensure that time based ids dont show up as equal
        //
        try
        {
            Thread.sleep( 1L);
        }
        catch( InterruptedException e)
        {
            //
            // If this happens, no worries
        }
    }


    private static String replaceFront( String front, String s)
    {
        return front + s.substring( front.length());
    }

    private static String getLastChars( String s, int i)
    {
        return s.substring( s.length() - i, s.length());
    }

    public String getClientCompanyName( String clientId, boolean create)
    {
        if( create)
        {
            Calendar cal = Calendar.getInstance();

            String name = "Barium-"             +
                cal.get( Calendar.YEAR)         + "-" +
                cal.get( Calendar.MONTH)        + "-" +
                cal.get( Calendar.DAY_OF_MONTH) + "_" +
                cal.get( Calendar.HOUR_OF_DAY)  + "-" +
                cal.get( Calendar.MINUTE)       + "-" +
                cal.get( Calendar.SECOND)       + "-" +
                cal.get( Calendar.MILLISECOND);

            clientNames.put( clientId, name);
        }

        return clientNames.get( clientId);
    }

    public long getCounter()
    {
        ++uniqueCounter;

        return uniqueCounter;
    }
}
