package org.drools.semantics.builder.reasoner;

import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.descr.AnnotatedBaseDescr;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;



public class DLRecognitionBuildContext {

    private int counter = 0;
    private Stack<BuildStep> vars = new Stack<BuildStep>();
    private String source;

    public DLRecognitionBuildContext() {
        push();
    }

    public void push() {
        vars.push( new BuildStep( "$x_" + counter++ ) );
    }

    public void pop() {
        vars.pop();
    }

    public String getScopedIdentifier() {
        return vars.peek().getVar();
    }

    public BuildStep getScopedContext() {
        return vars.peek();
    }

    public boolean isPropertyBound( String propKey ) {
        return vars.peek().isPropertyBound( propKey );
    }

    public String bindProperty( String prop ) {
        return vars.peek().bindProperty( prop );
    }

    public String getPropertyKey( String prop ) {
        return vars.peek().getPropertyKey( prop );
    }

    public void clearBindings() {
        vars.peek().clearBindings();
    }

    public String peekParent() {
        BuildStep temp = vars.pop();
        BuildStep parent = vars.size() > 0 ? vars.peek() : null;
        vars.push( temp );
        return parent != null ? parent.getVar() : null;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void resetSource() {
        source = null;
    }



    public static class BuildStep {
        private String var;
        private Map<String,PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>>> patterns;
        private Map<String,String> propertyVars = new HashMap<String,String>();

        private BuildStep( String var ) {
            this.var = var;
        }

        public String getVar() {
            return var;
        }

        public void addPattern( String type, PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>> builder ) {
            patterns.put( type, builder );
        }

        public PatternDescrBuilder<? extends CEDescrBuilder<? extends CEDescrBuilder, ? extends AnnotatedBaseDescr>> getBuilder( String type ) {
            return patterns.get( type );
        }

        public boolean hasBuilder( String key ) {
            return patterns.containsKey( key );
        }

        public String bindProperty( String property ) {
            String propKey = getPropertyKey( property );
            if ( ! propertyVars.containsKey( propKey) ) {
                propertyVars.put( propKey, property );
            }
            return propKey;
        }

        public boolean isPropertyBound( String propKey ) {
            return propertyVars.containsKey( propKey );
        }

        public String getPropertyKey( String prop ) {
            return var + "_" + prop;
        }

        public void clearBindings() {
            propertyVars.clear();
        }



    }
}


