package com.tradedoubler.billing.domain.parameter;

import com.tradedoubler.billing.domain.type.IntervalType;
import com.tradedoubler.billing.domain.type.ProducerEventType;

/**
 * @author Thomas Rambrant (thore)
 */

public class ProducerParameter extends Parameter
{
    //
    // The interval definition that controls the creation of input data.
    // The interval and duration is specified in seconds all according to the interval type
    //
    private IntervalType      intervalType = IntervalType.NONE;
    private Long              timeLimit;
    private Long              interval;
    private Long              duration;
    private Long              interval2;
    private Long              duration2;

    //
    // The definition of the production of faulty input data.
    // The interval and duration is given in entities, every second or every third etc... all according to the
    // interval type.
    //
    private ProducerEventType eventType = ProducerEventType.NONE;
    private ProducerEventType eventType2 = ProducerEventType.NONE;
    private IntervalType      errorIntervalType = IntervalType.NONE;
    private Long              errorInterval;
    private Long              errorDuration;
    private Long              errorInterval2;
    private Long              errorDuration2;


    public IntervalType getIntervalType()
    {
        return this.intervalType;
    }

    public void setIntervalType( IntervalType intervalType)
    {
        this.intervalType = intervalType;
    }

    public Long getTimeLimit()
    {
        return timeLimit;
    }

    public void setTimeLimit( Long timeLimit)
    {
        this.timeLimit = timeLimit;
    }

    public Long getInterval()
    {
        return interval;
    }

    public void setInterval( Long interval)
    {
        this.interval = interval;
    }

    public Long getDuration()
    {
        return duration;
    }

    public void setDuration( Long duration)
    {
        this.duration = duration;
    }

    public ProducerEventType getEventType()
    {
        return eventType;
    }

    public void setEventType( ProducerEventType eventType)
    {
        this.eventType = eventType;
    }

    public ProducerEventType getEventType2()
    {
        return eventType2;
    }

    public void setEventType2( ProducerEventType eventType2)
    {
        this.eventType2 = eventType2;
    }

    public Long getInterval2()
    {
        return interval2;
    }

    public void setInterval2( Long interval2)
    {
        this.interval2 = interval2;
    }

    public Long getDuration2()
    {
        return duration2;
    }

    public void setDuration2( Long duration2)
    {
        this.duration2 = duration2;
    }

    public IntervalType getErrorIntervalType()
    {
        return this.errorIntervalType;
    }

    public void setErrorIntervalType( IntervalType errorIntervalType)
    {
        this.errorIntervalType = errorIntervalType;
    }

    public Long getErrorInterval()
    {
        return errorInterval;
    }

    public void setErrorInterval( Long errorInterval)
    {
        this.errorInterval = errorInterval;
    }

    public Long getErrorDuration()
    {
        return errorDuration;
    }

    public void setErrorDuration( Long errorDuration)
    {
        this.errorDuration = errorDuration;
    }

    public Long getErrorInterval2()
    {
        return errorInterval2;
    }

    public void setErrorInterval2( Long errorInterval2)
    {
        this.errorInterval2 = errorInterval2;
    }

    public Long getErrorDuration2()
    {
        return errorDuration2;
    }

    public void setErrorDuration2( Long errorDuration2)
    {
        this.errorDuration2 = errorDuration2;
    }

    @Override
    public String toString()
    {
        return "ProducerParameter{" +
            "\tintervalType      = " + intervalType      + "\n" +
            "\ttimeLimit         = " + timeLimit         + "\n" +
            "\tinterval          = " + interval          + "\n" +
            "\tduration          = " + duration          + "\n" +
            "\tinterval2         = " + interval2         + "\n" +
            "\tduration2         = " + duration2         + "\n" +
            "\teventType         = " + eventType         + "\n" +
            "\terrorIntervalType = " + errorIntervalType + "\n" +
            "\terrorInterval     = " + errorInterval     + "\n" +
            "\terrorDuration     = " + errorDuration     + "\n" +
            "\terrorInterval2    = " + errorInterval2    + "\n" +
            "\terrorDuration2    = " + errorDuration2    + "\n" +
            '}';
    }
}
