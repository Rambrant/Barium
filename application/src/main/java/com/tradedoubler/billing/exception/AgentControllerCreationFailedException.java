package com.tradedoubler.billing.exception;

/**
 * @author Thomas Rambrant (thore)
 */

public class AgentControllerCreationFailedException extends RuntimeException
{
    public AgentControllerCreationFailedException( String message, Exception e)
    {
        super( message, e);
    }
}
