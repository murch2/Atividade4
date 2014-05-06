package modelo;

import java.util.List;
import java.util.Vector;

public class TodasCondicoes {
	
	private List<Classe> classes;
	
	
	public TodasCondicoes() {
		this.classes = new Vector<Classe>();
	}

	public List<Classe> getClasses() {
		return classes;
	}

	public void setClasses(List<Classe> classes) {
		this.classes = classes;
	}
	
	public void addClasses(Classe cls) {
		this.classes.add(cls); 
	}
}
