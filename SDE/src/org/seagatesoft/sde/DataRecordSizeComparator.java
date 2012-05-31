package org.seagatesoft.sde;

import java.util.Comparator;

public class DataRecordSizeComparator implements Comparator<DataRecord>
{
	public int compare(DataRecord a, DataRecord b)
	{
		int sizeA = a.size();
		int sizeB = b.size();
		
		
		if (sizeA < sizeB)
		{
			return Integer.MIN_VALUE;
		}
		else if (sizeA > sizeB) 
		{
			return Integer.MAX_VALUE;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof DataRecord)
		{
			return true;
		}
		else
		{
			return false; 
		}
	}
}