package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import backend.Lexer;
import backend.Token;
import backend.TokenKind;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;


public class WindowController implements Initializable {
	public final static int WINDOW_HEIGHT = 675;
	public final static int WINDOW_WIDTH = 1200;
	
	@FXML private SplitPane verticalSplit;
	@FXML private SplitPane horizontalSplit;
	
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
	void openFile(ActionEvent event) {
		FileChooser filechooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Lolcode File", "*.lol");
		
		filechooser.getExtensionFilters().add(extFilter);
		File file = filechooser.showOpenDialog(null);
		
		if (file != null) {
			codeTextArea.clear();
			
			try {
				Scanner sc = new Scanner(file);
				
				while(sc.hasNextLine()) {
					codeTextArea.appendText(sc.nextLine() + "\n");
				}
				
				sc.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	
		
		
	}
	
	@FXML
	void saveFile(ActionEvent event) {
		FileChooser filechooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Lolcode File", "*.lol");
		
		filechooser.getExtensionFilters().add(extFilter);
		File file = filechooser.showSaveDialog(null);
		
		if (file != null) {
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(codeTextArea.getText());
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    @FXML
    void runProgram(ActionEvent event) {
    	String fp = codeTextArea.getText();
    	
    	fp = fp.replaceAll("\t", "");
    	
    	Lexer lexer = new Lexer(fp);
    	Token token;
    	
    	tokenTable.getItems().clear();
    	
    	do {
    		token = lexer.nextToken();
    		tokenTable.getItems().add(token);
    		
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
    
    @FXML
    void displayAbout(ActionEvent event) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("About");
    	alert.setHeaderText("A simple Lolcode interpreter. \n" + 
    						"Lolcode is an esoteric programming language based on an internet meme called \"lolcat\"\n" + 
    						"Learn more at: lolcode.org/");
    	alert.setContentText("Github Page:\n" + "https://github.com/jpcarreon/loco");
    	
    	
    	alert.showAndWait();
    }
    
    @FXML
    void foldAll(ActionEvent event) {
		verticalSplit.setDividerPosition(0, 1.0);
    	horizontalSplit.setDividerPosition(1, 1.0);
    	horizontalSplit.setDividerPosition(0, 1.0);
    }
    
    @FXML
    void defaultFold(ActionEvent event) {
    	verticalSplit.setDividerPosition(0, 0.8);
    	horizontalSplit.setDividerPosition(0, 0.6);
    	horizontalSplit.setDividerPosition(1, 0.8);
    }
    
    private String displayInputBox(String title, String prompt) {
    	InputWindow popup = new InputWindow(title, prompt);
    	popup.display();
    	
    	
    	return popup.getValue();
    }
}
