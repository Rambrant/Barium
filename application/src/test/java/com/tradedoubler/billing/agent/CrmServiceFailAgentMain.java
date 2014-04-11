package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.service.crm.CrmService;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class CrmServiceFailAgentMain extends AgentMainBase
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

        addAgent( new CrmServiceFailAgent( parameter));
        execute( 4L);

        evaluateFailState( CrmService.class.getSimpleName());
    }
}
