package com.tradedoubler.billing.agentbase;

import com.google.gson.Gson;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.InvoicingRuleCreated;
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

@SuppressWarnings( { "FieldCanBeLocal"})
public abstract class CreateInvoiceRuleBase extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( CreateInvoiceRuleBase.class);

    private InvoicingRuleBuilder ruleBuilder;
    private Gson                 gson;

    public CreateInvoiceRuleBase(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException
    {
        super( parameter);

        validateParam( parameter);

        this.ruleBuilder = ruleBuilder;
        this.gson        = GsonFactory.getGson();
    }

    public abstract CrmMessageDto storeMessage( CrmMessageDto dto, String entityId);

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

    public CrmMessageDto createMessage( ProducerEventType event)
    {
        CrmMessageDto        dto  = new CrmMessageDto();
        InvoicingRuleCreated rule;

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
            // Create a new message with an "unique" invoicing rule
            //
            rule = ruleBuilder.buildInvoicingRuleCreated(
                event,
                System.currentTimeMillis());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( event.asDbType());

            LOGGER.info(
                "***** InvoicingRule created: " + new Date() + " (" + dto.getEventType() + "), " +
                "messageId " + dto.getMessageId() + ", " +
                "entityId " + rule.getInvoicingRule().getInvoicingRuleId() + ", " +
                "clientId " + rule.getClient().getClientId() + ", " +
                "agreementId " + rule.getAgreement().getSourceSystemAgreementId());

            return storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

        case ORDER:

            //
            // Create a normal message but mark it as out_of_order. This flags ensures that the message is skipped
            // until the corresponding update message has been processed. The receiving end of the update message is
            // responsible for the clearance of this flag.
            //
            rule = ruleBuilder.buildInvoicingRuleCreated(
                event,
                System.currentTimeMillis());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.NONE);
            dto.setOutOfOrder( true);

            LOGGER.info(
                "***** InvoicingRule created: " + new Date() + " (" + dto.getEventType() + "), " +
                "messageId " + dto.getMessageId() + ", " +
                "entityId " + rule.getInvoicingRule().getInvoicingRuleId() + ", " +
                "clientId " + rule.getClient().getClientId() + ", " +
                "agreementId " + rule.getAgreement().getSourceSystemAgreementId());

            return storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

        case SEQUENCE:

            //
            // Create two messages, the first one is a new unique invoice rule. The second one is the same rule
            // but with a creation time prior to the first one
            //
            long          seqTime = System.currentTimeMillis();
            CrmMessageDto seqDto  = new CrmMessageDto();

            //
            // Create the valid message
            //
            InvoicingRuleCreated seqRule = ruleBuilder.buildInvoicingRuleCreated( event, seqTime);

            seqDto.setMessageId( seqRule.getMetaData().getMessageId().getGuid());
            seqDto.setJsonMessage( gson.toJson( seqRule));
            seqDto.setEventType( ProducerEventDbType.NONE);

            LOGGER.info(
                "***** InvoicingRule created: " + new Date() + " (" + seqDto.getEventType() + "), " +
                "messageId " + seqDto.getMessageId() + ", " +
                "entityId " + seqRule.getInvoicingRule().getInvoicingRuleId() + ", " +
                "clientId " + seqRule.getClient().getClientId() + ", " +
                "agreementId " + seqRule.getAgreement().getSourceSystemAgreementId());

            storeMessage( seqDto, seqRule.getInvoicingRule().getInvoicingRuleId().toString());

            //
            // Create the second message with an older time
            //
            rule = new InvoicingRuleCreated(
                ruleBuilder.buildMetadata( seqTime - 2000L, ruleBuilder.getInvoiceRuleCreatedMessageType()),
                seqRule.getInvoicingRule(),
                seqRule.getAgreement(),
                seqRule.getClient());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.SEQUENCE);

            LOGGER.info(
                "***** InvoicingRule created: " + new Date() + " (" + dto.getEventType() + "), " +
                "messageId " + dto.getMessageId() + ", " +
                "entityId " + rule.getInvoicingRule().getInvoicingRuleId() + ", " +
                "clientId " + rule.getClient().getClientId() + ", " +
                "agreementId " + rule.getAgreement().getSourceSystemAgreementId());

            return storeMessage( dto, rule.getInvoicingRule().getInvoicingRuleId().toString());

        default:

            Assert.fail( "Unhandled event type");
        }

        //
        // No message was created...
        //
        return null;
    }

    public void validateParam( ProducerParameter parameter) throws ProducerCreationFailedException
    {
        if( parameter.getEventType() == ProducerEventType.ORDER)
        {
            throw new ProducerCreationFailedException( "Out of order errors not allowed on CreateInvoice");
        }
    }
}
