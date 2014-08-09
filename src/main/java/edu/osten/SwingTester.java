package edu.osten;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import edu.osten.Buildable;
import groovy.lang.GroovyClassLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static com.google.common.base.Charsets.*;

/**
 * Created by osten on 2014-07-15.
 */
public class SwingTester extends Application{

    private SwingNode swingPanel = new SwingNode(); ;
    private final String basicImports =
                    "import groovy.swing.SwingBuilder\n" +
                    "import net.miginfocom.swing.MigLayout\n" +
                    "import edu.osten.Buildable\n" +
                    "import javax.swing.*\n" +
                    "import java.awt.*\n";

    @Override
    public void start( final Stage primaryStage ) throws Exception
    {
        final StackPane panel = new StackPane();
        panel.getChildren().setAll( swingPanel );

        final TextArea codeArea = TextAreaBuilder.create().minHeight(500).build();

        final File scriptFile = File.createTempFile( "script", ".groovy" );

        codeArea.textProperty().addListener( new ChangeListener<String>()
        {
            @Override
            public void changed( ObservableValue<? extends String> arg0, String arg1, String arg2 )
            {
                try
                {
                    Files.write( basicImports + arg2, scriptFile, UTF_8);
                    ClassLoader parent = getClass().getClassLoader();

                    GroovyClassLoader loader = new GroovyClassLoader(parent);
                    try
                    {
                        parent.loadClass(Buildable.class.getName());
                    }
                    catch(ClassNotFoundException e)
                    {
                        System.out.println("Class not really found");
                    }
                    Class groovyClass = null;
                    try{
                        groovyClass = loader.parseClass( scriptFile );
                    }catch(MultipleCompilationErrorsException e  ){
                        JLabel label = new JLabel("Groovy code does not compile");
                        swingPanel.setContent( label );
                        label.revalidate();
                        label.repaint();
                        return;
                    }catch(ClassCastException e ){
                        JLabel label = new JLabel("Groovy class probably does not implement the Buildable interface");
                        swingPanel.setContent( new JLabel("Groovy class probably does not implement the Buildable interface") );
                        label.revalidate();
                        label.repaint();
                        return;
                    }
                    final Buildable buildable = (Buildable) groovyClass.newInstance();
                    Platform.runLater( new Runnable() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JComponent component = buildable.build();
                                        swingPanel.setContent(component);
                                        component.revalidate();
                                        component.repaint();
                                    }catch( Exception e){
                                        //silent
                                    }
                                }
                            });
                        }
                    });
                }
                catch( InstantiationException | IllegalAccessException | IOException e )
                {
                    return;
                }
            }
        } );

        primaryStage.setScene(SceneBuilder
                .create()
                .width(1200)
                .height(480)
                .root(
                        SplitPaneBuilder
                                .create()
                                .items(
                                        panel,
                                        VBoxBuilder
                                                .create()
                                                .children(
                                                        codeArea
                                                ).build()
                                ).build()
                ).build());

        primaryStage.show();

        // ScenicView.show( primaryStage.getScene() );
    }

    public static void main( String[] args )
    {
        Application.launch(args);
    }

}
