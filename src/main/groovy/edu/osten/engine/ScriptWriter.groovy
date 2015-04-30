package edu.osten.engine;

import com.google.common.io.Files;

import java.io.File;

import static com.google.common.base.Charsets.UTF_8;

public class ScriptWriter {

    private File scriptFile = File.createTempFile("script", ".groovy")
    private String basicImports = ''
    private String additionalImports = ''

    public ScriptWriter( String imports ){
        this.basicImports = imports
    }

    public ScriptWriter(){
        this( '' )
        Files.getResourceAsStream('/defaults/imports.txt').readLines().each {
            basicImports += it + '\n'
        };
    }

    public void setAdditionalImports( String additionaImports ){
        this.additionalImports = additionalImports
    }

    public File write( String newScript ){

        String completeScript = basicImports + additionalImports + newScript
        Files.write completeScript, scriptFile, UTF_8
        return scriptFile
    }
}
