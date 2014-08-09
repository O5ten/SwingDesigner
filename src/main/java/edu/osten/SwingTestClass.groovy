package edu.osten

/**
 * Created by osten on 2014-07-15.
 */
import groovy.swing.SwingBuilder

import javax.swing.JComponent
import java.awt.BorderLayout as BL


class SwingTestClass implements Buildable{
    private count = 0;

    @Override
    def JComponent build()
    {
        JComponent component = new SwingBuilder().
            label()
            {
                borderLayout()
                button(text:'Click Me',
                        actionPerformed: {count++; textlabel.text = "Clicked ${count} time(s)."; println "clicked"},
                        constraints:BL.SOUTH)
            }

        println "built!"

    }
}
