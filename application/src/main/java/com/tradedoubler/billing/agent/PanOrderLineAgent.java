package com.tradedoubler.billing.agent;

import com.google.gson.Gson;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dao.PanOrderLineDao;
import com.tradedoubler.billing.derby.dto.OrderLineDto;
import com.tradedoubler.billing.domain.generator.OrderLineBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.service.pan.BariumOrderLine;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.type.ProducerEventDbType;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanOrderLineAgent extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( PanOrderLineAgent.class);

    private PanOrderLineDao  orderLineDao;
    private OrderLineBuilder ruleBuilder;
    private Gson             gson;

    public PanOrderLineAgent(
        ProducerParameter parameter,
        OrderLineBuilder  orderLineBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter);

        validateParam( parameter);

        this.orderLineDao = new PanOrderLineDao();
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
        // Create the message according to the event type. We will "fix" the lines according to the event type
        // when we unpack this message on the Mule side
        //
        switch( event)
        {
        case NONE:
        case CREATE:
        case SYNTAX:
        case ILLEGAL:

            //
            // Create a new "meta" order line
            //
            line = ruleBuilder.buildOrderLine();

            //
            // Set it up for "storage"
            //
            dto.setBatchId( line.getBatchId());
            dto.setNumOrderLines( line.getNumberOfOrderLines());
            dto.setOrderLineId( line.getOrderLineId());
            dto.setJsonOrderLine( gson.toJson( line));
            dto.setEventType( event.asDbType());

            //
            // Check the validity
            //
            if( dto.getEventType() == ProducerEventDbType.ILLEGAL && dto.getNumOrderLines() < 2)
            {
                throw new IllegalArgumentException( "ILLEGAL events must have 2 or more order lines specified");
            }

            //
            // Store it
            //
            storeMessage( dto, line.getSystemAgreementId());

            break;

        case ORDER:

            throw new IllegalArgumentException( "ORDER events not possible on PanOrderLineAgent");

        case SEQUENCE:

            throw new IllegalArgumentException( "SEQUENCE events not possible on PanOrderLineAgent");

        default:

            Assert.fail( "Unhandled event type");
        }
    }

    private void storeMessage( OrderLineDto dto, String agreementId)
    {
        orderLineDao.create( dto, agreementId);

        LOGGER.info(
            "***** Order line created: " + new Date() + " (" + dto.getEventType() + "), " +
                "entityId " + dto.getBatchId() + ", " +
                "orderLineId " + dto.getOrderLineId());
    }

    private void validateParam( ProducerParameter parameter)
    {
        if( parameter.getEventType()  == ProducerEventType.ORDER ||
            parameter.getEventType2() == ProducerEventType.ORDER)
        {
            throw new IllegalArgumentException( "ORDER events not possible on PanOrderLineAgent");
        }

        if( parameter.getEventType()  == ProducerEventType.SEQUENCE ||
            parameter.getEventType2() == ProducerEventType.SEQUENCE)
        {
            throw new IllegalArgumentException( "SEQUENCE events not possible on PanOrderLineAgent");
        }
    }
}
