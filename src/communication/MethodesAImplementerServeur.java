package communication;

import java.util.List;

import metier.Carte;
import metier.Partie;
import metier.User;

public class MethodesAImplementerServeur {
	
	// La classe Serveur contient la liste des parties;
	List<Partie> parties;
	
	// La classe Client contient une instance de FenetrePrincipale fp;
	
	
	//	envoie au client concern� la liste des parties
	public void refreshListParties(List<Partie> parties){ 
		// socket.write(Flag.REFRESH_LIST_PARTIES:parties)
	}
	
	// envoie au client concern� la r�ponse � sa demande pour rejoindre une partie
	public void rejoindrePartie(boolean reponse){
		// socket.write(Flag.REJOINDRE:reponse)
	}
	
	// d�s que Serveur re�oit un Flag.REJOINDRE_PARTIE et que sa r�ponse est positive, il envoie � tous les participants
	// de sa partie le nom du joueur (pour pouvoir l'afficher)
	public void sendNewParticipant(String nom_user){
		// socket.write(Flag.SEND_PARTICIPANTS: nom_user);
	}
	
	// demande a tous les joueurs de la partie d'envoyer une carte
	public void sendCarte(){
		// socket.write(Flag.SEND_CARTE);
	}
	
	// demande a tous les joueurs de la partie d'envoyer une ligne
	public void sendLigne(){
		// socket.write(Flag.SEND_LIGNE);
	}
	
	// dit au joueur concern� que c'est trop tard pour envoyer une carte
	public void sendTroTardCarte(){
		// socket.write(Flag.TROP_TARD_POUR_CARTE);
	}
	
	// dit au joueur concern� que c'est trop tard pour envoyer une ligne
	public void sendTropTardLigne(){
		// socket.write(Flag.TROP_TARD_POUR_LIGNE);
	}
	
	// donne au client concern� les tetes de boeufs � ajouter pour son user
	public void refreshBeef(int nbBeef){
		// socket.write(Flag.REFRESH_BEEF: nbBeef);
	}
	
	// donne au client concern� le nouveau contenu des lignes
	public void refreshLignes(List<List<Carte>> lignes){
		// socket.write(Flag.REFRESH_LIGNES: lignes);
	}
	
	// affiche aux clients de la partie le ou les gagnants
	public void afficheGagnant(String ... nomGagnant){
		// socket.write(Flag.GAGNANT:nomGagnant[0],nomGagnant[1],...);
	}
	
	// affiche aux clients de la partie le perdant
	public void affichePerdants(String nomPerdant){
		// socket.write(Flag.GAGNANT:nomPerdant);
	}
			
	
	public void ecoute(){
		
		// Ecoute ce que le client dit. Re�oit, la plupart du temps, un Flag suivi de donn�es.
		// 
		// switch(Flag re�u){
		//
		// case Flag.CREATION_PARTIE:
		//		- ajoute � sa liste de partie la partie re�u avec ce flag (faire new Partie avec les param�tres re�us)
		
		// case Flag.REJOINDRE_PARTIE :
		//		- verifie si il reste une place
		//		- si oui, ajoute le user � la partie et effectue this.rejoindrePartie(true)
		//		- si non, this.rejoindrePartie(false);
		
		// case Flag.SEND_CARTE
		//		- retrouver la partie gra�e au param�tre re�u idPartie
		//		- faire new Carte avec la valeur re�ue
		//		- ajouter � la partie la carte (il sait quelle carte correspond � quel joueur)
		
		
		// case Flag.SEND_LIGNE
		//		- retrouver la partie gra�e au param�tre re�u idPartie
		//		- ajouter � la partie le int re�u
		
		
		// case Flag.REFRESH_LISTE_PARTIES:
		//		- this.refreshListeParties(parties);
		
	}
}
