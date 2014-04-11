package com.tradedoubler.billing.service.azure;

import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.queue.client.CloudQueue;
import com.microsoft.windowsazure.services.queue.client.CloudQueueClient;
import com.tradedoubler.billing.exception.AzureQueueFailedException;
import com.tradedoubler.billing.property.BillingIntegrationProperties;

/**
 * @author Thomas Rambrant (thore)
 */

public class AzureService
{
    private BillingIntegrationProperties properties;

    public AzureService( BillingIntegrationProperties properties)
    {
        this.properties = properties;
    }

    public AzureQueue getQueue( String queueName)
    {
        try
        {
            //
            // Define the connection URL
            //
            String connectionString =
                "DefaultEndpointsProtocol=http;" +
                "AccountName=" + properties.getAzureAccountName() + ";" +
                "AccountKey="  + properties.getAzureAccountKey();

            //
            // Get the Azure client and set the properties
            //
            CloudStorageAccount storageAccount = CloudStorageAccount.parse( connectionString);
            CloudQueueClient    queueClient    = storageAccount.createCloudQueueClient();

            queueClient.setTimeoutInMs( properties.getAzureRequestTimeoutMs());

            //
            // Return the queue
            //
            CloudQueue queue = queueClient.getQueueReference( queueName);

            queue.createIfNotExist();

            return new AzureQueue( queue, properties.getAzureVisibilityTimeoutSec());
        }
        catch( Exception e)
        {
            throw new AzureQueueFailedException( "Failed to get queue" + queueName, e);
        }
    }
}
