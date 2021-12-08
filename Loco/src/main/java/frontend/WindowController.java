package frontend;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import backend.Evaluator;
import backend.SymTabEntry;
import backend.Token;
import backend.TokenKind;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


public class WindowController implements Initializable {
	private Evaluator evaluator;
	
	private boolean isPTreeShow;
	private boolean showRuntime;
	private boolean wrapText;
	private String codeBackup;
	private int loopLimit;

	private CodeArea codeArea;

	public final static int WINDOW_HEIGHT = 675;
	public final static int WINDOW_WIDTH = 1200;
	
	@FXML private SplitPane verticalSplit;
	@FXML private SplitPane horizontalSplit;

	@FXML private StackPane codeStackPane;

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
		CodeHighlighter codeHighlighter = new CodeHighlighter();

		lexemeColumn.setCellValueFactory(new PropertyValueFactory<Token, String>("value"));
		tokenKindColumn.setCellValueFactory(new PropertyValueFactory<Token, TokenKind>("tokenKind"));
		
		identifierColumn.setCellValueFactory(new PropertyValueFactory<SymTabEntry, String>("identifier"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<SymTabEntry, String>("value"));

		codeArea = codeHighlighter.getCodeArea();
		codeStackPane.getChildren().add(new VirtualizedScrollPane<>(codeArea));

		isPTreeShow = showRuntime = wrapText = false;
		loopLimit = 999;
	}
	
	@FXML
	void openFile(ActionEvent event) {
		if (!codeArea.isEditable()) return;
		
		FileChooser filechooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Lolcode File", "*.lol");
		
		filechooser.getExtensionFilters().add(extFilter);
		File file = filechooser.showOpenDialog(null);
		
		if (file != null) {
			codeArea.clear();
			
			try {
				Scanner sc = new Scanner(file);
				
				while(sc.hasNextLine()) {
					codeArea.appendText(sc.nextLine() + "\n");
				}
				
				sc.close();

				codeArea.displaceCaret(0);
				codeArea.requestFollowCaret();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	
		
		
	}
	
	@FXML
	void saveFile(ActionEvent event) {
		if (!codeArea.isEditable()) return;
		
		FileChooser filechooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Lolcode File", "*.lol");
		
		filechooser.getExtensionFilters().add(extFilter);
		File file = filechooser.showSaveDialog(null);
		
		if (file != null) {
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(codeArea.getText());
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    @FXML
    void runProgram(ActionEvent event) {
    	if (!codeArea.isEditable()) return;
    	
    	long startTime = System.nanoTime();

    	try {
    		String fp = codeArea.getText();

        	evaluator = new Evaluator(fp, this);
    	} catch (Exception e) {
    		return;
    	}
    	
    	evaluator.changeLoopLimit(loopLimit);
    	
    	//	hide console text area
    	verticalSplit.setDividerPosition(0, 1.0);
    	
    	//	update symboltable and tokentable
    	symbolTable.getItems().clear();
    	tokenTable.getItems().clear();
    	for (Token token : evaluator.getTokens()) {
    		tokenTable.getItems().add(token);
    	}
    	
    	//	update parsetree
    	parseTreeTextArea.clear();
    	parseTreeTextArea.appendText(evaluator.getStrParseTree());
    	
    	
    	//	check if there are any parser errors
    	consoleTextArea.clear();
    	for (String string: evaluator.getParserDiagnostics()) {
    		consoleTextArea.appendText(string + "\n");
    		verticalSplit.setDividerPosition(0, 0.8);
    	}
    	
    	//	 repeatedly executes the lines of code
    	while (!evaluator.isPCEmpty()) {
			evaluator.nextInstruction();
			updateSymbolTable();
		}
    	
    	//	check if there are any semantic analyzer errors
    	if (!evaluator.getEvalDiagnostics().isBlank()) {
    		verticalSplit.setDividerPosition(0, 0.8);
    		consoleTextArea.appendText(evaluator.getEvalDiagnostics() + "\n");
    	}
    	
    	long totalTime = System.nanoTime() - startTime;
    	if (showRuntime) updateConsole("\nProgram Runtime: " + totalTime / 1000000.0 + "ms");

    	parseTreeTextArea.positionCaret(0);
    }
    
    //	does almost the same thing as runProgram() except it doesn't run instructions until the file is done
    @FXML
    void runDebug(ActionEvent event) {
    	if (!codeArea.isEditable()) return;
    	
    	try {
    		String fp = codeArea.getText();
        	
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
    	
    	codeBackup = codeArea.getText();
    	codeArea.clear();
    	codeArea.setEditable(false);
    	setDebugText();
    	
    	nextLineBtn.setDisable(false);
    }
    
    //	executes the next line of code
    @FXML
    void runNextLine(ActionEvent event) {
    	if (evaluator.isPCEmpty()) {
			if (!evaluator.getEvalDiagnostics().isBlank()) {
				verticalSplit.setDividerPosition(0, 0.8);
	    		consoleTextArea.appendText(evaluator.getEvalDiagnostics() + "\n");
	    	}
			
			if (!codeArea.isEditable()) {
				codeArea.clear();
				codeArea.appendText(codeBackup);
		    	codeArea.setEditable(true);
			}
			
			nextLineBtn.setDisable(true);
    	} else {
    		evaluator.nextInstruction();
    		updateSymbolTable();
    		
    		codeArea.clear();
    		setDebugText();
    	}
    }
    
    @FXML
    void changeFont(ActionEvent event) {
    	String input = displayInputBox("Change Font Size", "Font Size: ");
    	double fontSize = 20;
    	Alert alert = new Alert(AlertType.ERROR);

    	//	check if input is empty
    	if (input.isEmpty() || input.isBlank()) {
    		alert.setHeaderText("Empty Input!");
    		alert.show();
    		
    		return;
    	}
    	
    	//	check if input is a valid number
    	try {
    		fontSize = Double.parseDouble(input);
    	} catch(Exception e) {
    		alert.setHeaderText("Invalid Input!");
    		alert.show();
    	}
    	
    	//	change font size
		codeArea.setStyle("-fx-font-size: "+fontSize+"px;");
    	consoleTextArea.setFont(Font.font("Consolas", fontSize));
    	parseTreeTextArea.setFont(Font.font("Consolas", fontSize));

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
    	//	sends the symboltable to the front if parsetree is showing and vice-versa
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
    void showProgramRuntime(ActionEvent event) {
    	showRuntime = !showRuntime;
    }

	@FXML
	void wrapText(ActionEvent event) {
		wrapText = !wrapText;
		codeArea.setWrapText(wrapText);
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
    	else if (event.getCode() == KeyCode.F8 && !codeArea.isEditable()) runNextLine(null);
    	else if (event.getCode() == KeyCode.F12) displayAbout(null);
    }
    
    public void updateConsole(String string) {
    	//	add text to the console text area
    	if (!string.isEmpty()) {
    		verticalSplit.setDividerPosition(0, 0.8);
        	consoleTextArea.appendText(string);
    	}
    }
    
    //	ask for user input
    public String getYarnInput(String varid) {
    	InputWindow popup = new InputWindow("GIMMEH", "Set " + varid + ": ");
    	Alert alert = new Alert(AlertType.ERROR);
    	String string = new String();
    	
    	popup.display();
    	
    	string = popup.getValue();
    	
    	//	input must not be empty
    	if (string.isEmpty() || string.isBlank()) {
    		alert.setHeaderText("Empty Input!");
    		alert.show();
    		
    		return "";
    	}
    	
    	return string;
    }
    
    //	modifies the codeArea to show which line is being executed
    private void setDebugText() {
    	int counter = 0;
    	int currentLine = evaluator.getNextLineNum() - 1;
    	int caretpos = 0;
    	
    	if (currentLine < 0) {
    		runNextLine(null);
    		return;
    	}

        codeArea.appendText("==== DEBUGGING MODE ====\n\n");
    	//	looks for the next line to be executed
    	for (String i : codeBackup.split("\n")) {
    		if (counter++ == currentLine) {
                codeArea.appendText("=>\t" + i + "\n");
				caretpos = codeArea.getLength();
			} else {
                codeArea.appendText("\t" + i + "\n");
			}
    	}

        codeArea.displaceCaret(caretpos - 1);
        codeArea.requestFollowCaret();
    }
    
    public void updateSymbolTable() {
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
