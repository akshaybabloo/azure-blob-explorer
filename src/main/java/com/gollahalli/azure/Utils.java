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
import java.util.ArrayList;
import java.util.List;


/**
 * Utilities for {@link AzureBlobUpload} and {@link AzureBlobDownload}.
 */
public class Utils {

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
}