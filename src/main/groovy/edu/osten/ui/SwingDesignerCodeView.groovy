package edu.osten.ui

import com.google.common.collect.Lists
import edu.osten.engine.GroovyCompiler
import edu.osten.engine.ScriptWriter
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import net.miginfocom.swing.MigLayout
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.StyleSpans

import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import java.awt.Color

import static edu.osten.ui.UISupport.computeAllHighlighting
import static javafx.application.Platform.runLater
import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE
import static javax.swing.SwingUtilities.invokeLater

class SwingDesignerCodeView extends Application {

    private GroovyCompiler groovyCompiler
    private ScriptWriter scriptWriter
    private CodeArea codeArea
    private JTextArea textArea = new JTextArea(lineWrap: true, wrapStyleWord: true)
    private SwingDesignerResultView resultView

    SwingDesignerCodeView() {
        this.groovyCompiler = new GroovyCompiler()
        this.scriptWriter = new ScriptWriter()
        this.resultView = new SwingDesignerResultView()
    }

    @Override
    void start(Stage stage) throws Exception {

        AnchorPane topAnchorPane = new AnchorPane()

        ToggleButton toggleButton = new ToggleButton(text: 'Show Result')

        toggleButton.onAction = new EventHandler<ActionEvent>() {
            @Override
            void handle(ActionEvent event) {
                if (resultView.isShowing()) {
                    resultView.hide()
                } else {
                    resultView.show()
                    runLater {
                        resultView.swingNode.content.revalidate()
                    }
                }
            }
        }

        codeArea = new CodeArea()
        codeArea.replaceText ''
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        AnchorPane rightPane = new AnchorPane()

        topAnchorPane.children.addAll codeArea, toggleButton

        AnchorPane.setTopAnchor codeArea, 0.0
        AnchorPane.setLeftAnchor codeArea, 0.0
        AnchorPane.setRightAnchor codeArea, 0.0
        AnchorPane.setBottomAnchor codeArea, 28.0

        AnchorPane.setBottomAnchor toggleButton, 0.0

        TabPane tabPane = new TabPane()
        tabPane.tabClosingPolicy = UNAVAILABLE

        Tab codeTab = new Tab('Groovy Editor')
        codeTab.content = topAnchorPane

        Tab exampleTab = new Tab('Examples')
        VBox exampleBox = new VBox()
        exampleBox.getStyleClass().add('example-box')

        List<Control> examples = Lists.newArrayList();
        for (File example : new File('./target/classes/examples/').listFiles()) {
            final String aPath = example.path
            def btn = new Button(example.name.replace('_', ' ').split('\\.')[0]);
            btn.onAction = new EventHandler<ActionEvent>() {
                @Override
                void handle(ActionEvent actionEvent) {
                    setExample aPath
                }
            }
            examples.add btn
        }
        exampleBox.children.addAll examples
        exampleTab.content = exampleBox

        tabPane.getTabs().addAll codeTab, exampleTab

        AnchorPane.setTopAnchor tabPane, 0.0
        AnchorPane.setRightAnchor tabPane, 0.0
        AnchorPane.setBottomAnchor tabPane, 0.0
        AnchorPane.setLeftAnchor tabPane, 0.0

        rightPane.children.add tabPane

        def inCaseOfErrorCallback = {
            JPanel panel = new JPanel(new MigLayout('wrap', '0[grow, fill]0', '0[]0[grow,fill]push'))
            panel.add new JLabel(text: 'Your groovy has trouble compiling').with {
                it.foreground = Color.RED
                it
            }
            panel.add textArea

            resultView.swingNode.content = panel
            invokeLater({
                panel.revalidate()
                panel.repaint()
            })
        }

        codeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String newScript) {
                try {
                    File file = scriptWriter.write(newScript)
                    def buildable = groovyCompiler.compile(file, inCaseOfErrorCallback)
                    runLater({
                        StyleSpans<Collection<String>> spans = computeAllHighlighting(newScript)
                        if (spans) {
                            codeArea.setStyleSpans 0, computeAllHighlighting(newScript)
                        }
                        invokeLater({
                            try {
                                def component = buildable.main()
                                resultView.swingNode.content = component
                                component.revalidate()
                                component.repaint()
                            } catch (any) {
                                textArea.text = any.message
                                inCaseOfErrorCallback.call()
                            }
                        })
                    })
                }
                catch (any) {
                    textArea.text = any.message
                    inCaseOfErrorCallback.call()
                    return
                }
            }
        })
        Scene scene = new Scene(rightPane, 800, 600)
        scene.stylesheets.add SwingDesignerCodeView.class.getResource('/style.css').toExternalForm()
        stage.scene = scene
        stage.title = 'Swing Designer'
        stage.show()
    }

    public void setExample(String path) {
        String code = ''
        new File(path).readLines().each {
            code += it + '\n'
        };
        codeArea.replaceText code
    }
}
