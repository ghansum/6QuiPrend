package communication;

import graphique.FenetrePrincipale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import log.MonLogClient;
import metierDAO.UserDAO;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import utils.JSONDecode;
import utils.JSONEncode;

public class User extends WebSocketClient implements Comparable<User> {

	private transient int userId;

	private String userNickname;

	private String userEmail;

	private String userPassword;

	private int currentBeef;

	public User(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public User(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("opened connection");
		send(JSONEncode.encodeConnect(userNickname));
		// if you plan to refuse connection based on ip or httpfields overload:
		// onWebsocketHandshakeReceivedAsClient
	}

	public int getUserId() {
		return this.userId;
	}

	public String getUserNickname() {
		return this.userNickname;
	}

	public String getUserEmail() {
		return this.userEmail;
	}

	public int getCurrentBeef() {
		return this.currentBeef;
	}

	public void setCurrentBeef(int currentBeef) {
		this.currentBeef = currentBeef;
	}

	public void setUser(int userId, String userNickname) {
		this.userId = userId;
		this.userNickname = userNickname;
		this.currentBeef = 0;
	}

	public int compareTo(User user) {
		if (this.getCurrentBeef() > user.getCurrentBeef()) {
			return 1;
		} else if (this.getCurrentBeef() < user.getCurrentBeef()) {
			return -1;
		} else {
			return 0;
		}
	}

	// donne la liste des cartes et send celle choisie
	// le nombre d'essai est limit� � 5 pour ne pas faire attendre ind�finiment
	// les autres joueurs
	// ! premier element de la liste est l'id de la partie ! 
	public void chooseCard(ArrayList<Integer> cards) {
		int cardValue;
		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		int idParty = cards.get(0);
		cards.remove(0);
		
		System.out.print("[ ");
		for(Integer c:cards){
			System.out.print("("+c+")");
		}
		System.out.println(" ]");
		for (int i = 0; i < 5; i++) {
			try {
				String in = sysin.readLine();
				cardValue = Integer.parseInt(in);
				if (cards.contains(cardValue)) {
					send(JSONEncode.encodeCarte(userNickname,cardValue, idParty));
					return;
				}
				else{
					System.out.println("Erreur cette Carte n'existe pas dans votre main");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		cardValue = Collections.max(cards);
		System.out.println("Vous vous �tes tromp� de carte trop de fois! Nous choisissons la carte " + cardValue + " � votre place :D ");
		send(JSONEncode.encodeCarte(userNickname,cardValue, idParty));
	}

	@Override
	public void onMessage(String message) {
		System.out.println("json: " + message);
		String flag = JSONDecode.getFlag(message);

		switch (flag) {
		case "getNickName":
			break;
		case Flag.SEND_CARTE:
			chooseCard(JSONDecode.decodeSendCards(message));
			break;
		case Flag.MESSAGE:
			System.out.println(JSONDecode.decodeMessage(message));
			break;
		case Flag.PARTIE_COMMENCE:
			System.out.println("La partie commence! Bon jeu :)");
			break;
		default:
			System.out.println("Error: ce flag n'existe pas.");
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// The codecodes are documented in class
		// org.java_websocket.framing.CloseFrame
		System.out.println("Connection closed by "
				+ (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception ex) {
		ex.printStackTrace();
		// if the error is fatal then onClose will be called additionally
	}

	// julien mdp

	// /*
	public static void main(String[] args) throws URISyntaxException,
			IOException {
		// ici authentification;
		// fenetre.getId
		// fenetre .getNickName

		User usr = new User(new URI("ws://localhost:12345"));
		
		Random random = new Random(System.nanoTime());
		usr.userId = random.nextInt();
		usr.userNickname = "toto" + usr.userId;
		usr.connect();

		System.out.println("bienvenue " + usr.userNickname);
		
		BufferedReader sysin = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			String in = sysin.readLine();
			if (in.equals("1")) {
				System.out.println("send create");
				usr.send(JSONEncode.encodeCreatePartie(usr.userNickname
						+ "Party", 2, true, usr.userNickname));
			}
			if (in.equals("2")) {
				System.out.println("send join");
				usr.send(JSONEncode.encodeJoinParty(usr.userNickname, 1));
			}
			// usr.sendWithFlag(in,"titi");
		}
	}// */

	// code d�j� bien assez gal�re a debug sans que j'ai � fouiller 10000 ans au
	// milieu de l'ui pour trouver ce que je veux
	/*
	 * public static void main( String[] args ) throws URISyntaxException,
	 * IOException {
	 * 
	 * 
	 * try { for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	 * if ("Nimbus".equals(info.getName())) {
	 * UIManager.setLookAndFeel(info.getClassName()); break; } } } catch
	 * (Exception e) { // If Nimbus is not available, you can set the GUI to
	 * another look and feel. }
	 * 
	 * Object sync = new Object(); FenetrePrincipale fenetre = new
	 * FenetrePrincipale(sync);
	 * 
	 * synchronized (sync) { try { sync.wait(); } catch (InterruptedException e)
	 * { new MonLogClient().add(e.getMessage());
	 * System.out.println(e.getMessage()); } } User user = null; try { user =
	 * new User( new URI( "ws://localhost:12345" )); } catch (URISyntaxException
	 * e1) { e1.printStackTrace(); } user.connect(); fenetre.setUser(user); int
	 * id = fenetre.getIdUser();
	 * 
	 * BufferedReader sysin = new BufferedReader( new InputStreamReader(
	 * System.in ) ); while(true){ String in = null; try { in =
	 * sysin.readLine(); } catch (IOException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } user.sendWithFlag(in,"titi"); } }//
	 */

}