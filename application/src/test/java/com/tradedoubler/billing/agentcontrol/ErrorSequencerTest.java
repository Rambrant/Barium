package com.tradedoubler.billing.agentcontrol;

import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.IntervalType;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */
public class ErrorSequencerTest
{
    @Test
    public void testGetCurrentErrorDefault() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        ErrorSequencer sequencer = new ErrorSequencer( parameter);

        for( int i = 1; i < 10; ++i)
        {
            ProducerEventType event = sequencer.getNextErrorInSequence();

            System.out.print( "Error: " + event + "\n");

            Assert.assertTrue( "Wrong event type", event.equals( parameter.getEventType()));
        }
    }

    @Test
    public void testGetCurrentErrorUniform() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        parameter.setErrorIntervalType( IntervalType.UNIFORM);
        parameter.setEventType( ProducerEventType.SYNTAX);
        parameter.setErrorInterval( 3L);

        ErrorSequencer sequencer = new ErrorSequencer( parameter);

        for( int i = 1; i < 10; ++i)
        {
            ProducerEventType event = sequencer.getNextErrorInSequence();

            System.out.print( "Error: " + event + "\n");

            if( ! event.equals( ProducerEventType.NONE))
            {
                Assert.assertTrue( "Wrong interval", i % parameter.getErrorInterval() == 0);
                Assert.assertTrue( "Wrong event type", event.equals( parameter.getEventType()));
            }
        }
    }

    @Test
    public void testGetCurrentErrorSquare() throws Exception
    {
        ProducerParameter parameter = new ProducerParameter();

        parameter.setErrorIntervalType( IntervalType.SQUARE);
        parameter.setEventType( ProducerEventType.SYNTAX);
        parameter.setEventType2( ProducerEventType.ORDER);

        parameter.setErrorInterval(  4L);
        parameter.setErrorDuration(  12L);
        parameter.setErrorInterval2( 3L);
        parameter.setErrorDuration2( 9L);

        ErrorSequencer sequencer = new ErrorSequencer( parameter);

        long progress = 0L;

        for( int i = 0; i < 40; ++i)
        {
            ProducerEventType event = sequencer.getNextErrorInSequence();

            if( progress >= parameter.getErrorDuration() + parameter.getErrorDuration2())
            {
                //
                // start it all over again repeating the sequence again and again
                //
                progress = 0L;
            }

            progress++;

            System.out.print( "count: " + i + "\tprogress " + progress + "\tevent: " + event + "\n");

            if( ! event.equals( ProducerEventType.NONE))
            {
                if( progress <= parameter.getErrorDuration())
                {
                    Assert.assertTrue(
                        "Wrong interval " + parameter.getErrorInterval(),
                        progress % parameter.getErrorInterval() == 0);

                    Assert.assertTrue(
                        "Wrong event type " + event,
                        event.equals( parameter.getEventType()));
                }
                else
                {
                    Assert.assertTrue(
                        "Wrong interval " + parameter.getErrorInterval2(),
                        progress % parameter.getErrorInterval2() == 0);

                    Assert.assertTrue(
                        "Wrong event type " + event,
                        event.equals( parameter.getEventType2()));
                }
            }
        }
    }
}
