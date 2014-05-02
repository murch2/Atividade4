package modelo;

import java.util.List;

public class MCDC {
	
	private String methodName;
	
	private List<Decisao> mcdcList;
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public List<Decisao> getMcdcList() {
		return mcdcList;
	}
	
	public void setMcdcList(List<Decisao> mcdcList) {
		this.mcdcList = mcdcList;
	}
}