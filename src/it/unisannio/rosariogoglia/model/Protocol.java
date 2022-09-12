package it.unisannio.rosariogoglia.model;

public class Protocol {
	
	private int idProtocol;
	private String protocol;
	
	
	public Protocol() {}
	
	
	public Protocol(int idProtocol, String protocol) {
		this.idProtocol = idProtocol;
		this.protocol = protocol;
	}


	public int getIdProtocol() {
		return idProtocol;
	}


	public void setIdProtocol(int idProtocol) {
		this.idProtocol = idProtocol;
	}


	public String getProtocol() {
		return protocol;
	}


	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	
	
	

}
