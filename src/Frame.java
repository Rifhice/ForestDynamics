import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Frame extends JFrame{ //implements ActionListener{
	private	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private	int width = (int)screenSize.getWidth();
	private	int height = (int)screenSize.getHeight();

	public Frame(Foret foret){
		System.out.println(width + " / " + height);
		new JFrame();
		this.setTitle("Dynamique forestière !"); // Donne un nom a la fenetre
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // Arrete le programme lors de la fermeture
		this.setSize((int)(width/100.0*75),(int)(height/100.0*75)); // La fenetre prend tout l'ecran de base
		this.setVisible(true); // Rend la fenetre visible
		this.setResizable(true); // Autorise le resize			
		this.setLayout(null);
		this.setContentPane(new Screen(this,foret));
        this.setVisible(true);
	}
}
