package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class Producer extends Agent
{
    private HeartBeat      heartBeat;
    private ErrorSequencer errorSequencer;

    //
    // Creates a new input data Producer according to the given setup parameter
    //
    protected Producer( ProducerParameter parameter) throws ProducerCreationFailedException
    {
        //
        // Create a new heartbeat that controls when some input should be produced.
        //
        heartBeat = new HeartBeat( parameter);

        //
        // Create the controller that decides the type of error to produce (if any)
        //
        errorSequencer = new ErrorSequencer( parameter);
    }

    //
    // This subclass method is called every time the heartbeat kicks in... This is the place for the
    // production of the input
    //
    abstract protected void executeBeat( ProducerEventType currentEvent);

    //
    // The method that is called by the AgentController at second interval.
    // It handles the call to the subclass executeBeat and handleFailStatusChange
    //
    @Override
    public void execute()
    {
        //
        // is it time for a new beat?
        //
        if( heartBeat.isTimeForBeat())
        {
            executeBeat( errorSequencer.getNextErrorInSequence());
        }
    }
}
