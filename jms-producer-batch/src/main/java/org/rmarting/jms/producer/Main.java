/**
 * 
 */
package org.rmarting.jms.producer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.rmarting.jms.producer.model.Catalog;
import org.rmarting.jms.producer.util.MarshallerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rmarting
 */
public class Main {

	private static final String[] ARTITS = new String[] { "Madonna", "Eminem", "Michael Jackson", "Heroes del Silencio",
			"Bruce Sprinsteen", "Beyonce" };

	private static final String[] TITLES = new String[] { "Rebel Heart", "Erotica", "Like a Virgin", "Recovery",
			"Infinite", "Encore", "Bad", "Thriller", "Dangerous", "Senderos de Traicion", "El Espiritu del Vino",
			"Senda", "The River", "Born in the USA", "The Rising", "Lucky Town", "Lemonade", "B'day", "Speak My Mind" };

	private static final String[] DESCRIPTIONS = new String[] { "Pop Start", "Hip Hop Singer", "The One",
			"The Best Spanish Rock Band", "The Best", "The Single lady" };

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("Starting JMS Batch Producer");
		
		try {
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://amq-broker-amq-tcp:61616");

			// Create a Connection
			Connection connection = connectionFactory.createConnection("admin", "admin");
			connection.start();
			
			logger.info("Connected to AMQ Broker");

			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue("jms/queue/Catalog");
			
			logger.info("Destination defined");

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			
			// Create a messages
			Catalog catalog = new Catalog();
			catalog.setArtist(ARTITS[(new Random().nextInt(ARTITS.length))]);
			catalog.setTitle(TITLES[(new Random().nextInt(TITLES.length))]);
			catalog.setDescription(DESCRIPTIONS[(new Random().nextInt(DESCRIPTIONS.length))]);
			catalog.setPrice(Float.valueOf((new Random(100)).nextInt(40)));
			catalog.setPublicationDate(randomPublicationDate());

			String jsonCatalog = MarshallerUtil.pojo2JSon(catalog);

			TextMessage message = session.createTextMessage(jsonCatalog);

			logger.info("Created Catalog to send");
			
			producer.send(message);

			logger.info("Catalog data sent");
			
			// Clean up
			session.close();
			connection.close();
		} catch (Exception e) {
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
		
		logger.info("Ended JMS Batch Producer");
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
