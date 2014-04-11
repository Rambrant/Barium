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

public class CrmUpdateInvoiceRuleAgentMain extends AgentMainBase
{
    @Test
    public void updateInvoiceRuleTest() throws Exception
    {
        getServer().cleanDatabase();

        //
        // Setup the agent and start it
        //
        ProducerParameter createParameter = new ProducerParameter();
        createParameter.setEventType( ProducerEventType.NONE);

        ProducerParameter parameter = new ProducerParameter();
        parameter.setIntervalType(      IntervalType.UNIFORM);
        parameter.setInterval(          2L);
        parameter.setEventType(         ProducerEventType.SYNTAX);
        parameter.setErrorIntervalType( IntervalType.UNIFORM);
        parameter.setErrorInterval(     1L);

        CrmCreateInvoiceRuleAgent createAgent = new CrmCreateInvoiceRuleAgent(
            createParameter,
            new InvoicingRuleBuilder( getWrapper(), getIdGenerator(), "Simple"));

        addAgent( new CrmUpdateInvoiceRuleAgent(
            parameter,
            createAgent,
            new InvoicingRuleBuilder( getWrapper(), getIdGenerator(), "Simple")));

        execute(  4L);

        evaluateMessages( ProducerEventDbType.SYNTAX);
    }
}
