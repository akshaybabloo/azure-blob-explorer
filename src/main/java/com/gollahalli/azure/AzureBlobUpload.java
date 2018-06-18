package com.gollahalli.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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
     * @param containerName Container name you want to uploadFromFile or create.
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
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobUpload(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }


    /**
     * Upload a single file to Azure blob.
     *
     * @param pathFileName Absolute path to the file.
     * @return URL of the uploaded file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public String uploadFromFile(String pathFileName) throws URISyntaxException, StorageException, IOException {

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);

        if (!Utils.containerExists(cloudBlobClient, this.containerName)) {
            Utils.createContainer(cloudBlobContainer);
        }

        return "";

    }


    /**
     * @param folderPath Absolute path to a folder.
     * @return URL of the uploaded location.
     */
    public String uploadFromFolder(String folderPath) {
        return "";
    }

}
