import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ActorListScraper
{
	private static final String LIST_ACTORS_URL_BASE = "http://www.imdb.com/title/";
	private static final String LIST_ACTORS_URL_END = "/fullcredits?ref_=tt_cl_sm#cast";
	private static final String ACTOR_SEARCH_PHRASE = "2\" alt=\"";
	
	private long executionTime;
	
	public ActorListScraper() throws Exception
	{
		executionTime = System.currentTimeMillis();
		System.out.println("Starting to scrape actors...");
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (String s : MovieListJob.getHm().keySet())
		{
			pool.execute(new ActorListJob(LIST_ACTORS_URL_BASE, s, LIST_ACTORS_URL_END,
					ACTOR_SEARCH_PHRASE,null));
		}
		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.MINUTES);
		ActorListJob.printSb();
		executionTime = System.currentTimeMillis()-executionTime;
	}
	
	public void printInfo() 
	{ 
		System.out.printf("Found %d unique actor-movie pairs. Took %.2f seconds to scrape.\n", 
			getNumActorsFoundTotal(), executionTime/1000.0);
	}
	public static int getNumActorsFoundTotal() {return ActorListJob.getHm().size();}

}
