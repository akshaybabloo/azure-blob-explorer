# Azure Blob Explorer

A simple and easy to use library to upload, download and delete all your assets/files/folders to Azure blob containers.

## Instillation

Via Maven

```xml
<dependency>
  <groupId>com.gollahalli.azure</groupId>
  <artifactId>azure-blob-explorer</artifactId>
  <version>1.1.1</version>
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
azureBlobDownload.downloadFile("fileName.txt", "path/to/download/");

// Folder
azureBlobUpload.downloadFolder("./folderName/", "path/to/download/");
```

To delete a file, folder or a container:

```java
AzureBlobDelete blobDelete = new AzureBlobDelete("account name", "account key", "container name");

// Delete file.
blobDelete.deleteFile("blobName/fileName.txt");

// Delete a blob folder and its content
blobDelete.deleteBlob("blobName/BlobToDelete/");

// Delete container
blobDelete.deleteContainer();
```
