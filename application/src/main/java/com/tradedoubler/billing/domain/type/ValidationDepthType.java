package com.tradedoubler.billing.domain.type;

/**
 * @author Thomas Rambrant (thore)
 */

public enum ValidationDepthType implements Type< ValidationDepthType, Long>
{
    //
    // A Shallow scan. We will only go through the input data to the flows and check that all data has been
    // handled as they should
    //
    SHALLOW( 0L),

    //
    // A more thorough scan where we try to match all known data to see if something has gone wrong
    //
    DEEP( 1L);


    private Long id;

    ValidationDepthType( Long id)
    {
        this.id = id;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public boolean isAnyOf( ValidationDepthType... types)
    {
        return new TypeImpl< ValidationDepthType, Long>().isAnyOf( this, types);
    }

    public static ValidationDepthType getById( Long statusId)
    {
        return new TypeImpl< ValidationDepthType, Long>().getById( statusId, values());
    }
}
