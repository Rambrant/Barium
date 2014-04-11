package com.tradedoubler.billing.exception;

/**
 * @author Thomas Rambrant (thore)
 */

public class AzureQueueFailedException extends RuntimeException
{
    public AzureQueueFailedException( String message, Exception e)
    {
        super( message, e);
    }
}
