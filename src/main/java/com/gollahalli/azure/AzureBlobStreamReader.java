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
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements streaming contents from Azure blob containers.
 */
public class AzureBlobStreamReader {

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
    public AzureBlobStreamReader(String accountName, String accountKey, String containerName) {
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
    public AzureBlobStreamReader(String accountName, String accountKey, String containerName, boolean useHttps) {
        this.accountName = accountName;
        this.accountKey = accountKey;
        this.containerName = containerName;
        this.useHttps = useHttps;
        LOGGER.traceEntry();
        LOGGER.debug("Account Name: {}, Container Name: {}, Use HTTPS?: {}", this.accountName, this.containerName, this.useHttps);
    }

    /**
     * Read the file from the blob as {@link InputStreamReader}.
     * <p>
     * Example:
     * <pre>
     *     {@code
     *     AzureBlobStreamReader streamReader = new AzureBlobStreamReader("account name", "account key", "container name");
     *     InputStreamReader reader = streamReader.streamFileReader("path/to/fileName.txt");
     *     }
     * </pre>
     * @param blobPathFileName Path with file name.
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

        LOGGER.traceExit();
        return new InputStreamReader(inputStream);
    }

    /**
     * Contents of the folder/blob as a stream.
     * <p>
     * Example:
     * <pre>
     *     {@code
     *     AzureBlobStreamReader streamReader = new AzureBlobStreamReader("account name", "account key", "container name");
     *     List<InputStreamReader> reader = streamFolderReader.streamFileReader("path/to/folder");
     *     }
     * </pre>
     *
     * @param blobFolderPath Path to the blob.
     * @return A list of {@link InputStreamReader}
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     */
    public List<InputStreamReader> streamFolderReader(String blobFolderPath) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("blobFolderPath: {}.", blobFolderPath);

        List<InputStreamReader> inputStreamReaders = new ArrayList<>();

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        List<String> listBlobs = StorageUtils.listBlobs(cloudBlobContainer, blobFolderPath);
        int count = listBlobs.size();
        LOGGER.debug("Number of Files: {}", count);

        CloudBlockBlob cloudBlockBlob;
        InputStream inputStream;
        for (int i = 0; i < count; i++) {
            cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(listBlobs.get(i));
            inputStream = cloudBlockBlob.openInputStream();
            inputStreamReaders.add(new InputStreamReader(inputStream));
            LOGGER.debug("Count: {}, File Read: {}.", i + 1, listBlobs.get(i));
        }

        LOGGER.traceExit();
        return inputStreamReaders;
    }

    /**
     * Contents of the folder/blob as a stream with it's file name.
     * <p>
     * Example:
     * <pre>
     *     {@code
     *     AzureBlobStreamReader streamReader = new AzureBlobStreamReader("account name", "account key", "container name");
     *     Pair<List<String>, List<InputStreamReader>> reader = streamFolderReader.streamFolderReaderPair("path/to/folder");
     *     }
     * </pre>
     *
     * @param blobFolderPath Path to the blob.
     * @return A pair of file names and the stream of data.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     */
    public Pair<List<String>, List<InputStreamReader>> streamFolderReaderPair(String blobFolderPath) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("blobFolderPath: {}.", blobFolderPath);

        List<String> blobPathNames = new ArrayList<>();
        List<InputStreamReader> inputStreamReaders = new ArrayList<>();

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        Pair blobRelativeNames = StorageUtils.getBlobRelativeNames(cloudBlobContainer, blobFolderPath, false);
        LOGGER.debug("Container Name: {}", blobRelativeNames.toString());

        List<String> blobPaths = (List<String>) blobRelativeNames.getKey();
        List<String> fileNames = (List<String>) blobRelativeNames.getValue();
        int count = blobPaths.size();
        LOGGER.debug("Number of Files: {}", count);

        CloudBlockBlob cloudBlockBlob;
        InputStream inputStream;
        for (int i = 0; i < count; i++) {
            cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPaths.get(i));
            inputStream = cloudBlockBlob.openInputStream();
            inputStreamReaders.add(new InputStreamReader(inputStream));
            blobPathNames.add(fileNames.get(i));
            LOGGER.debug("Count: {}, File Read: {}.", i + 1, blobPaths.get(i));
        }

        LOGGER.traceExit();
        return new Pair<>(blobPathNames, inputStreamReaders);
    }


    /**
     * Contents of the folder/blob as a stream with it's file name.
     * <p>
     * Example:
     * <pre>
     *     {@code
     *     AzureBlobStreamReader streamReader = new AzureBlobStreamReader("account name", "account key", "container name");
     *     Pair<List<String>, List<InputStreamReader>> reader = streamFolderReader.streamFolderReaderPair("regex");
     *     }
     * </pre>
     *
     * @param regex Regular expression.
     * @return A pair of file names and the stream of data.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     */
    public Pair<List<String>, List<InputStreamReader>> streamFolderReaderRegexPair(String regex) throws URISyntaxException, StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("regex: {}.", regex);

        List<String> blobPathNames = new ArrayList<>();
        List<InputStreamReader> inputStreamReaders = new ArrayList<>();

        StorageCredentialsAccountAndKey accountAndKey = new StorageCredentialsAccountAndKey(this.accountName, this.accountKey);
        CloudStorageAccount account = new CloudStorageAccount(accountAndKey, this.useHttps);
        LOGGER.debug("Account URI: {}.", account.getBlobEndpoint());

        CloudBlobClient cloudBlobClient = account.createCloudBlobClient();
        CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(this.containerName);
        LOGGER.debug("Container Name: {}", this.containerName);

        Pair blobRelativeNames = StorageUtils.getBlobRelativeNames(cloudBlobContainer, "", true, regex);
        LOGGER.debug("Container Name: {}", blobRelativeNames.toString());

        List<String> blobPaths = (List<String>) blobRelativeNames.getKey();
        List<String> fileNames = (List<String>) blobRelativeNames.getValue();
        int count = blobPaths.size();
        LOGGER.debug("Number of Files: {}", count);

        CloudBlockBlob cloudBlockBlob;
        InputStream inputStream;
        for (int i = 0; i < count; i++) {
            cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobPaths.get(i));
            inputStream = cloudBlockBlob.openInputStream();
            inputStreamReaders.add(new InputStreamReader(inputStream));
            blobPathNames.add(fileNames.get(i));
            LOGGER.debug("Count: {}, File Read: {}.", i + 1, blobPaths.get(i));
        }

        LOGGER.traceExit();
        return new Pair<>(blobPathNames, inputStreamReaders);
    }

}
