package com.tradedoubler.billing.domain.generator;

import com.tradedoubler.billing.domain.DebitOrCredit;
import com.tradedoubler.billing.domain.Timestamp;
import com.tradedoubler.billing.domain.xml.XmlAttributeWrapper;
import com.tradedoubler.billing.service.pan.BariumOrderLine;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class OrderLineBuilder
{
    private static long nextOrderLineId = System.currentTimeMillis();

    private XmlAttributeWrapper xmlWrapper;
    private IdGenerator         idGenerator;
    private String              scenarioName;

    public OrderLineBuilder(
        XmlAttributeWrapper xmlWrapper,
        IdGenerator         idGenerator,
        String              scenarioName)
    {
        this.xmlWrapper   = xmlWrapper;
        this.idGenerator  = idGenerator;
        this.scenarioName = scenarioName;
    }

    public BariumOrderLine buildOrderLine()
    {
        //
        // Get hold of the most resent invoicingRuleId and use it to populate the order line
        //
        String invoicingRuleId = idGenerator.getInvoiceRuleId( false);

        BariumOrderLine orderLine = new BariumOrderLine(
            idGenerator.getBatchId(),
            "dummy",                                // systemAgreementId
            "dummy",                                // orderLineId
            true,                                   // invoiceable
            "5",                                    // salesPart    //110
            "Made by Barium-" + new Date(),         // description
            1,                                      // quantity
            BigDecimal.valueOf( 695.21),            // total
            BigDecimal.valueOf( 695.21),            // cost
            createPeriod(),                         // period
            new Timestamp( "2012-10-26T21:32:52"),  // dateOfPrepayment
            DebitOrCredit.DEBIT,                    // debitOrCredit
            "1234567890123",                        // credited invoice Id
            "1234567890");                          // splitter

        //
        // Patch the line to pick up the number of order lines
        //
        xmlWrapper.patchOrderLine( orderLine, scenarioName);

        //
        // Set the order line and the agreement ids
        //
        orderLine.setSystemAgreementId( idGenerator.getAgreementId( invoicingRuleId, false));
        orderLine.setOrderLineId( idGenerator.getOrderLineId( orderLine.getNumberOfOrderLines()));

        //
        // patch it again to set the agreement and line ids to a hard coded value if they exist
        //
        xmlWrapper.patchOrderLine( orderLine, scenarioName);

        return orderLine;
    }

    private String createPeriod()
    {
        Calendar cal = Calendar.getInstance();

        return String.format( "%02d%02d", cal.get( Calendar.YEAR) - 2000, cal.get( Calendar.MONTH) + 1);
    }
}
