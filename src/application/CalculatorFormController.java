package application;

import java.text.DecimalFormat;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class CalculatorFormController {
	private String equation = "";
	DecimalFormat df = new DecimalFormat("#.######");
	
    @FXML
    private VBox calc;
	
	@FXML
    private ScrollPane scroll;
    
    @FXML
    private VBox histBox;
    
    @FXML
    private Label equationLabel;
    
    @FXML
    public void initialize() {
    	scroll.vvalueProperty().bind(histBox.heightProperty());
    	
    	calc.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
            	if (event.getCharacter().charAt(0) == 13) {
            		handleEqual(new ActionEvent());
            	} else if (event.getCharacter().charAt(0) >= ' ' && event.getCharacter().charAt(0) <= '}') {
            		equation += event.getCharacter();
            		equationLabel.setText(equation);
            	} else if (event.getCharacter().charAt(0) == 8) {
            		equation = equation.substring(0, equation.length() - 1);
            		equationLabel.setText(equation);
            	}
            }
    	});
    }

    @FXML
    protected void handleButton(ActionEvent event) {
    	equation += ((Button)(event.getSource())).getText();
        equationLabel.setText(equation);
    }
    
    @FXML
    protected void handleEqual(ActionEvent event) {
    	if(equation.equals("")) {
    		return;
    	}
    	Label eq = new Label(equation);
    	histBox.getChildren().add(eq);
    	try {
    		Label res = new Label(String.valueOf(df.format(eval(equation))));
    		res.setOnMouseClicked(new EventHandler<MouseEvent>(){
  	          @Override
  	          public void handle(MouseEvent arg0) {
  	        	  equation += res.getText();
  	              equationLabel.setText(equation);
  	          }

    		});
    		res.setAlignment(Pos.CENTER_LEFT);
    		histBox.getChildren().add(res);
		} catch (RuntimeException e) {
    		Label res = new Label("Err");
    		res.setAlignment(Pos.CENTER_LEFT);
    		histBox.getChildren().add(res);
    		Alert a = new Alert(AlertType.ERROR);
            a.setContentText(e.getMessage()); 
            a.show(); 
		}
    	eq.setAlignment(Pos.CENTER_RIGHT);
		
		equation = "";
        equationLabel.setText(equation);
    }
    
    @FXML
    protected void handleDelete(ActionEvent event) {
    	if ((equation != null) && (equation.length() > 0)) {
    		equation = equation.substring(0, equation.length() - 1);
    	}
    	equationLabel.setText(equation);
    }

    @FXML
    protected void handleClear(ActionEvent event) {
    	histBox.getChildren().clear();
    	equation = "";
        equationLabel.setText(equation);
    }
    
    /*** Parsing equation written by Boann and found here:
     * 		https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
     * 
     * 	Released to the public domain under the creative commons license CC1.0
     * 
     * @param str: the expression to parse
     * @return the double value of the mathematical expression
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}