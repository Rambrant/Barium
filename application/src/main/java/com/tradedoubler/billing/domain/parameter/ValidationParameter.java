package com.tradedoubler.billing.domain.parameter;

import com.tradedoubler.billing.domain.type.ValidationDepthType;

/**
 * @author Thomas Rambrant (thore)
 */

public class ValidationParameter extends Parameter
{
    private ValidationDepthType depth = ValidationDepthType.SHALLOW;

    public ValidationDepthType getDepth()
    {
        return depth;
    }

    public void setDepth( ValidationDepthType depth)
    {
        this.depth = depth;
    }

    @Override
    public String toString()
    {
        return "ProducerParameter{" +
            "\tdepth = " + depth + "\n" +
            '}';
    }
}
