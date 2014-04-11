package com.tradedoubler.billing.domain.type;

import com.tradedoubler.billing.type.ProducerEventDbType;

/**
 * @author Thomas Rambrant (thore)
 */

public enum ProducerEventType implements Type< ProducerEventType, Long>
{
    //
    // No error.
    //
    NONE( 0L),

    //
    // We want an event with normal behavior (No error) but with a create semantic
    //
    CREATE( 1L),

    //
    // There is an ordering error. Eg and update comes before the create.
    // This error can only be set on update producers.
    //
    ORDER( 2L),

    //
    // The sequence of the data is wrong. Old changes of the same entity comes after a newer create or update
    //
    SEQUENCE( 3L),

    //
    // There is a syntax error in the data
    //
    SYNTAX( 4L),

    //
    // The data is illegal. Eg it tries to create the same entity twice or in case of an update, non existing entity
    //
    ILLEGAL( 5L);


    private Long id;

    ProducerEventType( Long id)
    {
        this.id = id;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public boolean isAnyOf( ProducerEventType... types)
    {
        return new TypeImpl< ProducerEventType, Long>().isAnyOf( this, types);
    }

    public static ProducerEventType getById( Long statusId)
    {
        return new TypeImpl< ProducerEventType, Long>().getById( statusId, values());
    }

    public ProducerEventDbType asDbType()
    {
        switch( ProducerEventType.getById( id))
        {
        case NONE:

            return ProducerEventDbType.NONE;

        case CREATE:

            return ProducerEventDbType.CREATE;

        case ORDER:

            return ProducerEventDbType.ORDER;

        case SEQUENCE:

            return ProducerEventDbType.SEQUENCE;

        case SYNTAX:

            return ProducerEventDbType.SYNTAX;

        case ILLEGAL:

            return ProducerEventDbType.ILLEGAL;
        }

        return null;
    }
}
