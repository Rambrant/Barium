package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import com.tradedoubler.billing.exception.ConsumerCreationFailedException;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class Consumer extends Agent
{
    private FailSequencer failSequencer;
    private Boolean       lastFailState;

    protected Consumer( ConsumerParameter parameter) throws ConsumerCreationFailedException
    {
        //
        // Create the controller that governs the state of the producer, Fail or not...
        //
        failSequencer = new FailSequencer( parameter);
    }

    //
    // This subclass method is called every time the failure state changes. The subclass uses this to
    // signal the input producer that it shall fail for the specified number of seconds
    //
    protected abstract void handleFailStatusChange( boolean failState);

    //
    // The thread run method that calls the subclass handleFailStatusChange methods at the specified interval
    //
    @Override
    public void execute()
    {
        //
        // Check the status of failure. Inform the subclass if it differs from the previous state
        //
        boolean failState = failSequencer.isInFailState();

        if( lastFailState == null || lastFailState != failState)
        {
            handleFailStatusChange( failState);
        }

        lastFailState = failState;
    }
}
