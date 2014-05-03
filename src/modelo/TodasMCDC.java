package modelo;

import java.util.List;
import java.util.Vector;

public class TodasMCDC {

	private List<Classe> classes;
	
	public TodasMCDC() {
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