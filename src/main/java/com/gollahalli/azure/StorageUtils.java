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

import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Utilities for {@link AzureBlobUpload} and {@link AzureBlobDownload}.
 */
public class StorageUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * List all the containers in the account provided.
     *
     * @param containerName Name of the container.
     * @return A list of containers.
     */
    public static List<String> listContainers(String containerName) {
        return null;
    }

    /**
     * List all the containers in the account provided.
     *
     * @param container {@link CloudBlobContainer} object.
     * @return A list of containers.
     */
    public static List<String> listContainers(CloudBlobContainer container) {

        List<String> uris = new ArrayList<String>();

        for (ListBlobItem blobItem : container.listBlobs()) {
            uris.add(blobItem.getUri().toString());
        }
        return uris;
    }

    /**
     * List all the containers in the account provided.
     *
     * @param blobClient {@link CloudBlobClient} object.
     * @return A list of containers.
     */
    public static List<String> listContainers(CloudBlobClient blobClient) {

        List<String> uris = new ArrayList<>();

        for (CloudBlobContainer blobItem : blobClient.listContainers()) {
            uris.add(blobItem.getName());
        }
        return uris;
    }

    /**
     * Creates a container.
     *
     * @param containerName Name of the container.
     * @return A URI of the container created.
     */
    public static String createContainer(String containerName) {
        return null;
    }

    /**
     * Creates a container.
     *
     * @param container {@link CloudBlobContainer} object.
     * @return Name of the container.
     * @throws StorageException If no container is found.
     */
    public static String createContainer(CloudBlobContainer container) throws StorageException {

        container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

        return null;
    }

    /**
     * Checks if a containers exists with the name given.
     *
     * @param containerName Name of the container.
     * @return <code>false</code> if container does not exist, <code>true</code> otherwise.
     */
    public static boolean containerExists(String containerName) {
        return false;
    }

    /**
     * Checks if a containers exists with the name given.
     *
     * @param container     {@link CloudBlobContainer} object.
     * @param containerName Name of the container to check.
     * @return <code>false</code> if container does not exist, <code>true</code> otherwise.
     */
    public static boolean containerExists(CloudBlobContainer container, String containerName) {
        return false;
    }

    /**
     * Checks if a containers exists with the name given.
     *
     * @param cloudBlobClient {@link CloudBlobClient} object.
     * @param containerName   Name of the container to check.
     * @return <code>false</code> if container does not exist, <code>true</code> otherwise.
     */
    public static boolean containerExists(CloudBlobClient cloudBlobClient, String containerName) {

        List<String> containers = listContainers(cloudBlobClient);

        return containers.contains(containerName);
    }

    /**
     * Returns the relative path of all the files in an absolute folder path.
     *
     * @param folderPath Absolute path of the folder.
     * @return All the paths of the files and folder from the root folder.
     */
    public static Pair<List, List> getRelativePaths(String folderPath) {
        // TODO: Add custom pairs.

        String parentDirectory = FilenameUtils.getName(folderPath);

        List<String> relativePaths = new ArrayList<>();
        List<String> absolutePaths = new ArrayList<>();

        File f = new File(folderPath);
        for (File k : FileUtils.listFiles(f, TrueFileFilter.TRUE, TrueFileFilter.TRUE)) {
            absolutePaths.add(k.getPath());
            relativePaths.add(k.getPath().replace(folderPath, parentDirectory));
        }

        return new Pair<>(absolutePaths, relativePaths);
    }

    /**
     * Lists all the contents in a blob recursively.
     *
     * @param cloudBlobContainer {@link CloudBlobClient} object.
     * @param blobFolderName     Path to the blob folder.
     * @return A array of blob URI's
     */
    public static ArrayList<URI> listBlobs(CloudBlobContainer cloudBlobContainer, String blobFolderName) throws URISyntaxException, StorageException {

        ArrayList<URI> uris = new ArrayList<>();

        for (ListBlobItem blob : cloudBlobContainer.listBlobs(blobFolderName, true)) {
            uris.add(blob.getUri());
        }

        return uris;
    }

    /**
     * @param cloudBlobContainer {@link CloudBlobContainer} object.
     * @param blobFolderName     Blob folder path.
     * @param folderPath         Local folder path (where you want to save).
     * @param keepBlobName       Keep the root name of the folder.
     * @return A pair of <code>blobPath</code> and <code>folderFilePath</code>.
     * @throws URISyntaxException If an invalid account name is provided.
     * @throws StorageException   Storage error.
     */
    public static Pair<List, List> getBlobRelativePaths(CloudBlobContainer cloudBlobContainer, String blobFolderName, String folderPath, boolean keepBlobName) throws URISyntaxException, StorageException {
        // TODO: Add custom pairs.
        List<String> blobPath = new ArrayList<>();
        List<String> folderFilePath = new ArrayList<>();
        String blobRootName = FilenameUtils.getName(blobFolderName);

        for (ListBlobItem blob : cloudBlobContainer.listBlobs(blobFolderName, true)) {
            if (keepBlobName) {
                blobPath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));
                folderFilePath.add(FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", "")));
            } else {
                blobPath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", ""));
                folderFilePath.add(FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", "")));
            }
        }

        return new Pair<>(blobPath, folderFilePath);
    }
}
