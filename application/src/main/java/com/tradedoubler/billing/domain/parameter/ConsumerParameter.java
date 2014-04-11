package com.tradedoubler.billing.domain.parameter;

/**
 * @author Thomas Rambrant (thore)
 */

public class ConsumerParameter extends Parameter
{
    //
    // The definitions of the sequence and duration of integration point failures. That is the times when the
    // surrounding environment fails for different reasons, eg. service down etc...
    //
    private Long failDelay     = 0L;
    private Long failDuration  = 0L;
    private Long failSuspend   = 0L;
    private Long failLimit;

    public Long getFailDelay()
    {
        return failDelay;
    }

    public void setFailDelay( Long failDelay)
    {
        this.failDelay = failDelay;
    }

    public Long getFailDuration()
    {
        return failDuration;
    }

    public void setFailDuration( Long failDuration)
    {
        this.failDuration = failDuration;
    }

    public Long getFailSuspend()
    {
        return failSuspend;
    }

    public void setFailSuspend( Long failSuspend)
    {
        this.failSuspend = failSuspend;
    }

    public Long getFailLimit()
    {
        return failLimit;
    }

    public void setFailLimit( Long failLimit)
    {
        this.failLimit = failLimit;
    }

    @Override
    public String toString()
    {
        return "ProducerParameter{" +
            "\tailDelay          = " + failDelay         + "\n" +
            "\tailDuration       = " + failDuration      + "\n" +
            "\tailSuspend        = " + failSuspend       + "\n" +
            "\tailLimit          = " + failLimit         + "\n" +
            '}';
    }
}
