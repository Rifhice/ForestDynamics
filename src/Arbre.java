import java.util.ArrayList;

public class Arbre {
	
	private final int id; // L'ID unique d'un arbre
	static int cpt=0;
	
	private double competition; // La competition appliqué sur un arbre

	private double[] position; // La position d'un arbre noté comme suit position[0] = x ; position[1] = y
	private ArrayList<Arbre> listeVoisin; // La liste des voisins d'un arbre
	private Foret foret; // La foret a laquelle l'arbre appartient
	static Screen screen;
	
	public Arbre(double x, double y, Foret f){ // Constructeur d'arbre
		id = cpt; // definition de son ID
		cpt++;
		position = new double[2]; // Definition de la position de l'arbre en fonction des parametre
		position[0] = x;
		position[1] = y;
		foret = f; // La foret
		listeVoisin = new ArrayList<Arbre>(); // Initialisation de sa liste de voisins
		rechercheVoisin(); // Recherche tous ces voisins et les ajoute a sa liste de voisins
		f.addArbre(this); // Ajoute l'arbre directement a sa foret dans la liste des arbres
	}
	
	public int getId(){
		return id;
	}
	
	public double[] getPosition(){
		return position;
	}
	
	
	public String toString(){
		return "Arbre Id : " + id + "\n pos : " + position[0] + "/" + position[1];
		//return "L'arbre numero " + id + " a un taux de reproduction de  : " + tauxReproduction + " une esperance de vie de : " + esperanceVie + " et une competition de " + competition + ". \n Il est en position " + position[0] + "/" + position[1];
	}
	
	public double calculSurfaceSol(double[] rayon){
		return (rayon[0] - rayon[1]) * (rayon[2] - rayon[3]);
	}	
	
	public int choixRayon(ArrayList<double[]> listeRayon){
		double sommeAire = 0.0;
		for(int x = 0 ; x < listeRayon.size() ; x++){
			sommeAire += calculSurfaceSol(listeRayon.get(x));
		}
		double e = java.lang.Math.random() * sommeAire;
		boolean trouve = false;
		int x = 0;
		double probMini = 0;
		double probMaxi = calculSurfaceSol(listeRayon.get(x));;
		
		while(!trouve && x < listeRayon.size()){
			if(e > probMini && e <= probMaxi){
				trouve = true;
			}
			if(!trouve){
				x++;
				probMini = probMaxi;
				probMaxi = probMaxi + calculSurfaceSol(listeRayon.get(x));
			}
		}
		return x;
	}
	
	public double[] seReproduit(){	// Permet a l'arbre de se reproduire dans son rayon d'action
		ArrayList<double[]> listeRayon = new ArrayList<double[]>(definirPerimetre(this.position[0],this.position[1],foret.getRayonDispersion()));
		// listeRayon contient l'ensemble des rayon dans lequel il peut agir un rayon est note comme suit double[0] = nord ; double[1] = sud ; double[2] = est ; double[3] = ouest
		// A noter qu'un arbre peut avoir au maximum 4 Rayon distinct     
		int x = choixRayon(listeRayon);
		double posX = (java.lang.Math.random() * (listeRayon.get(x)[2] - listeRayon.get(x)[3]) ) + listeRayon.get(x)[3];// On tire un random entre les position est et ouest du rayon;
		double posY = (java.lang.Math.random() * (listeRayon.get(x)[0] - listeRayon.get(x)[1]) ) + listeRayon.get(x)[1];	// On tire un random entre les position nord et sud du rayon
		new Arbre(posX,posY,foret);// On créer le nouvel arbre qui s'ajouteras tout seul a la foret
		return new double[]{posX,posY};
	}
	
	public void meurt(){ // Tue un arbre
		for(int x = 0 ; x < listeVoisin.size() ; x++){ 
			listeVoisin.get(x).supprVoisin(this); // L'enleve de la liste de tous ces voisins
			listeVoisin.get(x).calculCompetition(); // Recalcule la valeur de la competition du voisin en question
		}
		foret.suppArbre(this); // Enleve l'arbre de la foret
	}
	
	public void rechercheVoisin(){ // Recherche les voisins de l'arbre
		ArrayList<double[]> listeRayon = new ArrayList<double[]>(definirPerimetre(this.position[0] , this.position[1], foret.getRayonVoisinnage()));
		// listeRayon contient l'ensemble des rayon dans lequel il peut agir un rayon est note comme suit double[0] = nord ; double[1] = sud ; double[2] = est ; double[3] = ouest
		// A noté qu'un arbre peut avoir au maximum 4 Rayon distinct     
		ArrayList<Arbre> tmpForet = new ArrayList<Arbre>(foret.getListeArbre());
		for(int x = 0 ; x < tmpForet.size(); x++){ // Pour tout les arbre de la foret
			for(int y = 0; y < listeRayon.size(); y++){ // Pour chaque rayon
				if( tmpForet.get(x).estDansRayon(listeRayon.get(y)) ){  // On regarde si l'arbre est dans le rayon        
					listeVoisin.add(tmpForet.get(x)); // L'arbre est ajoute a la liste des voisins
					tmpForet.get(x).addVoisins(this); // L'arbre courant est ajoute a la liste de l'autre arbre
					tmpForet.get(x).calculCompetition(); // recalcul de la competition de l'autre arbre
				}
				else if(calculDistance(tmpForet.get(x)) > 2*foret.getRayonVoisinnage()){
					for(int i = 0; i < tmpForet.get(x).getListeVoisins().size();i++){
						tmpForet.remove(tmpForet.get(x).getListeVoisins().get(i));
					}
				}
			}
		}
		calculCompetition(); // Calcul de la competition de l'arbre courant
	}
  
	public double calculDistance(Arbre a){
		return java.lang.Math.sqrt( ((this.getPosition()[0] - a.getPosition()[0]) * (this.getPosition()[0] - a.getPosition()[0])) + ((this.getPosition()[1] - a.getPosition()[1]) * (this.getPosition()[1] - a.getPosition()[1])) );
	}
  
	public boolean estDansRayon(double[] rayon){ // Methode verifiant si un arbre est dans un rayon noté double[0] = nord ; double[1] = sud ; double[2] = est ; double[3] = ouest
		if(this.getPosition()[1] > rayon[1] & this.getPosition()[1] < rayon[0] && this.getPosition()[0] > rayon[3] & this.getPosition()[0] < rayon[2]){
			return true; 
		}
		return false;
	}
    
    public static void correctionPerimetre(double[] rayon){
		if(rayon[0] > 1){
			rayon[0] = 1;
		}
		if(rayon[1] < 0){
			rayon[1] = 0;
		}
		if(rayon[2] > 1){
			rayon[2] = 1;
		}
		if(rayon[3] < 0){
			rayon[3] = 0;
		}      
	}

	public static ArrayList<double[]> definirPerimetre(double positionX , double positionY, double rayonVoisinage){
		// Definir les different rayon a partir d'un point et d'un rayon.
		double posNord = positionY + rayonVoisinage;
		double posSud = positionY - rayonVoisinage;
		double posOuest = positionX - rayonVoisinage;
		double posEst = positionX + rayonVoisinage;
		ArrayList<double[]> listeRayon = new ArrayList<double[]>();

		if (posOuest < 0){ //gauche
			if (posNord > 1){ //haut
				double[] tmp = new double[4];
				//définir aire angle droite haut (retour gauche bas)
				tmp[0] = 1.0; //Nord
				tmp[1] = posSud; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = posOuest + 1.0; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord - 1.0; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = posEst; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord - 1.0; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = 1.0 + posOuest; //Ouest

				listeRayon.add(tmp);
			}
			else if (posSud < 0){ //bas
				double[] tmp = new double[4];
				//définir aire angle droite bas (retour gauche haut)
				tmp[0] = 1.0; //Nord
				tmp[1] = posSud + 1.0; //Sud
				tmp[2] = posEst; //Est
				tmp[3] = 0.0; //Ouest
		
				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = posOuest + 1.0; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = 1.0; //Nord
				tmp[1] = posSud + 1.0; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = posOuest + 1.0; //Ouest

				listeRayon.add(tmp);
			}
			else{ //gauche sans angle
				double[] tmp = new double[4];
				//définir aire gauche sans angle (retour droite)
				tmp[0] = posNord; //Nord
				tmp[1] = posSud; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = 1.0 + posOuest; //Ouest

				listeRayon.add(tmp);
			}
		}
		else if(posEst > 1){ //droite
			if (posNord > 1){ //haut
				double[] tmp = new double[4];
				//définir aire angle droite haut (retour gauche bas)
				tmp[0] = 1.0; //Nord
				tmp[1] = posSud; //Sud
				tmp[2] = posEst - 1.0; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord - 1.0; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = posOuest; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord - 1.0; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = posEst - 1.0; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);
			}
			else if (posSud < 0){//bas
				double[] tmp = new double[4];
				//définir aire angle droite bas (retour gauche haut)
				tmp[0] = 1.0; //Nord
				tmp[1] = posSud + 1.0; //Sud
				tmp[2] = 1.0; //Est
				tmp[3] = posOuest; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = posNord; //Nord
				tmp[1] = 0.0; //Sud
				tmp[2] = posEst - 1.0; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);

				tmp = new double[4];

				tmp[0] = 1.0; //Nord
				tmp[1] = posSud + 1.0; //Sud
				tmp[2] = posEst - 1.0; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);
			}
			else{ //droite sans angle
				double[] tmp = new double[4];
				//définir aire droite sans angle (retour gauche)
				tmp[0] = posNord; //Nord
				tmp[1] = posSud; //Sud
				tmp[2] = posEst - 1.0; //Est
				tmp[3] = 0.0; //Ouest

				listeRayon.add(tmp);
			}
		}

		if(posNord > 1 && posEst <= 1 && posOuest >= 0){ //haut
			double[] tmp = new double[4];
			//définir aire haut sans angle (retour bas)
			tmp[0] = posNord - 1.0; //Nord
			tmp[1] = 0.0; //Sud
			tmp[2] = posEst; //Est
			tmp[3] = posOuest; //Ouest

			listeRayon.add(tmp);
		}

		else if(posSud < 0 && posEst <= 1 && posOuest >= 0){ //bas
			double[] tmp = new double[4];
			//définir aire bas sans angle (retour haut)
			tmp[0] = 1.0; //Nord
			tmp[1] = posSud + 1.0; //Sud
			tmp[2] = posEst; //Est
			tmp[3] = posOuest; //Ouest

			listeRayon.add(tmp);
		}

		double[] tmp = new double[4];

		tmp[0] = posNord;
		tmp[1] = posSud;
		tmp[2] = posEst;
		tmp[3] = posOuest;

		listeRayon.add(tmp);

		for(int u = 0;u<listeRayon.size();u++){
			correctionPerimetre(listeRayon.get(u));
		}
		return listeRayon;
	}    

	private double calculCompetition(){ // Calcul de la competition
		competition = foret.getUmax() * listeVoisin.size();
		return competition;
	}
	
	public double getCompetition(){
		return calculCompetition();
	}
	
	private void addVoisins(Arbre a){
		listeVoisin.add(a);
	}
	
	private void supprVoisin(Arbre a){
		listeVoisin.remove(a);
	}
	
	
	public void printPerimetre(double[] t){ // Print un perimetre
		System.out.print("[");
		for(int x = 0 ; x < t.length ; x++){
			System.out.print(t[x] + "/");
		}
		System.out.println("]");
	}
	
	
	public ArrayList<Arbre> getListeVoisins(){
		return listeVoisin;
	}

	public static void main (String args[]) {
		
	}
}

