package edu.osten.gis.ui

import org.fxmisc.richtext.StyleSpans
import org.fxmisc.richtext.StyleSpansBuilder

import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.lang.String.join
import static java.util.Collections.emptyList
import static java.util.Collections.singleton;

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
            "transient", "try", "void", "volatile", "while"]

    static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + join("|", KEYWORDS) + ")\\b")
    static final Pattern SINGLE_STRING_PATTERN = Pattern.compile('(\'|\")([^(\'|\")\\r\\n]*)?(\'|\")')

    static StyleSpans<Collection<String>> computeKeywordHighlighting(String text) {
        computeHighlighting(['keyword': KEYWORD_PATTERN], text)
    }

    static StyleSpans<Collection<String>> computeSingleQuoteStringHighlighting(String text) {
        computeHighlighting(['single-quotes-string': SINGLE_STRING_PATTERN], text)
    }

    static StyleSpans<Collection<String>> computeAllHighlighting(String text) {
        computeHighlighting(['single-quotes-string': SINGLE_STRING_PATTERN, 'keyword': KEYWORD_PATTERN], text)
    }

    static StyleSpans<Collection<String>> computeHighlighting(Map<String, Pattern> patterns, String text) {
        try {
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<Collection<String>>()
            List<MapEntry> matchers = patterns.collect() { String k, Pattern p -> return p.matcher(text).find() ? new MapEntry(k, p.matcher(text)) : null }
            int lastMatchEnd = 0

            matchers.every { MapEntry v -> v.value.find() }
            while (matchers && matchers.any { v -> v.value.find(lastMatchEnd) }) {
                MapEntry matcherEntry = matchers.min { MapEntry v -> v.value.find(lastMatchEnd) ? v.value.start() : Integer.MAX_VALUE } ?: null
                if (!matcherEntry) {
                    continue;
                }
                Matcher matcher = matcherEntry.value
                String keyword = matcherEntry.key
                spansBuilder.add emptyList(), matcher.start() - lastMatchEnd
                spansBuilder.add singleton(keyword), matcher.end() - matcher.start()
                lastMatchEnd = matcher.end()
            }
            spansBuilder.add emptyList(), text.length() - lastMatchEnd
            spansBuilder.create()
        } catch (any) {
            //silent
        }
    }
}
