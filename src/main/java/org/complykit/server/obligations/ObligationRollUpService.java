package org.complykit.server.obligations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.complykit.server.observations.Observation;

@Stateless
public class ObligationRollUpService {

	@SuppressWarnings("unchecked")
	@Schedule(dayOfWeek = "*", hour = "*", minute = "*", second = "*/5", year="*", persistent = false)
	public void runRollUp() {
		
		EntityManager entityManager = getEntityManagerFactory().createEntityManager();
		Query q1 = entityManager.createQuery("from Obligation", Obligation.class);
		List<Obligation> obligations = q1.getResultList();
		
		for (Obligation obligation : obligations ) {
			Query q2 = entityManager.createQuery("from Observation where obligationId = ?", Observation.class);
			q2.setParameter(1, obligation.getId());
			
			int observationCount = q2.getResultList().size();
			//TODO this isn't going to work where we're updating the same set... need a way to mark as stale
			if (observationCount != obligation.getObservationCount()) {
				entityManager.getTransaction().begin();

				System.out.println("Obligation #"+obligation.getId()+" has "+observationCount+" observations");
				
				obligation.setObservationCount(observationCount);
				
				//TODO this would be so much easier with a distinct query, but it's kind of complex in JPA
				// need to figure out a better way to do it
				Map<String, Integer> counts = new HashMap<String, Integer>();
				List<Observation> observations = q2.getResultList();
				for (Observation observation : observations) {
					String observationType = observation.getObservationType();
					Integer count = 0;
					if (counts.containsKey(observationType)) {
						count = counts.get(observationType);
					}
					counts.put(observationType, (count + 1));
				}
				System.out.println("Made a table size "+counts.size());

				//loop through the table
				for (String foundType : counts.keySet()) {
					ObligationObservedSummary summaryObj = findObservedSummary(obligation, foundType);
					if (summaryObj == null) {
						summaryObj = new ObligationObservedSummary();
						summaryObj.setObservationType(foundType);
						summaryObj.setObligationId(obligation.getId());
						obligation.getObservationSummary().add(summaryObj);
					}
					
					double count = counts.get(foundType).doubleValue();
					double pctShare = 0;
					if (obligation.getObservationCount() > 0) // just as added protection
						pctShare = count / obligation.getObservationCount();
					
					summaryObj.setObservations(count);
					summaryObj.setPctShare(pctShare);
					
					entityManager.merge(summaryObj);
				}
				entityManager.merge(obligation);
				entityManager.getTransaction().commit();
			}
		}
		entityManager.close();
		
	}

	private ObligationObservedSummary findObservedSummary(Obligation obligation, String observationType) {
		if (obligation == null)
			return null;
		if (observationType == null)
			return null;
		for (ObligationObservedSummary summaryObj : obligation.getObservationSummary()) {
			if (observationType.equals(summaryObj.getObservationType()))
				return summaryObj;
		}
		return null;
	}
	
	//TODO seriously... need to figure out how to inject this
	private static EntityManagerFactory entityManagerFactory;
	private synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("complykit-jpa");
		}
		return entityManagerFactory;
	}
}
