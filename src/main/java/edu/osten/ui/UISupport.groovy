package edu.osten.ui;

import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UISupport {

    static final String[] KEYWORDS = [
            "def", "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"].toArray()

    static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b")


    static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = KEYWORD_PATTERN.matcher text
        int lastKwEnd = 0
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>()
        while (matcher.find()) {
            spansBuilder.add Collections.emptyList(), matcher.start() - lastKwEnd
            spansBuilder.add Collections.singleton("keyword"), matcher.end() - matcher.start()
            lastKwEnd = matcher.end()
        }
        spansBuilder.add Collections.emptyList(), text.length() - lastKwEnd
        return spansBuilder.create()
    }
}
