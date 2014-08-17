package org.complykit.server.application;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement
public class OperationResult implements Serializable {

	//required by Serializable
	private static final long serialVersionUID = 8946828893136322728L;

	private String status;
	private String identifier;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
