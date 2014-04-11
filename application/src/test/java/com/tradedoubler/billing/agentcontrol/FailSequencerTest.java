package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ConsumerParameter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class FailSequencerTest
{
    @Test
    public void testTimeLeftForFail() throws Exception
    {
        ConsumerParameter parameter = new ConsumerParameter();

        parameter.setFailDelay(    2L);
        parameter.setFailDuration( 3L);
        parameter.setFailSuspend(  1L);

        FailSequencer fail = new FailSequencer( parameter);

        long progress = 0L;

        for( int i = 1; i < 20; ++i)
        {
            if( progress >= parameter.getFailDelay() + parameter.getFailDuration() + parameter.getFailSuspend())
            {
                progress = 0L;
            }

            ++progress;

            boolean isFail = fail.isInFailState();

            System.out.print( "\n\tprogress: " + progress);

            if( isFail)
            {
                System.out.print( "\tFAIL");

                Assert.assertTrue(
                    "Wrong state: " + progress,
                    progress >  parameter.getFailDelay() &&
                    progress <= parameter.getFailDelay() + parameter.getFailDuration());
            }
            else
            {
                Assert.assertTrue(
                    "Wrong state: " + progress,
                    progress <= parameter.getFailDelay() ||
                    progress >  parameter.getFailDelay() + parameter.getFailDuration());
            }
        }
    }

    @Test
    public void testTimeLeftForFailWithLimit() throws Exception
    {
        ConsumerParameter parameter = new ConsumerParameter();

        parameter.setFailDelay(    1L);
        parameter.setFailDuration( 100L);
        parameter.setFailSuspend(  2L);
        parameter.setFailLimit(    4L);

        FailSequencer fail = new FailSequencer( parameter);

        long progress  = 0L;
        long failLimit = 0L;

        for( int i = 1; i < 10; ++i)
        {
            if( progress >= parameter.getFailDelay() + parameter.getFailDuration() + parameter.getFailSuspend())
            {
                progress = 0L;
            }

            ++progress;

            boolean isFail = fail.isInFailState();

            System.out.print( "\n\tprogress: " + progress);

            if( isFail)
            {
                failLimit = i;

                System.out.print( "\tFAIL");
            }
        }

        Assert.assertTrue(
            "Fail should have been stopped at: " + parameter.getFailLimit() + " beats",
                failLimit <=  parameter.getFailLimit());
    }
}
