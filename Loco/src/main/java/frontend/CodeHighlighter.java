package frontend;

import backend.TokenKind;
import javafx.scene.paint.Paint;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeHighlighter {
    private CodeArea codeArea;

    private static String expressionPattern;
    private static String assignmentPattern;
    private static String flowcontrolPattern;

    private static Pattern allPattern;

    //  TODO remove samplecode before release!
    private static final String sampleCode = String.join("\n", new String[] {
            "HAI",
            "   I HAS A var1 ITZ 4",
            "",
            "   SUM OF 1 AN 2",
            "   DIFF OF 4.6 AN \"2\"",
            "   PRODUKT OF var1 AN 2.6",
            "   QUOSHUNT OF 5 AN var2",
            "",
            "   BOTH OF var1 AN FAIL",
            "   EITHER OF \"2\" AN NOT 2.6",
            "   ALL OF 1 AN 2 AN 3 MKAY",
            "KTHXBYE"
    });

    CodeHighlighter() {
        codeArea = new CodeArea();
        codeArea.setLineHighlighterFill(Paint.valueOf("#E8E8FF"));
        codeArea.setLineHighlighterOn(true);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });

        keywordSetup();
        allPattern = Pattern.compile(
                "(?<EXPRESSION>" + expressionPattern + ")"
                        + "|(?<COMMENT>((OBTW.+?TLDR)|(BTW[^\n]*)))"
                        + "|(?<SE>(HAI|KTHXBYE))",
                Pattern.DOTALL
        );

        codeArea.replaceText(0, 0, sampleCode);

    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    private void keywordSetup() {
        String regex;
        expressionPattern = new String();

        expressionPattern += "\\b(";
        for (TokenKind i : TokenKind.values()) {
            regex = i.getRegex();
            regex = regex.replaceAll("\s", "\\\\s");
            regex = regex.replaceAll("(\\^|\\$)", "");


            if (i.getType().equals("mathop") || i.getType().equals("boolop") || i.getType().equals("cmpop") ||
                    i.getType().equals("infarop") || i.getType().equals("concat") || i.getType().equals("print") ||
                    i.getType().equals("exptypecast") || i == TokenKind.mkayToken || i == TokenKind.anToken ||
                    i == TokenKind.aToken
            ){
                expressionPattern += regex + "|";
            }
        }
        expressionPattern = expressionPattern.substring(0, expressionPattern.length() - 1);
        expressionPattern += ")\\b";

        System.out.println(expressionPattern);
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = allPattern.matcher(text);
        String styleClass;

        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {

            if (matcher.group("EXPRESSION") != null) {
                styleClass = "expression";
            /*
            } else if (matcher.group("ASSIGNMENT") != null) {
                styleClass = "assignment";
            } else if (matcher.group("FLOWCONTROL") != null) {
                styleClass = "flowcontrol";
            } else if (matcher.group("LITERAL") != null) {
                styleClass = "literal";
            */
            } else if (matcher.group("COMMENT") != null) {
                styleClass = "comment";
            } else if (matcher.group("SE") != null) {
                styleClass = "se";
            } else {
                styleClass = null;
            }



            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
