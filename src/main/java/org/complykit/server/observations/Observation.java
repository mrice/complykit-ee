package org.complykit.server.observations;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@Entity
@Table(name="observations")
@XmlRootElement
public class Observation implements Serializable {
	
	private static final long serialVersionUID = -1989462520379221887L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private Date recorded = new Date();
	private Long obligationId;
	//TODO implement later: private String value;			// the actual value
	//TODO implement later: private String valueType;  		//eg, count, percent, true/false, etc
	private String observationType;	// non-compliant, issue, possible, etc
	private String notes;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Date getRecorded() {
		return recorded;
	}
	public void setRecorded(Date recorded) {
		this.recorded = recorded;
	}

	@Column(name="obligation_id")
	public Long getObligationId() {
		return obligationId;
	}
	public void setObligationId(Long obligationId) {
		this.obligationId = obligationId;
	}

	//TODO implement later
		/*
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Column(name="value_type")
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	*/

	@Column(name="observation_type")
	public String getObservationType() {
		return observationType;
	}
	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}

	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
}
