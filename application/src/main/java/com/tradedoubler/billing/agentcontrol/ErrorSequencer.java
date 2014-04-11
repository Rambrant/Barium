package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;

/**
 * @author Thomas Rambrant (thore)
 */

public class ErrorSequencer
{
    private ProducerParameter parameter;
    private long              errorBeat = 0L;

    public ErrorSequencer( ProducerParameter parameter)
    {
        validateParam( parameter);

        this.parameter = parameter;
    }

    //
    // Return the next error in the sequence.
    //
    public ProducerEventType getNextErrorInSequence()
    {
        ++errorBeat;

        ProducerEventType result = ProducerEventType.NONE;

        switch( parameter.getErrorIntervalType())
        {
        case UNIFORM:

            //
            // Every third or forth etc... should be an event...
            //
            if( errorBeat % parameter.getErrorInterval() == 0)
            {
                result = parameter.getEventType();
            }

            break;

        case SQUARE:

            //
            // Same type of calculation as the uniform pattern but for two alternating durations.
            // A complication encountered is the fact that we have to reset the progress to start over
            // when the second interval has passed.
            //
            long              interval = parameter.getErrorInterval();
            ProducerEventType event    = parameter.getEventType();

            if( errorBeat >  parameter.getErrorDuration() &&
                errorBeat <= (parameter.getErrorDuration() + parameter.getErrorDuration2()))
            {
                interval = parameter.getErrorInterval2();
                event    = parameter.getEventType2();
            }

            if( errorBeat % interval == 0)
            {
                result = event;
            }

            if( errorBeat >= (parameter.getErrorDuration() + parameter.getErrorDuration2()))
            {
                errorBeat = 0L;
            }

            break;
        }

        //
        // Was not "caught" above. This means there should be no error
        //
        return result;
    }

    private void validateParam( ProducerParameter parameter)
    {
        switch( parameter.getErrorIntervalType())
        {
        case UNIFORM:

            if( (null == parameter.getErrorInterval()) ||
                 parameter.getErrorInterval() < 1L)
            {
                throw new IllegalArgumentException( "illegal error interval specified");
            }

            break;

        case SQUARE:

            //
            // Check interval 1
            //
            if( (null == parameter.getErrorInterval()) ||
                 parameter.getErrorInterval() < 1L)
            {
                throw new IllegalArgumentException( "illegal error interval specified");
            }

            if( (null == parameter.getErrorDuration()) ||
                 parameter.getErrorDuration() < parameter.getErrorInterval())
            {
                throw new IllegalArgumentException( "illegal error duration specified");
            }

            if( parameter.getErrorDuration() % parameter.getErrorInterval() != 0)
            {
                throw new IllegalArgumentException(
                    "Error duration must be multiple of the error interval");
            }

            //
            // Check interval 2
            //
            if( (null == parameter.getErrorInterval2()) ||
                 parameter.getErrorInterval2() < 1L)
            {
                throw new IllegalArgumentException( "illegal second error interval specified");
            }

            if( (null == parameter.getErrorDuration2()) ||
                 parameter.getErrorDuration2() < parameter.getErrorInterval2())
            {
                throw new IllegalArgumentException( "illegal second error duration specified");
            }

            if( parameter.getErrorDuration2() % parameter.getErrorInterval2() != 0)
            {
                throw new IllegalArgumentException(
                    "Second error duration must be multiple of the second error interval");
            }

            break;
        }
    }
}
