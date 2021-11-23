module Loco {
	exports SyntaxNodes;
	exports backend;
	exports frontend;


	requires javafx.controls;
	requires javafx.fxml;

	opens frontend to javafx.fxml;

}