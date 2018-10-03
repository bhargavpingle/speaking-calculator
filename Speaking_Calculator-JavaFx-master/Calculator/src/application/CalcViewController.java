package application;

import java.io.IOException;
import java.math.BigDecimal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CalcViewController {

	@FXML
	public Text output;
	private String backspace = "";
	public String value;
	public String temp;
	BigDecimal val;
	
	double result;


	@FXML
	public void userInput(ActionEvent event) {
		value = ((Button) event.getSource()).getText();	
		String currentExpression = output.getText() + value;
		output.setText(currentExpression);
		CalcModel.getInstance().setExpression(currentExpression);
	}
	
	public void equals(ActionEvent event){
		
		CalcController.getInstance().evaluateExpression();
		output.setText(CalcModel.getInstance().getRes() + "");
		
	}
/*	@FXML
	public void equalOperator(ActionEvent event) {
		try {
			if (output.getText().isEmpty()) {
				output.setText("");
			} else {

			}
		} catch (NumberFormatException num) {
		}
	}*/

	public void clearOperator(ActionEvent event) {
		output.setText("");
	}

	public void backOperator(ActionEvent event) {
		StringBuilder str = new StringBuilder(output.getText());
		str.deleteCharAt(output.getText().length() - 1);
		backspace = str.toString();
		output.setText(backspace);
	}

	public void plusOrMinusOperator(ActionEvent event) {
		try {
			int val = Integer.parseInt(output.getText());

			if (val != 0) {
				val = val * (-1);
				output.setText(Double.toString(val));
			}

		} catch (NumberFormatException num) {

		}
	}
	@FXML
	public void speechMode(ActionEvent event) throws IOException {
		Stage primaryStage = new Stage();
		Parent root = FXMLLoader.load(getClass().getResource("Speech.fxml"));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("SpeechCalc.css").toExternalForm());
		primaryStage.setTitle("Speech Calculator");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
