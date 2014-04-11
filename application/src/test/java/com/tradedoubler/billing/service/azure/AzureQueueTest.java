package com.tradedoubler.billing.service.azure;

import com.microsoft.windowsazure.services.queue.client.CloudQueueMessage;
import com.tradedoubler.billing.property.BillingIntegrationProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Rambrant (thore)
 */
public class AzureQueueTest
{
    private BillingIntegrationProperties properties;

    @Before
    public void setUp() throws Exception
    {
        properties = new BillingIntegrationProperties();
    }

    @Test
    public void testAzureQueueHandling() throws Exception
    {
        AzureService service = new AzureService( properties);

        AzureQueue queue = service.getQueue( "barium-test-queue");

        queue.addMessage( "test");

        CloudQueueMessage message = queue.getMessage();

        Assert.assertNotNull( message);
        Assert.assertEquals( "test", message.getMessageContentAsString());

        queue.deleteMessage( message);

        //
        // Wait some seconds beyond the visibility timeout
        //
        Thread.sleep( 15000L);

        CloudQueueMessage noMessage = queue.getMessage();

        Assert.assertNull( noMessage);
    }

    @Test
    public void testAzureClearQueue() throws Exception
    {
        AzureService service = new AzureService( properties);

        AzureQueue queue = service.getQueue( "barium-test-queue");

        queue.addMessage( "test");

        queue.clearQueue();

        CloudQueueMessage noMessage = queue.getMessage();

        Assert.assertNull( noMessage);
    }
}
