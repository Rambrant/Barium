package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.derby.dao.StartStopCommandDao;
import com.tradedoubler.billing.domain.parameter.ExecutionParameter;
import com.tradedoubler.billing.exception.AgentControllerCreationFailedException;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Thomas Rambrant (thore)
 */

public class AgentController
{
    public static final Logger LOGGER = Logger.getLogger( AgentController.class);

    private static StartStopCommandDao startStopDao;
    private static List< Agent>        agents;

    public AgentController()
    {
        try
        {
            startStopDao = new StartStopCommandDao();
            agents       = new ArrayList<Agent>();
        }
        catch( SQLException e)
        {
            throw new AgentControllerCreationFailedException( "Could not create the AgentController", e);
        }
    }

    public void addAgent( Agent agent)
    {
        agents.add( agent);
    }

    public List< Agent> getAgents()
    {
        return agents;
    }

    public void execute( ExecutionParameter parameters) throws Exception
    {
            //
            // Wait for the emulation to start the given number of seconds
            //
            LOGGER.info( "Delaying emulation " + parameters.getStartDelay() + " seconds [" + new Date() + "]");

            waitSeconds( parameters.getStartDelay());

            //
            // Prepare all agents by setting them up
            //
            for( Agent agent : agents)
            {
                agent.before();
            }

            //
            // Run the emulation the given number of seconds
            //
            LOGGER.info( "Running emulation " + parameters.getExecutionTime() + " seconds [" + new Date() + "]");

            executeLoop( parameters.getExecutionTime());

            //
            // Tear down all agents
            //
            for( Agent agent : agents)
            {
                agent.after();
            }

            //
            // Wait for the environment to process everything
            //
            LOGGER.info( "Cooling down " + parameters.getCoolDownTime() + " seconds [" + new Date() + "]");

            waitSeconds( parameters.getCoolDownTime());
    }

    private void executeLoop( Long executionTime) throws Exception
    {
        try
        {
            long waitUntil = System.currentTimeMillis() + (executionTime * 1000L);

            while( System.currentTimeMillis() < waitUntil)
            {
                long startTime = System.currentTimeMillis();

                for( Agent agent: agents)
                {
                    agent.execute();
                }

                long passedTime = 1000L - (System.currentTimeMillis() - startTime);

                if( passedTime > 0)
                {
                    Thread.sleep( passedTime);
                }
            }
        }
        catch( InterruptedException e)
        {
            //
            // Just continue
            //
        }
    }

    private void waitSeconds( Long waitTimeSec)
    {
        try
        {
            long waitUntil = System.currentTimeMillis() + (waitTimeSec * 1000L);
            long counter   = 0L;

            System.out.print( "                      ");

            long currentTime = System.currentTimeMillis();

            while( currentTime < waitUntil)
            {
                Thread.sleep( 1000L);

                //
                // This "dummy" read is there to keep the database connection to Derby alive. There is some sort
                // of bug...
                //
                startStopDao.readAll();

                ++counter;

                if( counter % 60 == 0)
                {
                    System.out.print( ".");
                }

                if( counter % 3600 == 0)
                {
                    System.out.print( "\n                      ");
                }

                currentTime = System.currentTimeMillis();
            }
        }
        catch( InterruptedException e)
        {
            throw new AgentControllerCreationFailedException( "Timeout in AgentController", e);
        }

        System.out.print( "\n");
    }
}
