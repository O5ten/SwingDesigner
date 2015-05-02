package edu.osten.gis.ui

import com.google.common.collect.Lists
import edu.osten.gis.engine.GroovyCompiler
import edu.osten.gis.engine.ScriptWriter
import javafx.application.Application
import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import net.miginfocom.swing.MigLayout
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.StyleSpans
import org.tbee.javafx.scene.layout.MigPane

import javax.swing.*
import java.awt.*
import java.util.List

import static edu.osten.gis.ui.UISupport.computeAllHighlighting
import static javafx.application.Platform.runLater
import static javax.swing.SwingUtilities.invokeLater

class DevelopmentView extends Application {

    private GroovyCompiler groovyCompiler
    private ScriptWriter scriptWriter
    private CodeArea codeArea
    private JTextArea textArea = new JTextArea(lineWrap: true, wrapStyleWord: true)
    private ResultView resultView
    private boolean isContinuousCompilationEnabled = true;
    private boolean isGroovyFX = false

    DevelopmentView() {
        this.groovyCompiler = new GroovyCompiler()
        this.scriptWriter = new ScriptWriter()
        this.resultView = new ResultView()
        this.codeArea = new CodeArea()
    }

    @Override
    void start(Stage stage) throws Exception {

        AnchorPane topAnchorPane = new AnchorPane()

        CheckBox showResultView = new CheckBox(text: 'Show Result')

        showResultView.onAction = [handle: { event ->
            if (resultView.isShowing()) {
                resultView.hide()
            } else {
                resultView.show()
                runLater {
                    resultView.swingNode.content.revalidate()
                }
            }
        }] as EventHandler

        resultView.showingProperty().addListener([changed: { observable, oldValue, newValue ->
            showResultView.selectedProperty().set(newValue)
        }] as ChangeListener)

        CheckBox continuousCompilationCheckBox = new CheckBox(text: 'Continuous Compilation', selected: true)

        continuousCompilationCheckBox.selectedProperty().addListener([changed: {
            observable, oldValue, newValue ->
                isContinuousCompilationEnabled = newValue
                triggerCompilation(codeArea.textProperty().getValue())
        }] as ChangeListener)

        ToggleGroup group = new ToggleGroup()
        RadioButton isJavaFXCheckBox = new RadioButton(text: 'GroovyFX', toggleGroup: group, selected: isGroovyFX)
        RadioButton isSwingBuilder = new RadioButton(text: 'SwingBuilder', toggleGroup: group, selected: !isGroovyFX)

        isJavaFXCheckBox.selectedProperty().addListener([changed: { observable, oldValue, newValue ->
            isGroovyFX = newValue
            triggerCompilation(codeArea.textProperty().getValue())
        }] as ChangeListener)


        codeArea.replaceText ''
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)

        AnchorPane rightPane = new AnchorPane()

        topAnchorPane.children.addAll codeArea, showResultView, continuousCompilationCheckBox, isJavaFXCheckBox,
                isSwingBuilder

        AnchorPane.setTopAnchor codeArea, 0.0
        AnchorPane.setLeftAnchor codeArea, 0.0
        AnchorPane.setRightAnchor codeArea, 0.0
        AnchorPane.setBottomAnchor codeArea, 28.0

        AnchorPane.setBottomAnchor showResultView, 0.0
        AnchorPane.setBottomAnchor continuousCompilationCheckBox, 0.0
        AnchorPane.setLeftAnchor continuousCompilationCheckBox, 130.0

        AnchorPane.setLeftAnchor isJavaFXCheckBox, 330.0
        AnchorPane.setBottomAnchor isJavaFXCheckBox, 0

        AnchorPane.setLeftAnchor isSwingBuilder, 430.0
        AnchorPane.setBottomAnchor isSwingBuilder, 0

        TabPane tabPane = new TabPane()
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        Tab codeTab = new Tab('Groovy Editor')
        codeTab.content = topAnchorPane

        Tab exampleTab = new Tab('Examples')
        VBox exampleBox = new VBox()
        exampleBox.getStyleClass().add('example-box')

        List<Control> examples = Lists.newArrayList();
        for (File example : new File('./target/classes/examples/').listFiles()) {
            final String aPath = example.path
            def btn = new javafx.scene.control.Button(example.name.replace('_', ' ').split('\\.')[0]);
            btn.onAction = [handle: { actionEvent -> setExample aPath }] as EventHandler
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

        codeArea.textProperty().addListener([changed: { obvservable, oldValue, newValue -> triggerCompilation(newValue) }] as ChangeListener)

        Scene scene = new Scene(rightPane, 800, 600)
        scene.stylesheets.add DevelopmentView.class.getResource('/style.css').toExternalForm()
        stage.scene = scene
        stage.title = 'Groovy Interface Sandbox'
        stage.show()
    }

    def inCaseOfErrorCallback = {
        JPanel panel = new JPanel(new MigLayout('wrap', '0[grow, fill]0', '0[]0[grow,fill]push'))
        panel.add new JLabel(text: 'Your groovy has trouble compiling').with {
            it.foreground = Color.RED
            it
        }
        panel.add textArea

        resultView.swingNode.content = panel
        resultView.showSwing()
        invokeLater({
            panel.revalidate()
            panel.repaint()
        })
    }

    private void triggerCompilation(String newScript) {
        try {
            File file = scriptWriter.write(newScript)
            runLater {
                StyleSpans<Collection<String>> spans = computeAllHighlighting(newScript)
                if (spans) {
                    codeArea.setStyleSpans 0, computeAllHighlighting(newScript)
                }
            }
            if (isContinuousCompilationEnabled) {
                def buildable = groovyCompiler.compile(file, inCaseOfErrorCallback)
                if (isGroovyFX) {
                    runLater {
                        try {
                            def component = buildable.main()
                            Parent root = component.scene.root
                            resultView.javaFXNode.children.clear()
                            resultView.javaFXNode.children.add root
                            resultView.showJavaFX()
                        } catch (any) {
                            textArea.text = any.message + '\n\n' + any.stackTrace.toArrayString()
                            inCaseOfErrorCallback.call()
                        }
                    }
                } else {
                    invokeLater {
                        try {
                            def component = buildable.main()
                            resultView.swingNode.content.removeAll()
                            resultView.swingNode.content = component
                            resultView.showSwing()
                            component.revalidate()
                            component.repaint()
                        } catch (any) {
                            textArea.text = any.message + '\n\n' + any.stackTrace.toArrayString()
                            inCaseOfErrorCallback.call()
                        }
                    }
                }
            }
            MigPane
        } catch (any) {
            textArea.text = any.message + '\n\n' + any.stackTrace.toArrayString()
            inCaseOfErrorCallback.call()
            return
        }
    }

    public void setExample(String path) {
        String code = ''
        new File(path).readLines().each {
            code += it + '\n'
        };
        codeArea.replaceText code
        triggerCompilation(code)
    }
}