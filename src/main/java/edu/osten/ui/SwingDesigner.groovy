package edu.osten.ui

import edu.osten.engine.GroovyCompiler
import edu.osten.engine.ScriptWriter
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory

import javax.swing.JLabel
import java.awt.Color

import static edu.osten.ui.UISupport.computeHighlighting
import static javafx.application.Platform.*
import static javax.swing.SwingUtilities.*

class SwingDesigner extends Application {

    private GroovyCompiler groovyCompiler
    private ScriptWriter scriptWriter
    private CodeArea codeArea

   SwingDesigner( ){
        this.groovyCompiler = new GroovyCompiler()
        this.scriptWriter = new ScriptWriter()
    }

    @Override
    void start(Stage stage) throws Exception {

        SwingNode swingNode = new SwingNode()
        swingNode.content = new JLabel(text: 'No groovy code has been added yet')

        codeArea = new CodeArea()
        Bindings.bindBidirectional()
        codeArea.replaceText ''
        codeArea.setParagraphGraphicFactory LineNumberFactory.get(codeArea)

        AnchorPane leftPane = new AnchorPane()
        AnchorPane.setTopAnchor swingNode, 0.0
        AnchorPane.setLeftAnchor swingNode, 0.0
        AnchorPane.setRightAnchor swingNode, 0.0
        AnchorPane.setBottomAnchor swingNode, 0.0

        AnchorPane rightPane = new AnchorPane()
        rightPane.minWidth = 500
        rightPane.maxWidth = 800
        AnchorPane.setTopAnchor codeArea, 0.0
        AnchorPane.setRightAnchor codeArea, 0.0
        AnchorPane.setBottomAnchor codeArea, 0.0
        AnchorPane.setLeftAnchor codeArea, 0.0

        HBox hbox = new HBox(leftPane, rightPane)
        HBox.setHgrow rightPane, Priority.NEVER
        HBox.setHgrow leftPane, Priority.ALWAYS
        leftPane.children.add swingNode
        rightPane.children.add codeArea

        def inCaseOfErrorCallback = {
            JLabel errorPanel = new JLabel(text: 'Your groovy has trouble compiling')
            errorPanel.foreground = Color.RED
            swingNode.content = errorPanel
            invokeLater({
                    errorPanel.revalidate()
                    errorPanel.repaint()
            })
        }

        codeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newScript) {
                try {
                    File file = scriptWriter.write(newScript)
                    def buildable = groovyCompiler.compile(file, inCaseOfErrorCallback)
                    runLater({
                        codeArea.setStyleSpans 0, computeHighlighting(newScript)
                        invokeLater({
                            try{
                                def component = buildable.main()
                                swingNode.content = component
                                component.revalidate()
                                component.repaint()
                            }catch(any){
                                inCaseOfErrorCallback.call()
                            }
                        })
                    })
                }
                catch (any) {
                    inCaseOfErrorCallback.call()
                    return
                }
            }
        })
        Scene scene = new Scene(hbox, 1250, 500)
        scene.stylesheets.add SwingDesigner.class.getResource('/style.css').toExternalForm()
        stage.scene = scene
        stage.title = 'Swing Designer'
        stage.show()
    }

    public void setExample(String exampleCode) {
        codeArea.replaceText exampleCode
    }
}
