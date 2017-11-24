package org.jms.batch.producer.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.rmarting.jms.producer.model.Catalog;
import org.rmarting.jms.producer.util.MarshallerUtil;

public class MarshallerUtilTest {

	private Date getPublicationDate() {
		GregorianCalendar gc = new GregorianCalendar();

		gc.set(2017, Calendar.NOVEMBER, 23, 0, 0, 0);
		gc.set(Calendar.MILLISECOND, 0);

		return gc.getTime();
	}

	@Test
	public void testPojo2JSon() {
		Catalog catalog = new Catalog();
		catalog.setArtist("Test Artist");
		catalog.setTitle("Test Title");
		catalog.setDescription("Test Description");
		catalog.setPrice(10f);
		catalog.setPublicationDate(getPublicationDate());

		String jsonCatalog = MarshallerUtil.pojo2JSon(catalog);

		assertEquals(
				"{\"id\":null,\"version\":0,\"artist\":\"Test Artist\",\"title\":\"Test Title\",\"description\":\"Test Description\",\"price\":10.0,\"publicationDate\":1511391600000}",
				jsonCatalog);
	}

	@Test
	public void testJson2Object() {
		String jsonCatalog = "{\"id\":1,\"version\":0,\"artist\":\"Test Artist\",\"title\":\"Test Title\",\"description\":\"Test Description\",\"price\":10.0,\"publicationDate\":1511391600000}";

		Catalog catalog = MarshallerUtil.json2Object(jsonCatalog);

		assertEquals(Long.valueOf(1), catalog.getId());
		assertEquals("Test Artist", catalog.getArtist());
		assertEquals("Test Title", catalog.getTitle());
		assertEquals("Test Description", catalog.getDescription());
		assertEquals(Float.valueOf(10), catalog.getPrice());
		assertEquals(getPublicationDate(), catalog.getPublicationDate());
	}

}
