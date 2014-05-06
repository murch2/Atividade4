package modelo;

public class Condicao {
	
	private String argumentoEsquerda;
	private String argumentoDireita;
	private String operador;
	
	private String codigo;
	private boolean valor; 
	
	public Condicao() {
		
	}

	public String getArgumentoEsquerda() {
		return argumentoEsquerda;
	}


	public void setArgumentoEsquerda(String argumentoEsquerda) {
		this.argumentoEsquerda = argumentoEsquerda;
	}


	public String getArgumentoDireita() {
		return argumentoDireita;
	}


	public void setArgumentoDireita(String argumentoDireita) {
		this.argumentoDireita = argumentoDireita;
	}


	public String getOperador() {
		return operador;
	}


	public void setOperador(String operador) {
		this.operador = operador;
	}



	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public Condicao getCopy(){
		Condicao nova = new Condicao();
		nova.setArgumentoEsquerda(this.argumentoEsquerda);
		nova.setArgumentoDireita(this.argumentoDireita);
		nova.setOperador(this.operador);
		nova.setCodigo(this.codigo);
		nova.setValor(this.getValor());	
		return nova;
	}

	public boolean getValor() {
		return valor;
	}

	public void setValor(boolean valor) {
		this.valor = valor;
	}
}