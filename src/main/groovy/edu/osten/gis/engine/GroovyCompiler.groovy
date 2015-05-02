package edu.osten.gis.engine

class GroovyCompiler {

    private ClassLoader parentLoader
    GroovyClassLoader loader

    GroovyCompiler(){
        this.parentLoader = getClass().classLoader
        this.loader = new GroovyClassLoader(parentLoader)
        loader.setShouldRecompile(true)
    }

    public Object compile( File file, Closure inCaseOfError ){
        try{
            loader.clearCache()
            def compiledClass = loader.parseClass(file)
            return compiledClass.newInstance()
        }catch(any){
            return inCaseOfError.call()
        }
    }
}
