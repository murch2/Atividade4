package modelo;

import java.util.List;
import java.util.Vector;

import modelo.Metodo;

public class Classe {
	
	private String nomeClasse; 
	private List<Metodo> metodos;
	
	public Classe () {
		this.metodos = new Vector<Metodo>(); 
	}
	
	public String getNomeClasse() {
		return nomeClasse;
	}
	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}
	public List<Metodo> getMetodos() {
		return metodos;
	}
	public void setMetodos(List<Metodo> metodos) {
		this.metodos = metodos;
	} 
	
	public void addMetodo (Metodo metodo) {
		this.metodos.add(metodo); 
	}
	
	
}
