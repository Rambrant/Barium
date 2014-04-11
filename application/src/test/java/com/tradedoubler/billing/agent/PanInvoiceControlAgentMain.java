package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentcontrol.Agent;
import com.tradedoubler.billing.derby.dao.InvoicingCommandDao;
import com.tradedoubler.billing.domain.parameter.ControlParameter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class PanInvoiceControlAgentMain extends AgentMainBase
{
    @Test( expected = RuntimeException.class)
    public void controlTest() throws Exception
    {
        getServer().cleanDatabase();

        InvoicingCommandDao commandDao = new InvoicingCommandDao();

        //
        // Setup the agent and start it
        //
        ControlParameter parameter = new ControlParameter();

        parameter.setStartDelay(  1L);
        parameter.setPeriodicity( 2L);

        Agent agent = new PanInvoiceControlAgent( parameter);

        //
        // Execute one tick. This will not start the invoicing
        //
        agent.execute();

        Assert.assertTrue( commandDao.isStopped());

        //
        // Execute one more tick. This will take the status to started
        //
        agent.execute();

        Assert.assertFalse( commandDao.isStopped());

        //
        // Advance the state to another invoice start. We have not reset the status to stopped so it will fail
        //
        agent.execute();
        agent.execute();
        agent.execute();
    }
}
