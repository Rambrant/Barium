package com.tradedoubler.billing.oracle.dao;

import org.apache.log4j.Logger;

import java.sql.Connection;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class SimpleDao
{
    protected Logger LOGGER = null;

    static protected Connection connection = null;

    @SuppressWarnings( "unchecked")
    public SimpleDao()
    {
        LOGGER = Logger.getLogger( getClass());
    }

    public static Connection getConnection()
    {
        if( connection == null)
        {
            throw new RuntimeException( "No DB connection found");
        }

        return connection;
    }

    public static void setConnection( Connection aConnection)
    {
        connection = aConnection;
    }
}