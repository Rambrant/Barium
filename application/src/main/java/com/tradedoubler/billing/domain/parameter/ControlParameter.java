package com.tradedoubler.billing.domain.parameter;

/**
 * @author Thomas Rambrant (thore)
 */

public class ControlParameter extends Parameter
{
    //
    // Defines the time in seconds that we wait before we start
    //
    private Long    startDelay  = 0L;
    private Long    periodicity = 0L;
    private Boolean triggerAx   = true;

    public void setStartDelay( Long startDelay)
    {
        this.startDelay = startDelay;
    }

    public Long getStartDelay()
    {
        return startDelay;
    }

    public Long getPeriodicity()
    {
        return periodicity;
    }

    public void setPeriodicity( Long periodicity)
    {
        this.periodicity = periodicity;
    }

    public Boolean getTriggerAx()
    {
        return triggerAx;
    }

    public void setTriggerAx( Boolean triggerAx)
    {
        this.triggerAx = triggerAx;
    }
}
