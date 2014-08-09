package edu.osten

import groovy.swing.SwingBuilder
import net.miginfocom.swing.MigLayout

import java.awt.BorderLayout as BL
import edu.osten.Buildable;
import javax.swing.*;
import java.awt.*;

class SwingTest implements Buildable{

    JComponent build(){
        int count = 0
        new SwingBuilder().
            panel( layout: new MigLayout('','','')){
                borderLayout()
                label(text:"Header 1")
            }
    }
}

