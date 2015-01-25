public class StringPair
{
	String x, y;
	
	public StringPair(String x, String y)
	{
		this.x=x; 
		this.y=y;
	}
	
	@Override
	public boolean equals(Object o)
	{
		StringPair t = (StringPair) o;
		if (x.equals(t.x) && y.equals(t.y)) return true;
		return false;
	}
	
	@Override
	public int hashCode()
	{
		int hc = 0;
		for (int i=0;i<x.length();i++)
			hc = ( 256*hc+x.charAt(i) ) %999983;
		for (int i=0;i<y.length();i++)
			hc = ( 256*hc+y.charAt(i) ) %999983;
		return hc;
	}
}
