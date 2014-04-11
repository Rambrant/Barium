package com.tradedoubler.billing.domain.xml;

import com.tradedoubler.billing.domain.parse.*;
import com.tradedoubler.billing.exception.ScenarioDataFileParseException;
import com.tradedoubler.billing.utils.XmlUtil;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * @author Thomas Rambrant (thore)
 */

public class XmlDataFileParser
{
    public XmlAttributeWrapper parseXmlFile( String fileName)
    {
        try
        {
            Document doc = XmlUtil.getDocument( fileName);

            XmlAttributeWrapper  attributeWrapper = new XmlAttributeWrapper();

            //
            // Parse the XML entities and store the attributes
            //
            attributeWrapper.setInvoicingRuleAttributes( new InvoicingRuleParser().parse( doc));
            attributeWrapper.setAgreementAttributes( new AgreementParser().parse( doc));
            attributeWrapper.setClientAttributes( new ClientParser().parse( doc));
            attributeWrapper.setInvoiceRecipientAttributes( new InvoiceRecipientParser().parse( doc));
            attributeWrapper.setBankAttributes( new BankParser().parse( doc));
            attributeWrapper.setMessageForInvoicingRuleAttributes( new InvoiceMessageForInvoicingRuleParser().parse( doc));
            attributeWrapper.setMessageForClientAttributes( new InvoiceMessageForClientParser().parse( doc));
            attributeWrapper.setMessageForMarketAttributes( new InvoiceMessageForMarketParser().parse( doc));
            attributeWrapper.setOrderLineAttributes( new OrderLineParser().parse( doc));

            //
            // Save the parsed data to the context
            //
            return attributeWrapper;
        }
        catch( Exception e)
        {
            try
            {
                throw new ScenarioDataFileParseException( e);
            }
            catch( IOException io)
            {
                throw new RuntimeException(io);
            }
        }
    }
}