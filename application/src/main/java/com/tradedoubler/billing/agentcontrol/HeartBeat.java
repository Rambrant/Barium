package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ProducerParameter;

/**
 * @author Thomas Rambrant (thore)
 */

public class HeartBeat
{
    //
    // The parameters that controls the beat frequency
    //
    private ProducerParameter parameter;
    private long              beatCount = 0L;

    public HeartBeat( ProducerParameter parameter)
    {
        validateParam( parameter);

        this.parameter = parameter;
    }

    //
    // The method returns true if its time to perform a beat, false otherwise...
    //
    public boolean isTimeForBeat()
    {
        boolean result = false;

        ++beatCount;


        if( parameter.getTimeLimit() != null && parameter.getTimeLimit() < beatCount)
        {
            return false;
        }

        switch( parameter.getIntervalType())
        {
        case UNIFORM:

            //
            // Every third or forth etc... should be an event...
            //
            if( beatCount % parameter.getInterval() == 0)
            {
                result = true;
            }

            break;

        case SQUARE:

            //
            // Same type of calculation as the uniform pattern but for two alternating durations.
            // A complication encountered is the fact that we have to reset the progress to start over
            // when the second interval has passed.
            //
            long interval = parameter.getInterval();

            if( beatCount >  parameter.getDuration() &&
                beatCount <= (parameter.getDuration() + parameter.getDuration2()))
            {
                interval = parameter.getInterval2();
            }

            if( beatCount % interval == 0)
            {
                result = true;
            }

            if( beatCount >= (parameter.getDuration() + parameter.getDuration2()))
            {
                beatCount = 0L;
            }

            break;
        }

        return result;
    }

    private void validateParam( ProducerParameter parameter)
    {
        switch( parameter.getIntervalType())
        {
        case UNIFORM:

            if(( null == parameter.getInterval()) ||
                 parameter.getInterval() < 1L)
            {
                throw new IllegalArgumentException( "illegal or missing interval");
            }

            break;

        case SQUARE:

            //
            // Check interval 1
            //
            if(( null == parameter.getInterval()) ||
                 parameter.getInterval() < 1L)
            {
                throw new IllegalArgumentException( "illegal or missing interval");
            }

            if(( null == parameter.getDuration()) ||
                 parameter.getDuration() < parameter.getInterval())
            {
                throw new IllegalArgumentException( "illegal or missing duration");
            }

            if( parameter.getDuration() % parameter.getInterval() != 0)
            {
                throw new IllegalArgumentException( "Duration must be multiple of the interval");
            }

            //
            // Check interval 2
            //
            if(( null == parameter.getInterval2()) ||
                 parameter.getInterval2() < 1L)
            {
                throw new IllegalArgumentException( "illegal or missing second interval");
            }

            if(( null == parameter.getDuration2()) ||
                 parameter.getDuration2() < parameter.getInterval2())
            {
                throw new IllegalArgumentException( "illegal or missing second duration");
            }

            if( parameter.getDuration2() % parameter.getInterval2() != 0)
            {
                throw new IllegalArgumentException( "Duration (2) must be multiple of the interval");
            }
            break;
        }
    }
}
