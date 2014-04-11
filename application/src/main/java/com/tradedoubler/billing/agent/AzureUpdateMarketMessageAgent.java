package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateMarketMessageBase;
import com.tradedoubler.billing.derby.dto.CrmMessageDto;
import com.tradedoubler.billing.domain.generator.InvoicingRuleBuilder;
import com.tradedoubler.billing.domain.parameter.ProducerParameter;
import com.tradedoubler.billing.exception.ProducerCreationFailedException;
import com.tradedoubler.billing.property.BillingIntegrationProperties;
import com.tradedoubler.billing.service.azure.AzureService;

import java.sql.SQLException;

/**
 * @author Thomas Rambrant (thore)
 */

public class AzureUpdateMarketMessageAgent extends UpdateMarketMessageBase
{
    private BillingIntegrationProperties properties;
    private AzureService                 azureService;

    public AzureUpdateMarketMessageAgent(
        ProducerParameter    parameter,
        InvoicingRuleBuilder ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, ruleBuilder);

        this.properties   = new BillingIntegrationProperties();
        this.azureService = new AzureService( properties);
    }

    @Override
    protected void before()
    {
        //
        // Cleanup before we commence (Things left on the azure queues from previous runs)
        //
        azureService.getQueue( properties.getMarketMessageUpdatedQueueName()).clearQueue();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        azureService.getQueue( properties.getMarketMessageUpdatedQueueName()).addMessage( dto.getJsonMessage());
    }
}
