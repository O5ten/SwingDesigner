package edu.osten.gis.ui


import javafx.embed.swing.SwingNode
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import javax.swing.*

class ResultView extends Stage {

    private SwingNode swingNode;
    private Parent javaFXNode;

    ResultView() {
        swingNode = new SwingNode()
        swingNode.content = new JLabel(text: 'No groovy code has been added yet')
        javaFXNode = new StackPane()

        AnchorPane mainPanel = new AnchorPane()
        AnchorPane.setTopAnchor swingNode, 0.0
        AnchorPane.setLeftAnchor swingNode, 0.0
        AnchorPane.setRightAnchor swingNode, 0.0
        AnchorPane.setBottomAnchor swingNode, 0.0

        AnchorPane.setTopAnchor javaFXNode, 0.0
        AnchorPane.setLeftAnchor javaFXNode, 0.0
        AnchorPane.setRightAnchor javaFXNode, 0.0
        AnchorPane.setBottomAnchor javaFXNode, 0.0

        mainPanel.children.addAll swingNode, javaFXNode

        Scene scene = new Scene(mainPanel, 800, 600)
        scene.stylesheets.add ResultView.class.getResource('/style.css').toExternalForm()
        this.scene = scene
        this.title = 'Result Viewer'
    }

    SwingNode getSwingNode() {
        return swingNode;
    }

    Parent getJavaFXNode() {
        return javaFXNode;
    }

    void showSwing() {
        swingNode.visibleProperty().setValue(true)
        javaFXNode.visibleProperty().setValue(false)
    }

    void showJavaFX() {
        swingNode.visibleProperty().setValue(false)
        javaFXNode.visibleProperty().setValue(true)
    }
}
