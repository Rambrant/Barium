package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.IntervalType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */

public class HeartBeatTest
{
    @Test
    public void testIsTimeForBeatUniform() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        long wantedInterval = 3L;

        parameter.setIntervalType( IntervalType.UNIFORM);
        parameter.setInterval(     wantedInterval);

        HeartBeat beat = new HeartBeat( parameter);

        for( int i = 1; i < 16; ++i)
        {
            if( beat.isTimeForBeat())
            {
                System.out.print( "progress: " + i + "\n");

                Assert.assertTrue(
                    "Wrong interval: (" + i + ") " + wantedInterval,
                    (i % wantedInterval == 0));
            }
        }
    }

    @Test
    public void testIsTimeForBeatUniformAgainstLimit() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        parameter.setIntervalType( IntervalType.UNIFORM);
        parameter.setInterval(     2L);
        parameter.setTimeLimit(    4L);

        HeartBeat beat = new HeartBeat( parameter);

        int progress = 0;

        for( int i = 1; i < 16; ++i)
        {

            if( beat.isTimeForBeat())
            {
                ++progress;
                System.out.print( "beat: " + i + "\n");
            }
        }

        System.out.print( "limit: " + parameter.getTimeLimit());

        Assert.assertTrue(
            "Wrong limit: (" + progress + ") " + parameter.getTimeLimit(),
            progress <= parameter.getTimeLimit());
    }

    @Test
    public void testIsTimeForBeatSquare() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        long wantedInterval1 = 3L;
        long duration1       = 6L;
        long wantedInterval2 = 2L;
        long duration2       = 6L;

        parameter.setIntervalType( IntervalType.SQUARE);
        parameter.setInterval(     wantedInterval1);
        parameter.setDuration(     duration1);
        parameter.setInterval2(    wantedInterval2);
        parameter.setDuration2(    duration2);

        HeartBeat beat = new HeartBeat( parameter);

        int lastSec  = 0;
        int progress = 0;

        for( int i = 1; i < 100; ++i)
        {
            ++progress;

            if( beat.isTimeForBeat())
            {
                System.out.print( "\nprogress: " + progress + "\tdiff " + (i-lastSec));

                if( progress > duration1 && progress <= (duration1 + duration2))
                {
                    Assert.assertTrue(
                        "Wrong interval: (" + i + "-" + lastSec + "=" + (i-lastSec) + ") " + wantedInterval2,
                        ((i-lastSec) == wantedInterval2));
                }
                else
                {
                    Assert.assertTrue(
                        "Wrong interval: (" + i + "-" + lastSec + "=" + (i-lastSec) + ") " + wantedInterval1,
                        ((i-lastSec) == wantedInterval1));
                }

                if( progress >= (duration1 + duration2))
                {
                    progress = 0;

                    System.out.print( "\t(repeat)");
                }

                lastSec = i;
            }
        }

        System.out.print( "\n");
        System.out.flush();
    }

    @Test
    public void testIsTimeForBeatSquareAgainstLimit() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        long wantedInterval1 = 2L;
        long duration1       = 4L;
        long wantedInterval2 = 1L;
        long duration2       = 6L;

        parameter.setIntervalType( IntervalType.SQUARE);
        parameter.setTimeLimit(    5L);
        parameter.setInterval(     wantedInterval1);
        parameter.setDuration(     duration1);
        parameter.setInterval2(    wantedInterval2);
        parameter.setDuration2(    duration2);

        HeartBeat beat = new HeartBeat( parameter);

        int lastSec  = 0;
        int progress = 0;
        int limit    = 0;

        for( int i = 1; i < 100; ++i)
        {
            ++progress;

            if( beat.isTimeForBeat())
            {
                limit = progress;

                System.out.print( "\nprogress: " + progress + "\tdiff " + (i-lastSec));

                if( progress > duration1 && progress <= (duration1 + duration2))
                {
                    Assert.assertTrue(
                        "Wrong interval: (" + i + "-" + lastSec + "=" + (i-lastSec) + ") " + wantedInterval2,
                        ((i-lastSec) == wantedInterval2));
                }
                else
                {
                    Assert.assertTrue(
                        "Wrong interval: (" + i + "-" + lastSec + "=" + (i-lastSec) + ") " + wantedInterval1,
                        ((i-lastSec) == wantedInterval1));
                }

                if( progress >= (duration1 + duration2))
                {
                    progress = 0;

                    System.out.print( "\t(repeat)");
                }

                lastSec = i;
            }
        }

        System.out.print( "\nlimit: " + parameter.getTimeLimit());

        Assert.assertTrue(
            "Wrong limit: (" + limit + ") " + parameter.getTimeLimit(),
            limit <= parameter.getTimeLimit());
    }
}
