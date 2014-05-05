package modelo;

public class Decisao {
	
	private Decisao decisaoEsquerda;
	private Decisao decisaoDireita; 
	private String operador;
	
	private String codigo;
	private Boolean valor;
	private Condicao condicao;

	public Decisao getDecisaoEsquerda() {
		return decisaoEsquerda;
	}

	public void setDecisaoEsquerda(Decisao decisaoEsquerda) {
		this.decisaoEsquerda = decisaoEsquerda;
	}

	public Decisao getDecisaoDireita() {
		return decisaoDireita;
	}

	public void setDecisaoDireita(Decisao decisaoDireita) {
		this.decisaoDireita = decisaoDireita;
	}

	public String getOperador() {
		return operador;
	}

	public void setOperador(String operador) {
		this.operador = operador;
	}
	
	public Condicao getCondicao() {
		return condicao;
	}

	public void setCondicao(Condicao condicao) {
		this.condicao = condicao; 
		this.setOperador(null); 
		this.setDecisaoEsquerda(null);
		this.setDecisaoDireita(null);
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Decisao() {
		
	}
	
	public void copiaDecisao(Decisao copia, Decisao original) {
		Condicao condition = new Condicao();
		
		if (original.getCondicao() != null) {
			condition = original.getCondicao().getCopy();
			copia.setCondicao(condition);
		}
		if (original.getDecisaoEsquerda() != null) {
			Decisao decision_left = new Decisao();
			copiaDecisao(decision_left,original.getDecisaoEsquerda());
			copia.setDecisaoEsquerda(decision_left);
		}
		if (original.getDecisaoDireita() != null) { 
			Decisao decision_right = new Decisao();
			copiaDecisao(decision_right,original.getDecisaoDireita()); 
			copia.setDecisaoDireita(decision_right);
		}									 	
				
		copia.setOperador(original.getOperador());
		copia.setCodigo(original.getCodigo());
		copia.setValor(original.getValor()); 
	}
	
}