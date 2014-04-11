package com.tradedoubler.billing.agentbase;

import com.google.gson.Gson;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.ClientUpdated;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.type.ProducerEventDbType;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.util.Date;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class UpdateClientBase extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( UpdateClientBase.class);

    private InvoicingRuleBuilder ruleBuilder;
    private Gson                 gson;

    public UpdateClientBase(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException
    {
        super( parameter);

        validateParam( parameter);

        this.ruleBuilder = ruleBuilder;
        this.gson        = GsonFactory.getGson();
    }

    public abstract void storeMessage( CrmMessageDto dto, String entityId);

    @Override
    protected void before() throws Exception
    {
        //
        // Nothing to do
        //
    }

    @Override
    protected void after()
    {
        //
        // Nothing to do
        //
    }

    @Override
    protected void executeBeat( ProducerEventType event)
    {
        createMessage( event);
    }

    public Long createMessage( ProducerEventType event)
    {
        CrmMessageDto dto  = new CrmMessageDto();
        ClientUpdated rule = null;

        //
        // Create the message according to the event type
        //
        switch( event)
        {
        case NONE:
        case CREATE:
        case SYNTAX:
        case ILLEGAL:

            //
            // Create a new update message from a previously created rule
            //
            rule = ruleBuilder.buildClientUpdated(
                System.currentTimeMillis(),
                event,
                event == ProducerEventType.CREATE);

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( event.asDbType());

            storeMessage( dto, rule.getClient().getClientId());

            LOGGER.info(
                "***** Client updated: " + new Date() + " (" + dto.getEventType() + "), " +
                    "messageId " + dto.getMessageId() + ", " +
                    "entityId " + rule.getClient().getClientId());

            break;

        case ORDER:

            //
            // Cant simulate an ordering event since a client can always be created/updated
            //
            throw new IllegalArgumentException( "ORDER events not possible on Clients");

        case SEQUENCE:

            //
            // Create two messages, the first one is a normal update on an existing invoice rule.
            // The second one is the same rule but with a creation time prior to the first one
            //
            long          seqTime = System.currentTimeMillis();
            CrmMessageDto seqDto  = new CrmMessageDto();

            //
            // Create the valid message
            //
            ClientUpdated seqClient = ruleBuilder.buildClientUpdated( seqTime, event, false);

            seqDto.setMessageId( seqClient.getMetaData().getMessageId().getGuid());
            seqDto.setJsonMessage( gson.toJson( seqClient));
            seqDto.setEventType( ProducerEventDbType.NONE);

            storeMessage( seqDto, seqClient.getClient().getClientId());

            LOGGER.info(
                "***** Client updated: " + new Date() + " (" + seqDto.getEventType() + "), " +
                    "messageId " + seqDto.getMessageId() + ", " +
                    "entityId " + seqClient.getClient().getClientId());

            //
            // Create the second message with an older time, (1 ms)
            //
            rule = new ClientUpdated(
                ruleBuilder.buildMetadata( seqTime - 2000L, ruleBuilder.getClientMessageType()),
                seqClient.getClient());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.SEQUENCE);

            storeMessage( dto, rule.getClient().getClientId());

            LOGGER.info(
                "***** Client updated: " + new Date() + " (" + dto.getEventType() + "), " +
                    "messageId " + dto.getMessageId() + ", " +
                    "entityId " + rule.getClient().getClientId());

            break;

        default:

            Assert.fail( "Unhandled event type");
        }

        //
        // No message was created...
        //
        return 0L;
    }

    private void validateParam( ProducerParameter parameter)
    {
        if( parameter.getEventType()  == ProducerEventType.ORDER ||
            parameter.getEventType2() == ProducerEventType.ORDER)
        {
            throw new IllegalArgumentException( "ORDER events not possible on Clients");
        }
    }
}
