
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.client.NuxeoClient;
import org.nuxeo.client.objects.Document;
import org.nuxeo.client.objects.blob.Blob;
import org.nuxeo.ecm.restapi.test.RestServerFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.ServletContainerFeature;

import com.nuxeo.sample.EntryPoint;

@RunWith(FeaturesRunner.class)
@Features({ RestServerFeature.class })
@org.nuxeo.runtime.test.runner.Deploy("com.nuxeo.sample.import.test:custom-doc-types.xml")
public class EndToEndTestWithCustomDocType {

	@Inject
	protected ServletContainerFeature servletContainerFeature;

	protected String getBaseURL() {
		int port = servletContainerFeature.getPort();
		return "http://localhost:" + port;
	}

	@Test
	public void testAgainstEmbeddedNuxeoServer() {

		NuxeoClient nuxeoClient = EntryPoint.getClient(getBaseURL());

		Document doc = EntryPoint.createCustomDocument(nuxeoClient, "/");
		assertNotNull(doc);
		assertNotNull(doc.getId());

		assertEquals(EntryPoint.SRC, doc.getPropertyValue("dc:source"));
		assertEquals("A", doc.getPropertyValue("cs:strField1"));
		assertEquals("B", doc.getPropertyValue("cs:strField2"));
		assertNotNull(doc.getPropertyValue("cs:dob"));

		Blob blob = doc.fetchBlob("file:content");
		assertNotNull(blob);
		assertEquals("attachement.png", blob.getFilename());
		assertEquals(92855L, blob.getContentLength());
		
		Blob blob2 = doc.fetchBlob("cs:customBlob");
		assertNotNull(blob2);
		assertEquals("pdf.pdf", blob2.getFilename());
		assertEquals(4827L , blob2.getContentLength());

		nuxeoClient.disconnect();

	}

}
