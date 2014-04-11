package com.tradedoubler.billing.service.azure;

import com.microsoft.windowsazure.services.core.storage.StorageException;
import com.microsoft.windowsazure.services.queue.client.CloudQueue;
import com.microsoft.windowsazure.services.queue.client.CloudQueueMessage;
import com.microsoft.windowsazure.services.queue.client.QueueRequestOptions;
import com.tradedoubler.billing.exception.AzureQueueFailedException;

/**
 * @author Thomas Rambrant (thore)
 */

public class AzureQueue
{
    private final CloudQueue cloudQueue;
    private final int        visibilityTimeout;

    public AzureQueue(
        CloudQueue cloudQueue,
        int        visibilityTimeout)
    {
        this.cloudQueue        = cloudQueue;
        this.visibilityTimeout = visibilityTimeout;
    }

    public void addMessage( String jsonMessage)
    {
        try
        {
            CloudQueueMessage message = new CloudQueueMessage( jsonMessage);

            cloudQueue.addMessage( message);
        }
        catch( StorageException e)
        {
            throw new AzureQueueFailedException( "Failed to add message to queue" + cloudQueue.getName(), e);
        }
    }

    public CloudQueueMessage getMessage()
    {
        try
        {
            return cloudQueue.retrieveMessage( visibilityTimeout, new QueueRequestOptions(), null);
        }
        catch( StorageException e)
        {
            throw new AzureQueueFailedException( "Failed to get message from queue" + cloudQueue.getName(), e);
        }
    }

    public void deleteMessage( CloudQueueMessage message) throws StorageException
    {
        try
        {
            cloudQueue.deleteMessage( message);
        }
        catch( StorageException e)
        {
            throw new AzureQueueFailedException( "Failed to delete message from queue" + cloudQueue.getName(), e);
        }
    }

    public void clearQueue()
    {
        try
        {
            cloudQueue.clear();
        }
        catch( StorageException e)
        {
            throw new AzureQueueFailedException( "Failed to delete message from queue" + cloudQueue.getName(), e);
        }
    }
}
