module Loco {
	exports SyntaxNodes;
	exports backend;
	exports frontend;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	
	opens frontend;
}