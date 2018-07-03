/**
 * MIT License
 * <p>
 * Copyright (c) 2018 Akshay Raj Gollahalli
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Implements streaming contents from Azure blob containers.
 */
public class AzureBlobStream {

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
     * Implements streaming contents from Azure blob containers with HTTPS.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobStream(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }

    /**
     * Implements streaming contents from Azure blob containers.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     * @param useHttps      <code>true</code> to use HTTPS to connect to the storage service endpoints;
     *                      otherwise, <code>false</code>. Defaults to <code>true</code>.
     */
    public AzureBlobStream(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.containerName = containerName;
        this.useHttps = useHttps;
        LOGGER.traceEntry();
        LOGGER.debug("Account Name: {}, Container Name: {}, Use HTTPS?: {}", this.accountName, this.containerName, this.useHttps);
    }

    /**
     * Read the file from the blob as {@link InputStreamReader}.
     *
     * @param blobPathFileName
     * @return Input stream reader of the file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     */
    public InputStreamReader streamFileReader(String blobPathFileName) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("blobPathFileName: {}.", blobPathFileName);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPathFileName);
        InputStream inputStream = cloudBlockBlob.openInputStream();

        return new InputStreamReader(inputStream);
    }
}
