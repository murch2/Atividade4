package modelo;

import java.util.List;
import java.util.Vector;

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
	
	public void copiaDecisao(DecisaoAux copia, DecisaoAux original) {
		copia.setCodigo(original.getCodigo());
		List<Condicao> copiaCondicoes = new Vector<Condicao>(); 
		
		for (Condicao condicao : original.condicoes) {
			Condicao c = new Condicao(); 
			c = condicao.getCopy(); 
			copiaCondicoes.add(c); 
		}
		copia.setCondicoes(copiaCondicoes);
	}
}
