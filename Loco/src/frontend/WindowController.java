package frontend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import backend.Evaluator;
import backend.SymTabEntry;
import backend.Token;
import backend.TokenKind;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class WindowController implements Initializable {
	private Evaluator evaluator;
	
	private boolean isPTreeShow;
	private String codeBackup;
	private int loopLimit;
	
	public final static int WINDOW_HEIGHT = 675;
	public final static int WINDOW_WIDTH = 1200;
	
	@FXML private SplitPane verticalSplit;
	@FXML private SplitPane horizontalSplit;
	
	@FXML private TextArea codeTextArea;
	@FXML private TextArea consoleTextArea;
	@FXML private TextArea parseTreeTextArea;
	
	@FXML private TableView<Token> tokenTable;
	@FXML private TableColumn<Token, String> lexemeColumn;
	@FXML private TableColumn<Token, TokenKind> tokenKindColumn;
	
	@FXML private TableView<SymTabEntry> symbolTable;
	@FXML private TableColumn<SymTabEntry, String> identifierColumn;
	@FXML private TableColumn<SymTabEntry, String> valueColumn;
	
	@FXML private Text symbolTableLabel;
	
	@FXML private MenuItem nextLineBtn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lexemeColumn.setCellValueFactory(new PropertyValueFactory<Token, String>("value"));
		tokenKindColumn.setCellValueFactory(new PropertyValueFactory<Token, TokenKind>("tokenKind"));
		
		identifierColumn.setCellValueFactory(new PropertyValueFactory<SymTabEntry, String>("identifier"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<SymTabEntry, String>("value"));
		
		isPTreeShow = false;
		loopLimit = 999;
	}
	
	@FXML
	void openFile(ActionEvent event) {
		if (!codeTextArea.isEditable()) return;
		
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
				
				codeTextArea.positionCaret(0);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	
		
		
	}
	
	@FXML
	void saveFile(ActionEvent event) {
		if (!codeTextArea.isEditable()) return;
		
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
    	if (!codeTextArea.isEditable()) return;
    	
    	try {
    		String fp = codeTextArea.getText();
        	fp = fp.replaceAll("\t", "");
        	
        	evaluator = new Evaluator(fp, this);
    	} catch (Exception e) {
    		return;
    	}
    	
    	evaluator.changeLoopLimit(loopLimit);
    	
    	verticalSplit.setDividerPosition(0, 1.0);
    	
    	symbolTable.getItems().clear();
    	tokenTable.getItems().clear();
    	for (Token token : evaluator.getTokens()) {
    		tokenTable.getItems().add(token);
    	}
    	
    	parseTreeTextArea.clear();
    	parseTreeTextArea.appendText(evaluator.getStrParseTree());
    	
    	consoleTextArea.clear();
    	for (String string: evaluator.getParserDiagnostics()) {
    		consoleTextArea.appendText(string + "\n");
    		verticalSplit.setDividerPosition(0, 0.8);
    	}
    	
    	while (!evaluator.isPCEmpty()) {
			evaluator.nextInstruction();
			updateSymbolTable();
		}
    	
    	if (!evaluator.getEvalDiagnostics().isBlank()) {
    		verticalSplit.setDividerPosition(0, 0.8);
    		consoleTextArea.appendText(evaluator.getEvalDiagnostics() + "\n");
    	}
    	
    	parseTreeTextArea.positionCaret(0);
    }
    
    @FXML
    void runDebug(ActionEvent event) {
    	if (!codeTextArea.isEditable()) return;
    	
    	try {
    		String fp = codeTextArea.getText();
        	fp = fp.replaceAll("\t", "");
        	
        	evaluator = new Evaluator(fp, this);
    	} catch (Exception e) {
    		return;
    	}
    	
    	evaluator.changeLoopLimit(loopLimit);
    	
    	verticalSplit.setDividerPosition(0, 1.0);
    	
    	symbolTable.getItems().clear();
    	tokenTable.getItems().clear();
    	for (Token token : evaluator.getTokens()) {
    		tokenTable.getItems().add(token);
    	}
    	
    	parseTreeTextArea.clear();
    	parseTreeTextArea.appendText(evaluator.getStrParseTree());
    	
    	consoleTextArea.clear();
    	for (String string: evaluator.getParserDiagnostics()) {
    		consoleTextArea.appendText(string + "\n");
    		verticalSplit.setDividerPosition(0, 0.8);
    	}
    	
    	codeBackup = codeTextArea.getText();
    	codeTextArea.clear();
    	codeTextArea.setEditable(false);
    	setDebugText();
    	
    	nextLineBtn.setDisable(false);
    }
    
    @FXML
    void runNextLine(ActionEvent event) {
    	if (evaluator.isPCEmpty()) {
			if (!evaluator.getEvalDiagnostics().isBlank()) {
				verticalSplit.setDividerPosition(0, 0.8);
	    		consoleTextArea.appendText(evaluator.getEvalDiagnostics() + "\n");
	    	}
			
			if (!codeTextArea.isEditable()) {
				codeTextArea.clear();
				codeTextArea.setText(codeBackup);
		    	codeTextArea.setEditable(true);
			}
			
			nextLineBtn.setDisable(true);
    	} else {
    		evaluator.nextInstruction();
    		updateSymbolTable();
    		
    		codeTextArea.clear();
    		setDebugText();
    	}
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
    	}
    	
    	try {
    		fontSize = Double.parseDouble(input);
    	} catch(Exception e) {
    		alert.setHeaderText("Invalid Input!");
    		alert.show();
    	}
    	
    	codeTextArea.setFont(Font.font("Consolas", fontSize));
    	consoleTextArea.setFont(Font.font("Consolas", fontSize));
    	parseTreeTextArea.setFont(Font.font("Consolas", fontSize));
    	
    	codeTextArea.positionCaret(0);
    	consoleTextArea.positionCaret(0);
    	parseTreeTextArea.positionCaret(0);
    }
    
    @FXML
    void changeLoopLimit(ActionEvent event) {
    	String input = displayInputBox("Change loop limit", "Loop Limit: ");
    	
    	Alert alert = new Alert(AlertType.ERROR);
    	
    	if (input.isEmpty() || input.isBlank()) {
    		alert.setHeaderText("Empty Input!");
    		alert.show();
    		
    		return;
    	}
    	
    	try {
    		loopLimit = Integer.parseInt(input);
    	} catch (Exception e) {
    		alert.setHeaderText("Invalid Input!");
    		alert.show();
    	}
    }
    
    @FXML
    void checkConsoleText(ActionEvent event) {
    	System.out.println("Appended");
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
    
    @FXML
    void showParseTree(ActionEvent event) {
    	if (isPTreeShow) {
    		symbolTableLabel.setText("Symbol Table");
    		symbolTable.toFront();
    		parseTreeTextArea.toBack();
    		symbolTable.setVisible(true);
    		
    	} else {
    		symbolTableLabel.setText("Parse Tree");
    		symbolTable.toBack();
    		parseTreeTextArea.toFront();
    		symbolTable.setVisible(false);
    	}
    	
    	isPTreeShow = !isPTreeShow;
    }
    
    @FXML
    void closeProgram(ActionEvent event) {
    	Stage stage = (Stage) tokenTable.getScene().getWindow();
    	stage.close();
    }
    
    @FXML
    void setHotKey(KeyEvent event) {
    	if (event.getCode() == KeyCode.F6) runProgram(null);
    	else if (event.getCode() == KeyCode.F7) runDebug(null);
    	else if (event.getCode() == KeyCode.F8 && !codeTextArea.isEditable()) runNextLine(null);
    	else if (event.getCode() == KeyCode.F12) displayAbout(null);
    }
    
    public void updateConsole(String string) {
    	if (!string.isEmpty()) {
    		verticalSplit.setDividerPosition(0, 0.8);
        	consoleTextArea.appendText(string);
    	}
    }
    
    public String getYarnInput(String varid) {
    	InputWindow popup = new InputWindow("GIMMEH", "Set " + varid + ": ");
    	Alert alert = new Alert(AlertType.ERROR);
    	String string = new String();
    	
    	popup.display();
    	
    	string = popup.getValue();
    	
    	if (string.isEmpty() || string.isBlank()) {
    		alert.setHeaderText("Empty Input!");
    		alert.show();
    		
    		return "";
    	}
    	
    	return string;
    }
    
    private void setDebugText() {
    	int counter = 0;
    	int currentLine = evaluator.getCurrentLine() - 1;
    	int caretpos = 0;
    	
    	
    	codeTextArea.appendText("==== DEBUGGING MODE ====\n\n");

    	for (String i : codeBackup.split("\n")) {
    		
    		if (!i.isBlank()) {
    			if (counter++ == currentLine) {
        			codeTextArea.appendText("=>\t" + i + "\n");
        			caretpos = codeTextArea.getLength();
        		} else {
        			codeTextArea.appendText("\t" + i + "\n");
        		}
    		} else {
    			codeTextArea.appendText("\t" + i + "\n");
    		}
    	}
    	
    	codeTextArea.positionCaret(caretpos);
    }
    
    private void updateSymbolTable() {
    	symbolTable.getItems().clear();
    	for (SymTabEntry entry : evaluator.getSymbolTable()) {
    		symbolTable.getItems().add(entry);
    	}
    }
    
    private String displayInputBox(String title, String prompt) {
    	InputWindow popup = new InputWindow(title, prompt);
    	popup.display();
    	
    	
    	return popup.getValue();
    }
}
