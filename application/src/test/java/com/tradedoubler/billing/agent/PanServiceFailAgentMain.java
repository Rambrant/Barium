package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.service.pan.PanService;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanServiceFailAgentMain extends AgentMainBase
{
    @Test
    public void serviceFailTest() throws Exception
    {
        getServer().cleanDatabase();

        //
        // Setup the agent and start it
        //
        ConsumerParameter parameter = new ConsumerParameter();

        parameter.setFailDelay(    1L);
        parameter.setFailDuration( 2L);
        parameter.setFailSuspend(  1L);

        addAgent( new PanServiceFailAgent( parameter));

        execute( 4L);

        evaluateFailState( PanService.class.getSimpleName());
    }
}
