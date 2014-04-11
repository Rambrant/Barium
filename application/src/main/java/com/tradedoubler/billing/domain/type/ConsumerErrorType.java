package com.tradedoubler.billing.domain.type;

/**
 * @author Thomas Rambrant (thore)
 */

public enum ConsumerErrorType implements Type<ConsumerErrorType, Long>
{
    NONE(    0L),
    FAIL(    1L),
    FORMAT(  2L),
    ILLEGAL( 3L);

    private Long id;

    ConsumerErrorType( Long id)
    {
        this.id = id;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public boolean isAnyOf( ConsumerErrorType... types)
    {
        return new TypeImpl<ConsumerErrorType, Long>().isAnyOf( this, types);
    }

    public static ConsumerErrorType getById( Long statusId)
    {
        return new TypeImpl<ConsumerErrorType, Long>().getById( statusId, values());
    }
}
