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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Implements downloading contents from Azure blob containers.
 */
public class AzureBlobDownload {

    private String accountName;
    private String accountKey;
    private String containerName;
    private boolean useHttps;

    private static final Logger LOGGER = LogManager.getLogger(AzureBlobDownload.class.getName());

    /**
     * Implements downloading contents from Azure blob containers.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     * @param useHttps      <code>true</code> to use HTTPS to connect to the storage service endpoints;
     *                      otherwise, <code>false</code>. Defaults to <code>true</code>.
     */
    public AzureBlobDownload(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.containerName = containerName;
        this.useHttps = useHttps;
        LOGGER.debug("Account Name: {}, Container Name: {}, Use HTTPS?: {}", this.accountName, this.containerName, this.useHttps);
    }

    /**
     * Implements downloading contents from Azure blob containers with HTTPS as <code>true</code> by default.
     *
     * @param accountName   Account name from your <strong>Access Keys</strong>.
     * @param accountKey    Account key from your <strong>Access Keys</strong>.
     * @param containerName Container name you want to uploadFromFile or create.
     */
    public AzureBlobDownload(String accountName, String accountKey, String containerName) {
        this(accountName, accountKey, containerName, true);
    }

    /**
     * Download a file from a given blob path of the container.
     *
     * @param blobPathFileName Path to the file name on the container.
     * @param saveToPath       Absolute path to a location on your computer.
     * @return The path of the file.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public String downloadFile(String blobPathFileName, String saveToPath) throws URISyntaxException, StorageException, IOException {
        LOGGER.traceEntry();
        LOGGER.debug("blobPathFileName: {}, saveToPath: {}.", blobPathFileName, saveToPath);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPathFileName);

        String fileName = FilenameUtils.getName(blobPathFileName);
        String localPath = FilenameUtils.concat(saveToPath, fileName);
        LOGGER.debug("Complete Path: {}", localPath);

        cloudBlockBlob.downloadToFile(localPath);
        LOGGER.traceExit("Saved @: {}", localPath);

        return localPath;

    }

    /**
     * Download a blob folder and its contents.
     *
     * @param blobFolderPath Folder path on the container to download.
     * @param saveToPath     Absolute path to a location on your computer.
     * @return Path of the folder.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public String downloadFolder(String blobFolderPath, String saveToPath) throws URISyntaxException, StorageException, IOException {
        return downloadFolder(blobFolderPath, saveToPath, true);
    }

    /**
     * Download a blob folder and its contents.
     *
     * @param blobFolderPath Folder path on the container to download.
     * @param saveToPath     Absolute path to a location on your computer.
     * @param keepBlobName   Keep the root name of the folder.
     * @return Path of the folder.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     * @throws IOException        If the file does not exist.
     */
    public String downloadFolder(String blobFolderPath, String saveToPath, boolean keepBlobName) throws URISyntaxException, StorageException, IOException {
        LOGGER.traceEntry();
        LOGGER.debug("blobFolderPath: {}, saveToPath: {}, keepBlobName?: {}.", blobFolderPath, saveToPath, keepBlobName);

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        Pair relativePaths = StorageUtils.getBlobRelativePaths(cloudBlobContainer, blobFolderPath, saveToPath, keepBlobName);
        LOGGER.debug("Container Name: {}", relativePaths.toString());

        List<String> blobPaths = (List<String>) relativePaths.getKey();
        List<String> folderFilePaths = (List<String>) relativePaths.getValue();
        int count = blobPaths.size();
        int counter = count;
        LOGGER.debug("Number of Files: {}", count);

        CloudBlockBlob cloudBlockBlob;
        for (int i = 0; i < count; i++) {
            cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPaths.get(i));
            File file = new File(folderFilePaths.get(i));
            cloudBlockBlob.download(FileUtils.openOutputStream(file, true));
            LOGGER.debug("Count: {}, File Saved To: {}.", i+1, file.getPath());
        }

        LOGGER.traceExit("Saved to: {}.", saveToPath);
        return saveToPath;
    }
}
