package org.seagatesoft.sde;

public class DataRecord
{
	private TagNode[] recordElements;
	
	public DataRecord(TagNode[] recordElements)
	{
		this.recordElements = recordElements;
	}
	
	public TagNode[] getRecordElements()
	{
		return recordElements;
	}

	public int size()
	{
		int size = 0;
		
		for(TagNode recordElement: recordElements)
		{
			size += recordElement.subTreeSize();
		}
			
		return size;
	}
	
	public String toString()
	{
		String s = "";
		
		for (TagNode recordElement: recordElements)
		{
			s += recordElement.toString() + " ";
		}

		return s;
	}
}