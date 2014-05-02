package modelo;

import java.util.List;

public class DecisaoAux {
	private String Codigo; 
	private List<Condicao> condicoes;
	
	
	public String getCodigo() {
		return Codigo;
	}
	
	public void setCodigo(String codigo) {
		Codigo = codigo;
	}
	
	public List<Condicao> getCondicoes() {
		return condicoes;
	}
	
	public void setCondicoes(List<Condicao> condicoes) {
		this.condicoes = condicoes;
	} 
}
