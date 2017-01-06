package gotcha.globals;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.*;

public final class Globals {
	// Private variables (although this is a "Global" class! :D)
	private static final String dbName = "gotchaDB";
	
	// Execute query via calling executeQuery method of Database
	public static final ResultSet executeQuery (String query) throws SQLException {
		try {
			Context context = new InitialContext();
			Database database = (Database)context.lookup(dbName);
			return database.executeQuery(query);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
