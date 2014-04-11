package com.tradedoubler.billing.domain.validator;


import com.tradedoubler.billing.domain.BillingData;
import com.tradedoubler.billing.domain.dto.SegmentTariffDto;
import com.tradedoubler.billing.exception.InvoicingSolutionValidationException;
import com.tradedoubler.billing.oracle.dto.PanOrderLineDto;
import junit.framework.Assert;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanOrderLinesValidator extends ValidatorBase
{
    private BillingData billingData;


    public PanOrderLinesValidator( BillingData billingData ) throws SQLException
    {
        this.billingData = billingData;
    }

    public void validate( List< PanOrderLineDto> orderLines) throws InvoicingSolutionValidationException, SQLException
    {
        List< SegmentTariffDto> segmentTariffs = billingData.getSegmentTariffs();

        long numIncludedTariffs = 0L;
        long numMatchedLines    = 0L;

        for( SegmentTariffDto segmentTariff : segmentTariffs)
        {
            if( billingData.getProgram().isPrepayment())
            {
                if( ! segmentTariff.getEventTypeId().isTrafficTransaction())
                {
                    numIncludedTariffs++;
                }
            }
            else
            {
                numIncludedTariffs++;
            }

            if( validateTariffAgainstOrderLines( segmentTariff, orderLines))
            {
                numMatchedLines++;
            }
        }

        Assert.assertTrue(
            "Number of matched segmentTariffs differs",
            numIncludedTariffs * billingData.getAffiliates().size() == numMatchedLines);
    }

    private boolean validateTariffAgainstOrderLines(
        SegmentTariffDto       segmentTariff,
        List< PanOrderLineDto> orderLines)
    {
        Long eventId   = segmentTariff.getEventTypeId().getId() == 50L ? 305L : segmentTariff.getEventTypeId().getId();
        Long SegmentId = segmentTariff.getId();

        for( PanOrderLineDto orderLine : orderLines)
        {
            if( isEqual( orderLine.getSegmentTariffId(), SegmentId) &&
                isEqual( orderLine.getSalesPart(),       eventId))
            {
                boolean matched = false;

                if( segmentTariff.getEventTypeId().isTrafficTransaction() ||
                    segmentTariff.getEventTypeId().isTrackBackEvent()     ||
                    segmentTariff.getEventTypeId().isFileHostingEvent())
                {
                    matched = validateTraffic( segmentTariff, orderLine);
                }

                if( segmentTariff.getEventTypeId().isManualTransactionEvent())
                {
                    matched = validateManualTransaction( segmentTariff, orderLine);
                }

                if( segmentTariff.getEventTypeId().isDigitalWalletEvent())
                {
                    matched = validateDigitalWallet( segmentTariff, orderLine);
                }

                if( matched)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean validateTraffic(
        SegmentTariffDto segmentTariff,
        PanOrderLineDto  orderLine )
    {
        Double fee    = segmentTariff.getFee() == null ? 0.0 : segmentTariff.getFee();
        Double tdFee  = segmentTariff.getTdCommissionFee() == null ? 0.0 : segmentTariff.getTdCommissionFee();
        Long   numOff = segmentTariff.getNumberOf() == null ? 1L : segmentTariff.getNumberOf();

        if( ! isEqual( orderLine.getCost(), fee * numOff))
        {
            return false;
        }

        if( ! isEqual( orderLine.getTotal(), (fee + tdFee) * numOff))
        {
            return false;
        }

        if( ! isEqual( orderLine.getQuantity(), 1L))
        {
            return false;
        }

        if( ! isEqual( orderLine.getDebit().equals( "Y"), segmentTariff.getType().getId().equals( "D")))
        {
            return false;
        }

        return true;
    }

    private boolean validateManualTransaction(
        SegmentTariffDto segmentTariff,
        PanOrderLineDto orderLine )
    {
        Double fee = segmentTariff.getFee() == null ? 0.0 : segmentTariff.getFee();

        if( ! isEqual( orderLine.getCost(), 0.0))
        {
            return false;
        }

        if( ! isEqual( orderLine.getTotal(), fee))
        {
            return false;
        }

        if( ! isEqual( orderLine.getQuantity(), 1L))
        {
            return false;
        }

        if( ! isEqual( orderLine.getDebit().equals( "Y"), segmentTariff.getType().getId().equals( "D")))
        {
            return false;
        }

        return true;
    }

    private boolean validateDigitalWallet(
        SegmentTariffDto segmentTariff,
        PanOrderLineDto  orderLine )
    {
        Double fee    = segmentTariff.getFee() == null ? 0.0 : segmentTariff.getFee();

        if( ! isEqual( orderLine.getCost(), fee))
        {
            return false;
        }

        if( ! isEqual( orderLine.getTotal(), fee))
        {
            return false;
        }

        if( ! isEqual( orderLine.getQuantity(), 1L))
        {
            return false;
        }

        if( ! isEqual( orderLine.getDebit().equals( "Y"), segmentTariff.getType().getId().equals( "D")))
        {
            return false;
        }

        return true;
    }

    private <T> boolean isEqual( T a, T b)
    {
        if( a == null && b == null)
        {
            return true;
        }

        if( a == null  ||  b == null)
        {
            return false;
        }

        return a.equals( b);
    }
}
