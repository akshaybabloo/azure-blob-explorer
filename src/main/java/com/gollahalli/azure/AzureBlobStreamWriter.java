package com.gollahalli.azure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
