package edu.osten.ui

import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

import javax.swing.JLabel

class SwingDesignerResultView extends Stage {

    private SwingNode swingNode;

    SwingDesignerResultView() {
        swingNode = new SwingNode()
        swingNode.content = new JLabel(text: 'No groovy code has been added yet')

        AnchorPane mainPanel = new AnchorPane()
        AnchorPane.setTopAnchor swingNode, 0.0
        AnchorPane.setLeftAnchor swingNode, 0.0
        AnchorPane.setRightAnchor swingNode, 0.0
        AnchorPane.setBottomAnchor swingNode, 0.0

        mainPanel.children.add swingNode

        Scene scene = new Scene(mainPanel, 800, 600)
        scene.stylesheets.add SwingDesignerResultView.class.getResource('/style.css').toExternalForm()
        this.scene = scene
        this.title = 'Swing Designer'
    }

    SwingNode getSwingNode() {
        return swingNode;
    }
}
