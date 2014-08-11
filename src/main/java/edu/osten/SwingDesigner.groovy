package edu.osten

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.StyleSpans
import org.fxmisc.richtext.StyleSpansBuilder

import javax.swing.*
import java.util.regex.Matcher
import java.util.regex.Pattern

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.io.Files.write
import static edu.osten.SwingDesignerApp.*
import static java.awt.Color.RED
import static java.io.File.createTempFile
import static javafx.scene.layout.AnchorPane.*

class SwingDesignerApp extends Application {

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

    static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");

    String basicImports =
            '''
                import groovy.swing.SwingBuilder
                import net.miginfocom.swing.MigLayout
                import javax.swing.*
                import java.awt.*
                import static java.awt.Color.*

        '''

    String exampleCode = '''
def JComponent build(){
    new SwingBuilder().
    panel( layout: new MigLayout('wrap','','')){
        JLabel aLabel = label(text:"Header 1")
        button(text:"clickable", actionPerformed: { aLabel.text = 'Oh no! The button was clicked!'})
    }
}'''

    File scriptFile = createTempFile("script", ".groovy");

    @Override
    void start(Stage stage) throws Exception {

        SwingNode swingNode = new SwingNode()
        swingNode.content = new JLabel(text: 'No groovy code has been added yet')

        CodeArea codeArea = new CodeArea()
        codeArea.replaceText ''

        AnchorPane leftPane = new AnchorPane()
        setTopAnchor(swingNode, 0.0)
        setLeftAnchor(swingNode, 0.0)
        setRightAnchor(swingNode, 0.0)
        setBottomAnchor(swingNode, 0.0)
        AnchorPane rightPane = new AnchorPane()

        HBox hbox = new HBox(leftPane, rightPane)
        HBox.setHgrow(rightPane, Priority.SOMETIMES)
        HBox.setHgrow(leftPane, Priority.ALWAYS)
        leftPane.children.add swingNode

        setTopAnchor(codeArea, 0.0)
        setRightAnchor(codeArea, 0.0)
        setBottomAnchor(codeArea, 0.0)
        setLeftAnchor(codeArea, 0.0)

        rightPane.children.add codeArea



        codeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newScript) {
                try {
                    codeArea.setStyleSpans(0, computeHighlighting(newScript));
                    write(basicImports + newScript, scriptFile, UTF_8);
                    ClassLoader parent = getClass().getClassLoader();
                    GroovyClassLoader loader = new GroovyClassLoader(parent);
                    def buildable;
                    try {
                        def groovyClass = loader.parseClass(scriptFile);
                        buildable = groovyClass.newInstance();
                    } catch (Exception e) {
                        JLabel errorPanel = new JLabel(text: 'There\'s a disturbance in your groovy')
                        errorPanel.setForeground(RED)
                        swingNode.content = errorPanel
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                errorPanel.revalidate()
                                errorPanel.repaint()
                            }
                        })
                        return;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                void run() {
                                    try {
                                        def component = buildable.build()
                                        swingNode.content = component
                                        component.revalidate()
                                        component.repaint()
                                    } catch (Exception e) {

                                        //silent
                                    }
                                }
                            })
                        }
                    });
                }
                catch (InstantiationException | IllegalAccessException | IOException e) {
                    return;
                }
            }
        })
        codeArea.replaceText exampleCode
        Scene scene = new Scene(hbox, 1250, 500)
        scene.getStylesheets().add(SwingDesignerApp.class.getResource("/style.css").toExternalForm())
        stage.setScene(scene)
        stage.show()
    }

    StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = KEYWORD_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}

Application.launch(SwingDesignerApp.class, args);
