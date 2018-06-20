package com.gollahalli.azure;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implements deleting files and blobs.
 */
public class AzureBlobDelete {

    private String accountName;
    private String accountKey;
    private String containerName;
    private boolean useHttps;

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
     * @return
     * @throws URISyntaxException
     */
    public URI deleteFile(String blobPathFileName) throws URISyntaxException {

        return new URI("");
    }

    /**
     * @param blobFolderPath
     * @return
     * @throws URISyntaxException
     */
    public URI deleteBlob(String blobFolderPath) throws URISyntaxException {

        return new URI("");
    }
}
