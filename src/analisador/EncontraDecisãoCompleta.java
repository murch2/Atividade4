package analisador;

import modelo.Condicao;
import modelo.Decisao;
import recoder.convenience.TreeWalker;
import recoder.java.Identifier;
import recoder.java.ProgramElement;
import recoder.java.expression.Literal;
import recoder.java.expression.Operator;
import recoder.java.expression.ParenthesizedExpression;
import recoder.java.expression.literal.BooleanLiteral;
import recoder.java.expression.operator.ComparativeOperator;
import recoder.java.expression.operator.Equals;
import recoder.java.expression.operator.GreaterOrEquals;
import recoder.java.expression.operator.GreaterThan;
import recoder.java.expression.operator.LessOrEquals;
import recoder.java.expression.operator.LessThan;
import recoder.java.expression.operator.LogicalAnd;
import recoder.java.expression.operator.LogicalNot;
import recoder.java.expression.operator.LogicalOr;
import recoder.java.expression.operator.NotEquals;
import recoder.java.reference.VariableReference;

public class EncontraDecisãoCompleta {
	
	public static void encontraDesicaoCompletaR(TreeWalker arvore, Decisao decisao) {
		
		decisao.setCodigo(arvore.getProgramElement().toSource());
		
		if ((arvore.getProgramElement() instanceof VariableReference) ||
			(arvore.getProgramElement() instanceof BooleanLiteral) ||
			(arvore.getProgramElement() instanceof ComparativeOperator)) {
			
			
			//TODO não chamar esse método quando for TodasDecisões. 
			EncontraDecisãoCompleta.decisaoSimples(arvore, decisao);
		}
		else if (arvore.getProgramElement() instanceof LogicalAnd ||
				arvore.getProgramElement() instanceof LogicalOr) {
			
			Decisao decisaoEsquerda = new Decisao();
			Decisao decisaoDireita = new Decisao();
			
			decisao.setDecisaoEsquerda(decisaoEsquerda);
			decisao.setDecisaoDireita(decisaoDireita);
						
			if (arvore.getProgramElement() instanceof LogicalAnd)
				decisao.setOperador("AND");
			else
				decisao.setOperador("OR");
			
			int id_avo = arvore.getProgramElement().getID();
			
			ProgramElement elementoEsquerdo = ((Operator) arvore.getProgramElement()).getChildAt(0);
			ProgramElement elementoDireito = ((Operator) arvore.getProgramElement()).getChildAt(1);
			
			arvore =  new TreeWalker(elementoEsquerdo);
			
			if (elementoEsquerdo instanceof ParenthesizedExpression) {	
				arvore.next();
				encontraDesicaoCompletaR(arvore, decisaoEsquerda);
			}
			else if ((elementoEsquerdo instanceof VariableReference) || 
					elementoEsquerdo instanceof Operator) {				
				encontraDesicaoCompletaR(arvore, decisaoEsquerda);
			}			
			else {
				System.err.println("Argumento da esquerda do operador lógico desconhecido.");
				System.exit(0);
			}
			
			arvore =  new TreeWalker(elementoDireito);
			
			if (elementoDireito instanceof ParenthesizedExpression) {
				arvore.next();
				encontraDesicaoCompletaR(arvore, decisaoDireita);
			}
						
			else if ((elementoDireito instanceof VariableReference) || 
					elementoDireito instanceof Operator) {	
				encontraDesicaoCompletaR(arvore, decisaoDireita);
			}
			else {
				System.err.println("Argumento da direita do operador lógico desconhecido.");
				System.exit(0);
			}
			
			while (arvore.getProgramElement() != null &&
					arvore.getProgramElement().getASTParent().getID() != id_avo)
				arvore.next();
		}

		else if (arvore.getProgramElement() instanceof LogicalNot) {

			Decisao d = new Decisao();

			decisao.setOperador("NOT");
			decisao.setDecisaoEsquerda(d);

			while (arvore.getProgramElement() instanceof LogicalNot)
				arvore.next();

			if (arvore.getProgramElement() instanceof ParenthesizedExpression) {
				arvore.next();
				EncontraDecisãoCompleta.encontraDesicaoCompletaR(arvore, d);
			}

			else if (arvore.getProgramElement() instanceof VariableReference)
				EncontraDecisãoCompleta.encontraDesicaoCompletaR(arvore, d);

			else {
				System.err.println("Argumento filho do operador lógico de negação desconhecido.");
				System.exit(0);
			}
		}

		else {
			System.err.println("Argumento da decisão desconhecido.");
			System.exit(0);
		}
	}

	
//	Uma decisao simples possui 3 possiveis exemplos: 
//	1 - váriável: if (a) 
//	2 - boolean:  if (true)
//  3 - Condição: if (a > 5)
	
	private static void decisaoSimples (TreeWalker arvore, Decisao decisao) {
//		System.out.println("ARVORE INTIERA ------------------------------------");
		
//		while (arvore.next()) {
//			System.out.println("elemento (Codigo) = " + arvore.getProgramElement().toSource());
//			System.out.println("Pai do elemento = " + arvore.getProgramElement().getASTParent().toSource());
//		}
		
		
		if (arvore.getProgramElement() instanceof VariableReference) {

			Condicao condicaoFinal = new Condicao();

			condicaoFinal.setCodigo(arvore.getProgramElement().toSource());
			condicaoFinal.setArgumentoEsquerda(arvore.getProgramElement().toSource());
			condicaoFinal.setArgumentoDireita(null);
			condicaoFinal.setOperador(null);
			decisao.setCondicao(condicaoFinal);

			//Andando na árvore. 
			arvore.next();
			arvore.next();
		}

		else if (arvore.getProgramElement() instanceof BooleanLiteral) {

			Condicao condicaoFinal = new Condicao();

			condicaoFinal.setCodigo(arvore.getProgramElement().toSource());

			condicaoFinal.setArgumentoEsquerda(arvore.getProgramElement().toSource());
			condicaoFinal.setArgumentoDireita(null);
			condicaoFinal.setOperador(null);		
			decisao.setCondicao(condicaoFinal);

			//Andando na Árvore
			arvore.next();
		}
		
		else if (arvore.getProgramElement() instanceof ComparativeOperator) {

			Condicao condicaoFinal = new Condicao();
		
			condicaoFinal.setCodigo(arvore.getProgramElement().toSource());
			
			EncontraDecisãoCompleta.achaOperando(arvore, condicaoFinal);

			int id_avo = arvore.getProgramElement().getASTParent().getID();
			int id_pai = arvore.getProgramElement().getID();

//			Indo para o filho esquerdo
			arvore.next();
			arvore.next(); 

			if (arvore.getProgramElement() instanceof ParenthesizedExpression)
				arvore.next();

			ProgramElement elementoEsquerdo = arvore.getProgramElement();

			//Indo para o filho direito 
			arvore.next();
			
			//Voltando para o pai (para depois pegar o lado direito
			while (arvore.getProgramElement().getASTParent().getID() != id_pai)
				arvore.next();

			
			ProgramElement elementoDireito = arvore.getProgramElement();

			// assign the lhs of the condition
			if (elementoEsquerdo instanceof Identifier || elementoEsquerdo instanceof VariableReference)
				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());

			else if (elementoEsquerdo instanceof Literal)
				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());

			else if (elementoEsquerdo instanceof Operator)
				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());
			
//			else if (elementoEsquerdo instanceof MethodReference)
//				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());
//
//			else if (elementoEsquerdo instanceof ArrayReference)
//				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());
//
//			else if (elementoEsquerdo instanceof MetaClassReference)
//				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());
//
//			else if (elementoEsquerdo instanceof ThisReference)
//				condicaoFinal.setArgumentoEsquerda(elementoEsquerdo.toSource());
			
			else {
				System.err.println("Argumento da esquerda desconhecido.");
				System.exit(0);
			}

			// assign the rhs of the condition
			if (elementoDireito instanceof Identifier || elementoDireito instanceof VariableReference)
				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());

			else if (elementoDireito instanceof Literal)
				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());

			else if (elementoDireito instanceof Operator)
				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());

//			else if (elementoDireito instanceof MethodReference)
//				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());
//
//			else if (elementoDireito instanceof ArrayReference)
//				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());
//
//			else if (elementoDireito instanceof MetaClassReference)
//				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());
//
//			else if (elementoDireito instanceof ThisReference)
//				condicaoFinal.setArgumentoDireita(elementoDireito.toSource());

			else {
				System.err.println("Argumento da direita desconhecido.");
				System.exit(0);
			}


			decisao.setCondicao(condicaoFinal); 

			//Andando até o irmão (Supondo Que os dois sejam parte de uma decisão)
			while (arvore.getProgramElement() != null &&
					arvore.getProgramElement().getASTParent().getID() != id_avo)
				arvore.next();
		}

	}
	
	private static void achaOperando (TreeWalker arvore, Condicao condicaoFinal) {
	
		if (arvore.getProgramElement() instanceof GreaterThan) {
			condicaoFinal.setOperador("MAIOR");
		}
		else if (arvore.getProgramElement() instanceof GreaterOrEquals) {
			condicaoFinal.setOperador("MAIOR_OU_IGUAL ");
		}
		else if (arvore.getProgramElement() instanceof LessThan) {
			condicaoFinal.setOperador("MENOR");
		}
		else if (arvore.getProgramElement() instanceof LessOrEquals) {
			condicaoFinal.setOperador("MENOR_OU_IGUAL");
		}
		else if (arvore.getProgramElement() instanceof Equals) {
			condicaoFinal.setOperador("IGUALDADE");
		}
		else if (arvore.getProgramElement() instanceof NotEquals) {
			condicaoFinal.setOperador("DESIGUALDADE");
		}
		else {
			System.err.println("Operador não reconhecido");
			System.exit(0);
		}
	}

}
