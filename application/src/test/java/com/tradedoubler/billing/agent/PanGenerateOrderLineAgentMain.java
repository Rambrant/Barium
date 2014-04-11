package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.domain.generator.OrderLineBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.IntervalType;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.type.ProducerEventDbType;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanGenerateOrderLineAgentMain extends AgentMainBase
{
    @Test
    public void createOrderLineTest() throws Exception
    {
        getServer().cleanDatabase();

        //
        // Setup the agent and start it
        //
        ProducerParameter parameter = new ProducerParameter();

        parameter.setIntervalType(      IntervalType.UNIFORM);
        parameter.setInterval(          2L);
        parameter.setEventType(         ProducerEventType.NONE);
        parameter.setErrorIntervalType( IntervalType.UNIFORM);
        parameter.setErrorInterval(     1L);

        addAgent( new PanOrderLineAgent(
            parameter,
            new OrderLineBuilder( getWrapper(), getIdGenerator(), "Simple")));

        execute( 4L);

        evaluateMessages( ProducerEventDbType.NONE);
    }
}
