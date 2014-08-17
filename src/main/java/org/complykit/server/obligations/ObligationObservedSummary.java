package org.complykit.server.obligations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="observation_summaries")
@XmlRootElement
@SuppressWarnings("restriction")
public class ObligationObservedSummary {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="obligation_id")
	private Long obligationId;
	
	@Column(name="observation_type")
	private String observationType;
	
	private Double observations;
	
	@Column(name="pct_share")
	private Double pctShare;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getObligationId() {
		return obligationId;
	}
	public void setObligationId(Long obligationId) {
		this.obligationId = obligationId;
	}

	public String getObservationType() {
		return observationType;
	}
	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}

	public Double getObservations() {
		return observations;
	}
	public void setObservations(Double observations) {
		this.observations = observations;
	}

	public Double getPctShare() {
		return pctShare;
	}
	public void setPctShare(Double pctShare) {
		this.pctShare = pctShare;
	}
	
}
