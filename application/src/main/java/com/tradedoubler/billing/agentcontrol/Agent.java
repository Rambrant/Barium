package com.tradedoubler.billing.agentcontrol;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class Agent
{
    //
    // The subclass method is called when the framework "starts" the agent. Use it to initialize used resources
    //
    protected abstract void before() throws Exception;

    //
    // The subclass method is called when the framework "stops" the agent. Use it to release used resources
    //
    protected abstract void after() throws Exception;

    //
    // This subclass method is called every second... (or as close as possible)
    // This is the place for periodic tasks such as generating input
    //
    public abstract void execute() throws Exception;


    //
    // Returns the name of the Agent
    //
    public String getAgentName()
    {
        return this.getClass().getSimpleName();
    }

}
