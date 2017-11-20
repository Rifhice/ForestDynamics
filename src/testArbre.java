import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;



public class testArbre {
	
	Foret f;
	
	static Arbre a1;
	static Arbre a2;
	
	Arbre a3;
	Arbre a4;
	Arbre a5;
	Arbre a6;
	Arbre a7;
	Arbre a8;
	Arbre a9; 
	
	@BeforeClass
	public static void avantTests() {
		System.out.println("------------------------");
		System.out.println("Before class");
		System.out.println("------------------------");
	}
	
	@Before
	public void avantTest() {
		f = new Foret();
		f.setStatForet(0.7,0.2,0.005,0.1,0.1,20);
		a1 = new Arbre(0.5,0.5,f);
		a2 = new Arbre(0.55,0.45,f);
		
		a3 = new Arbre(0.95,0.05,f);
		a4 = new Arbre(0.02,0.14,f);
		a5 = new Arbre(0.87,0.97,f);
		a6 = new Arbre(0.02,0.97,f);
		a7 = new Arbre(0.87,0.14,f);
		a8 = new Arbre(0.95,0.5,f);
		a9 = new Arbre(0.02,0.5,f);
		System.out.println(a9.getListeVoisins());
		System.out.println("------------------------");
		System.out.println("before");
	}
	
	@Test
	public void testId() {
		org.junit.Assert.assertFalse(a1.getId() == a2.getId() && a3.getId() == a4.getId() && a5.getId() == a6.getId());
	}
	
	@Test
	public void testPosition() {
		org.junit.Assert.assertTrue(a1.getPosition()[0] == 0.5 && a1.getPosition()[1] == 0.5);
		org.junit.Assert.assertFalse(a2.getPosition()[0] == 0.5 && a2.getPosition()[1] == 0.5);
		org.junit.Assert.assertTrue(a3.getPosition()[0] == 0.95 && a3.getPosition()[1] == 0.05);
		org.junit.Assert.assertFalse(a4.getPosition()[0] == 0.5 && a4.getPosition()[1] == 0.5);
		org.junit.Assert.assertTrue(a5.getPosition()[0] == 0.87 && a5.getPosition()[1] == 0.97);
		org.junit.Assert.assertFalse(a6.getPosition()[0] == 0.5 && a6.getPosition()[1] == 0.5);
		org.junit.Assert.assertTrue(a7.getPosition()[0] == 0.87 && a7.getPosition()[1] == 0.14);
	}	
	
	@Test
	public void testToString() {
		org.junit.Assert.assertEquals(a1.toString(), "Arbre Id : " + a1.getId() + "\n pos : " + a1.getPosition()[0] + "/" + a1.getPosition()[1]);
		org.junit.Assert.assertEquals(a2.toString(), "Arbre Id : " + a2.getId() + "\n pos : " + a2.getPosition()[0] + "/" + a2.getPosition()[1]);
		org.junit.Assert.assertEquals(a3.toString(), "Arbre Id : " + a3.getId() + "\n pos : " + a3.getPosition()[0] + "/" + a3.getPosition()[1]);
		org.junit.Assert.assertEquals(a4.toString(), "Arbre Id : " + a4.getId() + "\n pos : " + a4.getPosition()[0] + "/" + a4.getPosition()[1]);
		org.junit.Assert.assertEquals(a5.toString(), "Arbre Id : " + a5.getId() + "\n pos : " + a5.getPosition()[0] + "/" + a5.getPosition()[1]);
		org.junit.Assert.assertEquals(a6.toString(), "Arbre Id : " + a6.getId() + "\n pos : " + a6.getPosition()[0] + "/" + a6.getPosition()[1]);
		org.junit.Assert.assertEquals(a7.toString(), "Arbre Id : " + a7.getId() + "\n pos : " + a7.getPosition()[0] + "/" + a7.getPosition()[1]);

	}	
	

	@Test
	public void testCalculSurfaceSol(){
		double[] tmp = new double[]{1,0,1,0};
		double[] tmp2 = new double[]{2,0,2,0};
		org.junit.Assert.assertTrue(a1.calculSurfaceSol(tmp) == 1);
		org.junit.Assert.assertTrue(a1.calculSurfaceSol(tmp) < a1.calculSurfaceSol(tmp2));
	}

	
	@Test
	public void testSeReproduit(){
		int tmp = f.getSizeForet();
		a1.seReproduit();
		org.junit.Assert.assertTrue(f.getSizeForet() == tmp + 1);
	}

	@Test
	public void testMeurt(){
		int tmp = f.getSizeForet();
		f.getListeArbre().get(f.getSizeForet()-1).meurt();
		org.junit.Assert.assertTrue(f.getSizeForet() == tmp - 1);
	}	

	@Test
	public void testRechercheVoisins(){		
		org.junit.Assert.assertTrue(a1.getListeVoisins().contains(a2));
		org.junit.Assert.assertTrue(a2.getListeVoisins().contains(a1));
		
		org.junit.Assert.assertTrue(a3.getListeVoisins().contains(a4));
		org.junit.Assert.assertTrue(a3.getListeVoisins().contains(a5));
		org.junit.Assert.assertTrue(a3.getListeVoisins().contains(a6));
		org.junit.Assert.assertTrue(a3.getListeVoisins().contains(a7));
		
		org.junit.Assert.assertTrue(a8.getListeVoisins().contains(a9));
	}	
	
	@Test
	public void testEstDansLeRayon() {
		double[] tmp = new double[]{1,0,1,0};
		double[] tmp2 = new double[]{0.3,0,1,0};
		
		org.junit.Assert.assertTrue(a1.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a2.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a3.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a4.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a5.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a6.estDansRayon(tmp));
		org.junit.Assert.assertTrue(a7.estDansRayon(tmp));
		
		org.junit.Assert.assertFalse(a1.estDansRayon(tmp2));
	}
	
	@After
	public void apresTest() {
		System.out.println("Fin d'un Test");
		System.out.println("------------------------");
	}
	
	@AfterClass
	public static void apresTests() {
		System.out.println("------------------------");
		System.out.println("Fin des Test");
		System.out.println("------------------------");
	}

}
