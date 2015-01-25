import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class main_imdb
{
	protected static HashMap<String, Character> hm = new HashMap<>(); //HTML code -> characters
	protected static HashSet<String> hs = new HashSet<>(); //stores new HTML codes found
	
	public static void main(String[] args) throws Exception
	{
		//populate Hash map to allow HTML decoding
		populateHm();
		
		//initialize database with tables
		DbDriver db = new DbDriver();
		
		//Scrape list of movies
		MovieListScraper movieScraper = new MovieListScraper();
		movieScraper.printInfo();
		
		//store movies into SQL. via batch insert
		String sqlCommand = "INSERT INTO movies(identifier,title) values (?, ?)";
		storeIntoDB(db, sqlCommand, MovieListJob.getHm());
		
		//Scrape all actors from all movies
		ActorListScraper actorScraper = new ActorListScraper();
		actorScraper.printInfo();		
		
		//store actors into SQL. via batch insert
		sqlCommand = "INSERT INTO actors(name,movie_id) values (?, ?)";
		storeIntoDB2(db, sqlCommand, ActorListJob.getHm());
		
		//print new HTML codes encountered
		printNewHtmlCodes();
		
		db.closeDatabase();
	}

	
	private static void storeIntoDB2(DbDriver db, String sqlCommand,
			ConcurrentHashMap<StringPair, Boolean> hm2) throws Exception
	{
		PreparedStatement pst = db.getPreparedStatement(sqlCommand);
		int batchSize = 1000, count = 0;
		
		for (StringPair pair : hm2.keySet()) 
		{ 
		    pst.setString(1, pair.x);
		    pst.setString(2, pair.y);
		    pst.addBatch();     
		    if(++count % batchSize == 0) pst.executeBatch();
		}
		pst.executeBatch(); // insert remaining records
		db.closePst();
		System.out.printf("Stored %d records into mySQL.\n", count);
	}


	private static void storeIntoDB(DbDriver db, String sqlCommand, 
			ConcurrentHashMap<String, String> hm)  throws Exception
	{
		PreparedStatement pst = db.getPreparedStatement(sqlCommand);
		int batchSize = 1000, count = 0;
		
		for (String identifier : hm.keySet()) 
		{ 
		    pst.setString(1, identifier);
		    pst.setString(2, hm.get(identifier));
		    pst.addBatch();     
		    if(++count % batchSize == 0) pst.executeBatch();
		}
		pst.executeBatch(); // insert remaining records
		db.closePst();
		System.out.printf("Stored %d records into mySQL.\n", count);
	}

	private static void printNewHtmlCodes()
	{
		if (!hs.isEmpty())
		{
			System.out.println("New HTML codes encountered:");
			for (String s: hs) 
				System.out.println(s+" ");
		}
	}

	private static void populateHm()
	{
		hm.put("&#xE9;", 'é');
		hm.put("&#xF4;", 'ô');
		hm.put("&#xE7;", 'ç');
		hm.put("&#xF6;", 'ö');
		hm.put("&#xB7;", '·');
		hm.put("&#xE2;", 'â');
		hm.put("&#x27;", '\'');
		hm.put("&#xBD;", '½');
		hm.put("&#xF1;", 'ñ');
		hm.put("&#xFC;", 'ü');
		hm.put("&#xA2;", '¢');
		hm.put("&#xB2;", '²');
		hm.put("&#xB3;", '³');
		hm.put("&#xC6;", 'Æ');
		hm.put("&#xA1;", '¡');
		hm.put("&#xE3;", 'ã');
		hm.put("&#xE8;", 'è');
		hm.put("&#x26;", '&'); 
	}
}
