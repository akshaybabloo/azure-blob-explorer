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
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Implements uploading files to Azure blob containers.
 */
public class AzureBlobUpload {

    private String accountName;
    private String accountKey;
    private String containerName;
    private boolean useHttps;

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Implements uploading files to Azure blob containers.
     *
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
     * Implements uploading files to Azure blob containers with HTTPS as <code>true</code> by default.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobUpload(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }


    /**
     * Upload a single file to Azure blob and specify the path to the blob folder.
     *
     * @param pathFileName Absolute path with file name.
     * @param blobPath     Path of the blob folder.
     * @return URL of the uploaded file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public URI uploadFromFile(String pathFileName, String blobPath) throws URISyntaxException, StorageException, IOException {

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);

        if (!StorageUtils.containerExists(cloudBlobClient, this.containerName)) {
            StorageUtils.createContainer(cloudBlobContainer);
        }

        String fileName = FilenameUtils.getName(pathFileName);
        pathFileName = FilenameUtils.normalize(pathFileName);

        CloudBlockBlob blob;
        if (blobPath != null) {
            blob = cloudBlobContainer.getBlockBlobReference(blobPath + fileName);
        } else {
            blob = cloudBlobContainer.getBlockBlobReference(fileName);
        }

        blob.uploadFromFile(pathFileName);

        return blob.getUri();
    }

    /**
     * Upload a single file to the root of Azure container.
     *
     * @param pathFileName Absolute path with file name.
     * @return URL of the uploaded file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public URI uploadFromFile(String pathFileName) throws URISyntaxException, StorageException, IOException {
        return uploadFromFile(pathFileName, null);
    }


    /**
     * Do a recursive upload of the folder path provided to a given blob path.
     *
     * @param folderPath Absolute path to a folder.
     * @param blobPath   Path of the blob folder.
     * @return URL of the uploaded location.
     * @throws URISyntaxException Is used by {@link CloudStorageAccount}.
     * @throws StorageException   If container is not found.
     * @throws IOException        If your absolute path contains no file.
     */
    public URL uploadFromFolder(String folderPath, String blobPath) throws URISyntaxException, StorageException, IOException {
        // TODO: Remove redundant code.

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);

        if (!StorageUtils.containerExists(cloudBlobClient, this.containerName)) {
            StorageUtils.createContainer(cloudBlobContainer);
        }

        Pair pair = StorageUtils.getRelativePaths(folderPath);

        // Absolute paths
        List<String> absolutePath = (List<String>) pair.getKey();
        // Relative paths.
        List<String> relativePath = (List<String>) pair.getValue();

        CloudBlockBlob blob = null;
        int count = absolutePath.size();

        if (blobPath != null) {
            for (int i = 0; i < count; i++) {
                blob = cloudBlobContainer.getBlockBlobReference(blobPath + relativePath.get(i));
                blob.uploadFromFile(absolutePath.get(i));
            }
        } else {

            for (int i = 0; i < count; i++) {
                blob = cloudBlobContainer.getBlockBlobReference(relativePath.get(i));
                blob.uploadFromFile(absolutePath.get(i));
            }
        }

        assert blob != null;
        return new URL(new URL(blob.getParent().getUri().toString()), FilenameUtils.getName(folderPath));
    }

    /**
     * Do a recursive upload of the folder path provided.
     *
     * @param folderPath Absolute path to a folder.
     * @return URL of the uploaded file.
     * @throws URISyntaxException Is used by {@link CloudStorageAccount}.
     * @throws StorageException   If container is not found.
     * @throws IOException        If your absolute path contains no file.
     */
    public URL uploadFromFolder(String folderPath) throws URISyntaxException, StorageException, IOException {
        return uploadFromFolder(folderPath, null);
    }

}
