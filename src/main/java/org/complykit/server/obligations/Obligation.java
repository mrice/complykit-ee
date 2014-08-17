package org.complykit.server.obligations;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@Entity
@Table(name="obligations")
@XmlRootElement
public class Obligation implements Serializable {

	//required by Serializable
	private static final long serialVersionUID = -1843851976419729801L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String category;
	private String source;
	private String directive;
	
	//these are set internally:
	private int observationCount;

	@OneToMany(mappedBy="obligationId", fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
	private List<ObligationObservedSummary> observationSummary;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public String getDirective() {
		return directive;
	}
	public void setDirective(String directive) {
		this.directive = directive;
	}
	
	public int getObservationCount() {
		return observationCount;
	}
	public void setObservationCount(int observationCount) {
		this.observationCount = observationCount;
	}

	public List<ObligationObservedSummary> getObservationSummary() {
		return observationSummary;
	}
	public void setObservationSummary(
			List<ObligationObservedSummary> observationSummary) {
		this.observationSummary = observationSummary;
	}

}
