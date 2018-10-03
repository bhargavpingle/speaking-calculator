package application;

public class CalcModel {
	private double res;
	private String expression;
	
	private CalcModel() {}
	
	private static final CalcModel INSTANCE = new CalcModel();
	
	public static final CalcModel getInstance() {
		
		return INSTANCE;
	}
	
	public double getRes() {
		return res;
	}

	public void setRes(double res) {
		this.res = res;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	
}
