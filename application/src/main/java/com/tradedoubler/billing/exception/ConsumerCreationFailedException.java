package com.tradedoubler.billing.exception;

/**
 * @author Thomas Rambrant (thore)
 */

public class ConsumerCreationFailedException extends Exception
{
    public ConsumerCreationFailedException( String message, Exception e)
    {
        super( message, e);
    }
}
