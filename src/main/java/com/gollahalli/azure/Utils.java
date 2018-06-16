package com.gollahalli.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * Utilities for {@link AzureBlobUpload} and {@link AzureBlobDownload}.
 */
public class Utils {

    private static final Logger LOGGER = LogManager.getLogger();
    private CloudStorageAccount account;

    /**
     * @param account
     *      Requires {@link CloudStorageAccount} details.
     */
    public Utils(CloudStorageAccount account) {
        this.account = account;
    }

    /**
     * List all the containers in the account provided.
     *
     * @return A list of containers.
     */
    public List<String> listContainers(){
        return null;
    }

    /**
     * Creates a container.
     *
     * @param containerName
     *      Name of the container.
     * @return
     *      A URI of the container created.
     */
    public String createContainer(String containerName){
        return null;
    }

    /**
     * Checks if a containers exists with the name given.
     *
     * @param containerName
     *      Name of the container.
     * @return <code>false</code> if container does not exist, <code>true</code> otherwise.
     */
    public boolean conatinerExists(String containerName){
        return false;
    }
}
