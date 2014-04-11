package com.tradedoubler.billing.agentbase;

import com.google.gson.Gson;
import com.tradedoubler.billing.agent.CrmCreateInvoiceRuleAgent;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.InvoicingRuleCreated;
import com.tradedoubler.billing.domain.InvoicingRuleUpdated;
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

public abstract class UpdateInvoiceRuleBase extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( UpdateInvoiceRuleBase.class);

    private InvoicingRuleBuilder      ruleBuilder;
    private CrmCreateInvoiceRuleAgent createAgent;
    private Gson                      gson;

    public UpdateInvoiceRuleBase(
        ProducerParameter         parameter,
        CrmCreateInvoiceRuleAgent createAgent,
        InvoicingRuleBuilder      ruleBuilder) throws ProducerCreationFailedException
    {
        super( parameter);

        validateParam( parameter, createAgent);

        this.createAgent = createAgent;
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
        CrmMessageDto        dto  = new CrmMessageDto();
        InvoicingRuleUpdated rule = null;

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
            rule = ruleBuilder.buildInvoicingRuleUpdated(
                event,
                System.currentTimeMillis());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( event.asDbType());

            LOGGER.info(
                "***** InvoicingRule updated: " + new Date() + " (" + dto.getEventType() + "), " +
                    "messageId " + dto.getMessageId() + ", " +
                    "entityId " + rule.getInvoicingRule().getInvoicingRuleId());

            storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

            break;

        case ORDER:

            //
            // Prepare a create message that is put on hold. Remember the id so we can associate with it.
            // Fetch the invoice rule id
            //
            CrmMessageDto createDto = createAgent.createMessage( ProducerEventType.ORDER);

            InvoicingRuleCreated orderRule = gson.fromJson( createDto.getJsonMessage(), InvoicingRuleCreated.class);

            //
            // Create a normal update message associated with the create message that we have put on hold
            // The receiving end of the update message is responsible for the "release" of the create message.
            //
            rule = new InvoicingRuleUpdated(
                ruleBuilder.buildMetadata( System.currentTimeMillis(), ruleBuilder.getInvoiceRuleUpdatedMessageType()),
                orderRule.getInvoicingRule());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setAssociatedRowId( createDto.getId());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.NONE);
            dto.setOutOfOrder( true);

            LOGGER.info(
                "***** InvoicingRule updated: " + new Date() + " (" + dto.getEventType() + "), " +
                    "messageId " + dto.getMessageId() + ", " +
                    "entityId " + rule.getInvoicingRule().getInvoicingRuleId());

            storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

            break;

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
            InvoicingRuleUpdated seqRule = ruleBuilder.buildInvoicingRuleUpdated( event, seqTime);

            seqDto.setMessageId( seqRule.getMetaData().getMessageId().getGuid());
            seqDto.setJsonMessage( gson.toJson( seqRule));
            seqDto.setEventType( ProducerEventDbType.NONE);

            LOGGER.info(
                "***** InvoicingRule updated: " + new Date() + " (" + seqDto.getEventType() + "), " +
                    "messageId " + seqDto.getMessageId() + ", " +
                    "entityId " + seqRule.getInvoicingRule().getInvoicingRuleId());

            storeMessage( seqDto, seqRule.getInvoicingRule().getInvoicingRuleId().toString());

            //
            // Create the second message with an older time, (1 ms)
            //
            rule = new InvoicingRuleUpdated(
                ruleBuilder.buildMetadata( seqTime - 2000L, ruleBuilder.getInvoiceRuleUpdatedMessageType()),
                seqRule.getInvoicingRule());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.SEQUENCE);

            LOGGER.info(
                "***** InvoicingRule updated: " + new Date() + " (" + dto.getEventType() + "), " +
                    "messageId " + dto.getMessageId() + ", " +
                    "entityId " + rule.getInvoicingRule().getInvoicingRuleId());

            storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

            break;

        default:

            Assert.fail( "Unhandled event type");
        }

        //
        // No message was created...
        //
        return 0L;
    }

    private void validateParam( ProducerParameter parameter, CrmCreateInvoiceRuleAgent createAgent)
    {
        if( parameter.getEventType()  == ProducerEventType.ORDER ||
            parameter.getEventType2() == ProducerEventType.ORDER)
        {
            if( createAgent == null)
            {
                throw new IllegalArgumentException( "ORDER errors not possible without a CrmCreateInvoiceRuleAgent");
            }
        }

        if( parameter.getEventType()  == ProducerEventType.CREATE ||
            parameter.getEventType2() == ProducerEventType.CREATE)
        {
            throw new IllegalArgumentException( "CREATE events not allowed for Update Client");
        }
    }
}
