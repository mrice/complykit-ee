package org.complykit.server.obligations;

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

//TODO replace all this implementation with injectable DAO services
@Path("/obligation")
public class ObligationResource {	
	
	//TODO add logging
	//TODO add more robust handling
	//TODO add JSON support

	//TODO put some controls on this so it doesn't blow up the machine on a big data set
	@SuppressWarnings("unchecked")
	@Path("/")
	@GET
	@Produces("text/xml")
	public List<Obligation> list() throws Exception {
		
		EntityManager entityManager = null;
		List<Obligation> result = null;
		try {

			entityManager = getEntityManagerFactory().createEntityManager();
			
			Query query = entityManager.createQuery("from Obligation", Obligation.class);
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
	@Path("/{id}")
	@Produces("text/xml")
	//TODO should I be returning a response object where I have more control?
	public Obligation get(@PathParam("id") String id) throws Exception {
		//TODO switch to guava
		if (id == null)
			throw new Exception("missing id");
		else if (id.trim().length()==0)
			throw new Exception("missing id");
		
		Obligation obligation = null;
		EntityManager entityManager = null;
		try {

			entityManager = getEntityManagerFactory().createEntityManager();
			
			Query query = entityManager.createQuery("select o from Obligation o where id=?", Obligation.class);
			query.setParameter(1, new Long(id));
			obligation = (Obligation)query.getSingleResult();
			
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
		
		return obligation;
	}

	@PUT
	@Path("/{id}")
	@Produces("text/xml")
	@Consumes("text/xml")
	public OperationResult update(@PathParam("id") Long id, Obligation updated) {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();

			Query query = entityManager.createQuery("select o from Obligation o where id=?", Obligation.class);
			query.setParameter(1, id);
			Obligation obligation = (Obligation)query.getSingleResult();
			
			obligation.setCategory(updated.getCategory());
			obligation.setDirective(updated.getDirective());
			obligation.setSource(updated.getSource());

			entityManager.getTransaction().begin();
			System.out.println("About to store obligation: "+obligation.getId());
			
			entityManager.merge(obligation);
			entityManager.getTransaction().commit();

			result.setStatus("updated");
			result.setIdentifier(obligation.getId().toString());
			
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

	@POST
	@Produces("text/xml")
	@Consumes("text/xml")
	public OperationResult insert(Obligation obligation) {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();
			
			entityManager.getTransaction().begin();
			System.out.println("About to store obligation: "+obligation.getDirective());
			
			entityManager.persist(obligation);
			entityManager.getTransaction().commit();

			result.setStatus("inserted");
			result.setIdentifier(obligation.getId().toString());
			
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
	@Path("/{id}")
	@Produces("text/xml")
	public OperationResult delete(@PathParam("id") Long id) {

		OperationResult result = new OperationResult();
		EntityManager entityManager = null;
		try {
			
			entityManager = getEntityManagerFactory().createEntityManager();

			Query query = entityManager.createQuery("select o from Obligation o where id=?", Obligation.class);
			query.setParameter(1, id);
			Obligation obligation = (Obligation)query.getSingleResult();
			
			entityManager.getTransaction().begin();
			System.out.println("About to delete obligation: "+obligation.getId());
			
			entityManager.remove(obligation);
			entityManager.getTransaction().commit();

			//TODO replace these with an enumeration
			result.setStatus("deleted");
			result.setIdentifier(obligation.getId().toString());
			
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
	
	private static EntityManagerFactory entityManagerFactory;
	private synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("complykit-jpa");
		}
		return entityManagerFactory;
	}
	
}
