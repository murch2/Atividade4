package analisador;

import java.util.List;
import java.util.Vector;

import modelo.Classe;
import modelo.Condicao;
import modelo.Decisao;
import modelo.DecisaoAux;
import modelo.Metodo;
import modelo.TodasCondicoes;
import modelo.TodasDecisoes;
import modelo.TodasMCDC;
import recoder.java.ProgramElement;
import recoder.abstraction.Method;
import recoder.convenience.TreeWalker;
import recoder.java.CompilationUnit;
import recoder.java.declaration.ClassDeclaration;
import recoder.java.statement.Do;
import recoder.java.statement.For;
import recoder.java.statement.If;
import recoder.java.statement.While;
import suporte.XML;

public class ConditionAnalyzer  {
	
	private static int indexGlobal = 0; 
		
	private List<Decisao> getRequisitiosMCDC(List<Decisao> decisoes) {
		
		List<Decisao> decisoesMCDC = new Vector<Decisao>();

		for (Decisao decisao : decisoes) {
	
	        List<String> tabelaVerdade = new Vector<String>();
	        
	        geraTabelaVerdade(tabelaVerdade, "", 0, getNumeroDeCondicoes(decisao));
	    
	        List<String> copiaTabelaverdade = new Vector<String>();
	        
	        for (String string : tabelaVerdade) {
				copiaTabelaverdade.add(string);
			}
	        
	        List<String> result = new Vector<String>();
	        List<String> testCasesCopy = new Vector<String>();

	        for(String teste: tabelaVerdade) {
	        	testCasesCopy.add(teste);
	        	Decisao decisao2 = new Decisao();
				decisao.copiaDecisao(decisao2,decisao);
				
				setValorCondicoes(decisao2,teste);
				 
				indexGlobal = 0; 
				char valor = valorEsperado(decisao2, teste); 
				
				if (valor == 'T')
					decisao2.setValor(true);
				else 
					decisao2.setValor(false);
				
				result.add(valor + ""); 
								
	        }
	        
	        List<String> testesValidos = this.eliminaTestesAmbuiguos(testCasesCopy, result);
	        
	        for (String testeValido: testesValidos) {
	        	Decisao decisao2 = new Decisao();
				decisao.copiaDecisao(decisao2,decisao);
				
				setValorCondicoes(decisao2,testeValido);
				 
				indexGlobal = 0; 
				char valor = valorEsperado(decisao2, testeValido); 
				
				if (valor == 'T')
					decisao2.setValor(true);
				else 
					decisao2.setValor(false);
				
				decisoesMCDC.add(decisao2); 
	        }	        
		} 
		return decisoesMCDC;
	}
	
	public List<String> eliminaTestesAmbuiguos (List<String> testCases, List<String> saida) {
		List<String> result = new Vector<String>(); 

		String[][] matriz = criaMatriz(testCases, saida);
		
		for (int i = 0; i < matriz.length - 1; i++) {
			for (int j = 0; j < matriz[0].length; j++){
				int k;
				for (k = 0; k < matriz[0].length; k++){
					if (!matriz[i][j].equals(matriz[i][k]) && !matriz[matriz.length-1][j].equals(matriz[matriz.length-1][k])) {
						boolean testeBom = true; 
						for (int index = 0; index < matriz.length - 1 && testeBom; index++)
							if (index != i && !matriz[index][j].equals(matriz[index][k])) {
									testeBom = false; 
							}
						if (testeBom) {
							if (!result.contains(testCases.get(j))){
								result.add(testCases.get(j));
							}
							if (!result.contains(testCases.get(k))){
								result.add(testCases.get(k));
							}
							//Maneira de dar break 
							k = j = matriz[0].length;
						}
					}
				}
			}
		}
		return result; 
	}
	 
	public String[][] criaMatriz (List<String> testCases, List<String> saida) {
		
		String[][] matriz = new String[testCases.get(0).length() + 1][testCases.size()]; 
		int j = 0; 
		
		for (String string : testCases) {
			for (int i = 0; i < string.length(); i++) {
				String s = string.charAt(i) + "";
				matriz[i][j] = s;    
			}
			j++; 
		}
		
		int i = testCases.get(0).length(); 
		
		for (j = 0; j < testCases.size(); j++)
			matriz[i][j] = saida.get(j); 
		
		return matriz; 
	}
	 
	public String setValorCondicoes(Decisao decisao, String teste){
		
		if (decisao.getCondicao() != null) {
			Condicao condicao = decisao.getCondicao();
	
			char c = ' '; 
			c = teste.charAt(0);
			
			if(c == 'T'){
				condicao.setValor(true);
			}	
			else {
				condicao.setValor(false);
			}
			
			teste = teste.substring(1);
			return teste;

		} 
		else {
			Decisao decisaoEsquerda = decisao.getDecisaoEsquerda(); 
			Decisao decisaoDireita = decisao.getDecisaoDireita();
			
			teste = setValorCondicoes(decisaoEsquerda,teste);
			teste = setValorCondicoes(decisaoDireita,teste);
			
			return teste;
		} 
	}	
	 
	public char valorEsperado(Decisao decisao, String teste) {

		if (decisao.getCondicao() != null) {
			char c = ' '; 
			c = teste.charAt(indexGlobal++); 
			return c; 
		} 
		else {
			Decisao decisaoEsquerda = decisao.getDecisaoEsquerda(); 
			Decisao decisaoDireita = decisao.getDecisaoDireita();
			
			if (decisao.getOperador().equals("OR")) {
				if(valorEsperado(decisaoEsquerda,teste) == 'T' || valorEsperado(decisaoDireita,teste) == 'T') {
					return 'T';
				}
				return 'F';
			}
			else if (decisao.getOperador().equals("AND")) {
				if (valorEsperado(decisaoEsquerda,teste) == 'T' && valorEsperado(decisaoDireita,teste) == 'T') {
					return 'T';
				}
				return 'F';
			}
			else {
				return 'F';	
			}
		} 
	}

	public void getTodasCondicoes(List<Method> todosMetodos) {
		TodasCondicoes tc = new TodasCondicoes();

		String nomeClasse = ""; 
		String nomeClasseAnterior = ""; 
		List<Condicao> condicoes = new Vector<Condicao>();
		Classe cls = new Classe();

		for (Method metodo : todosMetodos) {

			nomeClasse = metodo.getContainer().toString(); 
			nomeClasse = nomeClasse.substring(nomeClasse.lastIndexOf(" ") + 1); 

			if (!nomeClasse.equals(nomeClasseAnterior)) {
				nomeClasseAnterior = nomeClasse.toString(); 
				cls = new Classe();
				cls.setNomeClasse(nomeClasse);
				tc.addClasses(cls);
			}

			Metodo m = new Metodo(); 
			m.setMetodo(metodo.getFullName().toString());

			Vector<ProgramElement> decisoes = this.getTodasDecisoesDoMetodo(metodo);
			List<Decisao> todasDecisoes = this.encontraDecisoes(decisoes);
			List<DecisaoAux> decisoesAuxiliares = new Vector<DecisaoAux>();
			
			
			for (Decisao decisao : todasDecisoes) {
				DecisaoAux d = new DecisaoAux(); 
				d.setCodigo(decisao.getCodigo());
				getCondicoesDasDecisoes(decisao, condicoes);
				condicoes = getRequisitosTodasCondicoes(condicoes);
				d.setCondicoes(condicoes);
				decisoesAuxiliares.add(d); 
				condicoes = new Vector<Condicao>();
			}
			
			m.setDecisoesAuxiliares(decisoesAuxiliares);
			cls.addMetodo(m);

		}
		XML.criaXML(tc, XML.Tipo.TODASCONDICOES);
	}

	public void getTodasMCDC(List<Method> todosMetodos) {
		
		TodasMCDC todasmcdc = new TodasMCDC(); 
		
		String nomeClasse = ""; 
		String nomeClasseAnterior = ""; 
		Classe cls = new Classe();

		for (Method metodo : todosMetodos) {

			nomeClasse = metodo.getContainer().toString(); 
			nomeClasse = nomeClasse.substring(nomeClasse.lastIndexOf(" ") + 1); 

			if (!nomeClasse.equals(nomeClasseAnterior)) {
				nomeClasseAnterior = nomeClasse.toString(); 
				cls = new Classe();
				cls.setNomeClasse(nomeClasse);
				todasmcdc.addClasses(cls);
			}

			Metodo m = new Metodo(); 
			m.setMetodo(metodo.getFullName().toString());

			Vector<ProgramElement> decisoes = this.getTodasDecisoesDoMetodo(metodo);
			List<Decisao> todasDecisoes = this.encontraDecisoes(decisoes);
			todasDecisoes = getRequisitiosMCDC(todasDecisoes); 
			m.setDecisoes(todasDecisoes);
			cls.addMetodo(m);
		}
		
		XML.criaXML(todasmcdc, XML.Tipo.MCDC);
	}

	public void getTodasDecisoes(List<Method> todosMetodos) {
		
		TodasDecisoes td = new TodasDecisoes();
		
		String nomeClasse = ""; 
		String nomeClasseAnterior = ""; 
		
		Classe cls = new Classe();
		
		for (Method metodo : todosMetodos) {
			 
			nomeClasse = metodo.getContainer().toString(); 
			nomeClasse = nomeClasse.substring(nomeClasse.lastIndexOf(" ") + 1); 
			
			if (!nomeClasse.equals(nomeClasseAnterior)) {
				nomeClasseAnterior = nomeClasse.toString(); 
				cls = new Classe();
				cls.setNomeClasse(nomeClasse);
				td.addClasses(cls);
			}
			
			Metodo m = new Metodo(); 
			m.setMetodo(metodo.getFullName().toString());
			
			Vector<ProgramElement> decisoes = this.getTodasDecisoesDoMetodo(metodo);
			List<Decisao> todasDecisoes = this.encontraDecisoes(decisoes);
			
			todasDecisoes = getRequisitosTodasDecisoes(todasDecisoes);
			m.setDecisoes(todasDecisoes);
			cls.addMetodo(m);

		}	
		
		XML.criaXML(td, XML.Tipo.TODASDECISOES);
	}
	
	private List<Decisao> getRequisitosTodasDecisoes(List<Decisao> decisoes) {

		List<Decisao> retorno = new Vector<Decisao>();;
		
		for (Decisao decisao : decisoes) {
			Decisao verdadeira = decisao; 
			Decisao falsa = new Decisao(); 
			
			verdadeira.setValor(true);
			
			verdadeira.copiaDecisao(falsa, verdadeira);
			
			falsa.setValor(false);
			retorno.add(falsa);
			retorno.add(verdadeira);
		}
		
		return retorno; 
	}
	
	private List<Decisao> encontraDecisoes (Vector<ProgramElement> decisoes) {
		List<Decisao> result = new Vector<Decisao>();
		 
		for (ProgramElement elemento : decisoes) {
			Decisao decisao = new Decisao();	
			TreeWalker arvore = new TreeWalker(elemento);
			
			try {
				arvore.next(); 
				EncontraDecisãoCompleta.encontraDesicaoCompletaR(arvore, decisao);
			}
			catch (Exception ex) {
				System.err.println("Erro ao Encontrar decisões");
			}
			result.add(decisao);
		}
		return result; 
	}
	
	public List<Method> getTodosMetodos(List<CompilationUnit> arvoreAbstrata) {

		List<Method> metodos = new Vector<Method>();

		//Tutorial do Alexandre
		for (CompilationUnit cunit : arvoreAbstrata) {
			TreeWalker tw = new TreeWalker(cunit);
			while (tw.next()) {
				ProgramElement pe = tw.getProgramElement();
				if (pe instanceof ClassDeclaration) {
					ClassDeclaration cls = (ClassDeclaration)pe;
					metodos.addAll(cls.getMethods()); 
					}
			}
		}
		return metodos;
	}
	
	public Vector<ProgramElement> getTodasDecisoesDoMetodo(Method metodo) {

			Vector<ProgramElement> decisoes = new Vector<ProgramElement>();
			
			TreeWalker tw = new TreeWalker((ProgramElement) metodo);
			
			while (tw.next()) {
				if (tw.next(If.class)) {
					decisoes.add(((If) tw.getProgramElement()).getExpression());
				} 
				else if (tw.next(While.class)) {
					decisoes.add(((While) tw.getProgramElement()).getExpressionAt(0));
				} 
				else if (tw.next(Do.class)) {
					decisoes.add(((Do) tw.getProgramElement()).getChildAt(1));
				}
				else if (tw.next(For.class)) {
					decisoes.add(((For) tw.getProgramElement()).getChildAt(1));
				} 
			}
			
			return decisoes;
		}
	

	private void getCondicoesDasDecisoes(Decisao decisao, List<Condicao> condicoes) {

		if (decisao.getCondicao() != null)
			condicoes.add(decisao.getCondicao());
		
		else {
		
			if (decisao.getDecisaoEsquerda() != null)
				getCondicoesDasDecisoes(decisao.getDecisaoEsquerda(), condicoes);
			
			if (decisao.getDecisaoDireita() != null)
				getCondicoesDasDecisoes(decisao.getDecisaoDireita(), condicoes);
		}
	}

 
	private List<Condicao> getRequisitosTodasCondicoes(List<Condicao> condicoes) {

		List<Condicao> retorno = new Vector<Condicao>(); 

		for (Condicao condicao : condicoes) {

			Condicao verdadeira = condicao;
			Condicao falsa = new Condicao();

			verdadeira.setValor(true);  

			falsa = verdadeira.getCopy();
			falsa.setValor(false);

			retorno.add(falsa);
			retorno.add(verdadeira);
		} 
		return retorno;
	}

	public int getNumeroDeCondicoes(Decisao decisao){

		if (decisao.getCondicao() != null) {
			return 1;
		}
		else {
			if (decisao.getDecisaoDireita() == null)
				return getNumeroDeCondicoes(decisao.getDecisaoEsquerda()); 
			else 
				return (getNumeroDeCondicoes(decisao.getDecisaoEsquerda()) + getNumeroDeCondicoes(decisao.getDecisaoDireita()));
		}
	}
	

    public void geraTabelaVerdade(List<String> tabelaVerdade, String condicoes, int index, int tamanho){
        
    	String condicoesEsquerda ,condicoesDireita;
       
        if (index < tamanho) {
       
            index++;
           
            condicoesEsquerda = condicoes + "T";
            geraTabelaVerdade(tabelaVerdade,condicoesEsquerda, index, tamanho);
           
            condicoesDireita = condicoes + "F";
            geraTabelaVerdade(tabelaVerdade,condicoesDireita, index, tamanho);
        }
        else {
        	tabelaVerdade.add(condicoes);
        }
    }
}