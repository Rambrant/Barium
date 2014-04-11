package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.derby.DerbyServer;
import com.tradedoubler.billing.domain.parameter.ExecutionParameter;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.IntervalType;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.util.ExecutionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public class ProducerTest
{
    class TestProducer extends Producer
    {
        private long beatCount     = 0L;
        private long beatInterval;
        private long lastTime;
        private long lastErrorBeat = 0L;
        private long errorInterval;

        protected TestProducer( ProducerParameter parameter) throws ProducerCreationFailedException
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

        public long getErrorInterval()
        {
            return errorInterval;
        }

        public long getBeatInterval()
        {
            return (beatInterval + 500L) / 1000L;   //Rounding...
        }

        @Override
        protected void executeBeat( ProducerEventType currentEvent)
        {
            long currTime = System.currentTimeMillis();

            System.out.print( "Time: " + new Date( currTime) + "\terror: " + currentEvent + "\n");

            ++beatCount;

            beatInterval = currTime - lastTime;
            lastTime     = currTime;

            if( ! currentEvent.equals( ProducerEventType.NONE))
            {
                errorInterval = beatCount - lastErrorBeat;
                lastErrorBeat = beatCount;
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
    public void testProducer() throws Exception
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
        ProducerParameter producerParameter = new ProducerParameter();

        producerParameter.setIntervalType(      IntervalType.UNIFORM);
        producerParameter.setInterval(          2L);
        producerParameter.setEventType(         ProducerEventType.SYNTAX);
        producerParameter.setErrorIntervalType( IntervalType.UNIFORM);
        producerParameter.setErrorInterval(     3L);

        TestProducer producer = new TestProducer( producerParameter);

        AgentController controller = new AgentController();

        controller.addAgent( producer);
        controller.execute( executionParameter);

        Assert.assertEquals( producer.getAgentName(), TestProducer.class.getSimpleName());
        Assert.assertEquals( producerParameter.getInterval().longValue(), producer.getBeatInterval());
        Assert.assertEquals( producerParameter.getErrorInterval().longValue(), producer.getErrorInterval());
    }
}
