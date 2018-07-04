package com.gollahalli.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implements streaming contents to Azure blob containers.
 */
public class AzureBlobStreamWriter {

    /**
     * Account name, it's usually your container name.
     */
    private String accountName;

    /**
     * Account key.
     */
    private String accountKey;

    /**
     * Your container name.
     */
    private String containerName;

    /**
     * Enable HTTPS while using this
     */
    private boolean useHttps;

    private static final Logger LOGGER = LogManager.getLogger(AzureBlobDownload.class.getName());

    /**
     * Implements streaming contents to Azure blob containers with HTTPS.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobStreamWriter(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }

    /**
     * Implements streaming contents to Azure blob containers.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     * @param useHttps      <code>true</code> to use HTTPS to connect to the storage service endpoints;
     *                      otherwise, <code>false</code>. Defaults to <code>true</code>.
     */
    public AzureBlobStreamWriter(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.containerName = containerName;
        this.useHttps = useHttps;
        LOGGER.traceEntry();
        LOGGER.debug("Account Name: {}, Container Name: {}, Use HTTPS?: {}", this.accountName, this.containerName, this.useHttps);
    }

    /**
     * Writes a file to the blob as a stream.
     *
     * @param blobPathFileName Blob path with file name.
     * @param content          Can be any content of type <code>byte[]</code>
     * @return URI of the file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        Used by {@link IOException}.
     */
    public URI streamFileWriter(String blobPathFileName, byte[] content) throws URISyntaxException, StorageException, IOException {
        LOGGER.traceEntry();
        LOGGER.debug("blobPathFileName: {}.", blobPathFileName);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPathFileName);
        BlobOutputStream outputStream = cloudBlockBlob.openOutputStream();
        outputStream.write(content);
        outputStream.close();

        LOGGER.traceExit("File Name '{}' uploaded.", blobPathFileName);
        return cloudBlockBlob.getUri();
    }
}
