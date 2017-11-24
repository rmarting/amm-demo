package org.rmarting.jms.consumer.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.rmarting.jms.consumer.model.Catalog;
import org.springframework.stereotype.Repository;

/**
 * 
 */
@Transactional
@Repository
public class CatalogService {
	
	@PersistenceContext
	private EntityManager em;

	public void create(Catalog entity) {
		em.persist(entity);
	}

}
