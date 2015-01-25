import java.util.concurrent.ConcurrentHashMap;

public class ActorListJob extends ListJob implements Runnable
{
	//stores actors scraped in a thread-safe Set backed by a ConcurrentHashMap
	private static ConcurrentHashMap<StringPair, Boolean> hm_actorIdentifier = new ConcurrentHashMap<>(); 
	private static StringBuffer sb = new StringBuffer(1000000);
	private static int sbCount = 0;
	
	public ActorListJob(String urlBase, String urlBody, String urlEnd, String searchPhrase, String name)
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
			
			//extract all actors. 
			//loop stores the result of substring search from index=start into i
			for (int start=0, i=0; (i=textMovieList.indexOf(searchPhrase,start))!= -1 ; start=i+1) 
			{
				numFoundByThread++;
				
				//extract actor name
				int nextQuotationMarkIndex = textMovieList.indexOf("\"",i+searchPhrase.length());
				String actorName = textMovieList.substring(i+searchPhrase.length(), nextQuotationMarkIndex);
				
				//replaces HTML codes in movie title with corresponding characters
				if ( (actorName.indexOf('&') )!=-1 ) //contains '&'
					if (actorName.matches("&+.*;") ) //regex
						actorName=replaceHtmlCodes(actorName);
				
				//store into set
				hm_actorIdentifier.put(new StringPair(actorName, urlBody), false);
				
				//print tests
				incSbCount();
				sb.append(getSbCount()).append(" ").append(actorName).append(" ").append(urlBody).append("\n");
				if (getSbCount()%20000==0) printSb();
				
			}
			executionTime = System.currentTimeMillis()-curr;
		}
		catch (Exception e)
		{
			System.out.println(urlBase+urlBody+urlEnd);
			e.printStackTrace();
		}	
	}
	
	protected synchronized static void printSb() 
	{ 
		System.out.print(sb);
		sb = new StringBuffer(1000000);
	}
	private synchronized int getSbCount() { return sbCount; }
	private synchronized void incSbCount() { sbCount++; }
	
	@Override
	protected String tryDownload(String url, int maxTries) throws Exception
	{
		String htmlText="";
		for (int numTries=1; numTries<=maxTries; numTries++) 
		{
			if (numTries>1) System.out.println("Attempt #"+numTries+ " " + urlBody);
			htmlText = readHtml(url);
			if ( (htmlText.length()==0) ) //didn't download
			{
				if (numTries == maxTries)
					throw new Exception("Server timed out.");
				else Thread.sleep(1000);
			}
			else break;
		}
		return htmlText;
	}
	
	public static ConcurrentHashMap<StringPair, Boolean> getHm() { return hm_actorIdentifier; }

}
