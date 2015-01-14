package metierDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import metier.User;
import utils.MonLogClient;

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
	public static User verifieAuthentification(String login, String pass) {
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "SELECT * FROM "+DatabaseUtils.TABLE_USER+" WHERE nickname = ? AND password = ?";
				
				statement = CONNECTION.prepareStatement(requete);
				statement.setString(1, login);		// Rempli le premier "?" avec une valeur
				statement.setString(2, pass);
				ResultSet result = statement.executeQuery();
				
				// Une fois que l'on a r�cup�r� l'id du compte
				if(result!= null && result.first()==true){
					new MonLogClient().add("R�cup�ration du compte depuis la base de donn�es (apr�s authentification)");
					return new User(result.getInt("id"), result.getString("nickname"), 
							result.getString("email"), result.getString("password")); 
				
				} else { // aucun user existe avec ce login/mdp
                    JOptionPane.showMessageDialog(null,
                            "Login ou mot de passe invalide !",
                            "Connexion refus�e",
                            JOptionPane.ERROR_MESSAGE);
                }
			}
				
		} catch (SQLException e) {
		    return null;
		}
		return null;
	}

	public static boolean createUser(User user) {
		PreparedStatement statement;
		String requete;
		try{
			if(CONNECTION!=null){
				requete = "INSERT INTO "+DatabaseUtils.TABLE_USER+" VALUES (?,?,?)";

				statement = DatabaseConnection.getInstance().prepareStatement(requete);
				statement.setString(1, user.getUserNickname());		// Rempli le premier "?" avec une valeur
				statement.setString(2, user.getUserEmail());
				statement.setString(3, user.getUserPassword());
				statement.executeQuery();
				new MonLogClient().add("Cr�ation du nouveau compte pour "+user.getUserNickname());
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
