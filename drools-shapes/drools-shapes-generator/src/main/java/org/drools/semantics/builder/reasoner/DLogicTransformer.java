package org.drools.semantics.builder.reasoner;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class DLogicTransformer {

    private OWLOntology onto;

    public DLogicTransformer( OWLOntology onto ) {
        this.onto = onto;
    }

    public Map<OWLClassExpression,OWLClassExpression> getDefinitions() {
        Map<OWLClassExpression,OWLClassExpression> defs = new HashMap<OWLClassExpression, OWLClassExpression>();

        for ( OWLClass klass : onto.getClassesInSignature( true ) ) {
            for ( OWLEquivalentClassesAxiom equiv : onto.getEquivalentClassesAxioms( klass ) ) {
                List<OWLClassExpression> args = equiv.getClassExpressionsAsList();
                if ( args.size() == 2 && ! args.get( 0 ).isAnonymous() ) {
                    defs.put( klass, toDNF( args.get( 1 ) ) );
                } else {
                    throw new UnsupportedOperationException( "Unable to process " + equiv );
                }
            }
        }
        return defs;
    }

    public OWLClassExpression toDNF( OWLClassExpression in ) {
        OWLClassExpression nnf = in.getNNF();
        DNFVisitor visitor = new DNFVisitor();
        nnf.accept( visitor );
        OWLClassExpression dnf = visitor.getDNF();
        System.out.println( dnf );
        return dnf;
    }

    private class DNFVisitor implements OWLClassExpressionVisitor {

        private Stack<List<OWLClassExpression>> DNF;
        private OWLDataFactory factory;

        private DNFVisitor() {
            factory = onto.getOWLOntologyManager().getOWLDataFactory();
            DNF = new Stack<List<OWLClassExpression>>();
            DNF.push( new ArrayList<OWLClassExpression>() );
        }

        public OWLClassExpression getDNF() {
            Set<OWLClassExpression> set = new HashSet( DNF.pop() );
            if ( set.size() == 1 && set.iterator().next() instanceof OWLObjectUnionOf ) {
                return set.iterator().next();
            } else {
                return factory.getOWLObjectUnionOf( set );
            }
        }

        public void visit( OWLClass ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        private OWLClassExpression processAtom( OWLClassExpression ce ) {
            return factory.getOWLObjectIntersectionOf( ce );
        }


        public void visit( OWLObjectIntersectionOf ce ) {
            List<List<OWLClassExpression>> bits = new ArrayList<List<OWLClassExpression>>( ce.getOperands().size() );
            int card = 1;

            for ( OWLClassExpression x : ce.getOperands() ) {
                DNF.push( new ArrayList<OWLClassExpression>() );
                x.accept( this );
                List<OWLClassExpression> bit = DNF.pop();
                card *= bit.size();
                bits.add( bit );
            }

            List<OWLClassExpression>[] ands = new ArrayList[ card ];
            for ( int j = 0; j < card; j++ ) {
                ands[ j ] = new ArrayList<OWLClassExpression>();
            }

            int blocks = 1;
            for ( int k = 0; k < bits.size(); k++ ) {
                List<OWLClassExpression> bit = bits.get( k );
                int M = bit.size();
                int N = card / M;

                for ( int j = 0; j < M; j++ ) {
                    int reps = card / ( blocks * M );
                    int step = card / blocks;

                    for ( int i = 0; i < blocks; i++ ) {
                        for ( int l = 0; l < reps; l++ ) {
                            ands[ j * reps + i * step + l ].add( bit.get( j ) );
                        }
                    }

                }
                blocks *= M;
            }

            OWLObjectIntersectionOf[] owlAnds = new OWLObjectIntersectionOf[ ands.length ];
            for ( int j = 0; j < ands.length; j++ ) {
                List<OWLClassExpression> args = ands[ j ];
                for ( int k = 0; k < args.size(); k++ ) {
                    OWLClassExpression expr = args.get( k );
                    if ( expr instanceof OWLObjectUnionOf && ((OWLObjectUnionOf) expr).getOperands().size() == 1 ) {
                        args.set( k, ((OWLObjectUnionOf) expr).getOperands().iterator().next() );
                    }
                }
                HashSet<OWLClassExpression> andArgs = new HashSet<OWLClassExpression>();
                for ( OWLClassExpression expr : ands[ j ] ) {
                    if ( expr instanceof OWLObjectIntersectionOf ) {
                        andArgs.addAll( ((OWLObjectIntersectionOf) expr).getOperands() );
                    } else {
                        andArgs.add( expr );
                    }
                }
                owlAnds[ j ] = factory.getOWLObjectIntersectionOf( andArgs );
            }

            DNF.peek().add( factory.getOWLObjectUnionOf( owlAnds ) );
        }

        public void visit( OWLObjectUnionOf ce ) {
            DNF.push( new ArrayList<OWLClassExpression>() );
            for ( OWLClassExpression x : ce.getOperands() ) {
                x.accept( this );
            }
            List<OWLClassExpression> newArgs = DNF.pop();
            DNF.peek().addAll( newArgs );
        }

        public void visit( OWLObjectComplementOf ce ) {
            DNF.peek().add( processAtom( ce ) );
        }




        public void visit( OWLObjectSomeValuesFrom ce ) {
            DNF.peek().add( factory.getOWLObjectSomeValuesFrom( ce.getProperty(), toDNF( ce.getFiller() ) ) );
        }

        public void visit( OWLObjectAllValuesFrom ce ) {
//            DNF.peek().add( factory.getOWLObjectAllValuesFrom( ce.getProperty(), toDNF( ce.getFiller() ) ) );
            DNF.peek().add(
                    factory.getOWLObjectComplementOf(
                            factory.getOWLObjectSomeValuesFrom( ce.getProperty(),
                                    toDNF( factory.getOWLObjectComplementOf( ce.getFiller() ).getNNF() )
                            )
                    )
            );
        }

        public void visit( OWLObjectMinCardinality ce ) {
            DNF.peek().add( factory.getOWLObjectMinCardinality( ce.getCardinality(), ce.getProperty(), toDNF( ce.getFiller() ) ) );
        }

        public void visit( OWLObjectExactCardinality ce ) {
            DNF.peek().add( factory.getOWLObjectExactCardinality( ce.getCardinality(), ce.getProperty(), toDNF( ce.getFiller() ) ) );
        }

        public void visit( OWLObjectMaxCardinality ce ) {
            DNF.peek().add( factory.getOWLObjectMaxCardinality( ce.getCardinality(), ce.getProperty(), toDNF( ce.getFiller() ) ) );
        }





        public void visit( OWLObjectHasValue ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLObjectHasSelf ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLObjectOneOf ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataSomeValuesFrom ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataAllValuesFrom ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataHasValue ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataMinCardinality ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataExactCardinality ce ) {
            DNF.peek().add( processAtom( ce ) );
        }

        public void visit( OWLDataMaxCardinality ce ) {
            DNF.peek().add( processAtom( ce ) );
        }
    }
}
