package org.seagatesoft.sde.dataregionsfinder;

import org.seagatesoft.sde.TagNode;

/**
 * Kelas yang berfungsi sebagai key untuk menyimpan hasil pembandingan generalized nodes. Terfiri dari tiga properti,
 * yaitu tag node parent (tagNode), ukuran generalized node (combinationSize), dan awal dari generalized node (startChildNumber).
 *  
 * @author seagate
 *
 */
public class ComparisonResultKey implements Comparable
{
	TagNode tagNode;
	int combinationSize;
	int startChildNumber;
	
	public ComparisonResultKey(TagNode tagNode, int combinationSize, int startChildNumber)
	{
		this.tagNode = tagNode;
		this.combinationSize = combinationSize;
		this.startChildNumber = startChildNumber;
	}
	
	public int compareTo(Object o)
	{
		if (o instanceof ComparisonResultKey)
		{
			ComparisonResultKey castedO = (ComparisonResultKey) o;
			
			if ( (tagNode == castedO.tagNode) && (combinationSize == castedO.combinationSize) && (startChildNumber == castedO.startChildNumber) )
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
		else
		{
			return -1;
		}
	}

	public boolean equals(Object o)
	{
		if (o instanceof ComparisonResultKey)
		{
			ComparisonResultKey castedO = (ComparisonResultKey) o;
			
			return (tagNode == castedO.tagNode) && (combinationSize == castedO.combinationSize) && (startChildNumber == castedO.startChildNumber);
		}
		else
		{
			return false;
		}
	}
	
	public String toString()
	{
		return "Tag Node: " + tagNode.toString() + ", Combination Size: " + combinationSize + ", Start Child Number: " + startChildNumber; 
	}
}