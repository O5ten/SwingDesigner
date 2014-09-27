package edu.osten.ui

import com.google.common.io.Files
import edu.osten.engine.GroovyCompiler
import edu.osten.engine.ScriptWriter
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingNode
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory

import javax.swing.JLabel
import java.awt.Color

import static edu.osten.ui.UISupport.computeHighlighting
import static javafx.application.Platform.*
import static javafx.geometry.Orientation.*
import static javafx.scene.control.TabPane.TabClosingPolicy.*
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
        codeArea.replaceText ''
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        AnchorPane leftPane = new AnchorPane()
        AnchorPane.setTopAnchor swingNode, 0.0
        AnchorPane.setLeftAnchor swingNode, 0.0
        AnchorPane.setRightAnchor swingNode, 0.0
        AnchorPane.setBottomAnchor swingNode, 0.0

        AnchorPane rightPane = new AnchorPane()

        TabPane tabPane = new TabPane()
        tabPane.tabClosingPolicy = UNAVAILABLE

        Tab codeTab = new Tab('Groovy Editor')
        codeTab.content = codeArea

        Tab exampleTab = new Tab('Examples')
        VBox exampleBox = new VBox()
        exampleBox.setStyle('-fx-padding: 8; -fx-spacing: 8; -fx-alignment: top-center;')
        Button clickables = new Button('Clickable Buttons')
        clickables.onAction = new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent actionEvent) {
                setExample('/examples/button.txt')
            }
        }

        Button gradients = new Button('Gradient Panels')
        gradients.onAction = new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent actionEvent) {
                setExample('/examples/gradient.txt')
            }
        }

        Button sweden = new Button('Swedish Flag')
        sweden.onAction = new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent actionEvent) {
                setExample('/examples/sweden.txt')
            }
        }

        exampleBox.children.addAll clickables, gradients, sweden
        exampleTab.content = exampleBox

        tabPane.getTabs().addAll(codeTab, exampleTab)

        AnchorPane.setTopAnchor tabPane, 0.0
        AnchorPane.setRightAnchor tabPane, 0.0
        AnchorPane.setBottomAnchor tabPane, 0.0
        AnchorPane.setLeftAnchor tabPane, 0.0

        SplitPane split = new SplitPane()
        split.getItems().addAll(leftPane, rightPane)
        split.orientation = Orientation.HORIZONTAL

        leftPane.children.add swingNode
        rightPane.children.add tabPane

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
        Scene scene = new Scene(split, 800, 600)
        scene.stylesheets.add SwingDesigner.class.getResource('/style.css').toExternalForm()
        stage.scene = scene
        stage.title = 'Swing Designer'
        stage.show()
    }

    public void setExample(String resourcePath) {
        String code = ''
        Files.getResourceAsStream(resourcePath).readLines().each {
            code += it + '\n'
        };
        codeArea.replaceText code
    }
}
