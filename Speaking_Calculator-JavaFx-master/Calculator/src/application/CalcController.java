package application;

import com.udojava.evalex.Expression;

public class CalcController {

	private CalcController() {}

	private static final CalcController INSTANCE = new CalcController();

	public static final CalcController getInstance() {

		return INSTANCE;
	}

	public void evaluateExpression() {

		Expression expression = new Expression(CalcModel.getInstance().getExpression());
		expression.setPrecision(8);
		double result = expression.eval().doubleValue();
		CalcModel.getInstance().setRes(result);
	}
}
