import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MovieListScraper
{	
	private static final String LIST_MOVIES_URL_BASE = "http://www.imdb.com/list/ls057823854/?start=";
	private static final String LIST_MOVIES_URL_END = "&view=compact&sort=listorian:asc";
	private static final String MOVIE_SEARCH_PHRASE = "le\"><a href=\"/title/";
	
	private long executionTime;
	
	//Scrape the list of movies released in 1970-2014
	public MovieListScraper() throws Exception
	{		
		executionTime = System.currentTimeMillis();
		ExecutorService pool = Executors.newFixedThreadPool(20);
		for (int movieIndex=1; movieIndex<10000; movieIndex+=250)
		{
			pool.execute(new MovieListJob(LIST_MOVIES_URL_BASE, movieIndex+"", LIST_MOVIES_URL_END,
					MOVIE_SEARCH_PHRASE,null));
		}
		pool.shutdown();
		pool.awaitTermination(5, TimeUnit.MINUTES);
		executionTime = System.currentTimeMillis()-executionTime;
	}
	public void printInfo() 
	{ 
		System.out.printf("Found %d movies. Took %.2f seconds to scrape.\n", 
				getNumMoviesFoundTotal(), executionTime/1000.0);
	}
	public static int getNumMoviesFoundTotal() {return MovieListJob.getHm().size();}
}
