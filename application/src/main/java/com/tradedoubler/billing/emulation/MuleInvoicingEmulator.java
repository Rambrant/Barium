package com.tradedoubler.billing.emulation;

import com.tradedoubler.billing.domain.type.InvoiceStatusType;
import com.tradedoubler.billing.oracle.dao.AxInvoiceDao;
import com.tradedoubler.billing.oracle.dao.PanOrderLineDao;
import com.tradedoubler.billing.oracle.dto.PanOrderLineDto;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class MuleInvoicingEmulator
{
    public static final Logger LOGGER = Logger.getLogger( MuleInvoicingEmulator.class);

    PanOrderLineDao panOrderLineDao;
    AxInvoiceDao    axInvoiceDao;

    public MuleInvoicingEmulator()
    {
        panOrderLineDao = new PanOrderLineDao();
        axInvoiceDao    = new AxInvoiceDao();
    }

    public int emulateMuleInvoicing() throws SQLException
    {
        List< PanOrderLineDto> orderLines    = new ArrayList< PanOrderLineDto>();
        List< PanOrderLineDto> forecastLines = new ArrayList< PanOrderLineDto>();

        //
        // Read all existing OrderLines until we are told that invoicing should start
        //
        LOGGER.info( "Waiting for order lines");

        while( ! isReadyToInvoice())
        {
            orderLines.addAll( getOrderLines());

            forecastLines.addAll( getForecastOrderLines());
        }

        //
        // Confirm invoicing
        //
        LOGGER.info( "Invoicing started!");

        invoicingStarted();

        //
        // Report back all forecast lines
        //
        LOGGER.info( "Forecast order lines delivered (" + forecastLines.size() + " lines)");

        //
        // Report back all order lines as if invoiced
        //
        LOGGER.info( "Report order lines as invoiced! (" + orderLines.size() + " lines)");

        List< String> createdInvoices = invoiceCreatedAx( orderLines);

        //
        // Report back all invoices as paid
        //
        for( String invoiceNumber: createdInvoices)
        {
            LOGGER.info( "Report invoice '" + invoiceNumber + "' as paid!");

            invoicePaid( invoiceNumber);
        }

        return orderLines.size();
    }

    private boolean isReadyToInvoice() throws SQLException
    {
        String result = axInvoiceDao.isReadyToInvoice();

        if( result.equalsIgnoreCase( "Y"))
        {
            return true;
        }

        if( result.equalsIgnoreCase( "N"))
        {
            return false;
        }

        throw new RuntimeException( "Call of function billing_pkg.readyToInvoice failed! " + result);
    }

    private void invoicingStarted() throws SQLException
    {
        String result = axInvoiceDao.invoicingStarted();

        if( result.startsWith( "-1" ))
        {
            throw new RuntimeException( "Error confirming invoicing started!");
        }
    }

    private List< PanOrderLineDto> getOrderLines() throws SQLException
    {
        List< PanOrderLineDto> orderLineList = new ArrayList< PanOrderLineDto>();

        String batchId = panOrderLineDao.getBatchId();

        if( batchId.isEmpty()           ||
            batchId.startsWith( "-1"))
        {
            throw new RuntimeException( "Error fetching order line batch id");
        }

        if( batchId.equals( "0"))
        {
            waitSome( 5000L);

            return orderLineList;
        }

        LOGGER.info( "Getting order lines for batchId " + batchId );

        orderLineList = panOrderLineDao.getOrderLinesByBatchId( batchId);

        String result = panOrderLineDao.updateBatchOfOrderLines( batchId);

        if( result.startsWith( "-1"))
        {
            throw new RuntimeException( "Error confirming order lines with batch id " + batchId);
        }

        return orderLineList;
    }

    private List< PanOrderLineDto> getForecastOrderLines() throws SQLException
    {
        List< PanOrderLineDto> forecastOrderLineList = new ArrayList< PanOrderLineDto>();

        String batchId = panOrderLineDao.getForecastBatchId();

        if( batchId.isEmpty()           ||
            batchId.startsWith( "-1"))
        {
            throw new RuntimeException( "Error fetching forecast order line batch id");
        }

        if( batchId.equals( "0"))
        {
            waitSome( 5000L);

            return forecastOrderLineList;
        }

        LOGGER.info( "Getting forecast order lines for batchId " + batchId);

        forecastOrderLineList = panOrderLineDao.getForecastOrderLinesByBatchId( batchId );

        String result = panOrderLineDao.updateBatchOfForecastOrderLines( batchId );

        if( result.startsWith( "-1"))
        {
            throw new RuntimeException( "Error confirming forecast order lines with batch id " + batchId);
        }

        return forecastOrderLineList;
    }

    private List< String> invoiceCreatedAx( List<PanOrderLineDto> orderLines)
    {
        Date       date      = new Date();
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format( date);

        try
        {
            return axInvoiceDao.invoiceCreatedAx( orderLines, dateFormatted);
        }
        catch( SQLException e)
        {
            throw new RuntimeException( "Invoice could not be saved in PAN", e);
        }
    }

    private void invoicePaid( String invoiceNumber)
    {
        try
        {
            axInvoiceDao.setInvoiceStatus( invoiceNumber, InvoiceStatusType.INVOICE_STATUS_PAID);
        }
        catch( SQLException e)
        {
            throw new RuntimeException( "Invoice could not set to paid in PAN", e);
        }
    }

    private void waitSome( long waitTime)
    {
        try
        {
            Thread.sleep( waitTime );
        }
        catch( InterruptedException e)
        {
            throw new RuntimeException( "Timeout in " + getClass(), e);
        }
    }
}
