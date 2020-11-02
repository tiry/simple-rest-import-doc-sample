package com.nuxeo.sample;

import java.io.InputStream;
import java.util.GregorianCalendar;

import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.client.objects.blob.StreamBlob;
import org.nuxeo.client.objects.upload.BatchUpload;
import org.nuxeo.client.objects.upload.BatchUploadManager;

public class EntryPoint {

	public static final String SRC = "Java Client";

	public static NuxeoClient getClient(String connectionURL) {
		NuxeoClient nuxeoClient = new NuxeoClient.Builder().url(connectionURL)
				.authentication("Administrator", "Administrator").connect();
		// fetch all schemas
		nuxeoClient = nuxeoClient.schemas("*");
		return nuxeoClient;
	}

	public static void setMetaData(Document document) {
		document.setPropertyValue("dc:title", "new title");
		document.setPropertyValue("dc:source", SRC);
		if (document.getType().equals("CustomDoc")) {
			document.setPropertyValue("cs:strField1","A");
			document.setPropertyValue("cs:strField2","B");
			document.setPropertyValue("cs:dob","2020-11-03T00:00:00.000+01:00");
		}
	}

	public static void setBlob(BatchUploadManager batchUploadManager, Document document, Blob blob) {
		BatchUpload batchUpload = batchUploadManager.createBatch();
		batchUpload = batchUpload.upload("1", blob);
		document.setPropertyValue("file:content", batchUpload.getBatchBlob());
	}

	public static void setBlobs(BatchUploadManager batchUploadManager, Document document, Blob[] blobs) {
		BatchUpload batchUpload = batchUploadManager.createBatch();
		
		int fileIdx = 1;
		for (Blob blob:blobs) {
			batchUpload = batchUpload.upload(fileIdx+"", blob);
			fileIdx++;
		}
		document.setPropertyValue("file:content", batchUpload.getBatchBlob("1"));
		document.setPropertyValue("cs:customBlob", batchUpload.getBatchBlob("2"));
		
	}
	public static Document createDocument(NuxeoClient nuxeoClient, String folderPath) {

		Document root = nuxeoClient.repository().fetchDocumentRoot();

		Document targetFolder = nuxeoClient.repository().fetchDocumentByPath(folderPath);

		InputStream stream = EntryPoint.class.getResourceAsStream("/picture.png");
		Blob blob = new StreamBlob(stream, "attachement.png");
		BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();

		// first create a Document of type 'File'
		Document document = Document.createWithName("file", "File");

		// attach a blob to it
		setBlob(batchUploadManager, document, blob);
		
		// set the meta-data
		setMetaData(document);

		document = nuxeoClient.repository().createDocumentByPath(targetFolder.getPath(), document);

		return document;
	}


	public static Document createCustomDocument(NuxeoClient nuxeoClient, String folderPath) {

		Document root = nuxeoClient.repository().fetchDocumentRoot();

		Document targetFolder = nuxeoClient.repository().fetchDocumentByPath(folderPath);

		InputStream stream1 = EntryPoint.class.getResourceAsStream("/picture.png");
		Blob blob1 = new StreamBlob(stream1, "attachement.png");
		
		InputStream stream2 = EntryPoint.class.getResourceAsStream("/sample.pdf");
		Blob blob2 = new StreamBlob(stream2, "pdf.pdf");
		
		BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();

		// first create a Document of type 'File'
		Document document = Document.createWithName("custom", "CustomDoc");

		// attach a blob to it
		setBlobs(batchUploadManager, document, new Blob[] {blob1, blob2});
		
		// set the meta-data
		setMetaData(document);

		document = nuxeoClient.repository().createDocumentByPath(targetFolder.getPath(), document);

		return document;
	}

	public static void main(String[] args) {

		// default parameter to target a local running Nuxeo
		String connectionURL = "http://127.0.0.1:8080/nuxeo";
		String folderPath = "/default-domain/workspaces/Import";

		if (args != null) {
			connectionURL = args[0];
			folderPath = args[1];
		}

		NuxeoClient nuxeoClient = getClient(connectionURL);
		
		createDocument(nuxeoClient, folderPath);
		
		nuxeoClient.disconnect();
	}

}
