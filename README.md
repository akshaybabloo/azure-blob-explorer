# Azure Blob Explorer

A simple and easy to use library to upload all your assets/files/folders to Azure blob containers.

> Download files have not yet been implemented.

## Instillation

Via Maven

```xml
<dependency>
  <groupId>com.gollahalli.azure</groupId>
  <artifactId>azure-blob-explorer</artifactId>
  <version>0.1.0-BETA</version>
</dependency>
```

## Usage

To upload a file or a folder:

```java
import com.gollahalli.azure.AzureBlobUpload;

AzureBlobUpload azureBlobUpload = new AzureBlobUpload("account name", "account key", "container name");

// Single file.
azureBlobUpload.uploadFromFile("path/to/file.txt");

// Folder
azureBlobUpload.uploadFromFolder("path/to/folder/");
```

To Download a file or a folder:

```java
import com.gollahalli.azure.AzureBlobDownload;

AzureBlobDownload azureBlobDownload = new AzureBlobDownload("account name", "account key", "container name");

// Single file.
azureBlobDownload.downloadFile("fileName.txt");

// Folder
azureBlobUpload.downloadFolder("./folderName/");
```
