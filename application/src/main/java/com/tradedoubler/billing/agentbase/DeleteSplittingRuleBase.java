package com.tradedoubler.billing.agentbase;

import com.google.gson.Gson;
import com.tradedoubler.billing.agentcontrol.Producer;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.SplittingRuleDeleted;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.domain.type.ProducerEventType;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.transform.json.gson.GsonFactory;
import com.tradedoubler.billing.type.ProducerEventDbType;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * @author Thomas Rambrant (thore)
 */

public abstract class DeleteSplittingRuleBase extends Producer
{
    protected static final Logger LOGGER  = Logger.getLogger( DeleteSplittingRuleBase.class);

    private InvoicingRuleBuilder ruleBuilder;
    private Gson                 gson;

    public DeleteSplittingRuleBase(
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
        CrmMessageDto        dto  = new CrmMessageDto();
        SplittingRuleDeleted rule = null;

        System.out.print( "\t***** Splitting Rule deleted: (" + event + ")\n");

        //
        // Create the message according to the event type
        //
        switch( event)
        {
        case NONE:

            //
            // Create a new update message from a previously created rule
            //
            rule = ruleBuilder.buildSplittingRuleDeleted(
                System.currentTimeMillis());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.NONE);

            storeMessage( dto, rule.getSplittingRuleId().toString());

            break;

        case ORDER:

            //
            // Cant simulate an ordering event since a client can always be created/updated
            //TODO
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
            SplittingRuleDeleted seqRule = ruleBuilder.buildSplittingRuleDeleted( seqTime);

            seqDto.setMessageId( seqRule.getMetaData().getMessageId().getGuid());
            seqDto.setJsonMessage( gson.toJson( seqRule));
            seqDto.setEventType( ProducerEventDbType.NONE);

            storeMessage( seqDto, seqRule.getSplittingRuleId().toString());

            //
            // Create the second message with an older time, (1 ms)
            //
            rule = new SplittingRuleDeleted(
                ruleBuilder.buildMetadata( seqTime - 2000L, ruleBuilder.getSplittingRuleMessageType()),
                seqRule.getSplittingRuleId().getGuid(),
                seqRule.getInvoiceRecipientId().getGuid(),
                seqRule.getMarketId().getOrganizationId());

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.SEQUENCE);

            storeMessage( dto, rule.getSplittingRuleId().toString());

            break;

        case SYNTAX:

            //
            // Create a message with syntax event. We do this by giving a "broken" guid
            //
            rule = ruleBuilder.buildSplittingRuleDeleted( System.currentTimeMillis());

            rule.setSplittingRuleId( "Broken Guid");

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.SYNTAX);

            storeMessage( dto, rule.getSplittingRuleId().toString());

            break;

        case ILLEGAL:

            //
            // Create a message with illegal market ids. This should fail at the other end
            //
            rule = ruleBuilder.buildSplittingRuleDeleted( System.currentTimeMillis());


//TODO make it illegal
//            for( Market market: rule.getClient().getMarketIds())
//            {
//                market.setOrganizationId( 999999999);
//            }

            dto.setMessageId( rule.getMetaData().getMessageId().getGuid());
            dto.setJsonMessage( gson.toJson( rule));
            dto.setEventType( ProducerEventDbType.ILLEGAL);

            storeMessage( dto, rule.getSplittingRuleId().toString());

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
