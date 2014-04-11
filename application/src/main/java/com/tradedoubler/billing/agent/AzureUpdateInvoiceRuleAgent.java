package com.tradedoubler.billing.agent;

import com.tradedoubler.billing.agentbase.UpdateInvoiceRuleBase;
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

public class AzureUpdateInvoiceRuleAgent extends UpdateInvoiceRuleBase
{
    private BillingIntegrationProperties properties;
    private AzureService                 azureService;

    public AzureUpdateInvoiceRuleAgent(
        ProducerParameter         parameter,
        CrmCreateInvoiceRuleAgent createAgent,
        InvoicingRuleBuilder      ruleBuilder) throws ProducerCreationFailedException, SQLException
    {
        super( parameter, createAgent, ruleBuilder);

        this.properties   = new BillingIntegrationProperties();
        this.azureService = new AzureService( properties);
    }

    @Override
    protected void before()
    {
        //
        // Cleanup before we commence (Things left on the azure queues from previous runs)
        //
        azureService.getQueue( properties.getInvoicingUpdatedQueueName()).clearQueue();
    }

    @Override
    public void storeMessage( CrmMessageDto dto, String entityId)
    {
        azureService.getQueue( properties.getInvoicingUpdatedQueueName()).addMessage( dto.getJsonMessage());
    }
}
