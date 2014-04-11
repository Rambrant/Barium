package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.service.ax.AxService;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class AxServiceFailAgentMain extends AgentMainBase
{
    @Test
    public void createInvoiceRuleTest() throws Exception
    {
        getServer().cleanDatabase();

        //
        // Setup the agent and start it
        //
        ConsumerParameter parameter = new ConsumerParameter();

        parameter.setFailDelay(    1L);
        parameter.setFailDuration( 2L);
        parameter.setFailSuspend(  1L);

        addAgent( new AxServiceFailAgent( parameter));
        execute( 4L);

        evaluateFailState( AxService.class.getSimpleName());
    }
}
