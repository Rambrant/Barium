package com.tradedoubler.billing.domain.type;

/**
 * @author Thomas Rambrant (thore)
 */

public enum IntervalType implements Type< IntervalType, Long>
{
    //
    // No interval at all. Nothing happens
    //
    NONE( 0L),

    //
    // The interval pattern is to be every second or every third etc... in a uniform beat
    //
    UNIFORM( 1L),

    //
    // Can best be described as two alternating uniform patterns with a controlled duration for both.
    //
    SQUARE( 3L);


    private Long id;

    IntervalType( Long id)
    {
        this.id = id;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public boolean isAnyOf( IntervalType... types)
    {
        return new TypeImpl< IntervalType, Long>().isAnyOf( this, types);
    }

    public static IntervalType getById( Long statusId)
    {
        return new TypeImpl< IntervalType, Long>().getById( statusId, values());
    }
}
