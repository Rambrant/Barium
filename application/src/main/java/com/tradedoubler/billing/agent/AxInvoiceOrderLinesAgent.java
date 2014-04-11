package com.tradedoubler.billing.agent;

import com.google.gson.Gson;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dao.AxOrderLineDao;
import com.tradedoubler.billing.derby.dto.OrderLineDto;
import com.tradedoubler.billing.domain.generator.OrderLineBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.service.pan.BariumOrderLine;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class AxInvoiceOrderLinesAgent extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( AxInvoiceOrderLinesAgent.class);

    private AxOrderLineDao   orderLineDao;
    private OrderLineBuilder ruleBuilder;
    private Gson             gson;

    public AxInvoiceOrderLinesAgent(
        ProducerParameter parameter,
        OrderLineBuilder  orderLineBuilder ) throws ProducerCreationFailedException, SQLException
    {
        super( parameter);

        validateParam( parameter);

        this.orderLineDao = new AxOrderLineDao();
        this.ruleBuilder  = orderLineBuilder;
        this.gson         = GsonFactory.getGson();
    }

    @Override
    protected void before() throws Exception
    {
        //
        // Nothing to do
        //
    }

    @Override
    protected void after()
    {
        //
        // Nothing to do
        //
    }

    @Override
    protected void executeBeat( ProducerEventType event)
    {
        OrderLineDto    dto  = new OrderLineDto();
        BariumOrderLine line = null;

        //
        // Create the message according to the event type
        //
        switch( event)
        {
        case NONE:
        case CREATE:
        case ILLEGAL:

            //
            // Create a new "meta" order line
            //
            line = ruleBuilder.buildOrderLine();

            dto.setBatchId( line.getBatchId());
            dto.setNumOrderLines( line.getNumberOfOrderLines());
            dto.setOrderLineId( line.getOrderLineId());
            dto.setJsonOrderLine( gson.toJson( line));
            dto.setEventType( event.asDbType());

            storeMessage( dto, line.getSystemAgreementId());

            break;

        case SYNTAX:

            throw new IllegalArgumentException( "SYNTAX events not possible on AxInvoiceOrderLinesAgent");

        case ORDER:

            throw new IllegalArgumentException( "ORDER events not possible on AxInvoiceOrderLinesAgent");

        case SEQUENCE:

            throw new IllegalArgumentException( "SEQUENCE events not possible on AxInvoiceOrderLinesAgent");

        default:

            Assert.fail( "Unhandled event type");
        }
    }

    private void storeMessage( OrderLineDto dto, String agreementId)
    {
        orderLineDao.create( dto, agreementId);

        LOGGER.info(
            "***** Invoice order lines created: " + new Date() + " (" + dto.getEventType() + "), " +
                "entityId " + dto.getBatchId() + ", " +
                "orderLineId " + dto.getOrderLineId());
    }

    private void validateParam( ProducerParameter parameter)
    {
        if( parameter.getEventType()  == ProducerEventType.ORDER ||
            parameter.getEventType2() == ProducerEventType.ORDER)
        {
            throw new IllegalArgumentException( "ORDER events not possible on AxInvoiceOrderLinesAgent");
        }

        if( parameter.getEventType()  == ProducerEventType.SYNTAX ||
            parameter.getEventType2() == ProducerEventType.SYNTAX)
        {
            throw new IllegalArgumentException( "SYNTAX events not possible on AxInvoiceOrderLinesAgent");
        }

        if( parameter.getEventType()  == ProducerEventType.SEQUENCE ||
            parameter.getEventType2() == ProducerEventType.SEQUENCE)
        {
            throw new IllegalArgumentException( "SEQUENCE events not possible on AxInvoiceOrderLinesAgent");
        }
    }
}
