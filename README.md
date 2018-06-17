# Azure Blob Explorer

<p align="center"><img src="https://www.dropbox.com/s/clbr2r25afk3b34/coming-soon-ribbon.jpg?raw=1" alt="Coming Soon" width="500"></p>

A simple and easy to use library to upload all your assets/files/folders to Azure blob containers.

## Instillation

Via Maven

```xml
<dependency>
  <groupId>com.gollahalli.azure</groupId>
  <artifactId>azure-blob-explorer</artifactId>
  <version>1.0</version>
</dependency>
```

## Usage

To upload a file or a folder:

```java
import com.gollahalli.azure.AzureBlobUpload;

AzureBlobUpload azureBlobUpload = new AzureBlobUpload("account name", "account key", "container name");

// Single file.
azureBlobUpload.upload("fileName.txt");

// Folders
azureBlobUpload.recursiveUpload("./folderName/");
```

To Download a file or a folder:

```java
import com.gollahalli.azure.AzureBlobDownload;

AzureBlobDownload azureBlobDownload = new AzureBlobDownload("account name", "account key", "container name");

// Single file.
azureBlobDownload.download("fileName.txt");

// Folders
azureBlobUpload.recursiveDownload("./folderName/");
```
