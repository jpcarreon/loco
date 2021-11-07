package frontend;

import backend.Lexer;
import backend.Token;
import backend.TokenKind;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;


public class WindowController {	
	
	public final static int WINDOW_HEIGHT = 640;
	public final static int WINDOW_WIDTH = 1000;
	
	@FXML
    private TextArea codeTextArea;
	
	@FXML
    private TextArea consoleTextArea;
	
	@FXML
    private TableView tokenTable;

    @FXML
    void runProgram(ActionEvent event) {
    	String fp = codeTextArea.getText();
    	
    	fp = fp.replaceAll("\t", "");
    	
    	Lexer lexer = new Lexer(fp);
    	Token token;
    	
    	do {
    		token = lexer.nextToken();
    		token.viewToken();
    	} while (token.getKind() != TokenKind.eofToken);
    	
    }
    
    @FXML
    void changeFont(ActionEvent event) {
    	String input = displayInputBox("Change Font Size", "Font Size: ");
    	double fontSize = codeTextArea.getFont().getSize();
    	Alert alert = new Alert(AlertType.ERROR);
    	
    	if (input.isEmpty() || input.isBlank()) {
    		alert.setHeaderText("Empty Input!");
    		alert.show();
    		
    		return;
    	};
    	
    	try {
    		fontSize = Double.parseDouble(input);
    	} catch(Exception e) {
    		alert.setHeaderText("Invalid Input!");
    		alert.show();
    	}
    	
    	codeTextArea.setFont(Font.font("Consolas", fontSize));
    	consoleTextArea.setFont(Font.font("Consolas", fontSize));
    }
    
    private String displayInputBox(String title, String prompt) {
    	AlertWindow popup = new AlertWindow(title, prompt);
    	popup.display();
    	
    	
    	return popup.getValue();
    }
}
