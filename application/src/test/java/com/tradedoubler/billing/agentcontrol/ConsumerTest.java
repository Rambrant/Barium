package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.derby.DerbyServer;
import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.domain.parameter.ExecutionParameter;
import com.tradedoubler.billing.exception.ConsumerCreationFailedException;
import com.tradedoubler.billing.util.ExecutionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class ConsumerTest
{
    class TestConsumer extends Consumer
    {
        private long lastFailTime;
        private long failDuration;

        protected TestConsumer( ConsumerParameter parameter) throws ConsumerCreationFailedException
        {
            super( parameter);
        }

        @Override
        protected void before() throws Exception
        {
            System.out.print( "Time: " + new Date() + "\tbefore\n");
        }

        @Override
        protected void after() throws Exception
        {
            System.out.print( "Time: " + new Date() + "\tafter\n");
        }

        public long getFailDuration()
        {
            return (failDuration + 500L) / 1000L;
        }

        @Override
        protected void handleFailStatusChange( boolean failState)
        {
            long currTime = System.currentTimeMillis();

            System.out.print(
                "Time: " + new Date( currTime) +
                    "\tstate: " + failState + "\n");

            if( failState)
            {
                lastFailTime = currTime;
            }
            else
            {
                failDuration = currTime - lastFailTime;
            }
        }
    }

    @Before
    public void setUp() throws Exception
    {
        DerbyServer server = ExecutionUtil.setupDatabase();
        server.cleanDatabase();
    }

    @After
    public void tearDown() throws Exception
    {
        ExecutionUtil.tearDownDatabase();
    }

    @Test
    public void testConsumer() throws Exception
    {
        //
        // Execution parameters
        //
        ExecutionParameter executionParameter = new ExecutionParameter();

        executionParameter.setStartDelay(    0L);
        executionParameter.setExecutionTime( 8L);
        executionParameter.setCoolDownTime(  0L);

        //
        // Do not change these parameters. They are what they are so that the test code in the TestConsumer can
        // be made as simple as possible. The different intervals are thoroughly tested by their own test suites
        //
        ConsumerParameter ConsumerParameter = new ConsumerParameter();

        ConsumerParameter.setFailDelay(    2L);
        ConsumerParameter.setFailDuration( 2L);
        ConsumerParameter.setFailSuspend(  1L);

        TestConsumer consumer = new TestConsumer( ConsumerParameter);

        AgentController controller = new AgentController();

        controller.addAgent( consumer);
        controller.execute( executionParameter);

        Assert.assertEquals( consumer.getAgentName(), TestConsumer.class.getSimpleName());
        Assert.assertEquals( ConsumerParameter.getFailDuration().longValue(), consumer.getFailDuration());
    }
}
