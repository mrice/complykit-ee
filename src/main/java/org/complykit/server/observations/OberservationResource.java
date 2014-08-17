package org.complykit.server.observations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.complykit.server.application.OperationResult;
import org.complykit.server.obligations.Obligation;

//TODO replace all this implementation with injectable DAO services

@Path("/obligation/{obligationId}/observation")
public class OberservationResource {
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("")
	@Produces("text/xml")
	public List<Observation> list(@PathParam("obligationId") Long obligationId) {

		EntityManager entityManager = null;
		List<Observation> result = null;
		try {

			entityManager = getEntityManagerFactory().createEntityManager();
			Obligation obligation = retrieveObligation(obligationId);
			
			Query query = entityManager.createQuery("from Observation where obligationId=?", Observation.class);
			query.setParameter(1, obligation.getId());
			result = query.getResultList();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}
			
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}

		return result;		
	}

	@GET
	@Path("/{observationId}")
	@Produces("text/xml")
	public Observation get(@PathParam("obligationId") Long obligationId, @PathParam("observationId") Long obversationId) throws Exception {

		EntityManager entityManager = null;
		Observation result = null;
		//TODO add much better handling here
		Obligation obligation = retrieveObligation(obligationId);		
		if (obligation == null) 
			throw new Exception("failed to find obligation with id: "+obligationId);
		
		try {

			entityManager = getEntityManagerFactory().createEntityManager();
			
			Query query = entityManager.createQuery("from Observation where id=?", Observation.class);
			query.setParameter(1, obversationId);
			result = (Observation)query.getSingleResult();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}
			
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}

		return result;		
	}
	
	@POST
	@Produces("text/xml")
	@Consumes("text/xml")
	public OperationResult insert(@PathParam("obligationId") Long obligationId, Observation observation) throws Exception {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;

		//TODO add much better handling here
		Obligation obligation = retrieveObligation(obligationId);		
		if (obligation == null) 
			throw new Exception("failed to find obligation with id: "+obligationId);
		
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();
			
			entityManager.getTransaction().begin();
			System.out.println("About to store observation for obligation "+obligation.getId());
			
			//this isn't necessary
			//TODO determine whether we should be be doing this in a more JPA way
			observation.setObligationId(obligation.getId());
			
			entityManager.persist(observation);
			entityManager.getTransaction().commit();

			result.setStatus("inserted");
			result.setIdentifier(observation.getId().toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}

			result.setStatus("error");
			
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		
		return result;
	}
	
	@PUT
	@Path("/{observationId}")
	@Produces("text/xml")
	@Consumes("text/xml")
	public OperationResult update(@PathParam("obligationId") Long obligationId, @PathParam("observationId") Long observationId, Observation updated) throws Exception {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;
		//TODO add much better handling here
		Obligation obligation = retrieveObligation(obligationId);		
		if (obligation == null) 
			throw new Exception("failed to find obligation with id: "+obligationId);
		
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();

			Query query = entityManager.createQuery("select o from Observation o where id=?", Observation.class);
			query.setParameter(1, observationId);
			Observation observation = (Observation)query.getSingleResult();
			
			//TODO add in something a little more elegant do make value merge
			observation.setObligationId(obligation.getId());
			observation.setNotes(updated.getNotes());
			observation.setObservationType(updated.getObservationType());
//TODO		observation.setValue(updated.getValue());
//TODO 		observation.setValueType(updated.getValueType());

			entityManager.getTransaction().begin();
			System.out.println("About to store observation: " + observation.getId());
			
			entityManager.merge(observation);
			entityManager.getTransaction().commit();

			result.setStatus("updated");
			result.setIdentifier(observation.getId().toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}

			result.setStatus("error");
			
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		
		return result;
	}

	@DELETE
	@Path("/{observationId}")
	@Produces("text/xml")
	public OperationResult delete(@PathParam("obligationId") Long obligationId, @PathParam("observationId") Long id) throws Exception {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;
		Obligation obligation = retrieveObligation(obligationId);		
		if (obligation == null) 
			throw new Exception("failed to find obligation with id: "+obligationId);
		
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();

			Query query = entityManager.createQuery("select o from Observation o where id=?", Observation.class);
			query.setParameter(1, id);
			Observation observation = (Observation)query.getSingleResult();
			
			entityManager.getTransaction().begin();
			System.out.println("About to delete observation: "+observation.getId());
			
			entityManager.remove(observation);
			entityManager.getTransaction().commit();

			//TODO replace these with an enumeration
			result.setStatus("deleted");
			result.setIdentifier(observation.getId().toString());
			
		} catch (Exception e) {
			
			e.printStackTrace();
			if (entityManager != null) {
				entityManager.getTransaction().rollback();
			}

			result.setStatus("error");
			
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Used internally
	 * @param id
	 * @return
	 */
	private Obligation retrieveObligation(Long id) {

		EntityManager entityManager = null;
		Obligation obligation = null;
		
		try {
			entityManager = getEntityManagerFactory().createEntityManager();

			Query query = entityManager.createQuery("select o from Obligation o where id=?", Obligation.class);
			query.setParameter(1, new Long(id));
			obligation = (Obligation)query.getSingleResult();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		
		return obligation;
		
	}

	private static EntityManagerFactory entityManagerFactory;
	private synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("complykit-jpa");
		}
		return entityManagerFactory;
	}
}
