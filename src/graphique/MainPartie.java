package graphique;

import java.util.ArrayList;
import java.util.List;

import metier.Carte;
import metier.Partie;
import metier.User;

public class MainPartie {
	
	public static void main(String[] args){
		
		User user = new User("Nourdine", "nourdine@gmail.com", "ghansum");
		User user2 = new User("Patrick", "patrick@gmail.com", "pol");
		List<Carte> listC = new ArrayList<>();
		Partie partie = new Partie("Partie1", 2, false, user);
		partie.addPlayer(user2, listC);
		
		System.out.println("Nom de la partie : "+partie.getNom());
		System.out.println("Nombre de joueurs : "+partie.getNbJoueursMax());
		System.out.println("****************** debut de la partie ***************\n");
		
		partie.startGame();
	}
}