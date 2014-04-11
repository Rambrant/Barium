package com.tradedoubler.billing.domain.parameter;

/**
 * @author Thomas Rambrant (thore)
 */

public class ExecutionParameter extends Parameter
{
    //
    // Defines the time we wait before we create output, how long we execute the test and for how long we wait
    // for Mule to get its job done before we end the execution phase
    //
    private Long startDelay    = 0L;
    private Long executionTime;
    private Long coolDownTime;

    public Long getStartDelay()
    {
        return startDelay;
    }

    public Long getExecutionTime()
    {
        return executionTime;
    }

    public Long getCoolDownTime()
    {
        return coolDownTime;
    }

    public void setStartDelay( Long startDelay)
    {
        this.startDelay = startDelay;
    }

    public void setExecutionTime( Long executionTime)
    {
        this.executionTime = executionTime;
    }

    public void setCoolDownTime( Long coolDownTime)
    {
        this.coolDownTime = coolDownTime;
    }
}
