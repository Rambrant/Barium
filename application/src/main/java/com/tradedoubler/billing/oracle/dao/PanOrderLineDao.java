package com.tradedoubler.billing.oracle.dao;

import com.tradedoubler.billing.dao.AbstractDaoImpl;
import com.tradedoubler.billing.factory.EntityFactory;
import com.tradedoubler.billing.oracle.dto.PanOrderLineDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanOrderLineDao extends AbstractDaoImpl< PanOrderLineDao>
{
    final String ADD_ORDER_LINE_JOB = "{ " +
        " ? ="                                +                               //  1
        " call billing_pkg.addOrderLinesJob(" +
        " ?,"                                 +   // program id               //  2
        " SYSDATE,"                           +   // period end
        " 'Y',"                               +   // include debit
        " 'Y',"                               +   // include credit
        " 'Y',"                               +   // include file hosting
        " 'Y',"                               +   // include affiliate manual
        " 'Y',"                               +   // include program manual
        " ?) }";                                  // created by               //  3

    public static final String ORDER_LINE_STATUS_JOB_SQL =
        " SELECT"                        +
            " COUNT(*) as count"         +
        " FROM"                          +
            " order_line_job"            +
        " WHERE"                         +
            " ol_job_status_id != 3 AND" +
            " ol_job_status_id != 4";

    public static final String START_ORDER_LINE_JOB_SQL =
        "{ call CONFIG_UTILS.UpdateValue( 'BILL_DAILY_OL_CREATION', 'Y') }";

    private static final String GET_BATCH_ID_SQL =
        "{ ? = " +                                  //  1
        "call billing_pkg.fetchOrderLineBatchId() }";

    private static final String GET_FORECAST_BATCH_ID_SQL =
        "{ ? = " +                                  //  1
        "call billing_pkg.fetchOrderLineBatchIdPP() }";

    private static final String GET_ORDER_LINES_SQL =
        " SELECT" +
            " CASE WHEN v.program_id IS NULL THEN '1' ELSE '2' END rev_type,"       +
            " ol.*,"                                                                +
            " p.currency_id,"                                                       +
            " p.invoice_td_org_id"                                                  +
        " FROM"                                                                     +
            " order_lines ol"                                                       +
            " INNER JOIN programs p ON p.id = ol.program_id "                       +
            " LEFT OUTER JOIN v_private_network_programs v ON v.program_id = p.id"  +
        " WHERE"                                                                    +
            " ol.order_line_batch_id = ?";

    private static final String GET_FORECAST_ORDER_LINES_SQL =
        " SELECT "                                           +
            " ol.*,"                                         +
            " p.currency_id"                                 +
        " FROM"                                              +
            " order_lines ol"                                +
            " INNER JOIN programs p ON p.id = ol.program_id" +
        " WHERE"                                             +
            " ol.order_line_batch_id_pp = ?"                 +
        " ORDER BY"                                          +
            " ol.id";

    private static final String UPDATE_BATCH_ORDER_LINES_SQL =
        "{ ? =" +                                       //  1
        " call billing_pkg.updateBatchOfOrderLines(" +
        " ?) }";                                        //  2

    private static final String UPDATE_BATCH_FORECAST_ORDER_LINES_SQL =
        "{ ? =" +                                       //  1
        " call billing_pkg.updateBatchOfOrderLinesPP(" +
        " ?) }";                                        //  2

    private static final String GET_ORDER_LINES_BY_PROGRAM_SQL =
        " SELECT" +
            " CASE WHEN v.program_id IS NULL THEN '1' ELSE '2' END rev_type,"       +
            " ol.*,"                                                                +
            " p.currency_id,"                                                       +
            " p.invoice_td_org_id"                                                  +
        " FROM"                                                                     +
            " order_lines ol"                                                       +
            " INNER JOIN programs p ON p.id = ol.program_id "                       +
            " LEFT OUTER JOIN v_private_network_programs v ON v.program_id = p.id"  +
        " WHERE"                                                                    +
            " ol.program_id = ? AND" +
            " ol.created >= ?"       +
        " ORDER BY" +
            " ol.id DESC";

    public PanOrderLineDao()
    {
        super();
    }

    public void createOrderLinesJob( Long programId, Long personId) throws SQLException
    {
        //
        // Create the order line job
        //
        this.insertOrderLinesJob(
            programId,
            personId );
    }

    public void processOrderLinesJob() throws SQLException
    {
        //
        // Start the processing of the traffic data to create the order lines
        //
        this.startProcessOrderLineJob();

        //
        // Wait for the orderLineProcessing to be ready
        //
        waitForOrderLineJob();
    }

    public String getBatchId() throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( GET_BATCH_ID_SQL);

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

    public String getForecastBatchId() throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( GET_FORECAST_BATCH_ID_SQL);

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

    public List< PanOrderLineDto> getOrderLinesByBatchId( String batchId) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        ResultSet         resultSet         = null;

        List< PanOrderLineDto> orderLines = new ArrayList< PanOrderLineDto>();

        try
        {
            preparedStatement = connection.prepareStatement( GET_ORDER_LINES_SQL );

            preparedStatement.setString( 1, batchId );

            resultSet = preparedStatement.executeQuery();

            EntityFactory< PanOrderLineDto> invoiceEntityFactory = new EntityFactory< PanOrderLineDto>();

            while( resultSet.next())
            {
                orderLines.add( invoiceEntityFactory.createEntityFromResultSet(
                    resultSet,
                    Arrays.asList("*"),
                    PanOrderLineDto.class));
            }

            return orderLines;
        }
        finally
        {
            if( preparedStatement != null)
            {
                preparedStatement.close();
            }

            if( resultSet != null)
            {
                resultSet.close();
            }
        }
    }

    public List< PanOrderLineDto> getForecastOrderLinesByBatchId( String batchId) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        ResultSet         resultSet         = null;

        List< PanOrderLineDto> orderLines = new ArrayList< PanOrderLineDto>();

        try
        {
            preparedStatement = connection.prepareStatement( GET_FORECAST_ORDER_LINES_SQL);

            preparedStatement.setString( 1, batchId );

            resultSet = preparedStatement.executeQuery();

            EntityFactory< PanOrderLineDto> invoiceEntityFactory = new EntityFactory< PanOrderLineDto>();

            while( resultSet.next())
            {
                orderLines.add( invoiceEntityFactory.createEntityFromResultSet(
                    resultSet,
                    Arrays.asList("*"),
                    PanOrderLineDto.class));
            }

            return orderLines;
        }
        finally
        {
            if( preparedStatement != null)
            {
                preparedStatement.close();
            }

            if( resultSet != null)
            {
                resultSet.close();
            }
        }
    }

    public List< PanOrderLineDto> getOrderLinesByProgramId( Long programId, Timestamp startTime) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        ResultSet         resultSet         = null;

        List< PanOrderLineDto> orderLines = new ArrayList< PanOrderLineDto>();

        try
        {
            preparedStatement = connection.prepareStatement( GET_ORDER_LINES_BY_PROGRAM_SQL);

            preparedStatement.setLong(      1, programId);
            preparedStatement.setTimestamp( 2, startTime);

            resultSet = preparedStatement.executeQuery();

            EntityFactory< PanOrderLineDto> invoiceEntityFactory = new EntityFactory< PanOrderLineDto>();

            while( resultSet.next())
            {
                orderLines.add( invoiceEntityFactory.createEntityFromResultSet(
                    resultSet,
                    Arrays.asList("*"),
                    PanOrderLineDto.class));
            }

            return orderLines;
        }
        finally
        {
            if( preparedStatement != null)
            {
                preparedStatement.close();
            }

            if( resultSet != null)
            {
                resultSet.close();
            }
        }
    }

    public String updateBatchOfOrderLines( String batchId) throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( UPDATE_BATCH_ORDER_LINES_SQL);

            callableStatement.registerOutParameter( 1, Types.VARCHAR);
            callableStatement.setString(            2, batchId);

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

    public String updateBatchOfForecastOrderLines( String batchId) throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( UPDATE_BATCH_FORECAST_ORDER_LINES_SQL);

            callableStatement.registerOutParameter( 1, Types.VARCHAR);
            callableStatement.setString(            2, batchId);

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

    private long insertOrderLinesJob( Long programId, Long personId) throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( ADD_ORDER_LINE_JOB);

            callableStatement.registerOutParameter( 1, Types.NUMERIC);
            callableStatement.setLong(              2, programId);
            callableStatement.setLong(              3, personId);

            callableStatement.execute();

            long retVal = callableStatement.getLong( 1);

            LOGGER.info( String.format( "Inserted order line job %d", retVal));

            return retVal;
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }
        }
    }

    private void startProcessOrderLineJob() throws SQLException
    {
        CallableStatement callableStatement = null;

        try
        {
            callableStatement = connection.prepareCall( START_ORDER_LINE_JOB_SQL);

            callableStatement.execute();

            LOGGER.info( "OrderLine processing started");
        }
        finally
        {
            if( callableStatement != null)
            {
                callableStatement.close();
            }
        }
    }

    private void waitForOrderLineJob() throws SQLException
    {
        LOGGER.info( String.format( "Waiting for Order Lines to be processed"));

        Long numJobs;

        do
        {
            try
            {
                Thread.sleep( 1000);    // Just an arbitrary number. Increase if necessary
            }
            catch( InterruptedException e)
            {
                throw new RuntimeException( e);
            }

            numJobs = getNumberOfUnprocessedOrderLineJobs();
        }
        while( ! numJobs.equals( 0L));

        LOGGER.info( String.format( "Order Line processing done!"));
    }

    private Long getNumberOfUnprocessedOrderLineJobs() throws SQLException
    {
        PreparedStatement preparedStatement = null;
        ResultSet         resultSet         = null;

        try
        {
            preparedStatement = connection.prepareStatement( ORDER_LINE_STATUS_JOB_SQL);

            resultSet = preparedStatement.executeQuery();

            if( resultSet.next())
            {
                return resultSet.getLong( "count");
            }
            else
            {
                throw new RuntimeException( "Could not get the number of unprocessed OrderLine Jobs");
            }
        }
        finally
        {
            if( preparedStatement != null)
            {
                preparedStatement.close();
            }

            if( resultSet != null)
            {
                resultSet.close();
            }
        }
    }
}