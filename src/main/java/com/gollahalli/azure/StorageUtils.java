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
 * Utilities for {@link AzureBlobUpload}, {@link AzureBlobDownload} and {@link AzureBlobDelete}.
 */
public class StorageUtils {

    private static final Logger LOGGER = LogManager.getLogger(StorageUtils.class.getName());

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
     * @param container {@link CloudBlobContainer} object.
     * @return URI of the container.
     * @throws StorageException If no container is found.
     */
    public static URI createContainer(CloudBlobContainer container) throws StorageException {
        LOGGER.traceEntry();
        LOGGER.debug("Creating Container: {}", container.getName());

        container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());

        LOGGER.debug("'{}' Created, URI: {}", container.getName(), container.getUri());
        LOGGER.traceExit("Container created.");
        return container.getUri();
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
        LOGGER.traceEntry();
        LOGGER.debug("Folder Path: {}.", folderPath);

        String parentDirectory = FilenameUtils.getName(folderPath);

        List<String> relativePaths = new ArrayList<>();
        List<String> absolutePaths = new ArrayList<>();

        File f = new File(folderPath);
        for (File k : FileUtils.listFiles(f, TrueFileFilter.TRUE, TrueFileFilter.TRUE)) {
            absolutePaths.add(k.getPath());
            relativePaths.add(k.getPath().replace(folderPath, parentDirectory));
            LOGGER.debug("Absolute Path: {}, Relative Path: {}.", k.getPath(), k.getPath().replace(folderPath, parentDirectory));
        }

        LOGGER.traceExit();
        return new Pair<>(absolutePaths, relativePaths);
    }

    /**
     * Lists all the contents in a blob recursively.
     *
     * @param cloudBlobContainer {@link CloudBlobContainer} object.
     * @param blobFolderName     Path to the blob folder.
     * @return A array of blob URI's
     */
    public static List<String> listBlobs(CloudBlobContainer cloudBlobContainer, String blobFolderName) {
        LOGGER.traceEntry();
        LOGGER.debug("Cloud Container Name: '{}', Blob Path to List: '{}'.", cloudBlobContainer, blobFolderName);

        List<String> paths = new ArrayList<>();

        for (ListBlobItem listBlobItem : cloudBlobContainer.listBlobs(blobFolderName, true)) {
            if (listBlobItem instanceof CloudBlob) {
                LOGGER.debug("Paths: {}.", ((CloudBlob) listBlobItem).getName());
                paths.add(((CloudBlob) listBlobItem).getName());
            }
        }

        LOGGER.traceExit();
        return paths;
    }

    /**
     * Get all the blob paths with its respective local paths to save in.
     *
     * @param cloudBlobContainer {@link CloudBlobContainer} object.
     * @param blobFolderName     Blob folder path.
     * @param folderPath         Local folder path (where you want to save).
     * @param keepBlobName       Keep the root name of the folder.
     * @return A pair of <code>blobPath</code> and <code>folderFilePath</code>.
     */
    public static Pair<List, List> getBlobRelativePaths(CloudBlobContainer cloudBlobContainer, String blobFolderName, String folderPath, boolean keepBlobName) {
        LOGGER.traceEntry();
        // TODO: Add custom pairs.
        List<String> blobPath = new ArrayList<>();
        List<String> folderFilePath = new ArrayList<>();
        String blobRootName = FilenameUtils.getName(blobFolderName);
        LOGGER.debug("blobRootName: {}", blobRootName);

        try {
            for (ListBlobItem blob : cloudBlobContainer.listBlobs(blobFolderName, true)) {

                blobPath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));
                LOGGER.debug("Blob Path: {}.", blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));

                if (keepBlobName) {
                    folderFilePath.add(FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", "")));
                    LOGGER.debug("Folder Path: {}.", FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", "")));
                } else {
                    folderFilePath.add(FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", "")));
                    LOGGER.debug("Folder Path: {}.", FilenameUtils.concat(folderPath, blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", "")));
                }
            }
        } catch (StorageException e) {
            System.out.println(e);
            LOGGER.error(e.getStackTrace());
        } catch (URISyntaxException e) {
            System.out.println(e);
            LOGGER.error(e.getStackTrace());
        }

        return new Pair<>(blobPath, folderFilePath);
    }

    /**
     * Just like {@link #getBlobRelativePaths} but instead of returning the local bath to store it returns the file name
     * and blob paths.
     *
     * @param cloudBlobContainer {@link CloudBlobContainer} object.
     * @param blobFolderName     Blob folder path.
     * @param keepBlobName       Keep the root name of the folder.
     * @return A pair of list of <code>blobPath</code> and <code>folderFilePath</code>.
     */
    public static Pair<List, List> getBlobRelativeNames(CloudBlobContainer cloudBlobContainer, String blobFolderName, boolean keepBlobName) {
        LOGGER.traceEntry();
        // TODO: Add custom pairs.
        List<String> blobPath = new ArrayList<>();
        List<String> folderFilePath = new ArrayList<>();
        String blobRootName = FilenameUtils.getName(blobFolderName);
        LOGGER.debug("blobRootName: {}", blobRootName);

        try {
            for (ListBlobItem blob : cloudBlobContainer.listBlobs(blobFolderName, true)) {

                blobPath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));
                LOGGER.debug("Blob Path: {}.", blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));

                if (keepBlobName) {
                    folderFilePath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));
                    LOGGER.debug("Folder Path: {}.", blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/", ""));
                } else {
                    folderFilePath.add(blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", ""));
                    LOGGER.debug("Folder Path: {}.", blob.getUri().toString().replace(blob.getContainer().getUri().toString() + "/" + blobRootName + "/", ""));
                }
            }
        } catch (StorageException e) {
            System.out.println(e);
            LOGGER.error(e.getStackTrace());
        } catch (URISyntaxException e) {
            System.out.println(e);
            LOGGER.error(e.getStackTrace());
        }

        return new Pair<>(blobPath, folderFilePath);
    }
}
