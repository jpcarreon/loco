package frontend;

import java.net.URL;
import java.util.ResourceBundle;

import backend.Lexer;
import backend.Token;
import backend.TokenKind;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;


public class WindowController implements Initializable {	
	
	public final static int WINDOW_HEIGHT = 675;
	public final static int WINDOW_WIDTH = 1200;
	
	@FXML private TextArea codeTextArea;
	
	@FXML private TextArea consoleTextArea;
	
	@FXML private TableView<Token> tokenTable;
	@FXML private TableColumn<Token, String> lexemeColumn;
	@FXML private TableColumn<Token, TokenKind> tokenKindColumn;
	
	@FXML private TableView<Token> symbolTable;
	@FXML private TableColumn<Token, String> identifierColumn;
	@FXML private TableColumn<Token, String> valueColumn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lexemeColumn.setCellValueFactory(new PropertyValueFactory<Token, String>("value"));
		tokenKindColumn.setCellValueFactory(new PropertyValueFactory<Token, TokenKind>("tokenKind"));
		
		
	}

    @FXML
    void runProgram(ActionEvent event) {
    	String fp = codeTextArea.getText();
    	
    	fp = fp.replaceAll("\t", "");
    	
    	Lexer lexer = new Lexer(fp);
    	Token token;
    	
    	do {
    		token = lexer.nextToken();
    		tokenTable.getItems().add(token);
    		token.viewToken();
    	} while (token.getTokenKind() != TokenKind.eofToken);
    	
    	
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
    
    private void setupTokenTable() {
    	
    }
    
    private String displayInputBox(String title, String prompt) {
    	InputWindow popup = new InputWindow(title, prompt);
    	popup.display();
    	
    	
    	return popup.getValue();
    }
}
