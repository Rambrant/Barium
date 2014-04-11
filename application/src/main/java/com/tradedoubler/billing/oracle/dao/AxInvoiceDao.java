package com.tradedoubler.billing.oracle.dao;

import com.tradedoubler.billing.domain.type.InvoiceStatusType;
import com.tradedoubler.billing.oracle.dto.PanOrderLineDto;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class AxInvoiceDao extends SimpleDao
{
    private static final String READY_TO_INVOICE_SQL =
        "{ ? = " +                                //  1
        "call billing_pkg.readyToInvoice() }";

    private static final String INVOICING_STARTED_SQL =
        "{ ? = " +                                //  1
        "call billing_pkg.invoicingStarted() }";

    final String INVOICE_CREATED_AX_SQL = "{" +
        " ? ="                                +   //  1
        " call billing_pkg.invoiceCreatedAx(" +
            " ?,"                             +   //  2 order line id
            " ?,"                             +   //  3 ax invoice number
            " ?,"                             +   //  4 TD org id
            " ?,"                             +   //  5 currency id
            " ?,"                             +   //  6 source system agreement id
            " ?) }";                              //  7 revenue type

    private static final String UPDATE_INVOICE_STATUS_SQL = "{" +
        " ? = " +                                 //  1
        " call billing_pkg.invoiceStatusChangedAx( " +
            " ?," +                               //  2 ax invoice number
            " ?) }";                              //  3 statusId


    public AxInvoiceDao()
    {
        super();
    }

    public String isReadyToInvoice() throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( READY_TO_INVOICE_SQL);

            callableStatement.registerOutParameter( 1, Types.VARCHAR);

            callableStatement.execute();

            return callableStatement.getString( 1);
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }
        }
    }

    public String invoicingStarted() throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( INVOICING_STARTED_SQL);

            callableStatement.registerOutParameter( 1, Types.VARCHAR);

            callableStatement.execute();

            return callableStatement.getString( 1);
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }
        }
    }

    public List< String> invoiceCreatedAx(
        List< PanOrderLineDto> orderLines,
        String                 time) throws SQLException
    {
        List< String> createdInvoices = new ArrayList< String>();

        CallableStatement callableStatement = null;

        try
        {
            connection.setAutoCommit( false);

            callableStatement = connection.prepareCall( INVOICE_CREATED_AX_SQL);

            for( PanOrderLineDto orderLine : orderLines)
            {
                String invoiceNumber = "Bill-" + orderLine.getProgramId() + "-" + time;

                if( ! createdInvoices.contains( invoiceNumber))
                {
                    createdInvoices.add( invoiceNumber);
                }

                callableStatement.registerOutParameter( 1, Types.VARCHAR );

                callableStatement.setLong(   2, orderLine.getId());
                callableStatement.setString( 3, invoiceNumber);
                callableStatement.setLong(   4, orderLine.getTDOrgId());
                callableStatement.setString( 5, orderLine.getCurrencyId());
                callableStatement.setString( 6, orderLine.getAgreementId());
                callableStatement.setString( 7, orderLine.getRevenueType());

                callableStatement.execute();
            }

            connection.commit();

            return createdInvoices;
        }
        catch( SQLException e)
        {
            connection.rollback();

            throw e;
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }

            connection.setAutoCommit( true);
        }
    }

    public String setInvoiceStatus( String invoiceNumber, InvoiceStatusType invoiceStatus) throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( UPDATE_INVOICE_STATUS_SQL);

            callableStatement.registerOutParameter( 1, Types.VARCHAR);

            callableStatement.setString( 2, invoiceNumber);
            callableStatement.setLong(   3, invoiceStatus.getId());

            callableStatement.execute();

            return callableStatement.getString( 1);
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }
        }
    }
}