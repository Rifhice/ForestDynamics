import java.util.ArrayList;

public class Foret {
	
	private ArrayList<Arbre> listeArbre; // Liste des arbres
	private ArrayList<double[]> population = new ArrayList<double[]>(); // Population a un temps t note comme suit double[0] = temps; double[1] = population
	private double rayonDispersionFeu = 0.02; // Rayon de dispersion du feu
	private double lambdaFeu = 0.5; // Feu par unité de temps
	
	private double uMax; // La competition maximale
	private double tauxReproduction; // Le taux de reproduction par unite de temps
	private double esperanceVie; // L'esperance de vie d'un arbre
	private double rayonDispersion; // Le rayon dans lequel un arbre peut se reproduire
	private double rayonVoisinage; // Le rayon dans lequel un arbre vois ses voisins
	private int populationInitale;

	
	public Foret(){ // Constructeur Foret
		listeArbre = new ArrayList<Arbre>(); // Initialisation de la liste
	}
	
	public Foret(Foret f){
		Arbre.cpt = 0;
		this.setStatForet(f.getTauxReproduction(),f.getEsperanceVie(),f.getUmax(),f.getRayonDispersion(),f.getRayonVoisinnage(),f.getPopulationInitiale());
		listeArbre = new ArrayList<Arbre>();
		for(int x = 0 ; x < f.getListeArbre().size();x++){
			Arbre tmp = new Arbre(f.getListeArbre().get(x).getPosition()[0],f.getListeArbre().get(x).getPosition()[1], this);
		}
	}
	
	public void setPopulationInitiale(int p){
		if(p > listeArbre.size()){
			int tmp =  listeArbre.size();
			for(int x = 0 ; x < p - tmp ; x++){
				Arbre temp = new Arbre(java.lang.Math.random(),java.lang.Math.random(),this);
			}
		}
		else{
			int tmp =  listeArbre.size();
			for(int x = 0 ; x < tmp - p ; x++){
				listeArbre.remove((int)(java.lang.Math.random() * listeArbre.size()));
			}
		}
	}
	
	public void setStatForet(double lb,double ld,double umax,double rd, double rc, int pop){
		uMax = umax;  
		tauxReproduction = lb; 
		esperanceVie = ld; 
		rayonDispersion = rd; 
		rayonVoisinage = rc; 
		populationInitale = pop;
	}
	

	
	public int mortCompetition(double e){
		boolean trouve = false;
		int x = 0;
		double probMini = 0;
		double probMaxi = listeArbre.get(0).getCompetition();
		
		while(!trouve && x < listeArbre.size()){
			if(e > probMini && e <= probMaxi){
				trouve = true;
			}
			if(!trouve){
				x++;
				probMini = probMaxi;
				probMaxi = probMaxi + listeArbre.get(x).getCompetition();
			}
		}
		return x;
	}
	
	public int getPopulationInitiale(){
		return populationInitale;
	}
	
	public double getTauxReproduction(){
		return tauxReproduction;
	}
	
	public double getEsperanceVie(){
		return esperanceVie;
	}

	public double getRayonDispersion(){
		return rayonDispersion;
	}
	
	public double getRayonVoisinnage(){
		return rayonVoisinage;
	}	
	
	public double getUmax(){
		return uMax;
	}
	
	public void addArbre(Arbre a){
		listeArbre.add(a);
	}
	
	public void suppArbre(Arbre a){
		listeArbre.remove(a);
	}
	
	public int getSizeForet(){
		return listeArbre.size();
	}
	
	public double getLambdaFeu(){
		return lambdaFeu;
	}
	
	public ArrayList<Arbre> getListeArbre(){
		return listeArbre;
	}
	
	public ArrayList<double[]> getPopulation(){
		return population;
	}
	
	public int moyennePopulation(){ // Calcule la moyenne de la population
		int tmp = 0;
		int first = 0;
		if(population.size() > 5000){
			first = population.size() - 5000;
		}
		for(int x = first ; x < population.size() ; x++){
			tmp += population.get(x)[1];
		}
		tmp = tmp / (population.size() - first);
		return tmp;
	}
	
	public void feuDeForet(){ // Declenche un feu de foret
		ArrayList<Arbre> arbreBrule = new ArrayList<Arbre>(dispersionFeu(java.lang.Math.random(),java.lang.Math.random()));
		// Definie la liste des arbres a etre brulés grace a la methode "dispersionFeu"
		for(int x = 0 ; x < arbreBrule.size() ; x++){
			arbreBrule.get(x).meurt(); // Les arbres brulés meurent
		}		
	}
	
	public ArrayList<Arbre> dispersionFeu(double x , double y){ // Definis les arbres a etre brulés
		double[] tmp = new double[2]; // Point de depart du feu
		tmp[0] = x;
		tmp[1] = y;
		
		ArrayList<double[]> pointAncrage = new ArrayList<double[]>(); // Liste des points d'ancrage du feu
		pointAncrage.add(tmp); // Ajoute le point de depart du feu a la liste de point d'ancrage
		
		ArrayList<Arbre> arbreBrule = new ArrayList<Arbre>(); // Créer la liste d'arbre qui vont brulés
			
		while(pointAncrage.size() != 0){ // Tant qu'il existe des point d'ancrages
			ArrayList<double[]> listeRayon = new ArrayList<double[]>(Arbre.definirPerimetre(pointAncrage.get(0)[0] , pointAncrage.get(0)[1],rayonDispersionFeu));
			// On definie les rayon en fonction du point d'ancrage courant et du rayon de dispersion du feu 
			
			for(int o = 0 ; o < listeArbre.size(); o++){ // Pour chaque arbre
				for(int p = 0; p < listeRayon.size(); p++){ // Pour chaque rayon 
					if( listeArbre.get(o).estDansRayon(listeRayon.get(p)) ){ // On verifie si l'arbre est dans le rayon    
						if(!arbreBrule.contains(listeArbre.get(o))){ // si L'arbre en question n'a pas deja etait brule
							arbreBrule.add(listeArbre.get(o)); // Ajoute l'arbre au arbres brules
							double[] temp = new double[2]; // Definis un point d'ancrage sur la position de l'arbre
							temp[0] = listeArbre.get(o).getPosition()[0];
							temp[1] = listeArbre.get(o).getPosition()[1];
							pointAncrage.add(temp); // Ajoute le nouveau point d'ancrage
						}
					}
				}
			}
			
			pointAncrage.remove(0);	// On enleve le point d'ancrage que l'on vient de verifier		
		}
		return arbreBrule; // On renvoie la liste des arbre brules
	}
	
	public String toString(){
		String res = new String();
		 for(int x = 0; x < listeArbre.size(); x++){
			 res += listeArbre.get(x).toString() + "\n"; 
		}
		return res;
	}


	public static void main (String args[]) {
		/*
		Foret foret = new Foret();
		
		Arbre a0 = new Arbre(0.7,0.6,foret);
		Arbre a6 = new Arbre(0.9,0.6,foret);	
		Arbre a1 = new Arbre(0.91,0.6,foret);
		Arbre a2 = new Arbre(0.54,0.45,foret);
		Arbre a5 = a0.seReproduit();
		
		System.out.println(foret.dispersionFeu(0.5,0.5));
		*/
	}
}

