package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;

/**
 * @author Thomas Rambrant (thore)
 */

public class FailSequencer
{
    //
    // The parameters that controls the beat frequency
    //
    private ConsumerParameter parameter;
    private long              failCount = 0L;

    //
    // The first time we asked for a fail status. Used together with the fail limit to
    // control the fail output
    //
    private long startTime = 0L;

    //
    // The last time we entered a controller period
    //
    private long baseTime = 0L;


    public FailSequencer( ConsumerParameter parameter)
    {
        validateParam( parameter);

        this.parameter = parameter;
    }

    public boolean isInFailState()
    {
        //
        // Check the time that has passed against the limit. Stop the production of fails if we pass the given limit
        //
        if( parameter.getFailLimit() != null &&  parameter.getFailLimit() <= failCount)
        {
            return false;
        }

        //
        // The different areas of failure and normal executions are
        // Delay   : a first period before a failure (normal execution)
        // Duration: this is the period where the producer should fail (failed state)
        // Suspend : the third period, a wait state before we start it all over again (normal execution)
        //
        boolean result = false;

        ++failCount;

        if( failCount >= parameter.getFailDelay() + parameter.getFailDuration() + parameter.getFailSuspend())
        {
            failCount = 0L;
        }

        if( failCount <= parameter.getFailDelay() + parameter.getFailDuration() &&
            failCount > parameter.getFailDelay())
        {
            result = true;
        }

        return result;
    }

    private void validateParam( ConsumerParameter parameter)
    {
        if(( parameter.getFailDelay()   != null && parameter.getFailDelay()   != 0) ||
           ( parameter.getFailSuspend() != null && parameter.getFailSuspend() != 0))
        {
            if( parameter.getFailDuration() == null || parameter.getFailDuration() == 0)
            {
                throw new IllegalArgumentException(
                    "Failure state must have a duration if a delay or suspend period is given");
            }
        }
    }

}
