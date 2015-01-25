import java.util.concurrent.ConcurrentHashMap;

public class MovieListJob extends ListJob implements Runnable
{
	//stores movies scraped
	private static ConcurrentHashMap<String,String> hm_identifierMovie = new ConcurrentHashMap<>(10000,0.75f,20);

	public MovieListJob(String urlBase, String urlBody, String urlEnd, String searchPhrase, String name)
	{ super(urlBase, urlBody, urlEnd, searchPhrase, name); }

	@Override
	public void run()
	{
		try
		{
			long curr = System.currentTimeMillis();
			String url = urlBase + urlBody + urlEnd;
				
			//Try downloading the web page up to 5 times, exit if fail
			String textMovieList = tryDownload(url, 5);
			
			//find all the movies. searches for the next movie at index = start, stores found-index into i.
			for (int start=0, i=0; (i=textMovieList.indexOf(searchPhrase,start))!= -1 ; start=i+1) //found!
			{
				numFoundByThread++;
			
				//extract identifier 
				String identifier = textMovieList.substring(i+searchPhrase.length(), 
						i+searchPhrase.length()+9);
				
				//extract movie title
				Pair indices = findIndices(textMovieList,i);
				if (indices==null) throw new Exception ("Pair error. Could not find movie");
				String title = textMovieList.substring(indices.x,indices.y);
				
				//replaces HTML codes in movie title with corresponding characters
				if (title.contains("&")) title=replaceHtmlCodes(title);
				
				//store in ConcurrentHashMap
				hm_identifierMovie.put(identifier, title);				
				
				//print tests
				System.out.printf("%-5d %-50s %s\n", hm_identifierMovie.size(), title, identifier);
			}
			executionTime = System.currentTimeMillis()-curr;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static ConcurrentHashMap<String, String> getHm() {return hm_identifierMovie;}
	
}
