package main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import metier.User;
import utils.EcranGauche;
import utils.PanneauBordure;
import authentification.FenetreConnexion;
import authentification.FenetreInscription;

public class FenetreAccueil extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private static final String texte_connexion = "Connexion";
	private static final String texte_deconnexion = "D�connexion";
	private static final String texte_inscription = "Inscription";
	private static final String texte_quitter = "Quitter";
	private FenetreAccueil context;
	private JMenuBar menuBar;;
	private JMenu menu;
	private JMenuItem item_connexion; // Apparait si non connect�
	private JMenuItem item_deconnexion; // Apparait si connect�
	private JMenuItem item_exit;
	
	// Connexion
	private JButton bouton_connexion;
	private JButton bouton_deconnexion;
	private boolean is_connected;
	
	// Autres boutons
	private JButton bouton_inscription;
	
	//Panneau
	private PanneauBordure panneau;
	private EcranGauche ecrangauche;


	public FenetreAccueil(){
		
		this.context = this;  // Pour pouvoir utiliser notre instance de fenetreAccueil partout (methodes statics, listeners, ...) lorsque this ne fonctionne pas
	    this.setTitle("6 Qui Prend");
	    
	    URL url_tmp = getClass().getResource("/images/logo 6QuiPrend.png");
		if(url_tmp!=null) this.setIconImage(new ImageIcon(url_tmp).getImage()); // Logo
		this.setLayout(new BorderLayout()); // Layout qui permet d'ajouter soit sur le bord, soit au centre
	    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    this.setMinimumSize(new Dimension(800,600));

	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);

	    //On initialise nos menus     
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		item_connexion = new JMenuItem(texte_connexion); // Apparait si non connect�
		item_deconnexion = new JMenuItem(texte_deconnexion); // Apparait si connect�
		item_exit = new JMenuItem(texte_quitter);
	    this.menu.add(item_connexion); // Au d�but il n'y a que connexion
	    this.menu.addSeparator();
	    this.menu.add(item_exit);
	    
	    item_connexion.addActionListener(this); // l'action du listener est d�finie plus bas (va diff�rencier les connexions et d�connexions)
	    item_deconnexion.addActionListener(this);
	    item_exit.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
                        context.dispatchEvent(new WindowEvent(context, WindowEvent.WINDOW_CLOSING)); // On ferme l'appli
                    }
                });
	    
	    menuBar.add(menu);
	    this.setJMenuBar(menuBar);
	    
	    // On cr�� nos boutons et ajoutons les listeners
	    // Boutons connexions/d�connexions
	    bouton_connexion = new JButton(texte_connexion);
        bouton_connexion.addActionListener(this); // L'action du listener est d�finie en bas
	    
	    bouton_deconnexion = new JButton(texte_deconnexion);
	    bouton_deconnexion.addActionListener(this);
	    
	    bouton_inscription = new JButton(texte_inscription);
	    bouton_inscription.addActionListener(this);
	    
	    
        // Partie centre/gauche
        ecrangauche = new EcranGauche(new BorderLayout());
        url_tmp = getClass().getResource("/images/fond 6QuiPrend.png");
        if(url_tmp!=null) ecrangauche.setImage(new ImageIcon(url_tmp).getImage());
        this.add(ecrangauche, BorderLayout.CENTER);

        // On cr�� un nouveau panneau sur la droite (avec un JPanel vide pour espacer)
        panneau = new PanneauBordure(this, bouton_connexion, bouton_inscription);
        url_tmp = getClass().getResource("/images/fonddroite.jpg");
        if(url_tmp!=null) panneau.setImage(new ImageIcon(url_tmp).getImage());
        this.add(panneau, BorderLayout.EAST);

	    this.setVisible(true);
	    this.revalidate();
	    this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Si c'est une connexion
		if(e.getActionCommand().equals(item_connexion.getText()) || e.getActionCommand().equals(bouton_connexion.getText())){
		
//			Pour les tests, commenter les 3 lignes suivantes et laisser d�comment� la 4 eme ligne
//			FenetreConnexion fenetreconnexion = new FenetreConnexion(context);
//			fenetreconnexion.setVisible(true);
//			if(fenetreconnexion.isSucceeded()){
			if(true){
		    	is_connected=true; // Flag pouvant servir plus tard
		    	
				// R�organisation des menus
				this.menu.remove(item_connexion); // Si la connexion � r�ussie, on l'enl�ve du menu
				this.menu.add(item_deconnexion,0); // et on ajoute le bouton deconnexion
				
// 				Pour les tests, remplacer fenetreconnexion.getUser() par un new User contenant les valeurs que vous d�sirez
		    	new AccueilUser(context, new User("Julien", "julien.margarido@gmail.com", "mdp"));
	        } else {
	        	is_connected=false;
	        }
	    
	    // Si c'est une d�connexion
		} else if (e.getActionCommand().equals(item_deconnexion.getText()) || e.getActionCommand().equals(bouton_deconnexion.getText())){
    		//confirmation
			int choix = JOptionPane.showConfirmDialog(null, "�tes-vous s�r de vouloir vous d�connecter ?", "Deconnexion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    		if(choix == JOptionPane.OK_OPTION){
    			// Deconnexion � coder ici (un peu comme connexion)
    			//
    			this.menu.remove(item_deconnexion); // Si la deconnexion � r�ussie, on l'enl�ve du menu
    			this.menu.add(item_connexion,0);
    			
    			ecrangauche.removeAll();
    			URL url_tmp = getClass().getResource("/images/fond 6QuiPrend.png");
    	        if(url_tmp!=null) ecrangauche.setImage(new ImageIcon(url_tmp).getImage());
    	        
    			this.remove(panneau);
    			panneau = new PanneauBordure(context, bouton_connexion,bouton_inscription);
    			url_tmp = getClass().getResource("/images/fonddroite.jpg");
    	        if(url_tmp!=null) panneau.setImage(new ImageIcon(url_tmp).getImage());
    	        this.add(panneau, BorderLayout.EAST);
    			context.revalidate(); // A mettre toujours avant repaint
    			context.repaint(); // Mise � jour de la fenetre, a faire souvent lorsque changement
    		}
		} else if (e.getActionCommand().equals(bouton_inscription.getText())){
			FenetreInscription fenetreinscription = new FenetreInscription(context);
			fenetreinscription.setVisible(true);
			if(fenetreinscription.isSucceeded()){
		    	is_connected=true; // Flag pouvant servir plus tard
		    	
				// R�organisation des menus
				this.menu.remove(item_connexion); // Si la connexion � r�ussie, on l'enl�ve du menu
				this.menu.add(item_deconnexion,0); // et on ajoute le bouton deconnexion
				
				// La boite contenant les infos de l'utilisateur sera modifi� dans les classes d'Accueil
		    	new AccueilUser(context, fenetreinscription.getUser());
	        } else {
	        	is_connected=false;
	        }
		}
		this.revalidate();
		this.repaint();
	}
	
	public boolean isConnected() {
		return is_connected;
	}
	
	public PanneauBordure getPanneauBordure(){
		return panneau;
	}
	
	public EcranGauche getEcranGauche(){
		return ecrangauche;
	}
	
	public JButton getBoutonDeconnexion(){
		return bouton_deconnexion;
	}
	
}