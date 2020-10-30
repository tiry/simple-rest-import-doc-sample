
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
public class EndToEndTest {

	@Inject
	protected ServletContainerFeature servletContainerFeature;

	protected String getBaseURL() {
		int port = servletContainerFeature.getPort();
		return "http://localhost:" + port;
	}

	@Test
	public void testAgainstEmbeddedNuxeoServer() {

		NuxeoClient nuxeoClient = EntryPoint.getClient(getBaseURL());

		Document doc = EntryPoint.createDocument(nuxeoClient, "/");
		assertNotNull(doc);
		assertNotNull(doc.getId());

		// doc.fet
		assertEquals(EntryPoint.SRC, doc.getPropertyValue("dc:source"));

		Blob blob = doc.fetchBlob();
		assertNotNull(blob);
		assertEquals("attachement.png", blob.getFilename());
		assertEquals(92855L, blob.getContentLength());

		nuxeoClient.disconnect();

	}

}
