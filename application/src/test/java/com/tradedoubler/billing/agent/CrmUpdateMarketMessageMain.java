package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.IntervalType;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.type.ProducerEventDbType;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmUpdateMarketMessageMain extends AgentMainBase
{
    @Test
    public void updateMarketMessageTest() throws Exception
    {
        getServer().cleanDatabase();

        //
        // Setup the agent and start it
        //
        ProducerParameter parameter = new ProducerParameter();

        parameter.setIntervalType(      IntervalType.UNIFORM);
        parameter.setInterval(          2L);
        parameter.setEventType(         ProducerEventType.SYNTAX);
        parameter.setErrorIntervalType( IntervalType.UNIFORM);
        parameter.setErrorInterval(     1L);

        addAgent( new CrmUpdateMarketMessageAgent(
            parameter,
            new InvoicingRuleBuilder( getWrapper(), getIdGenerator(), "Simple")));

        execute( 4L);

        evaluateMessages( ProducerEventDbType.SYNTAX);
    }
}
