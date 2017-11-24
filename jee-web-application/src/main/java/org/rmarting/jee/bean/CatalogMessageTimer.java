package org.rmarting.jee.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.rmarting.jee.model.Catalog;
import org.rmarting.jee.util.MarshallerUtil;

// FIXME
@Singleton
public class CatalogMessageTimer {

	private static final String[] ARTITS = new String[] { "Madonna", "Eminem", "Michael Jackson", "Heroes del Silencio",
			"Bruce Sprinsteen", "Beyonce" };

	private static final String[] TITLES = new String[] { "Rebel Heart", "Erotica", "Like a Virgin", "Recovery",
			"Infinite", "Encore", "Bad", "Thriller", "Dangerous", "Senderos de Traicion", "El Espiritu del Vino",
			"Senda", "The River", "Born in the USA", "The Rising", "Lucky Town", "Lemonade", "B'day", "Speak My Mind" };

	private static final String[] DESCRIPTIONS = new String[] { "Pop Start", "Hip Hop Singer", "The One",
			"The Best Spanish Rock Band", "The Best", "The Single lady" };

	// FIXME 
	@Schedule(hour = "*", minute = "*/2", second = "0", persistent = false)
	public void sendNewCatalogEntry() {
		String destinationName = "jms/queue/Catalog";
		Context ic = null;
		ConnectionFactory cf = null;
		Connection connection = null;

		try {
			ic = new InitialContext();

			cf = (ConnectionFactory) ic.lookup("ConnectionFactory");
			
			Queue queue = (Queue) ic.lookup(destinationName);

			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(queue);

			connection.start();

			Catalog catalog = new Catalog();
			catalog.setArtist(ARTITS[(new Random().nextInt(ARTITS.length))]);
			catalog.setTitle(TITLES[(new Random().nextInt(TITLES.length))]);
			catalog.setDescription(DESCRIPTIONS[(new Random().nextInt(DESCRIPTIONS.length))]);
			catalog.setPrice(Float.valueOf((new Random(100)).nextInt(40)));
			catalog.setPublicationDate(randomPublicationDate());
			
			String jsonCatalog = MarshallerUtil.pojo2JSon(catalog);

			TextMessage message = session.createTextMessage(jsonCatalog);

			publisher.send(message);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static int randBetween(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}

	private static Date randomPublicationDate() {
		GregorianCalendar gc = new GregorianCalendar();

		gc.set(Calendar.YEAR, randBetween(1930, 2016));

		int dayOfYear = randBetween(1, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
		gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

		return gc.getTime();
	}

}
