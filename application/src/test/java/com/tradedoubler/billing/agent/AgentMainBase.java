package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentcontrol.Agent;
import com.tradedoubler.billing.agentcontrol.AgentController;
import com.tradedoubler.billing.derby.DerbyServer;
import com.tradedoubler.billing.derby.dao.CrmCreateInvoiceRuleDao;
import com.tradedoubler.billing.derby.dao.FailStateDao;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.derby.dto.FailStateDto;
import com.tradedoubler.billing.domain.generator.IdGenerator;
import com.tradedoubler.billing.domain.parameter.ExecutionParameter;
import com.tradedoubler.billing.domain.xml.XmlAttributeWrapper;
import com.tradedoubler.billing.type.ProducerEventDbType;
import com.tradedoubler.billing.util.ExecutionUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class AgentMainBase
{
    private static DerbyServer         server;
    private static AgentController     controller;
    private static XmlAttributeWrapper wrapper;

    private static IdGenerator         idGenerator;

    @BeforeClass
    public static void setUp() throws Exception
    {
        server      = ExecutionUtil.setupDatabase();

        controller  = new AgentController();
        wrapper     = new XmlAttributeWrapper();
        idGenerator = new IdGenerator();
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        ExecutionUtil.tearDownDatabase();
    }

    protected static DerbyServer getServer()
    {
        return server;
    }

    public static XmlAttributeWrapper getWrapper()
    {
        return wrapper;
    }

    public static IdGenerator getIdGenerator()
    {
        return idGenerator;
    }

    protected void addAgent( Agent agent)
    {
        controller.addAgent( agent);
    }

    protected void execute( long executionTime) throws Exception
    {
        ExecutionParameter parameter = new ExecutionParameter();

        parameter.setStartDelay( 0L);
        parameter.setExecutionTime( executionTime);
        parameter.setCoolDownTime( 0L);

        controller.execute( parameter);
    }

    protected void evaluateMessages( ProducerEventDbType event) throws Exception
    {
        List<CrmMessageDto> messageList = new CrmCreateInvoiceRuleDao().readAll();

        for( CrmMessageDto messageDto: messageList)
        {
            System.out.print( "message: " + messageDto.getMessageId() + " [" + messageDto.getEventType() + "]\n");

            Assert.assertEquals( "Wrong event type", event, messageDto.getEventType());
        }
    }

    protected void evaluateFailState( String serviceName) throws Exception
    {
        List< FailStateDto> failList = new FailStateDao().readAll();

        Assert.assertNotNull( failList);

        for( FailStateDto failDto: failList)
        {
            System.out.print( "Fail: " + failDto + "\n");

            Assert.assertEquals(  "Wrong service name", serviceName, failDto.getServiceName());
        }
    }
}
