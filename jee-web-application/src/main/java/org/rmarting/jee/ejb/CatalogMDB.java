package org.rmarting.jee.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rmarting.jee.model.Catalog;
import org.rmarting.jee.util.MarshallerUtil;

/**
 * Message-Driven Bean implementation class for: CatalogMDB
 */
@MessageDriven(
	name = "CatalogMDB",
	activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/Catalog"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
	} 
)
public class CatalogMDB implements MessageListener {
	
	@PersistenceContext(unitName = "jee-web-application-persistence-unit")
	private EntityManager em;

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		Catalog catalog = null;
		
		if (message instanceof TextMessage) {
			TextMessage json = (TextMessage) message;
			
			try {
				catalog = MarshallerUtil.json2Object(json.getText());
				
				em.persist(catalog);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// TODO add logging		
		System.out.println("Message consumed: " + catalog);
	}

}
