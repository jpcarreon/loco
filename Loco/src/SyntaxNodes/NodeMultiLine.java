package SyntaxNodes;

import java.util.ArrayList;

import backend.Token;
import backend.TokenKind;

public class NodeMultiLine extends SyntaxNode {
	Token operation;
	
	Token loopid;
	Token optype;
	SyntaxNode condition;
	
	ArrayList<NodeLiteral> switchLiterals;
	ArrayList<SyntaxNode> statements;
	
	public NodeMultiLine(Token operation, Token loopid, Token optype, SyntaxNode condition, ArrayList<SyntaxNode> statements) {
		super(SyntaxType.loop);
		
		this.operation = operation;
		this.loopid = loopid;
		this.optype = optype;
		this.condition = condition;
		this.statements = statements;
		
	}
	
	public NodeMultiLine(Token operation, ArrayList<SyntaxNode> statements) {
		super(SyntaxType.ifblock);
		
		this.operation = operation;
		this.statements = statements;
		
	}
	
	public NodeMultiLine(Token operation, ArrayList<NodeLiteral> switchLiterals, ArrayList<SyntaxNode> statements) {
		super(SyntaxType.switchcase);
		
		this.operation = operation;
		this.switchLiterals = switchLiterals;
		this.statements = statements;
		
	}
	
	public void printChildren(int tab) {
		int i;
		
		printTab(tab);
		System.out.println("{");
		
		if (type == SyntaxType.ifblock) {
			printTab(tab);
			System.out.println(operation.getValue() + ": ");
			printTab(tab);
			System.out.println("0: ");
			statements.get(0).printChildren(tab + 1);
			
			if (statements.size() > 1) {
				printTab(tab);
				System.out.println("1: ");
				statements.get(1).printChildren(tab + 1);
			}
			
			
			
		} else if (type == SyntaxType.switchcase) {
			printTab(tab);
			System.out.println(operation.getValue() + ": ");
			
			for (i = 0; i < switchLiterals.size(); i++) {
				printTab(tab);
				
				if (switchLiterals.get(i).getToken().getTokenKind() == TokenKind.yarnToken) {
					System.out.println("\"" + switchLiterals.get(i).getToken().getValue() + "\": ");
				} else {
					System.out.println(switchLiterals.get(i).getToken().getValue() + ": ");
				}
				
				statements.get(i).printChildren(tab + 1);
			}
			
		} else if (type == SyntaxType.loop) {
			printTab(tab);
			System.out.print(operation.getValue() + ": " + loopid.getValue());
			System.out.println(" (" + optype.getValue() + ")");
			
			printTab(tab);
			System.out.println("Condition: ");
			condition.printChildren(tab + 1);
			
			printTab(tab);
			System.out.println("0: ");
			statements.get(0).printChildren(tab + 1);
		}
		
		printTab(tab);
		System.out.println("}");
	}
	
	public String getStrChildren(int tab) {
		String str = new String();
		int i;
		
		str += getStrTab(tab);
		str += "{\n";
		
		if (type == SyntaxType.ifblock) {
			str += getStrTab(tab);
			str += operation.getValue() + ": \n";
			str += getStrTab(tab);
			str += "0: \n";
			str += statements.get(0).getStrChildren(tab + 1);
			
			if (statements.size() > 1) {
				str += getStrTab(tab);
				str += "1: \n";
				str += statements.get(1).getStrChildren(tab + 1);
			}
			
		} else if (type == SyntaxType.switchcase) {
			str += getStrTab(tab);
			str += operation.getValue() + ": \n";
			
			for (i = 0; i < switchLiterals.size(); i++) {
				str += getStrTab(tab);
				
				if (switchLiterals.get(i).getToken().getTokenKind() == TokenKind.yarnToken) {
					str += "\""+ switchLiterals.get(i).getToken().getValue() + "\": \n";
				} else {
					str += switchLiterals.get(i).getToken().getValue() + ": \n";
				}
				
				str += statements.get(i).getStrChildren(tab + 1);
			}

			
		} else if (type == SyntaxType.loop) {
			str += getStrTab(tab);
			
			str += operation.getValue() + ": " + loopid.getValue();
			str += " (" + optype.getValue() + ")\n";
			
			str += getStrTab(tab);
			str += "Condition: \n";
			str += condition.getStrChildren(tab + 1);
			
			str += getStrTab(tab);
			str += "0: \n";
			str += statements.get(0).getStrChildren(tab + 1);

		}
		
		str += getStrTab(tab);
		str += "}\n";
		
		return str;
	}

	public Token getOperation() {
		return operation;
	}

	public Token getOpType() {
		return optype;
	}
	
	public SyntaxNode getCondition() {
		return condition;
	}
	
	public ArrayList<NodeLiteral> getSwitchLiterals() {
		return switchLiterals;
	}
	
	public ArrayList<SyntaxNode> getStatements() {
		return statements;
	}
	
	
}
