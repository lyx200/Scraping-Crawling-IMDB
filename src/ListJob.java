import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public abstract class ListJob
{
	protected String urlBase, urlBody, urlEnd, searchPhrase, name;
	protected long executionTime; //how long this job took to run
	protected int numFoundByThread; //number of movies found by this job
	
	public ListJob(String urlBase, String urlBody, String urlEnd, String searchPhrase, String name)
	{
		this.urlBase = urlBase;
		this.urlBody = urlBody;
		this.urlEnd = urlEnd;
		this.searchPhrase = searchPhrase;
		this.name = name;
		numFoundByThread = 0;
	}
	
	//finds all HTML codes which start with a '&' and replace them with corresponding characters
	protected String replaceHtmlCodes(String title)
	{
		for (int m=title.indexOf("&"); m!=-1; m=title.indexOf("&",m+1))
		{
			String htmlCode = title.substring(m,m+6);
			if (main_imdb.hm.containsKey(htmlCode) && !htmlCode.equals("&#x26;")) //ignore '&' for now
				title = title.substring(0,m) + main_imdb.hm.get(htmlCode) + title.substring(m+6);
			else if(!main_imdb.hm.containsKey(htmlCode)) main_imdb.hs.add(htmlCode); //adds unprecedented HTML codes to hash set
			if (m==title.length()) break;
		}
		//replaces HTML code for '&'
		for (int m = title.indexOf("&#x26;"); m!=-1; m = title.indexOf("&#x26;"))
			title = title.substring(0,m) + '&' + title.substring(m+6);
		return title;
	}
	
	//try downloading the page up to maxTries number of times
	protected String tryDownload(String url, int maxTries) throws Exception
	{
		String htmlText="";
		for (int numTries=1; numTries<=maxTries; numTries++) 
		{
			if (numTries>1) System.out.println("Attempt #"+numTries+ " " + urlBody);
			htmlText = readHtml(url);
			if ( (htmlText.indexOf(searchPhrase,0) ) == -1) //didn't download
			{
				if (numTries == maxTries)
					throw new Exception("Server timed out.");
				else Thread.sleep(1000);
			}
			else break;
		}
		return htmlText;
	}
	
	//connects to url, reads the stream, and returns the text as a String
	protected String readHtml(String url) throws Exception
	{
		URL serviceUrl = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader( serviceUrl.openStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ( (line=in.readLine()) != null)
			sb.append(line);
		return sb.toString(); 
	}
	
	//given a String s and a starting index to search, 
	//finds the beginning_index indexVal of the next String enclosed in ">VALUE<".
	//ending index is stored in iSearch after termination of algorithm
	protected static Pair findIndices(String s, int startingIndex)
	{
		boolean rightArrowFound = false; // rightarrow: >
		int indexVal=0;
		for ( int iSearch=startingIndex;iSearch<s.length();iSearch++)
		{
			char c = s.charAt(iSearch);
			if (c=='>') 
			{
				indexVal = iSearch;
				rightArrowFound = true;
			}
			else if (!rightArrowFound) continue;
			else if (c=='<' && iSearch==(indexVal+1)) continue;
			else if (c=='<') 
			{ 
				indexVal++; 
				return new Pair(indexVal,iSearch);
			}
		}
		return null;
	}
	
	public long getExecutionTime() {return executionTime;}
	public int getNumFound() {return numFoundByThread;}
	public String getName() {return name;}
	
	protected static class Pair
	{
		int x, y;
		Pair(int x, int y) { this.x = x; this.y = y; }
	}
}
