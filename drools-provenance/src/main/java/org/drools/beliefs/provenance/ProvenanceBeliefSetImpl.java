package org.drools.beliefs.provenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.drools.core.beliefsystem.BeliefSystem;
import org.drools.core.beliefsystem.jtms.JTMSBeliefSetImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.semantics.Literal;
import org.jboss.drools.provenance.RuleActivationImpl;
import org.kie.api.definition.rule.Rule;
import org.w3.ns.prov.Activity;
import org.w3.ns.prov.ActivityImpl;
import org.w3.ns.prov.Agent;
import org.w3.ns.prov.Entity;
import org.w3.ns.prov.EntityImpl;

public class ProvenanceBeliefSetImpl<M extends ProvenanceBelief<M>>
        extends JTMSBeliefSetImpl<M>
        implements ProvenanceBeliefSet<M> {

    private List<Activity> provenance = new ArrayList<Activity>();

    public ProvenanceBeliefSetImpl( BeliefSystem<M> beliefSystem, InternalFactHandle rootHandle ) {
        super( beliefSystem, rootHandle );
    }

    @Override
    public void add( M node ) {
        super.add( node );
        recordActivity( node, true );
    }

    @Override
    public void remove( M node ) {
        super.remove( node );
        recordActivity( node, false );
    }
    
    private void recordActivity( M node, boolean positiveAssertion ) {
    	Activity activity = this.createActivity( node, positiveAssertion );
    	
        this.provenance.add( activity );         
    }

    protected Activity createActivity( M node, boolean positiveAssertion ) {
    	// TODO: Just placeholder transforms
    	Rule droolsRule = node.getLogicalDependency().getJustifier().getRule();
    	
    	Object tgt = node.getLogicalDependency().getObject();

    	Activity activity = this.toActivity( null );

        Entity entity = new EntityImpl(); 
    	
    	for( Object justifier : node.getLogicalDependency().getJustifier().getObjects()) {
    		entity.addWasInvalidatedBy( this.toActivity( justifier ) );
    	}

        activity.addGenerated( entity );

        Agent rule = new RuleActivationImpl();
        rule.addName( new Literal( droolsRule.getPackageName() + "." + droolsRule.getName() ) );
           
        generateText( node );
        
        return activity;
    }
    
    protected Activity toActivity( Object object ) {
    	ActivityImpl activity = new ActivityImpl();
        activity.withEndedAtTime( new Date() );
        activity.withName( new Literal( UUID.randomUUID().toString() ) );
        
        return activity;
    }

    private void generateText(M node) {
        /*
		Activation<M> act = node.getLogicalDependency().getJustifier();
		if ( act.getRule().getMetaData().containsKey("Display") ) {
		CompiledTemplate template = TemplateManager.getRegistry().getNamedTemplate( (String) act.getRule().getMetaData().get("Display")); 		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Pattern p = (Pattern) act.getRule().getLhs().getChildren().get( 0 );
		String x = p.getAnnotations().get("Display").getPropertyValue("value").toString();
		
		for ( String key : act.getDeclarationIds() ) {
			map.put( key, act.getDeclarationValue( key ) );
		}
		System.out.println( TemplateRuntime.execute( template, map ) );
		}
		*/
	}

	public List<Activity> getProvenance() {
        return provenance;
    }


    public Collection<? extends Activity> getGeneratingActivities() {
        return provenance;
    }
}
