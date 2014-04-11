package com.tradedoubler.billing.exception;

/**
 * @author Thomas Rambrant (thore)
 */

public class XmlFileDataException extends RuntimeException
{
    public XmlFileDataException( String message)
    {
        super( message);
    }

    public XmlFileDataException( String message, Throwable e)
    {
        super( message, e);
    }
}
