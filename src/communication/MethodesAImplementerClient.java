package communication;

import metier.Partie;
import metier.User;

public class MethodesAImplementerClient {

	// La classe Client contient une instance de FenetrePrincipale fp;
	// La classe Serveur contient la liste des parties;
	
	
	// TODO dans Client.java : le user doit informer le serveur qu'il vient de cr�er une partie.
	// TODO dans Serveur.java : l'ajoute � sa liste des parties (r�cup�re donc ce qu'il y a sur le flux, construit un new Partie, et add dans sa liste).
	public void creationPartie(String nom,int nbMaxJoueur,boolean modePro, User user){
		//socket.write(#FLAG CREATION PARTIE:nom:nbMaxJoueur,modePro, user)
	}
	
	
	// TODO DANS Client.java : lorsque le user choisit de rejoindre une partie, il doit demander au serveur si je peux la rejoindre
	// et me retourne vrai ou faux.
	// TODO dans Serveur.java, si ce user peut rejoindre la partie (voir avec Patrick/Nourdine pour la verif), 
	// il l'ajoute dans la partie, et retourne vrai.
	public void rejoindrePartie(Partie idPartie, User user){
		//socket.write(#FLAG REJOINDRE PARTIE: idPartie, user)
		// return bool serait niquel, sinon faut voir dans l'�coute (voir en bas)
	}
	
	// TODO dans Client.java : envoie au serveur la valeur d'une carte, avec un flag contenant l'id de la partie
	// TODO dans Serveur.java : re�oit la carte, et la file � la partie en cours (partie sait quelle carte correspond � quel joueur)
	public void sendCarteChoisie(int idPartie, int carteValue){
		//socket.write(#FLAG ENVOYER CARTE PARTIE: idPartie : carteValue)
	}
	
	// TODO dans Client.java : envoie au serveur la valeur d'une carte, avec un flag contenant l'id de la partie
	// TODO dans Serveur.java : re�oit la carte, et la file � la partie en cours (partie sait quelle carte correspond � quel joueur)
	public void sendLigneChoisie(int idPartie, int numero){
		//socket.write(#FLAG ENVOYER CARTE PARTIE: idPartie : carteValue)
	}
	
	// rafraichir
	
	
	public void ecoute(){
		// Ecoute ce que le serveur dit
		// switch(flag re�u){
		//
		// case FLAG.REJOINDRE_PARTIE :
		//			l'interface fp attend un boolean
		// case FLAG.RAFRAICHIR:
		//			l'interface fp attend une liste de Partie
		// case FLAG.PARTIE_COMMENCE
		// 			l'interface attend qu'on lui dise que la partie commence
		// case FLAG.DONNE_TA_CARTE
		//			l'interface doit appeler la m�thode sendCarteChoisie(idPartie,valueCarte)
		// case FLAG.TROP_TARD_POUR_CARTE
		//			l'interface doit sendCarteChoisie avec une carte al�atoire
		// case FLAG.DONNE_LIGNE
		//			l'interface doit appeler la m�thode sendLigneChoisie(idPartie,numLigne)
		// case FLAG.TROP_TARD_POUR_LIGNE
		//			l'interface doit sendLigneChoisie avec une carte al�atoire
		
	}
	
	
	
	
	
}
