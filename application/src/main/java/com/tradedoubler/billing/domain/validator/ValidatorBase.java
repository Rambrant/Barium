package com.tradedoubler.billing.domain.validator;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

/**
 * @author Thomas Rambrant (thore)
 */

public class ValidatorBase
{
    protected static final Logger LOGGER  = Logger.getLogger( ValidatorBase.class);

    //
    // The base time we use to limit the validation to the data created since the test started.
    //
    private static Timestamp baseTime;

    public static Timestamp getBaseTime()
    {
        return baseTime;
    }

    public static void setBaseTime( Timestamp baseTime)
    {
        ValidatorBase.baseTime = baseTime;
    }

    public static void setBaseTimeToCurrentTime()
    {
        ValidatorBase.baseTime = new Timestamp( System.currentTimeMillis());
    }

    protected boolean validateTrue( String message, boolean condition)
    {
        if( ! condition)
        {
            printFail( message);
        }

        return condition;
    }

    protected boolean validateFalse( String message, boolean condition)
    {
        if( condition)
        {
            printFail( message);
        }

        return condition;
    }

    protected void printFail( String message)
    {
        LOGGER.error( message);
    }
}
