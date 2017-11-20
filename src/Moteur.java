import java.lang.*;
import java.io.*;
import java.util.*;


public class Moteur implements Runnable{
	
	static Foret foret; // La foret que le moteur va gerer
	static Foret foretInit;
	static double t; // Indice de temps courant
	static boolean pause = false;
	static boolean reset = false;
	static boolean getData = false;
	private Frame frame;
	Thread thread = new Thread(this);
	static Screen screen;
	static int evenement = 0;
	
	public Moteur(){
		thread.start();
	}

	public void run(){
		
		Foret foret = new Foret(); // Initialise la foret
		
		ArrayList<double[]> conf = getConf("conf.txt");
		if(!conf.isEmpty()){
			foret.setStatForet(conf.get(0)[0],conf.get(0)[1],conf.get(0)[2],conf.get(0)[3],conf.get(0)[4],(int)conf.get(0)[5]);
			evenement = (int)conf.get(0)[6];
		}
		
		Frame frame = new Frame(foret); // Créer la fenetre dans laquelle la foret va etre afficher

		while(Screen.scene == 1){
			System.out.println("Scene 1");
		}
		for(int o = 0 ; o < foret.getPopulationInitiale() ; o++){ // Genere autant d'arbre que l'aleatoire
			Arbre tmp = new Arbre(java.lang.Math.random(),java.lang.Math.random(),foret); // Creer les arbres aleatoirement sur la carte
		}
		
		foretInit = new Foret(foret);
		 
		t = 0.0; // Initialise le temps a 0
		
		while(true){ // Boucle infinie pour une evolution en temps réelle de la foret
			if(!pause || reset){		
				double[] tempo = {t * 10,foret.getSizeForet()}; // Ajout du temps et de la population courante de la foret a la liste population de la foret
				foret.getPopulation().add(tempo);
				
				double lambdaDglobal = foret.getSizeForet() * foret.getEsperanceVie(); // Definition du lambdaDglobal
				double lambdaBglobal = foret.getSizeForet() * foret.getTauxReproduction(); // Definition du lambdaBGlobal
				double sommeLambdaC = 0.0; // Initialisation de la somme des LambdaC
				
				for(int x = 0; x < foret.getSizeForet() ; x++){ // Pour tout les arbres de la foret
					sommeLambdaC = sommeLambdaC + foret.getListeArbre().get(x).getCompetition(); // Definition de la sommeLambdaC
				}
				double lambdaG = lambdaDglobal + lambdaBglobal  + sommeLambdaC; // + foret.getLambdaFeu();
				// Definition de lambda G

				t += (-(java.lang.Math.log(java.lang.Math.random()))) / lambdaG; // Calcul du prochain t ou un evenements occureras
				
				//System.out.println(foret.moyennePopulation() + "       " + t + "                        " + foret.getSizeForet()); // Affichage dans le terminal du temps et de la population actuelle
				
				double e = java.lang.Math.random()*lambdaG; // Random entre 0 et lambdaG pour definir quel evenement occurera
				
				if(e >= 0 && e <= lambdaDglobal){ // Verification si c'est une mort naturelle
					int tmp = (int)(java.lang.Math.random()*(foret.getSizeForet()-1) ); // Tirage aléatoire de l'arbre qui va mourrir
					//System.out.println("Mort de facon naturelle " + foret.getListeArbre().get(tmp));
					screen.drawCross(foret.getListeArbre().get(tmp).getPosition()[0],foret.getListeArbre().get(tmp).getPosition()[1],"redCross.png");
					foret.getListeArbre().get(tmp).meurt(); // Mort de l'arbre
				}
				else if(e > lambdaDglobal && e <= lambdaDglobal + lambdaBglobal){ // Verification si c'est une naissance
					int tmp = (int)( java.lang.Math.random() * (foret.getSizeForet()-1) ); // Tirage aléatoire de l'arbre qui va se reproduire
					double[] temp = foret.getListeArbre().get(tmp).seReproduit(); // L'arbre se reproduit	
					screen.drawCross(temp[0],temp[1],"greenCross.png");					
					//System.out.println("Naissance de l'arbre " + temp + "\n Issu de la reproduction de " + foret.getListeArbre().get(tmp));
				}
				else if(e > lambdaDglobal + lambdaBglobal && e <= lambdaG){ // Verification si c'est une mort par competition
					//System.out.println("Mort de par competition de " + foret.getListeArbre().get(y));
					int tmp = foret.mortCompetition(java.lang.Math.random()*sommeLambdaC);
					screen.drawCross(foret.getListeArbre().get(tmp).getPosition()[0],foret.getListeArbre().get(tmp).getPosition()[1],"redCross.png");
					foret.getListeArbre().get(tmp).meurt(); // Mort de l'arbre
				}
				/*
				else if(e > lambdaDglobal + lambdaBglobal + sommeLambdaC && e <= lambdaG){ // Verification si c'est un feu de foret
					foret.feuDeForet(); // Declechement du feu de foret
				}
				*/
				try { // Limiteur de Frames par seconde
					if(evenement != 0){
						Thread.sleep(1000 / evenement);
					}
				} catch (InterruptedException p) {
					p.printStackTrace();
				}
				if(reset){
					foret = new Foret(foretInit);
					screen.f = foret;
					screen.echelleX = screen.echelleXBase;
					screen.echelleY = screen.echelleYBase;
					reset = false;
					t = 0;
					System.out.println("Reseting [Moteur]");
				}
				
				if(getData){
					try{
						BufferedWriter fichier = initWriter("Data.txt"); // Créer un fichier de sortie pour les infos
						for(int l = 0 ; l < foret.getPopulation().size() ; l++){
							fichier.write(foret.getPopulation().get(l)[0] + " / " + foret.getPopulation().get(l)[1]); // Ecriture des infos dans le fichier texte
							fichier.newLine();
						}
					}
					catch (Exception p) {
						p.printStackTrace();
					}
					getData = false;
				}
				
			}
			else if (pause && !reset){
				System.out.println("Pause ! [Moteur]");
			}
		}
	}
	

	public static BufferedWriter initWriter(String destination){ // Methodes pour initialiser un fichier d'ecriture
		BufferedWriter fichier;
		try{
			fichier = new BufferedWriter(new FileWriter(destination));
			return fichier;
		}catch (Exception p) {
			p.printStackTrace();
		}
		return null;
	}
	

	public static boolean verifDouble(String s){
		s = correctionStringDouble(s);
		for(int x = 0 ; x < s.length() ; x++){
			if(!(s.charAt(x) == '.' | ((int)s.charAt(x) < 58 && (int)s.charAt(x) > 47))){
				return false;
			}
		}
		return true;
	}
	
	public static String correctionStringDouble(String s){
		return s.replace(",",".");
	}
	
	public static ArrayList<double[]> getConf(String fichier){
		String chaine="";
		//lecture du fichier texte	
		try{
			InputStream ips=new FileInputStream(fichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				chaine+=ligne+"\n";
			}
			br.close(); 
		}		
		catch (Exception e){
		}
		
		StringTokenizer conf = new StringTokenizer(chaine,"\n");
		ArrayList<StringTokenizer> detailConf= new ArrayList<StringTokenizer>();
		int nbConf = conf.countTokens();
		ArrayList<double[]> listeConf = new ArrayList<double[]>();
		
		for(int y = 0; y < nbConf;y++){
			String tmp = new String((String)conf.nextElement());
			detailConf.add(new StringTokenizer(tmp," "));
			double[] listeElement = new double[7];
			for(int x = 0; x < 7; x++){
				String Stmp = new String(correctionStringDouble((String)detailConf.get(y).nextElement()));
				if(verifDouble(Stmp)){
					listeElement[x] = Double.parseDouble(Stmp);
				}
			}
			listeConf.add(listeElement);
		}
		return listeConf;
	}	
	public static boolean apres(double t, double tActuel){
		return t<tActuel;
	}
	public static ArrayList getStatAt(double t, Foret f){
		ArrayList stat = new ArrayList();
		t *= 10;
		if(f.getPopulation().get(f.getPopulation().size()-1)[0] < t){
			return null;
		}
		int x = 0;
		if( f.getPopulation().get((f.getPopulation().size()-1)/2)[0] < t){
			x = f.getPopulation().size()/2;
			while(f.getPopulation().get(x)[0] < t){
				x++;
			}
		}
		else{
			x = f.getPopulation().size()/2;
			while(f.getPopulation().get(x)[0] > t){
				x--;
			}
		}
		
		stat.add(f.getPopulation().get(x)[1]);
		
		return stat;
	}
	
	public static void main (String args[]) {
		Moteur m = new Moteur();
	}
}

