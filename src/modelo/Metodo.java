package modelo;

import java.util.List;
import java.util.Vector;

import analisador.ConditionAnalyzer;

public class Metodo {
	private String metodo; 
	private List<Decisao> decisoes;
	private List<DecisaoAux> decisoesAuxiliares; 
	
	public Metodo() {
		this.setDecisoesAuxiliares(null);  
	}
	
	public String getMetodo() {
		return metodo;
	}
	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}
	public List<Decisao> getDecisoes() {
		return decisoes;
	}
	public void setDecisoes(List<Decisao> decisoes) {
		this.decisoes = decisoes;
	}

	public List<DecisaoAux> getDecisoesAuxiliares() {
		return decisoesAuxiliares;
	}

	public void setDecisoesAuxiliares(List<DecisaoAux> decisoesAuxiliares) {
		this.decisoesAuxiliares = decisoesAuxiliares;
	}
	
}
