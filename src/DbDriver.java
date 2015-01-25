import java.sql.*;

public class DbDriver
{
	private static final String URL = "jdbc:mysql://localhost:3306/?" +
	        "useUnicode=true&characterEncoding=UTF-8" +
	        "&rewriteBatchedStatements=true";
	private static final String USER = "root";
	private static final String PASSWORD = "OMITTED";
	private Statement st;
	private PreparedStatement pst;
	private Connection con;
	
	public DbDriver() throws Exception
	{
		//1. Get a connection to database
		con = DriverManager.getConnection(URL, USER, PASSWORD);
		
		//2. Create statement
		st = con.createStatement();
		
		//3. Database & table initializations
		st.execute("DROP DATABASE IF EXISTS imdb");
		st.execute("CREATE DATABASE IF NOT EXISTS imdb");
		st.execute("USE imdb");
		st.execute("CREATE TABLE IF NOT EXISTS movies("
				+ " identifier VARCHAR(255) NOT NULL PRIMARY KEY,  "
				+ " title VARCHAR(255) NOT NULL)"  );		
		st.execute("CREATE TABLE IF NOT EXISTS actors( "
				+ "name VARCHAR(255) BINARY NOT NULL,"
				+ "movie_id VARCHAR(255) NOT NULL,"
				+ "PRIMARY KEY(name, movie_id),"
				+ "FOREIGN KEY(movie_id) REFERENCES movies(identifier) )" );
	}
	
	public Statement getStatement() { return st; }
	
	public PreparedStatement getPreparedStatement(String preparedSql) throws Exception 
	{ return pst = con.prepareStatement(preparedSql); }
	
	public void closePst() throws Exception
	{ if (pst!=null) pst.close(); }
	
	public void closeDatabase() throws Exception
	{
		if (st!=null) st.close();
		if (pst!=null) pst.close();
		if (con!=null) con.close();
	}

}
