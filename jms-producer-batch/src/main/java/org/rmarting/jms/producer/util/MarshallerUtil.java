package org.rmarting.jms.producer.util;

import java.io.IOException;

import org.rmarting.jms.producer.model.Catalog;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarshallerUtil {

	private MarshallerUtil() {
		// Not instanciable
	}

	public static String pojo2JSon(Catalog catalog) {
		ObjectMapper mapper = new ObjectMapper();

		// Object to JSON in String
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(catalog);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonInString;
	}

	public static Catalog json2Object(String json) {
		ObjectMapper mapper = new ObjectMapper();

		// JSON from String to Object
		Catalog catalog = null;
		try {
			catalog = mapper.readValue(json, Catalog.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return catalog;
	}

}
