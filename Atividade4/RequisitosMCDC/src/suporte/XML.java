package suporte;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import modelo.Condicao;
import modelo.Decisao;
import modelo.TodasCondicoes;
import modelo.TodasDecisoes;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class XML {
	
	public static enum Tipo {
		TODASDECISOES,
		TODASCONDICOES,
		MCDC,
	}
	
	public static void criaXML (Object object, Tipo tipo) {
		System.out.println("Criando Arquivo XML ... ");
		
		String fileName = ""; 
		try {
			FileWriter fileWriter = null;
			XStream xstream = new XStream(new DomDriver());

			switch (tipo) {
			case TODASCONDICOES:
				fileName = "TodasCondições.xml"; 
				xstream.alias("allConditions", TodasCondicoes.class);
				xstream.alias("condition", Condicao.class);
				break;

			case TODASDECISOES:
				fileName = "TodasDecisões.xml"; 
				xstream.alias("TodasDecisões", TodasDecisoes.class);
				xstream.alias("Decisao", Decisao.class);
				break;

			case MCDC:
				fileName = "MCDC.xml"; 
				xstream.alias("allMCDCDecisions", TodasDecisoes.class);
				xstream.alias("decision", Decisao.class);
				break;

			default:
				break;
			}
			
			fileWriter = new FileWriter(fileName);
			BufferedWriter xmlFile = new BufferedWriter(fileWriter);	
			
			xmlFile.write(xstream.toXML(object));
			xmlFile.close(); 
			
			System.out.println("Arquivo " + fileName + "Criado com sucesso!");
		}

		catch (IOException e) {
			System.err.println("Problema gerando o arquivo XML");
			System.exit(0);
		}
	}
}
