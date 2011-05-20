import static org.junit.Assert.assertEquals;

import org.drools.chance.distribution.IDistribution;
import org.drools.lang.descr.TypeDeclarationDescr;


@Deprecated
public class TestStructure {

	private static int NUM = 10000000;
	//la velocità è indipendente dalla grandezza dello storico
	private static int LUNGSTO=10;
	private static IDistribution<String> testDistribution;
	private static TypeDeclarationDescr tdd;

//	@Test
//	public void BasicTestDump() {
//
//
//
//        //oggetto base
//		Person person=new Person("isabella",27);
//
//		//creazione Handler per wrappare person
//
//		try {
//			byte[] b=Handler_Param_Dump.dump(tdd,"structure/Handler");
//			ClassPersonLoader cl=new ClassPersonLoader("test.Person_Handler");
//			Object per_han=cl.getClassPerson(b).newInstance();
//
//			//Bean injection
//			Method mt=per_han.getClass().getMethod("setBean", new Object().getClass());
//			mt.invoke(per_han, person);
//
//			//test crisp  value throwgh handler
//			mt=per_han.getClass().getMethod("geteta");
//            Integer eta=(Integer) mt.invoke(per_han);
//
//            assertEquals(27,eta.intValue());
//
//
//
//
//            //set e get sullo storico Discrete Distribution
//            //costruzione prob discreta
//            ProbDegree pd1=new SimpleDegree(0.2);
//            //inserimento in element
//            ValueDegreePair<Integer> el1=new ValueDegreePair<Integer>(25,pd1);
//            //aggiunta a vector di prob
//            Vector<ValueDegreePair> vec1=new Vector<ValueDegreePair>();
//            vec1.add(el1);
//
//            //costruzione secondo valore
//            pd1=new SimpleDegree(0.8);
//            el1=new ValueDegreePair<Integer>(27,pd1);
//            vec1.add(el1);
//            Collections.sort(vec1, new ElementComparator());
//
//
//
//            DiscreteDistribution dd1= new DiscreteDistribution(vec1);
//
//
//            mt=per_han.getClass().getMethod("seteta",org.drools.chance.distribution.IDistribution.class);
//            mt.invoke(per_han,dd1);
//            mt=per_han.getClass().getMethod("geteta");
//            eta=(Integer) mt.invoke(per_han);
//            assertEquals(27,eta.intValue());
//
//
//
//            //inserimento nello storico di un nuovo campo
//            pd1=new SimpleDegree(0.2);
//            el1=new ValueDegreePair<Integer>(30,pd1);
//            Vector<ValueDegreePair> vec2=new Vector<ValueDegreePair>();
//            vec2.add(el1);
//
//            pd1=new SimpleDegree(0.5);
//            el1=new ValueDegreePair<Integer>(33,pd1);
//            vec2.add(el1);
//
//            pd1=new SimpleDegree(0.3);
//            el1=new ValueDegreePair<Integer>(31,pd1);
//            vec2.add(el1);
//
//
//            Collections.sort(vec2, new ElementComparator());
//
//
//           dd1= new DiscreteDistribution(vec2);
//
//           mt=per_han.getClass().getMethod("seteta",org.drools.chance.distribution.IDistribution.class);
//           mt.invoke(per_han,dd1);
//           mt=per_han.getClass().getMethod("geteta");
//           eta=(Integer) mt.invoke(per_han);
//           assertEquals(33,eta.intValue());
//
//
//
//           //lavoro sullo storico
//           mt=per_han.getClass().getMethod("getetaHistory");
//          ImperfectHistoryField storico=(ImperfectHistoryField)mt.invoke(per_han);
//           assertEquals(33,storico.getCrisp());
//           dd1=(DiscreteDistribution) storico.getPast(-1);
//
//           //verifico che a time -1 ho i valori inseriti la penultima volta
//           Collection<ValueDegreePair> collvalu=dd1.getAllValues();
//           ValueDegreePair[] verify= collvalu.toArray(new ValueDegreePair [collvalu.size()] );
//           assertEquals(27,((Integer)(verify[0].getObj())).intValue());
//           assertEquals(25,((Integer)(verify[1].getObj())).intValue());
//
//
//           //verifico a -2 che sia null perchè ancora non inserito
//           dd1=(DiscreteDistribution) storico.getPast(-2);
//           assertEquals(null,dd1);
//
//
//           //verifico che tutti i valori inseriti per ultimi sono corretti
//           dd1=(DiscreteDistribution) storico.getCurrent();
//           collvalu=dd1.getAllValues();
//           verify= collvalu.toArray(new ValueDegreePair [collvalu.size()] );
//           assertEquals(33,((Integer)(verify[0].getObj())).intValue());
//           assertEquals(31,((Integer)(verify[1].getObj())).intValue());
//           assertEquals(30,((Integer)(verify[2].getObj())).intValue());
//
//
//
//
//           //test sul campo String
//           pd1=new SimpleDegree(0.2);
//           ValueDegreePair<String> el2=new ValueDegreePair<String>("viola",pd1);
//           vec1=new Vector<ValueDegreePair>();
//           vec1.add(el2);
//
//           pd1=new SimpleDegree(0.5);
//           el2=new ValueDegreePair<String>("isabella",pd1);
//           vec1.add(el2);
//
//           pd1=new SimpleDegree(0.3);
//           el2=new ValueDegreePair<String>("carlotta",pd1);
//           vec1.add(el2);
//
//           Collections.sort(vec1, new ElementComparator());
//
//           dd1= new DiscreteDistribution(vec1);
//           mt=per_han.getClass().getMethod("setnome",org.drools.chance.distribution.IDistribution.class);
//           mt.invoke(per_han,dd1);
//           mt=per_han.getClass().getMethod("getnome");
//           String nome=(String) mt.invoke(per_han);
//           assertEquals("isabella",nome);
//           assertEquals("isabella",person.getName());
//
//
//
//           //insert
//           pd1=new SimpleDegree(1);
//           el2=new ValueDegreePair<String>("viola",pd1);
//           vec1=new Vector<ValueDegreePair>();
//           vec1.add(el2);
//
//           dd1= new DiscreteDistribution(vec1);
//           mt=per_han.getClass().getMethod("setnome",org.drools.chance.distribution.IDistribution.class);
//           mt.invoke(per_han,dd1);
//
//
//           //insert
//           pd1=new SimpleDegree(1);
//           el2=new ValueDegreePair<String>("carlotta",pd1);
//           vec1=new Vector<ValueDegreePair>();
//           vec1.add(el2);
//
//           dd1= new DiscreteDistribution(vec1);
//           mt=per_han.getClass().getMethod("setnome",org.drools.chance.distribution.IDistribution.class);
//           mt.invoke(per_han,dd1);
//
//           mt=per_han.getClass().getMethod("getnomeHistory");
//           ImperfectHistoryField storicoName=(ImperfectHistoryField)mt.invoke(per_han);
//           assertEquals("carlotta",(String)(((DiscreteDistribution)(storicoName.getCurrent())).getElement(0)).getObj());
//           assertEquals("viola",(String)(((DiscreteDistribution)(storicoName.getPast(-1))).getElement(0)).getObj());
//           assertEquals(storicoName.getSize(),2);
//
//
//
//
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}
//
//
//	@Test
//	 public void TestAccessor(){
//
//
//		//FactoryHandler.getGenHandler(tdd);
//
//	}
//
//
//
//
//	@Test
//	public void testVec(){
//
//          StoricoVec st=new StoricoVec(LUNGSTO);
//          StoricoVec st2=new StoricoVec(LUNGSTO);
//          StoricoVec st3=new StoricoVec(LUNGSTO);
//          for(int i=0;i<NUM;i++){
//        	  st.setValue(getDistribution());
//        	  st2.setValue(getDistribution());
//        	  st3.setValue(getDistribution());
//
//          }
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-2);
//        	  st2.getPast(-2);
//        	  st3.getPast(-2);
//          }
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-1);
//        	  st2.getPast(-1);
//        	  st3.getPast(-1);
//          }
//          assertEquals(LUNGSTO,st.getSize());
//
//
//	}
//
//
//
//	@Test
//	public void testArrayList(){
//
//
//          ImperfectHistoryField st=new ImperfectHistoryField(LUNGSTO);
//          ImperfectHistoryField st2=new ImperfectHistoryField(LUNGSTO);
//          ImperfectHistoryField st3=new ImperfectHistoryField(LUNGSTO);
//
//          for(int i=0;i<NUM;i++){
//        	  st.setValue(getDistribution());
//        	  st2.setValue(getDistribution());
//        	  st3.setValue(getDistribution());
//          }
//
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-2);
//        	  st2.getPast(-2);
//        	  st3.getPast(-2);
//
//          }
//
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-1);
//        	  st2.getPast(-1);
//        	  st3.getPast(-1);
//
//          }
//
//          assertEquals(LUNGSTO,st.getSize());
//	}
//
//
//
//	@BeforeClass
//	public static void buildDistribution() {
//		SimpleDegree pd1=new SimpleDegree(0.2);
//        ValueDegreePair<String> el2=new ValueDegreePair<String>("viola",pd1);
//        Vector vec1=new Vector<ValueDegreePair>();
//        vec1.add(el2);
//
//        pd1=new SimpleDegree(0.5);
//        el2=new ValueDegreePair<String>("isabella",pd1);
//        vec1.add(el2);
//
//        pd1=new SimpleDegree(0.3);
//        el2=new ValueDegreePair<String>("carlotta",pd1);
//        vec1.add(el2);
//
//
//        DiscreteDistribution dd1= new DiscreteDistribution(vec1);
//        testDistribution = dd1;
//
//	}
//
//
//	@BeforeClass
//	public static void buildDeclaration() {
//
//		//creazione field
//		TypeFieldDescr tf1=new TypeFieldDescr("eta",new PatternDescr("java/lang/Integer"));
//		tf1.addMetaAttribute("history", "3");
//		TypeFieldDescr tf2=new TypeFieldDescr("nome",new PatternDescr("java/lang/String"));
//		tf2.addMetaAttribute("history", "2");
//		//creazione TypeDeclarationDescr
//
//		//creazione del descrittore della classe
//
//		tdd=new TypeDeclarationDescr("Person");
//		tdd.addMetaAttribute("ROLE", "EVENT");
//		tdd.setNamespace("test");
//		tdd.addField(tf1);
//		tdd.addField(tf2);
//
//
//	}
//
//
//
//	@Test
//	public void testLinkedList(){
//		StoricoLink st=new StoricoLink(LUNGSTO);
//        StoricoLink st2=new StoricoLink(LUNGSTO);
//        StoricoLink st3=new StoricoLink(LUNGSTO);
//
//          for(int i=0;i<NUM;i++){
//        	  st.setValue(getDistribution());
//        	  st2.setValue(getDistribution());
//        	  st3.setValue(getDistribution());
//
//          }
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-2);
//        	  st2.getPast(-2);
//        	  st3.getPast(-2);
//          }
//          for(int i=0;i<NUM;i++){
//        	  st.getPast(-1);
//        	  st2.getPast(-1);
//        	  st3.getPast(-1);
//          }
//          assertEquals(LUNGSTO,st.getSize());
//
//
//	}
//
//
//
//
//
//
//	private IDistribution<String> getDistribution() {
//		return testDistribution;
//	}
//
//
//    @Test
//    public void testHandleFactory(){
////    	FactImperfectHandleFactory fact=new FactImperfectHandleFactory();
////    	Person p1=new Person(24,"francesca");
////    	fact.addNewImperfectFactHandle(p1, tdd);
//
//    }
//




}
