package metier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.Set;

import org.json.JSONObject;

import communication.Flag;
import communication.Serveur;
import log.MonLogPartie;
import metier.Carte;

public class Partie extends Thread implements Serializable{

	private static transient final long serialVersionUID = 1L;
	private transient List<Carte> listCard;
	private int id;
	// HashMap contient la cl� du joueur ainsi que la liste de ses cartes actuels
	private transient HashMap<String, List<Carte>> playerCard;
	private HashMap<String, Integer> playerBeef;
	private int nbJoueursMax;
	private boolean isProMode;
	private transient List<List<Carte>> rows;
	private List<Carte> selectedCardByPlayer = null;
	private String nom;
	private transient boolean isPlayerReach66=false;
	private transient boolean isInGame;
	private Serveur serveur;
	private int choosenRow;

	private MonLogPartie logPartie;

	public Partie(String nom, int nbJoueurs, boolean isProMode, String userNickname){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		isInGame = false;
		playerCard = new HashMap<String, List<Carte>>();
		playerBeef = new HashMap<String, Integer>();
		playerCard.put(userNickname, new ArrayList<Carte>());
		playerBeef.put(userNickname, 0);

		logPartie = new MonLogPartie(this);
	}


	// Utilis� uniquement par la socket pour afficher les informations utiles � l'affichage (tout n'�tant pas n�cessaire)
	public Partie(int id, String nom, int nbJoueurs, boolean isProMode, String[] userNicknames){
		this.listCard=new ArrayList<Carte>();
		this.nbJoueursMax = nbJoueurs;
		this.isProMode = isProMode;
		this.nom = nom;
		this.id = id;
		isInGame = false;
		playerCard = new HashMap<String, List<Carte>>();
		for(String userNickname : userNicknames){
			playerCard.put(userNickname, new ArrayList<Carte>());
		}
	}

	public void setChoosenRow(int row){
		choosenRow = row;
	}


	public void setServeur(Serveur serveur){
		this.serveur = serveur;
	}

	public void setId(int id){
		this.id = id;
	}


	@Override
	public void run() {
		logPartie.add("La partie "+id+" a �t� cr��", Level.INFO);
		try {
			synchronized (this) {
				while(getListUser().size()<nbJoueursMax){
					serveur.sendMessageListPlayers(getListUser(),"Partie en attente de joueurs...",false);
					System.out.println("En attente de joueur...");
					logPartie.add("En attente de joueur ...", Level.INFO);
					this.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Nombre de joueurs max non atteint");
			logPartie.add("Nombre de joueurs max non atteint", Level.WARNING);
		}
		JSONObject flag = new JSONObject();
		serveur.sendMessageListPlayers(getListUser(), flag.put("nomFlag", Flag.PARTIE_COMMENCE).toString(), true);
		System.out.println("La partie commence ! Bon jeu");
		logPartie.add("Debut de la partie", Level.INFO);
		startGame();
	}

	private void startGame(){
		isInGame = true;
		int cptRound = 1;
		while(!isPlayerReach66){
			initializeRound();
			currentBeefAllPlayers();
			serveur.sendMessageListPlayers(getListUser(),"Fin de la manche "+cptRound, false);
			System.out.println("Fin de la manche "+cptRound);
			logPartie.add("Fin de la manche "+ cptRound, Level.INFO);
			cptRound++;
		}
		//TODO : Revoir l'algo dans le cas ou y'a un egalite
		//TODO: ajouter dans client methode "youlose" "youwin"
		List<String> listWinnerAndLoser = GestionPartie.getWinnerAndLoser(getListUser());
		for(int i = 0; i<listWinnerAndLoser.size(); i++){
			String nickName = listWinnerAndLoser.get(i);
			if(i == listWinnerAndLoser.size()-1){
				serveur.sendMessage(nickName, listWinnerAndLoser.get(i)+" (Loser) -> "+getListUsersBeef(nickName)+ " tete de boeufs");
				System.out.println(nickName+" (Loser) -> "+getListUsersBeef(nickName)+ " tete de boeufs");
				logPartie.add(nickName+" a perdu avec "+getListUsersBeef(nickName)+" t�tes de boeufs", Level.INFO);
			} else {
				serveur.sendMessage(listWinnerAndLoser.get(i), listWinnerAndLoser.get(i)+" (Winner) -> "+getListUsersBeef(nickName)+ " tete de boeufs \n");
				System.out.println(listWinnerAndLoser.get(i)+" (Winner) -> "+getListUsersBeef(nickName)+ " tete de boeufs");
				logPartie.add(nickName+" a gagn� avec "+getListUsersBeef(nickName)+" t�tes de boeufs", Level.INFO);
			}
		}
		//TODO : UserDao.addPartieGagnant(user) set le user gagnant, same pour le loser 
	}


	private void initializeRound(){
		this.listCard = GestionPartie.initializeDeck(nbJoueursMax, isProMode);
		initializeRows();
		String user = null;
		// On distribue les cartes pour chaque joueur
		List<Carte> playerCards=new ArrayList<Carte>();
		logPartie.add("Distribution des cartes", Level.INFO);
		for(int i = 0; i<nbJoueursMax; i++){				// Ajout des system.out.println()
			playerCards =  GestionPartie.disturb(listCard); //disturb == distribu� chez celui qui a �crit �a :^p
			logPartie.add("Joueur "+i+" re�oit : "+getListUser().get(i), Level.INFO);
			System.out.println(getListUser().get(i));

			user=getListUser().get(i);
			playerCard.put(user, playerCards);

		}

		// On r�cup�re les 4 premi�res cartes et on les ajoute a chacune des rang�es 
		GestionPartie.iniatializeRowsFirstCard(rows, listCard);
		logPartie.add("Initialisation des premi�res de chaque rang�e", Level.INFO);

		//Repr�sente le d�roulement d'une manche
		int cptTurn = 0;
		Carte selectedCard = null;
		while(cptTurn<10){
			selectedCardByPlayer=new ArrayList<Carte>();

			//Affiche le plateau
			showGameArea();
			serveur.sendMessageListPlayers(getListUser(), showGameArea(), false);
			System.out.println("Tour num�ro : "+(cptTurn+1));
			logPartie.add("Tour num�ro "+(cptTurn+1), Level.INFO);
			serveur.sendMessageListPlayers(getListUser(), "Tour num�ro : "+(cptTurn+1)+"\n" , false);
			// Faire en sorte que chaque joueur selectionne une carte chacun a leur tour
			for(int i = 0; i<playerCard.size(); i++){
				//M�thode qui propose a chaque joueur de choisir sa carte, retourne une carte
				int valueCard;

				int j=0;
				//Affiche la liste des cartes du joueur
				System.out.print(getListUser().get(i)+" : [ ");
				ArrayList<Integer> arrPlayerCards = new ArrayList<Integer>();
				while(j<=playerCard.get(getListUser().get(i)).size()-1){
					System.out.print(playerCard.get(getListUser().get(i)).get(j).getValue()+"  ");
					arrPlayerCards.add(playerCard.get(getListUser().get(i)).get(j).getValue());
					j++;
				}
				System.out.println("] ");

				serveur.sendCardToUser(getListUser().get(i), arrPlayerCards, id);
				synchronized(selectedCardByPlayer){
					try {
						selectedCardByPlayer.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (i+1 < getListUser().size()) {
					System.out.println("Au tour de " + getListUser().get(i+1)+" : ");
					logPartie.add("Au tour de "+getListUser().get(i+1), Level.INFO);
				}
				valueCard = selectedCardByPlayer.get(selectedCardByPlayer.size() - 1).getValue();
				boolean saisieCard = false;
				int cptEssaie = 0;
				while(!saisieCard && cptEssaie<2){
					if(valueCard != -1 ){
						if(GestionPartie.getCardFromHand(playerCard.get(getListUser().get(i)),valueCard) != null){
							//TODO: send � la place des syso
							System.out.println("Vous avez saisie la valeur "+valueCard);
							logPartie.add(getListUser().get(i+1)+" a choisi la carte "+valueCard, Level.INFO);
							saisieCard = true;
						} else {
							System.err.println("Cette carte n'est pas dans votre main");
							System.err.println("Recommencez");
							logPartie.add("Carte non existante dans la main du joueur", Level.WARNING);
							valueCard = GestionPartie.selectValueCardToPlay();
							cptEssaie++;
						}
					} else {
						System.out.println("Mauvaise saisie, recommencez");
						logPartie.add("Mauvaise saisie, recommencez", Level.WARNING);
						valueCard = GestionPartie.selectValueCardToPlay();
						cptEssaie++;
					}
				}
				if(cptEssaie == 2){
					selectedCard = GestionPartie.chooseRDMCardForPlayer(playerCard.get(getListUser().get(i)));
					System.out.println("Une carte a �t� choisie pour vous "+selectedCard.getValue());
					logPartie.add("Une carte al�atoire a �t� choisi, c'est la carte "+selectedCard.getValue(), Level.INFO);
					cptEssaie = 0;
				} else {
					selectedCard = GestionPartie.getCardFromHand(playerCard.get(getListUser().get(i)), valueCard);
				}
				playerCard.get(getListUser().get(i)).remove(selectedCard);
				addSelectedCard(selectedCard);

				/*
				 * TODO : Ajout du while pour l'affichage des cartes qui ne sont pas encore jou�es
				 */
				int k=0;
				System.out.print("[ ");
				while(k<playerCard.get(getListUser().get(i)).size()){
					System.out.print(playerCard.get(getListUser().get(i)).get(k).getValue()+" ");
					k++;
				}
				System.out.println(" ]");

				//On l'ajoute dans une liste de carte qui repr�sente l'ensemble des cartes selectionne par les joueurs
			}
			List<Carte> sortedCardsSelection = new ArrayList<Carte>();
			for(Carte carte : selectedCardByPlayer){
				logPartie.add("Triage des cartes dans l'ordre croissant avant traitement ", Level.INFO);
				sortedCardsSelection.add(carte);
			}
			Collections.sort(sortedCardsSelection);
			// Pour chaque carte de la liste selectedCardByPlayer on regarde si on peut la jouer

			List<Carte> fourLastCardRows = GestionPartie.getLastCardRows(rows);
			Carte cardToPlace;
			for(int i=0; i<sortedCardsSelection.size();i++){
				cardToPlace = sortedCardsSelection.get(i);
				logPartie.add("Carte a place "+cardToPlace.getValue(), Level.INFO);
				int selectRow;
				//Si la carte est plus petite que les derni�res cartes de chaque rang�e
				//Le joueur prend alors la ligne
				int indexCardChoosen = selectedCardByPlayer.indexOf(cardToPlace);

				if(GestionPartie.isPlusPetit(cardToPlace, fourLastCardRows)){
					logPartie.add("Carte inf�rieure aux derni�res cartes de chaque rang�e", Level.INFO);
					String userGetRow  = getListUser().get(i);
					serveur.selectRowToUser( getListUser().get(i), id );
					synchronized(this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					int selectRowCollect = choosenRow;
					int cptEssaie = 0;
					while(!(selectRowCollect>0 && selectRowCollect<5) && cptEssaie<2){
						System.out.println("Saisie entre 1 et 4 !!!!");
						selectRowCollect = GestionPartie.getRowToCollect();
						cptEssaie++;
					}
					if(cptEssaie ==3 ){
						selectRowCollect = GestionPartie.getRDMRowForPlayer(rows);
					}
					logPartie.add(getListUser().get(i)+" a choisi la rang�e "+selectRowCollect , Level.INFO);
					int nbBeef = GestionPartie.countBeef(rows.get(selectRowCollect-1));
					logPartie.add("La rang�e contient "+nbBeef+" t�tes de boeufs" , Level.INFO);
					serveur.sendMessage(getListUser().get(i), "Vous avez saisie la rang�e : "+selectRowCollect+" qui contient "+nbBeef+" tete de boeufs");
					System.out.println("Vous avez saisie la rang�e : "+selectRowCollect+" qui contient "+nbBeef+" tete de boeufs");
					attributeBeef(selectRowCollect-1, cardToPlace, selectedCardByPlayer, userGetRow);
					logPartie.add(userGetRow+" encaisse "+nbBeef+" t�tes de boeufs" , Level.INFO);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				} else if (rows.get(GestionPartie.selectRow(cardToPlace, fourLastCardRows)).size()==5){
					String userGetRow  = getListUser().get(indexCardChoosen);
					// Si le joueur place la 6eme carte alors il prend la rang�e
					selectRow = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					serveur.sendMessage(getListUser().get(i), "Vous avez plac� la 6�me carte de la rang�e "+selectRow);
					logPartie.add("La rang�e atteint 6 cartes", Level.INFO);
					System.out.println("Vous avez plac� la 6�me carte de la rang�e "+selectRow);
					attributeBeef(selectRow, cardToPlace, selectedCardByPlayer, userGetRow);
					logPartie.add(userGetRow+" encaise la rang�e enti�re", Level.INFO);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
					logPartie.add("La carte de valeur "+cardToPlace.getValue()+" est plac�e en d�but de rang�e", Level.INFO);
				} else {
					selectRow  = GestionPartie.selectRow(cardToPlace, fourLastCardRows);
					rows.get(selectRow).add(cardToPlace);
					logPartie.add("La carte de valeur "+cardToPlace.getValue()+" est plac�e dans la rang�e "+selectRow, Level.INFO);
					fourLastCardRows = GestionPartie.getLastCardRows(rows);
				}
			}
			cptTurn++;
		}
	}

	public List<String> getListUser(){
		Set<String> users = playerCard.keySet();
		List<String> listUserName = new ArrayList<String>();
		for(String name : users){
			listUserName.add(name);
		}

		return listUserName;
	}

	public Integer getListUsersBeef(String nickName){
		for (Entry<String, Integer> entry : playerBeef.entrySet()) {
			if(entry.getKey().equals(nickName)){
				return entry.getValue();
			}
		}
		return -1;
	}
	
	public int getNbJoueursMax() {
		return nbJoueursMax;
	}

	public boolean addPlayer(String userNickname){
		if(getListUser().size() < getNbJoueursMax()){
			playerCard.put(userNickname, new ArrayList<Carte>());
			playerBeef.put(userNickname, 0);
			playerCard.put(userNickname, new ArrayList<Carte>());
			return true;
		}else{
			return false;
		}
	}

	//TODO: probleme User ne devrait plus apparaitre et ne pas �tre manipul�. 
	public void removePlayer(String nickName){
		playerCard.remove(nickName);
		playerBeef.remove(nickName);
		getListUser().remove(nickName);
		nbJoueursMax--;
	}

	public List<Carte> getListCard(){
		return this.listCard;
	}

	private void initializeRows(){
		rows = new ArrayList<List<Carte>>();
		for(int i = 0; i<4; i++){
			rows.add(new ArrayList<Carte>());
		}
	}

	public String getNom() {
		return nom;
	}

	public List<List<Carte>> getRows() {
		return rows;
	}

	public HashMap<String, List<Carte>> getPComptes(){
		return this.playerCard;
	}

	public HashMap<String, Integer> getMap(){
		return this.playerBeef;
	}

	public int getIdPartie(){
		return id;
	}

	public boolean isInGame(){
		return isInGame;
	}

	private void attributeBeef(int indexRow, Carte card, List<Carte> selectedCardByPlayer, String userGetRow){
		int nbBeef = GestionPartie.countBeef(rows.get(indexRow));
		int nbMapbeef = 0;
		String name="";
		//		System.out.println("*********** Test de la map : "+map.values().size());
		for (Entry<String, Integer> entry : playerBeef.entrySet()) {
			name=entry.getKey();
			if(name.equals(userGetRow)){
				nbMapbeef=entry.getValue()+nbBeef;
				entry.setValue(nbMapbeef);
				serveur.sendMessage(userGetRow, userGetRow+ " a maintenant :  "+entry.getValue()+ " tete de boeufs");
				System.out.println(userGetRow+ " a maintenant :  "+entry.getValue()+ " tete de boeufs");
			}
		}

		if(nbMapbeef>=66){
			isPlayerReach66 = true;
		}

		//Enleve la ligne selectRow et ajoute la carte du joueur
		rows.get(indexRow).clear();
		rows.get(indexRow).add(card);
	}

	public boolean isProMode(){
		return isProMode;
	}

	private String showGameArea(){
		StringBuffer bf = new StringBuffer("");
		bf.append("\n*****************************************\n");
		System.out.println("\n*****************************************");
		for(int r=0; r<rows.size();r++){
			listCard = rows.get(r);
			bf.append("          -----------------------------\n");
			System.out.println("          -----------------------------");
			bf.append("ligne "+(r+1)+" :");
			System.out.print("ligne "+(r+1)+" : ");
			for(Carte carte : listCard){
				bf.append("| "+carte.getValue()+" |");
				System.out.print("| "+carte.getValue()+" | ");
			}
			System.out.println();
			bf.append("\n          -----------------------------\n");
			System.out.println("          -----------------------------");

		}
		bf.append("*****************************************\n\n");
		System.out.println("*****************************************\n");
		return bf.toString();
	}

	private void currentBeefAllPlayers(){
		for (Entry<String, Integer> entry : playerBeef.entrySet()) {
			serveur.sendMessageListPlayers(getListUser(), entry.getKey() + " -> " + entry.getValue(), false);
			System.out.println(entry.getKey() + " -> " + entry.getValue()) ;
		}
	}

	public List<Carte> getSelectedCardByPlayer() {
		return selectedCardByPlayer;
	}

	public boolean addSelectedCard(Carte card){
		boolean isExist = false;
		for (Carte carte : selectedCardByPlayer) {
			if(carte.getValue() == card.getValue()){
				isExist = true;
			}
		}
		if(!isExist){
			return selectedCardByPlayer.add(card);
		}
		return false;
	}
}

