package analisador;

import java.util.List;

import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.abstraction.Method;
import recoder.io.PropertyNames;
import recoder.io.SourceFileRepository;
import recoder.java.CompilationUnit;

public class Main {

	public static void main(String[] args) {
		
		String path = new String();
		
		if (args.length == 1) {
			path = args[0];
			if (!args[0].substring(args[0].length() - 1).equals(new String ("/"))) {
				System.err.println("Por favor digite o caminho até a pasta que contém os arquivos java a serem analisados com o caractere '/' no final.");
				System.exit(0);
			}
		}
		else {
			System.err.println("Por favor digite o caminho até a pasta que contém os arquivos java a serem analisados");
			System.exit(0);
		}
		List<CompilationUnit> arvoreAbstrata = serviceConfiguration(path);
		analisadorSintatico(arvoreAbstrata);
	}
	
	//Método que cria arvore Abstrata do tutorial do Alexandre
	public static List<CompilationUnit> serviceConfiguration (String srcPath) {
		//create a service configuration
		CrossReferenceServiceConfiguration crsc = new CrossReferenceServiceConfiguration();
		
		//set the path to source code ("src" folder). 
		//multiple source code paths, as well as paths to libraries, can be separated via ":" or ";".
		crsc.getProjectSettings().setProperty(PropertyNames.INPUT_PATH, srcPath);
		crsc.getProjectSettings().ensureSystemClassesAreInPath();
		
		//tell Recoder to parse all .java files it can find in the directory "src"
		SourceFileRepository sfr = crsc.getSourceFileRepository();
		List<CompilationUnit> cul = null;
		try {
			cul = sfr.getAllCompilationUnitsFromPath();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		crsc.getChangeHistory().updateModel();
		return cul;
	}
	
	private static void analisadorSintatico(List<CompilationUnit> arvoreAbstrata) {
		ConditionAnalyzer conditionAnalyzer = new ConditionAnalyzer();
		List<Method> metodos = conditionAnalyzer.getTodosMetodos(arvoreAbstrata);
		conditionAnalyzer.getTodasCondicoes(metodos);
		conditionAnalyzer.getTodasDecisoes(metodos); 
		conditionAnalyzer.getTodasMCDC(metodos);
		
	}
}

