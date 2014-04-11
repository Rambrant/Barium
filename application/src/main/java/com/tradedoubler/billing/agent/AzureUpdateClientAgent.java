package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateClientBase;
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

public class AzureUpdateClientAgent extends UpdateClientBase
{
    private BillingIntegrationProperties properties;
    private AzureService                 azureService;

    public AzureUpdateClientAgent(
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
        azureService.getQueue( properties.getClientUpdatedQueueName()).clearQueue();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        azureService.getQueue( properties.getClientUpdatedQueueName()).addMessage( dto.getJsonMessage());
    }
}
