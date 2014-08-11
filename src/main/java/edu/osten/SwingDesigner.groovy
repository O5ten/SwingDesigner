package edu.osten

import com.google.common.io.Files
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.StyleClassedTextArea

import javax.swing.JLabel
import javax.swing.SwingUtilities

import static com.google.common.base.Charsets.UTF_8
import static com.google.common.io.Files.*
import static java.awt.Color.*
import static java.io.File.*

import static javafx.scene.layout.AnchorPane.*

class SwingDesignerApp extends Application {

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
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.replaceText ''

        AnchorPane leftPane = new AnchorPane()
        leftPane.minWidth = 500
        leftPane.prefWidth = 20000
        leftPane.minHeight = 500

        setTopAnchor(swingNode, 0.0)
        setLeftAnchor(swingNode, 0.0)
        setRightAnchor(swingNode, 0.0)
        setBottomAnchor(swingNode, 0.0)

        AnchorPane rightPane = new AnchorPane()
        rightPane.minWidth = 300
        rightPane.minHeight = 500
        HBox hbox = new HBox(leftPane, rightPane)
        leftPane.children.add swingNode

        setTopAnchor(codeArea, 0.0)
        setRightAnchor(codeArea, 0.0)
        setBottomAnchor(codeArea, 0.0)

        rightPane.children.add codeArea

        codeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newScript) {
                try {
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
        Scene scene = new Scene(hbox, 1000, 500)
        stage.setScene(scene)
        stage.show()
    }
}

Application.launch(SwingDesignerApp.class, args);
