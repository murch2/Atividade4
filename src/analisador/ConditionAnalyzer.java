package analisador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import modelo.Classe;
import modelo.Condicao;
import modelo.Decisao;
import modelo.DecisaoAux;
import modelo.Metodo;
import modelo.TodasCondicoes;
import modelo.TodasDecisoes;
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;

public class ConditionAnalyzer  {


		
	//Retorna um array com cada decisão representada em todos os seus possiveis valores(TODOS)
	private List<Decisao> getMCDCRequirements(List<Decisao> decisions) {
		
		List<Decisao> mcdcdecisions_temp = new Vector<Decisao>();;
		Iterator<Decisao> iterator = decisions.iterator(); 
		boolean auz = true; 
		while (iterator.hasNext()) {
			
			Decisao decision = iterator.next();
			
//	        String [] testcases_str = null;
	        List<String> testCases = new Vector<String>();
	        
	        // get all testCases (2^n)
	        getAllRequirementsFromConditions(testCases, "", 0, getNumberOfConditions(decision));
	        
	        //Copia do testCase porque ele é destruido. 
	    
	        List<String> testCasesCopy = new Vector<String>();
	        
	        for (String string : testCases) {
				testCasesCopy.add(string);
			}
	        
	        List<String> result = new Vector<String>(); 
	         
	        for(String test: testCases) {
	        	List<Boolean> testList = new Vector<Boolean>();
	        	List<Boolean> testList2 = new Vector<Boolean>();
	        	
	        	//Pegou a lista de String passou pra booleano e duplicou
				while(test.length() != 0) {
					char chr = ' ';
					chr = test.charAt(0);
					test = test.substring(1);
					
					if(chr == 'T'){
						testList.add(true); 
						testList2.add(true);
					}
					else {
						testList.add(false);
						testList2.add(false);
					}
					 
				}
				
				
	        	Decisao decision2 = new Decisao();
				decision.copiaDecisao(decision2,decision);
				//Até aqui ele duplicou as decisões e criou o vetor
				
				setTestCase(decision2,testList);
				
				boolean valor = expectedValue(decision2, testList2); 
				decision2.setValor(valor);
				
				if (valor) {
					result.add("T"); 
				} 
				else {
					result.add("F"); 
				}

				mcdcdecisions_temp.add(decision2);  
	        }
//			testCasesCopy tem a tabela verdade nesse ponto
	        this.eliminaTestesAmbuiguos(testCasesCopy, result); 
	        
		} 
		return mcdcdecisions_temp;
	}
	
	//Algoritmo do n³
	public List<String> eliminaTestesAmbuiguos (List<String> testCases, List<String> saida) {
		List<String> result = new Vector<String>(); 
		
		String[][] matriz = criaMatriz(testCases, saida);
		
		for (int i = 0; i < matriz.length; i++) {
			System.out.println("");
			for (int j = 0; j < matriz[0].length; j++)
				System.out.print(matriz[i][j] + " ");
		}
		
		return result; 
	}
	
	//Só chama se tiver testes 
	public String[][] criaMatriz (List<String> testCases, List<String> saida) {
		System.out.println("Saida : " + saida);
		
		System.out.println("TestCases: " + testCases);
		
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
	
	//Isso gera todas as combinações possiveis da tabela verdade e guarda em testCases.
    public void getAllRequirementsFromConditions(List<String> testCases, String conditions, int nivel, int nivelmax){
        
    	String conditions_left,conditions_right;
       
        if (nivel < nivelmax) {
       
            nivel++;
           
            conditions_left = conditions + "T";
            getAllRequirementsFromConditions(testCases,conditions_left, nivel, nivelmax);
           
            conditions_right = conditions + "F";
            getAllRequirementsFromConditions(testCases,conditions_right, nivel, nivelmax);
        }
        else {
        	testCases.add(conditions);
        }
    }
	
	
	//Coloca os valores do vetor booleano na respectiva condição. 
	public List<Boolean> setTestCase(Decisao decision, List<Boolean> testCase){

		// base o recursion
		if (decision.getCondicao() != null) {
			
			Condicao condition = decision.getCondicao();
	
			if (testCase.get(0) == true) {
				condition.setValor(true);
			}	
			else {
				condition.setValor(false);
			}
			
			testCase.remove(0);
			return testCase;

		} 
		else {
			Decisao lhs = decision.getDecisaoEsquerda(); 
			Decisao rhs = decision.getDecisaoDireita();
			
			testCase = setTestCase(lhs,testCase);
			testCase = setTestCase(rhs,testCase);
			
			return testCase;
		} 
	}	
	
	
	
	//Devolve se a decisão Completa eh true ou false. 
	public Boolean expectedValue(Decisao decision, List<Boolean> testCase) {

		// base o recursion
		if (decision.getCondicao() != null) {
			
			if(testCase.get(0) == true) {
				testCase.remove(0);
				return true;
			}	
			else {
				testCase.remove(0);
				return false; 
			}
		} 
		else {
			Decisao lhs = decision.getDecisaoEsquerda(); 
			Decisao rhs = decision.getDecisaoDireita();
			
			if (decision.getOperador().equals("OR")) {

				if(expectedValue(lhs,testCase) || expectedValue(rhs,testCase)) {

					return true;
				}
				
				return false;
			}
			else if (decision.getOperador().equals("AND")) {
				
				if (expectedValue(lhs,testCase) && expectedValue(rhs,testCase)) {
					
					return true;
				}
				
				return false;
			}
			else {
				return false;	
			}
		} 
	}

	
	/**
	 * Gets the number of Conditions inside a Decision.
	 * @param decision The Decision to be analyzed.
	 * @return The number of Conditions.
	 */
	public int getNumberOfConditions(Decisao decision){
		
		// base of recursion
		if (decision.getCondicao() != null) {

			return 1;
		}
		
		else {
			
			Decisao lhs = decision.getDecisaoEsquerda(); 
			Decisao rhs = decision.getDecisaoDireita();
			
			int left = 0;
			int right = 0;
			
			left = getNumberOfConditions(lhs);
			
			
			if (rhs != null)
				right = getNumberOfConditions(rhs); 

			return left + right;
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

	public void getAllMCDC(String xmlFileName, Method method) {
		
		Vector<ProgramElement> decisions = this.getTodasDecisoesDoMetodo(method);
		
		//AllMCDC aMcdc = new AllMCDC();
		
		TodasDecisoes ad = new TodasDecisoes();
		
		List<Decisao> allMCDCRequirements = new Vector<Decisao>();
		
		// for each expression found
		for (ProgramElement element : decisions) {
			
			// the whole expression is the main decision
			Decisao mainDecision = new Decisao();
			
			TreeWalker treeWalker = new TreeWalker(element);
			
			try {
				treeWalker.next();
				EncontraDecisãoCompleta.encontraDesicaoCompletaR(treeWalker, mainDecision);
			}
			
			catch (Exception ex) {
				System.err.println("Error while mapping: " + ex.toString());
			}
			
			allMCDCRequirements.add(mainDecision);
		}
		
		
		allMCDCRequirements = getMCDCRequirements(allMCDCRequirements);
		
//		ad.setDecisions(allMCDCRequirements);
		
		FileWriter fileWriter = null;
		
		try {
			
			fileWriter = new FileWriter(xmlFileName + ".xml");
		
			BufferedWriter xmlFile = new BufferedWriter(fileWriter);
			
			XStream xstream = new XStream(new DomDriver());
			xstream.alias("allMCDCDecisions", TodasDecisoes.class);
			xstream.alias("decision", Decisao.class);
//			xstream.alias("methodCall", MethodCall.class);
			
//			ad.setMethodName(method.getFullName());
			
			xmlFile.write(xstream.toXML(ad));
			xmlFile.close(); 
		}
		
		catch (IOException ex) {
			System.err.println("Problem generating the XML file: " + ex.toString());
		}
	}

	
    //OK
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
			
			//Mudar o nome desse método
			todasDecisoes = getRequisitosTodasDecisoes(todasDecisoes);
			m.setDecisoes(todasDecisoes);
			cls.addMetodo(m);

		}	
		
		XML.criaXML(td, XML.Tipo.TODASDECISOES);
	}
	
	//OK Esse metodo pode mudar (Lembrar de conversar com o Antoni
	private List<Decisao> getRequisitosTodasDecisoes(List<Decisao> decisions) {

		List<Decisao> retorno = new Vector<Decisao>();;
		
		for (Decisao decisao : decisions) {
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
	
	//OK
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
				//TODO mudar mensagem
				System.err.println("Error while mapping: " + ex.toString());
			}
			
			result.add(decisao);
			
		}
		return result; 
	}
	
	//OK
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
	//OK
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
	
	//OK
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

	//TODO Mudar para ficar igual o de decisões 
	private List<Condicao> getRequisitosTodasCondicoes(List<Condicao> conditions) {

		List<Condicao> conditions_temp = new Vector<Condicao>();;
		Iterator<Condicao> iterator = conditions.iterator(); 

		while (iterator.hasNext()) {

			Condicao trueCondition = iterator.next();
			Condicao falseCondition = new Condicao();

			trueCondition.setValor(true);  

			falseCondition = trueCondition.getCopy();
			falseCondition.setValor(false);

			conditions_temp.add(falseCondition);
			conditions_temp.add(trueCondition);
		} 
		return conditions_temp;
	}
	

	public void Imprime (List<String> matriz) {
		for (String string : matriz) {
			System.out.println(string);
		}
	}
}