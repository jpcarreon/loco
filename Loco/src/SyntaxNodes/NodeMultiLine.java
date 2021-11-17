package SyntaxNodes;

import java.util.ArrayList;

import backend.Token;

public class NodeMultiLine extends SyntaxNode {
	Token operation;
	
	Token loopid;
	Token optype;
	SyntaxNode condition;
	
	ArrayList<NodeLiteral> switchLiterals;
	ArrayList<NodeStatement> statements;
	
	public NodeMultiLine(Token operation, Token loopid, Token optype, SyntaxNode condition, ArrayList<NodeStatement> statements) {
		super(SyntaxType.loop);
		
		this.operation = operation;
		this.loopid = loopid;
		this.optype = optype;
		this.condition = condition;
		this.statements = statements;
		
	}
	
	public NodeMultiLine(Token operation, ArrayList<NodeStatement> statements) {
		super(SyntaxType.ifblock);
		
		this.operation = operation;
		this.statements = statements;
		
	}
	
	public NodeMultiLine(Token operation, ArrayList<NodeLiteral> switchLiterals, ArrayList<NodeStatement> statements) {
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
			
			printTab(tab);
			System.out.println("1: ");
			statements.get(1).printChildren(tab + 1);
			
			
		} else if (type == SyntaxType.switchcase) {
			printTab(tab);
			System.out.println(operation.getValue() + ": ");
			
			for (i = 0; i < switchLiterals.size(); i++) {
				printTab(tab);
				System.out.println(switchLiterals.get(i).getToken().getValue() + ": ");
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
		
		return str;
	}

}
