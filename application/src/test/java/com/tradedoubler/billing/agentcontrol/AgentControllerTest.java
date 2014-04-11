package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.derby.DerbyServer;
import com.tradedoubler.billing.domain.parameter.ExecutionParameter;
import com.tradedoubler.billing.util.ExecutionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class AgentControllerTest
{
    public class TestAgent extends Agent
    {
        private long waitTime;
        private long beforeTime;
        private long afterTime;
        private long executeNum = 0L;

        public TestAgent( long waitTime)
        {
            this.waitTime = waitTime;
        }

        public long getExecuteNum() { return executeNum; }

        public long getBeforeTime() { return beforeTime; }

        public long getAfterTime() { return afterTime; }

        @Override
        public String getAgentName() { return TestAgent.class.getSimpleName(); }

        @Override
        public void before() { beforeTime = System.currentTimeMillis(); }

        @Override
        public void after() { afterTime = System.currentTimeMillis(); }

        @Override
        public void execute()
        {
            try { ++executeNum; Thread.sleep( waitTime);} catch( InterruptedException e) { }
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
    public void testOneAgent() throws Exception
    {
        ExecutionParameter parameter = new ExecutionParameter();

        parameter.setStartDelay(    2L);
        parameter.setExecutionTime( 4L);
        parameter.setCoolDownTime(  2L);

        AgentController controller = new AgentController();

        TestAgent agent = new TestAgent( 175);

        controller.addAgent( agent);

        long startTime = System.currentTimeMillis();

        controller.execute( parameter);

        long stopTime = System.currentTimeMillis();

        Assert.assertTrue( closeEnough( startTime, 2L, agent.getBeforeTime(), 100L));
        Assert.assertTrue( closeEnough( startTime, 6L, agent.getAfterTime(), 100L));
        Assert.assertTrue( closeEnough( startTime, 8L, stopTime, 100L));
    }

    @Test
    public void testTenAgents() throws Exception
    {
        ExecutionParameter parameter = new ExecutionParameter();

        parameter.setStartDelay(    2L);
        parameter.setExecutionTime( 4L);
        parameter.setCoolDownTime(  2L);

        AgentController controller = new AgentController();

        TestAgent agent = new TestAgent( 175);

        controller.addAgent( agent);
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));
        controller.addAgent( new TestAgent( 175));

        long startTime = System.currentTimeMillis();

        controller.execute( parameter);

        long stopTime = System.currentTimeMillis();

        Assert.assertTrue( closeEnough( startTime, 2L, agent.getBeforeTime(), 100L));
        Assert.assertTrue( closeEnough( startTime, 6L, agent.getAfterTime(), 1500L));
        Assert.assertTrue( closeEnough( startTime, 8L, stopTime, 1500L));
    }

    private boolean closeEnough( long startTime, long offset, long agentTime, long tolerance)
    {
        long diffTime = Math.abs((startTime + (offset * 1000L)) - agentTime);

        return diffTime < tolerance;
    }
}
