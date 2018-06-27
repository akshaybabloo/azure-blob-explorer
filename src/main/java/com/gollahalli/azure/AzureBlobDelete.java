/**
 * MIT License
 *
 * Copyright (c) 2018 Akshay Raj Gollahalli
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gollahalli.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implements deleting file, blobs and containers.
 */
public class AzureBlobDelete {

    private String accountName;
    private String accountKey;
    private String containerName;
    private boolean useHttps;

    private static final Logger LOGGER = LogManager.getLogger(AzureBlobDownload.class.getName());


    /**
     * Implements deleting files and blobs.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     * @param useHttps      <code>true</code> to use HTTPS to connect to the storage service endpoints;
     *                      otherwise, <code>false</code>. Defaults to <code>true</code>.
     */
    public AzureBlobDelete(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.containerName = containerName;
        this.useHttps = useHttps;
        LOGGER.traceEntry();
        LOGGER.debug("Account Name: {}, Container Name: {}, Use HTTPS?: {}", this.accountName, this.containerName, this.useHttps);
    }

    /**
     * Implements downloading contents from Azure blob containers with HTTPS as <code>true</code> by default.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobDelete(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }

    /**
     * Deletes a file in the given path with the file name.
     *
     * @param blobPathFileName Blob path with file name to delete.
     * @return URI of the deleted file.
     * @throws URISyntaxException Is used by {@link CloudStorageAccount}.
     * @throws StorageException Is used by {@link CloudStorageAccount}.
     */
    public URI deleteFile(String blobPathFileName) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("blobPathFileName: {}.", blobPathFileName);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPathFileName);
        cloudBlockBlob.delete();
        LOGGER.debug("Deleted: {}.", cloudBlockBlob.getName());

        LOGGER.traceExit();
        return cloudBlockBlob.getUri();
    }

    /**
     * Permanently deletes the blob and it's contents.
     *
     * @param blobFolderPath Path to file or folder.
     * @return Deleted blobs URI
     * @throws URISyntaxException Is used by {@link CloudStorageAccount}.
     * @throws StorageException Is used by {@link CloudStorageAccount}.
     */
    public URI deleteBlob(String blobFolderPath) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("blobPathFileName: {}.", blobFolderPath);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobFolderPath);

        for (ListBlobItem listBlobItem : cloudBlobContainer.listBlobs(blobFolderPath, true)) {
            if (listBlobItem instanceof CloudBlob) {
                LOGGER.debug("Deleted: {}.", ((CloudBlob) listBlobItem).getName());
                ((CloudBlob) listBlobItem).delete();
            }
        }

        LOGGER.traceExit();
        return cloudBlockBlob.getUri();
    }

    /**
     * Deletes the container provided while creating the instance instance.
     *
     * @throws URISyntaxException Is used by {@link CloudStorageAccount}.
     * @throws StorageException Is used by {@link CloudStorageAccount}.
     */
    public void deleteContainer() throws URISyntaxException, StorageException {
        LOGGER.traceEntry();

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        cloudBlobContainer.deleteIfExists();
        LOGGER.traceExit("Deleted Container: {}.", this.containerName);
    }
}
