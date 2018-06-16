package com.gollahalli.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;

/**
 * Implements uploading files to Azure containers.
 */
public class AzureBlobUpload {

    private String accountName;
    private String accountKey;
    private String containerName;
    private boolean useHttps;

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to upload or create.
     * @param useHttps      <code>true</code> to use HTTPS to connect to the storage service endpoints;
     *                      otherwise, <code>false</code>. Defaults to <code>true</code>.
     */
    public AzureBlobUpload(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.useHttps = useHttps;
        this.containerName = containerName;
    }

    /**
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to upload or create.
     */
    public AzureBlobUpload(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }


    /**
     * Upload a single file to Azure blob.
     *
     * @param path An absolute path to the file.
     * @return URL of the uploaded file.
     * @throws URISyntaxException If an invalid account name is provided.
     */
    public String upload(String path) throws URISyntaxException {

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);

        CloudBlobClient cloudBlobClient = null;
        CloudBlobContainer cloudBlobContainer = null;

        cloudBlobClient = account.createCloudBlobClient();

        return "";

    }


    /**
     * @param folderPath Absolute path to a folder.
     * @return URL of the uploaded location.
     */
    public String recursiveUpload(String folderPath) {
        return "";
    }

}
