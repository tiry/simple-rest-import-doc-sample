package com.nuxeo.sample;

import java.io.InputStream;

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
	}

	public static void setBlob(BatchUploadManager batchUploadManager, Document document, Blob blob) {
		BatchUpload batchUpload = batchUploadManager.createBatch();
		batchUpload = batchUpload.upload("1", blob);
		document.setPropertyValue("file:content", batchUpload.getBatchBlob());
	}

	public static Document createDocument(NuxeoClient nuxeoClient, String folderPath) {

		Document root = nuxeoClient.repository().fetchDocumentRoot();

		Document targetFolder = nuxeoClient.repository().fetchDocumentByPath(folderPath);

		InputStream stream = EntryPoint.class.getResourceAsStream("/picture.png");
		Blob blob = new StreamBlob(stream, "attachement.png");
		BatchUploadManager batchUploadManager = nuxeoClient.batchUploadManager();

		Document document = Document.createWithName("file", "File");

		setBlob(batchUploadManager, document, blob);
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
