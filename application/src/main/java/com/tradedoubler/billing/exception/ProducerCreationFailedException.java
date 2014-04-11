package com.tradedoubler.billing.exception;

/**
 * @author Thomas Rambrant (thore)
 */

public class ProducerCreationFailedException extends Exception
{
    public ProducerCreationFailedException( String message, Exception e)
    {
        super( message, e);
    }

    public ProducerCreationFailedException( String message)
    {
        super( message);
    }
}
