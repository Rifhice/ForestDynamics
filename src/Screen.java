import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import java.text.*;
import java.awt.event.*;

public class Screen extends JPanel implements Runnable, ActionListener{

	static int scene = 1;
	private Font police = new Font("Arial", Font.BOLD, 9);
	private JTextField lb= new JTextField();
	private JTextField ld= new JTextField();
	private JTextField umax= new JTextField();
	private JTextField rd= new JTextField();
	private JTextField rc= new JTextField();
	private JTextField pop= new JTextField();
	private JTextField evenement= new JTextField();
	 

	JButton start = new JButton("Commencer la simulation");
	JButton changer = new JButton("Actualise");
	JButton pause = new JButton("Pause");
	JButton reset = new JButton("Reset");
	JButton getData = new JButton("Get Data");
	
	static boolean cross = false;
	static String fCross = new String();
	static double[] tCross = new double[2];
	
	Thread thread = new Thread(this);
	
	static Image tree = new ImageIcon("tree.jpg").getImage();
	
	FontMetrics fontMetrics;
	
	Frame frame; // La fenetre d'affichage
	
	Moteur m;
	
	static Foret f; // La foret qu'il va afficher
	
	static double echelleYBase = 1; // Definie l'echelle du graphe
	static double echelleXBase = 0.01; // Definie l'echelle du graphe
	
	static double echelleY = echelleYBase; // Definie l'echelle du graphe
	static double echelleX = echelleXBase; // Definie l'echelle du graphe
	
	public Screen (Frame frame, Foret f){
		this.frame = frame; // Inialisation de la fenetre
		this.f = f; // Initialisation de la foret
		Moteur.screen = this;
		thread.start(); // Debut du thread lancement de la methode run() de screen
	}
	
	public void paintComponent(Graphics g){ // Methode permetant de dessiner la foret et ces composants
		Graphics2D g2 = (Graphics2D)g; // Création d'un graphics2D pour pouvoir dessiner des points et autre
		g.setFont(police);
		g2.setFont(police);
		fontMetrics = g.getFontMetrics();
		g.clearRect(0, 0, this.frame.getWidth(), this.frame.getHeight()); // On effece l'ecran pour ne pas stacker les images
		if(scene == 1){
			drawConfBase(g,g2);
		}
		else if(scene == 2){        
			g2.setColor(Color.green); // La couleur est definies a vert
			drawForest(g,g2);
			drawGraph(g,g2);
			g2.setColor(Color.blue);
			drawUserInterface(g,g2);
			drawCurrentStats(g,g2);
			if(cross){
				drawCross(tCross[0],tCross[1],g,fCross);
				tCross = new double[2];
				cross = false;	
				fCross = new String();			
			}
		}
	}
	
	public static void drawCross(double x,double y,String fichier){
		cross = true;
		fCross = fichier;
		tCross[0] = x;
		tCross[1] = y;
	}
	
	public void drawCurrentStats(Graphics g,Graphics2D g2){
		g.drawString(f.getListeArbre().size() + "" , (int)(frame.getWidth()/ 100.0 * 72) , (int)(frame.getHeight() / 100.0 * 52));
		g.drawString(Moteur.t + "" , (int)(frame.getWidth()/ 100.0 * 80) , (int)(frame.getHeight() / 100.0 * 52));
	}	
	
	public void drawUserInterface(Graphics g,Graphics2D g2){
		int largeurTextField =  (int)(getHeight() / 100.0 * 2);
		int longueurTextField =  (int)(getWidth() / 100.0 * 4);
		int largeurButton =  (int)(getHeight() / 100.0 * 5);
		int longueurButton =  (int)(getWidth() / 100.0 * 7);
		
		int posXlb = (int)(getWidth()/ 100.0 * 70) + fontMetrics.stringWidth("Lambda B");
		lb.setBounds( posXlb,  (int)(getHeight() / 100.0 * 3) + largeurTextField / 2,longueurTextField,largeurTextField);
		
		int posXld = posXlb + longueurTextField + fontMetrics.stringWidth("Lambda D");
		ld.setBounds( posXld, (int)( getHeight() / 100.0 * 3+ largeurTextField / 2),longueurTextField,largeurTextField);
		
		int posXumax = posXld + longueurTextField + fontMetrics.stringWidth("Umax");
		umax.setBounds(posXumax,  (int)(getHeight() / 100.0 * 3+ largeurTextField / 2),longueurTextField,largeurTextField);
		
		int posXrd =(int)(getWidth()/ 100.0 * 70)  + fontMetrics.stringWidth("Dispersion");
		rd.setBounds(posXrd,  (int)(getHeight() / 100.0 * 8+ largeurTextField / 2),longueurTextField,largeurTextField);
		
		int posXrc = posXrd + longueurTextField + fontMetrics.stringWidth("Competition");
		rc.setBounds(posXrc,  (int)(getHeight() / 100.0 * 8+ largeurTextField / 2),longueurTextField,largeurTextField);
	
		int posXeve = posXrc + longueurTextField + fontMetrics.stringWidth("Evenement/s");
		evenement.setBounds(posXeve,  (int)(getHeight() / 100.0 * 8+ largeurTextField / 2),longueurTextField,largeurTextField);		
		
		int posXpop = (int)(getWidth()/ 100.0 * 70)  +  fontMetrics.stringWidth("Population initiale");
		pop.setBounds(posXpop,  (int)(getHeight() / 100.0 * 13+ largeurTextField / 2),longueurTextField,largeurTextField);	
		
		pause.setBounds((int)(getWidth()/100.0 * 81), (int)(getHeight()/100.0*55/2),longueurButton, largeurButton);
		
		changer.setBounds((int)(getWidth()/100.0 * 71), (int)(getHeight()/100.0*55/2), longueurButton, largeurButton);
		
		reset.setBounds((int)(getWidth()/100.0 * 91), (int)(getHeight()/100.0*55/2), longueurButton, largeurButton);
		
		getData.setBounds((int)(getWidth()/100.0 * 81), (int)(getHeight()/100.0*55/1.5), longueurButton, largeurButton);

		g2.draw(new Rectangle2D.Double(frame.getWidth()/ 100.0 * 69, frame.getHeight() / 100.0 * 2,(frame.getWidth()/ 100.0 * 29) ,(frame.getHeight() / 100.0 * 66)));
		g2.draw(new Line2D.Double(frame.getWidth()/ 100.0 * 69 ,frame.getHeight() / 100.0 * 66/2, frame.getWidth()/ 100.0 * 98, frame.getHeight() / 100.0 * 66/2));

		g.drawString("Lambda B",  posXlb - fontMetrics.stringWidth("Lambda B") ,  (int)(frame.getHeight() / 100.0 * 5));
		g.drawString("Lambda D",  posXld - fontMetrics.stringWidth("Lambda D") ,  (int)(frame.getHeight() / 100.0 * 5));
		g.drawString("Umax", posXumax - fontMetrics.stringWidth("Umax"),  (int)(frame.getHeight() / 100.0 * 5));
		g.drawString("Dispersion",  posXrd - fontMetrics.stringWidth("Dispersion"),  (int)(frame.getHeight() / 100.0 * 10));
		g.drawString("Competition",  posXrc - fontMetrics.stringWidth("Competition"),  (int)(frame.getHeight() / 100.0 * 10));
		g.drawString("Evenement/s",  posXeve - fontMetrics.stringWidth("Evenement/s") ,  (int)(frame.getHeight() / 100.0 * 10));		
		g.drawString("Population Initiale",  posXpop - fontMetrics.stringWidth("Population Initiale") ,  (int)(frame.getHeight() / 100.0 * 15));		

	}
	public void drawForest(Graphics g,Graphics2D g2){
		for(int x = 0 ; x < f.getSizeForet() ; x++){ // Pour chaque arbre on fait un point 
			g.drawImage(tree,(int)(f.getListeArbre().get(x).getPosition()[0] * (frame.getWidth()/ 100.0 * 65) + (frame.getWidth()/ 100.0 * 2)),(int)(f.getListeArbre().get(x).getPosition()[1] * (frame.getHeight() / 100.0 * 65) + (frame.getHeight() / 100.0 * 2)),(int)(frame.getWidth()/ 100.0 * 1),(int)(frame.getWidth()/ 100.0 * 1),null);
			//drawPoint(f.getListeArbre().get(x).getPosition()[0] * (frame.getWidth()/ 100.0 * 65) + (frame.getWidth()/ 100.0 * 2),f.getListeArbre().get(x).getPosition()[1] * (frame.getHeight() / 100.0 * 65) + (frame.getHeight() / 100.0 * 2),1,new Color(0,150,0),g2);
			//Dessin des arbres par un point
		}
		g2.draw(new Rectangle2D.Double(frame.getWidth()/ 100.0 * 2, frame.getHeight() / 100.0 * 2,(frame.getWidth()/ 100.0 * 66) ,(frame.getHeight() / 100.0 * 66)));
	}
	public void drawGraph(Graphics g,Graphics2D g2){
		int sautPoint = 0;
		if(echelleX < echelleY){
			sautPoint=(int)echelleY;
		}
		else{
			sautPoint=(int)echelleX;
		}

		for(int p = 0 ; p < f.getPopulation().size() ; p = p + sautPoint){ // Pour la taille de la liste
			if((frame.getHeight() / 100.0 * 94) - f.getPopulation().get(p)[1] / echelleY <  frame.getHeight() / 100.0 * 69){
				echelleY++;
				sautPoint++;
			}
			
			if( (frame.getWidth()/ 100.0 * 2) + f.getPopulation().get(p)[0]/ echelleX >  (frame.getWidth()/ 100.0 * 98)){
				echelleX +=0.01;
				sautPoint++;
			}
			
			drawPoint(frame.getWidth()/ 100.0 * 2 + f.getPopulation().get(p)[0] / echelleX, (frame.getHeight() / 100.0 * 94) - f.getPopulation().get(p)[1] / echelleY , (int)(1/sautPoint)+1 , Color.blue , g2);
			// Dessine les points du graphe
		}
		g2.setColor(Color.red);
		g2.draw(new Rectangle2D.Double(frame.getWidth()/ 100.0 * 2, frame.getHeight() / 100.0 * 69, (frame.getWidth()/ 100.0 * 96) ,frame.getHeight() / 100.0 * 25));

		g2.draw(new Line2D.Double(frame.getWidth()/ 100.0 * 2 , frame.getHeight() / 100.0 * 94 - f.moyennePopulation() / echelleY , (frame.getWidth()/ 100.0 * 98), frame.getHeight() / 100.0 * 94 - f.moyennePopulation() / echelleY ));
		// Dessine la ligne definissant la moyenne de la population
		g.drawString(f.moyennePopulation() + "",  (int)(frame.getWidth() / 100.0 * 98.1), (int)((frame.getHeight() / 100.0 * 94) - f.moyennePopulation()  / echelleY));
		drawGraduation(g,10);
	}
	public void drawPoint(double x,double y,int echelle,Color c,Graphics2D g){ // Methodes permettant de dessiner un point
		Point2D.Double point = new Point2D.Double(x, y); // Créer un objet point
		g.setColor(c); // Met la couleur a la couleur donné en parametre
		g.draw(new Ellipse2D.Double(point.x, point.y,echelle,echelle)); // Dessine le point
	}
	public void drawGraduation(Graphics g , int precision){
		
		
		g.drawString(0 + "" , (int)((frame.getWidth()/ 100.0 * 2)) - fontMetrics.stringWidth("0"),(int)(frame.getHeight() / 100.0 * 94) ); // Point 0 des axes
		g.drawString(0 + "" , (int)((frame.getWidth()/ 100.0 * 2)),(int)(frame.getHeight() / 100.0 * 95) ); // Point 0 des axes

		double maxX = (frame.getWidth()/ 100.0 * 98 - frame.getWidth()/ 100.0 * 2) * echelleX; // calcul Maximum X
		double maxY =(frame.getHeight()/ 100.0 * 94 - frame.getHeight()/ 100.0 * 69) * echelleY; // calcul Maximum Y 
				
		double interX = maxX / precision; // calsul des intervalles X
		double interY = maxY / precision; // calcul des intervalles Y 

		
		for(int x = 1; x < precision + 1;x++){
			double tmpX = interX * x / 10 ; // calcul du prochain point X
			double tmpY = interY * x; // calcul du prochain point Y 
				
			g.drawString(tmpX + "" , (int)((frame.getWidth()/ 100.0 * 2.1) +  interX * x / echelleX),(int)(frame.getHeight() / 100.0 * 95) ); // Dessin max X
			g.drawLine( (int)((frame.getWidth()/ 100.0 * 2) +  interX * x / echelleX) ,(int)(frame.getHeight() / 100.0 * 95) , (int)((frame.getWidth()/ 100.0 * 2) +  interX * x / echelleX) ,(int)(frame.getHeight() / 100.0 * 94));
			g.drawString((int)tmpY + "" , (int)(frame.getWidth()/ 100.0 * 2) - fontMetrics.stringWidth((int)tmpY+"") , (int)((frame.getHeight() / 100.0 * 94) - tmpY / echelleY)); // Dessin max Y
			g.drawLine((int)(frame.getWidth()/ 100.0 * 2) - fontMetrics.stringWidth((int)tmpY+"") , (int)((frame.getHeight() / 100.0 * 94) - tmpY / echelleY),(int)(frame.getWidth()/ 100.0 * 2) , (int)((frame.getHeight() / 100.0 * 94) - tmpY / echelleY));
		}
		
	}	
	
	public void drawCross(double x,double y,Graphics g,String fichier){
		Image cross = new ImageIcon(fichier).getImage();
		g.drawImage(cross,(int)(x * (frame.getWidth()/ 100.0 * 65) + (frame.getWidth()/ 100.0 * 2)),(int)(y * (frame.getHeight() / 100.0 * 65) + (frame.getHeight() / 100.0 * 2)),(int)(frame.getWidth()/ 100.0 * 1),(int)(frame.getWidth()/ 100.0 * 1),null);		
	}
	
	
	public void drawConfBase(Graphics g, Graphics2D g2){
		start.setBounds((int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2, (int)(getHeight()/100.0*50) - (int)(getHeight() / 100.0 * 5) / 2,(int)(getHeight() / 100.0 * 30), (int)(getHeight() / 100.0 * 5));
		changer.setText("Changer la configuration");
		changer.setBounds((int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2, (int)(getHeight()/100.0*45) - (int)(getHeight() / 100.0 * 5) / 2,(int)(getHeight() / 100.0 * 30), (int)(getHeight() / 100.0 * 5));

		g.drawString("Evenement/s : " + Moteur.evenement, (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*29) ); 		
		g.drawString("Lambda B : " + f.getTauxReproduction(), (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*41) ); 
		g.drawString("Lambda D : "+ f.getEsperanceVie() , (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*39) ); 
		g.drawString("Umax : "+  f.getUmax(), (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*37) ); 
		g.drawString("Rayon de dispersion : "+ f.getRayonDispersion() , (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*35) ); 
		g.drawString("Rayon de competition : " +  f.getRayonVoisinnage(), (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*33) ); 
		g.drawString("Population Initiale : " +  f.getPopulationInitiale(), (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2,(int)(getHeight()/100.0*31) ); 
		
		evenement.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Evenement/s : " + Moteur.evenement), (int)(getHeight()/100.0*29) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		lb.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Lambda B : " + f.getTauxReproduction()), (int)(getHeight()/100.0*41) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		ld.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Lambda D : " + f.getEsperanceVie()), (int)(getHeight()/100.0*39) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		umax.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Umax : " +  f.getUmax()), (int)(getHeight()/100.0*37) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		rd.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Rayon de dispersion : " + f.getRayonDispersion()), (int)(getHeight()/100.0*35) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		rc.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Rayon de competition : " + f.getRayonVoisinnage()), (int)(getHeight()/100.0*33) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));
		pop.setBounds(  (int)(getWidth()/100.0 * 50) - (int)(getHeight() / 100.0 * 30) / 2 + fontMetrics.stringWidth("Population Initiale : " +f.getPopulationInitiale()), (int)(getHeight()/100.0*31) - (int)(getHeight() / 100.0 * 2) / 2,(int)(getWidth() / 100.0 * 2),(int)(getHeight() / 100.0 * 2));

	}
	
	public void run(){ // Methode run venant de runnable declencher par Thread.start()
		
		System.out.println("Success Interface lancee !");
		
		repaint();
		
		lb.setFont(police);		
		ld.setFont(police);
		umax.setFont(police);
		rd.setFont(police);		
		rc.setFont(police);		
		pop.setFont(police);	
		evenement.setFont(police);
		
		start.addActionListener(this);	
		pause.addActionListener(this);		
		changer.addActionListener(this);		
		reset.addActionListener(this);
		getData.addActionListener(this);	
		
		evenement.addKeyListener(new ClavierListener());	
		lb.addKeyListener(new ClavierListener());	
		ld.addKeyListener(new ClavierListener());	
		umax.addKeyListener(new ClavierListener());	
		rd.addKeyListener(new ClavierListener());	
		rc.addKeyListener(new ClavierListener());	
		pop.addKeyListener(new ClavierListener());	
		//On ajoute le bouton au content pane de la JFrame
		
		frame.getContentPane().add(start);
		frame.getContentPane().add(changer);
		frame.getContentPane().add(evenement);
		frame.getContentPane().add(lb);
		frame.getContentPane().add(ld);
		frame.getContentPane().add(umax);
		frame.getContentPane().add(rd);
		frame.getContentPane().add(rc);
		frame.getContentPane().add(pop);
		
		while(true){
			if(!Moteur.pause){
				try { // Limiteur de Frames par seconde
					if(Moteur.evenement != 0){
						Thread.sleep(1000 / Moteur.evenement);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					repaint(); // Repaint l'ecran
			}
			else{
				System.out.println("Pause ! [Screen]");
			}
		}
	}
	
	public static void pause(int x){
			try { // Limiteur de Frames par seconde
				Thread.sleep(x);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == pause){
			if(!Moteur.pause){
				Moteur.pause = true;
				pause.setText("ReStart");
			}
			else{
				Moteur.pause = false;
				pause.setText("Pause");
			}
		}
		else if(e.getSource() == start){
			scene = 2;
			changer.setText("changer");
			frame.getContentPane().remove(start);
			frame.getContentPane().add(getData);
			frame.getContentPane().add(pause);
			frame.getContentPane().add(changer);
			frame.getContentPane().add(reset);
		}
		else if(e.getSource() == changer){
			double Dlb, Dld, Dumax, Drd,  Drc;
			int Dpop;
			if( !lb.getText().isEmpty() && Moteur.verifDouble( lb.getText() )){
				Dlb = Double.parseDouble(Moteur.correctionStringDouble(lb.getText()));
			}
			else{
				Dlb = f.getTauxReproduction();
			}
			if(!ld.getText().isEmpty() && Moteur.verifDouble( ld.getText() )){
				Dld = Double.parseDouble(Moteur.correctionStringDouble(ld.getText()));
			}
			else{
				Dld = f.getEsperanceVie();
			}
			if(!umax.getText().isEmpty() && Moteur.verifDouble( umax.getText() )){
				Dumax = Double.parseDouble(Moteur.correctionStringDouble(umax.getText()));	
			}
			else{
				Dumax = f.getUmax();
			}
			if(!rd.getText().isEmpty() && Moteur.verifDouble( rd.getText() )){
				Drd = Double.parseDouble(Moteur.correctionStringDouble(rd.getText()));
			}
			else{
				Drd = f.getRayonDispersion();
			}	
			if(!rc.getText().isEmpty() && Moteur.verifDouble( rc.getText() )){
				Drc = Double.parseDouble(Moteur.correctionStringDouble(rc.getText()));
			}
			else{
				Drc = f.getRayonVoisinnage();
			}
			if(!pop.getText().isEmpty() && Moteur.verifDouble( pop.getText() )){
				Dpop = (int)Double.parseDouble(Moteur.correctionStringDouble(pop.getText()));
			}
			else{
				Dpop = f.getPopulationInitiale();
			}
			if(!evenement.getText().isEmpty() && Moteur.verifDouble( evenement.getText() )){
				Moteur.evenement = (int)Double.parseDouble(Moteur.correctionStringDouble(evenement.getText()));
			}
			f.setStatForet( Dlb, Dld, Dumax, Drd,  Drc, Dpop);
			Moteur.foretInit.setPopulationInitiale(Dpop);
		}
		else if(e.getSource() == reset){
			Moteur.reset = true;
			if(!Moteur.pause){
				Moteur.pause = true;
				pause.setText("ReStart");
			}
		}
		else if(e.getSource() == getData){
			Moteur.getData = true;
		}
	}

	class ClavierListener implements KeyListener{
		
		public void keyPressed(KeyEvent event) {
		}

		public void keyReleased(KeyEvent event) {
		}

		public void keyTyped(KeyEvent event) {
		}   
	} 
	
	
	
	
	public static void main(String[] args){
	}
	
}
