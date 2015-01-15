package metierDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import utils.Md5;
import log.MonLogClient;
import metier.User;

/**
 * @(#) CompteDAO.java
 */

public class UserDAO {
	
	private static final Connection CONNECTION = DatabaseConnection.getInstance();
	
	/**
	 * V�rifie que les informations rentr�e sont bonnes
	 * @param login
	 * @param pass
	 * @return le compte associ�, ou null
	 */
	public static int verifieAuthentification(String login, String pass) {
		PreparedStatement statement;
		String requete;
		String passwd = Md5.encodeMd5(pass);
		try{
			if(CONNECTION!=null){
				requete = "SELECT * FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ? AND password = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setString(1, login);		// Rempli le premier "?" avec une valeur
				statement.setString(2, passwd);
				ResultSet result = statement.executeQuery();
				
				// Une fois que l'on a r�cup�r� l'id du compte
				if(result!= null && result.first()==true){
					new MonLogClient().add("R�cup�ration du compte depuis la base de donn�es (apr�s authentification)");
					return result.getInt("id"); 				
				} else { // aucun user existe avec ce login/mdp
                    JOptionPane.showMessageDialog(null,
                            "Login ou mot de passe invalide !",
                            "Connexion refus�e",
                            JOptionPane.ERROR_MESSAGE);
                }
			}
				
		} catch (SQLException e) {
		    return -1;
		}
		return -1;
	}

	public static boolean createUser(String nickname, String email, String password) {
		PreparedStatement statement;
		String requete;
		String encodedPasswd = Md5.encodeMd5(password);
		try{
			if(CONNECTION!=null){
				requete = "INSERT INTO "+DatabaseUtils.TABLE_USER+" VALUES (?,?,?)";

				statement = DatabaseConnection.getInstance().prepareStatement(requete);
				statement.setString(1, nickname);		// Rempli le premier "?" avec une valeur
				statement.setString(2, email);
				statement.setString(3, password);
				statement.executeQuery();
				new MonLogClient().add("Cr�ation du nouveau compte pour "+ nickname);
				return true;
			}
		} catch (SQLException e) {
		    return false;
		}
		return false;
	}
	
	/**
	 * Retourne vrai si un compte poss�de d�j� ce login
	 * @param login
	 * @return
	 */
	public static boolean existLogin(String login) {
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "SELECT * FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setString(1, login);
				ResultSet result = statement.executeQuery();
				
				if(result!= null && result.first()==true){ // Si y'a un r�sultat
					new MonLogClient().add("Le login est bien unique");
					return true;
				}
			}
		} catch(SQLException e){
			return false;
		}
		return false;
	}
	
}
