package org.rmarting.jms.consumer.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.rmarting.jms.consumer.model.Catalog;
import org.rmarting.jms.consumer.service.CatalogService;
import org.springframework.stereotype.Component;

@Component
public class JmsRoutes extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("amq:queue:jms/queue/Catalog")
			.id("inserto-into-db")
			.log("Saving '#${body}' into DB")
			.unmarshal().json(JsonLibrary.Jackson, Catalog.class)
			.bean(CatalogService.class, "create");
	}

}
