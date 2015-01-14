package metierDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import log.MonLogClient;

public class DatabaseConnection {
	  
	private static String url = "jdbc:mysql://37.59.110.237:3306/"; // ici 3306 correspond au port que MYSQL Installer m'a d�sign� (�a peut �tre un autre pour vous)
	private static String user = "remote";
	private static String passwd = "dantoncul"; // Ca c'est les miens
	private volatile static Connection connect;
	
	// constructeur priv� car c'est un singleton
	private DatabaseConnection(){ }

	/**
	 * 
	 * @return
	 */
	public static Connection getInstance(){
	  if(connect == null){
	    synchronized (DatabaseConnection.class) {
	    	if(connect == null){
		    	try {
		    		url += DatabaseUtils.BASE;
			        connect = DriverManager.getConnection(url, user, passwd);
			        new MonLogClient().add("Connection � la base r�ussie");
			    } catch (SQLException e) {
			    	new MonLogClient().add("Probl�me d'acc�s � la base distante"+e.getMessage());
			    	JOptionPane.showMessageDialog(null, "Message re�u : "+e.getMessage(), "Erreur de connexion � la base distante", JOptionPane.ERROR_MESSAGE);
			    }
	    	}
		}
	  }      
	  return connect;
	}    
}
