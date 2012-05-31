package org.seagatesoft.sde.datarecordsfinder;

import java.util.List;
import java.util.ArrayList;

import org.seagatesoft.sde.DataRecord;
import org.seagatesoft.sde.DataRegion;
import org.seagatesoft.sde.TagNode;
import org.seagatesoft.sde.treematcher.TreeMatcher;

public class MiningDataRecords implements DataRecordsFinder
{
	private TreeMatcher treeMatcher;

	public MiningDataRecords(TreeMatcher treeMatcher)
	{
		this.treeMatcher = treeMatcher;
	}

	public DataRecord[] findDataRecords(DataRegion dataRegion, double similarityTreshold)
	{
		// jika tidak kembalikan dataRegion karena merupakan data records
		
		// cek apakah ukuran generalized node-nya = 1
		if ( dataRegion.getCombinationSize() == 1)
		{
			TagNode parentNode = dataRegion.getParent();
			int startPoint = dataRegion.getStartPoint();
			int nodesCovered = dataRegion.getNodesCovered();
			
			// untuk setiap generalized node G dalam dataRegion
			for (int generalizedNodeCounter = startPoint; generalizedNodeCounter < startPoint + nodesCovered; generalizedNodeCounter++)
			{
				TagNode generalizedNode = parentNode.getChildAtNumber( generalizedNodeCounter );
				
				// cek apakah G merupakan data table row, jika ya kembalikan tiap generalized node sebagai data records
				if ( generalizedNode.subTreeDepth() <= 2)
				{
					return sliceDataRegion(dataRegion);
				}
				
				TagNode prevChild = generalizedNode.getChildAtNumber( 1 );

				// cek apakah semua anak dari G mirip, jika tidak kembalikan tiap generalized node sebagai data records
				for (int childCounter=2; childCounter <= generalizedNode.childrenCount() ; childCounter++ )
				{
					TagNode nextChild = generalizedNode.getChildAtNumber( childCounter );
					
					if ( treeMatcher.normalizedMatchScore(prevChild, nextChild) < similarityTreshold )
					{
						return sliceDataRegion(dataRegion);
					}
					
					prevChild = nextChild;
				}
			}
			
			List<DataRecord> dataRecordList = new ArrayList<DataRecord>();

			// kembalikan setiap node children dari tiap2 generalized node dari data region ini sebagai data records
			for (int generalizedNodeCounter = startPoint; generalizedNodeCounter < startPoint + nodesCovered; generalizedNodeCounter++)
			{
				TagNode generalizedNode = parentNode.getChildAtNumber( generalizedNodeCounter );
				
				for (TagNode childOfGeneralizedNode: generalizedNode.getChildren())
				{
					DataRecord dataRecord = new DataRecord( new TagNode[] { childOfGeneralizedNode} );
					dataRecordList.add( dataRecord );
				}
			}
			
			return dataRecordList.toArray( new DataRecord[0] );
		}
		
		// jika data region generalized node-nya terdiri lebih dari 1 node, 
		// maka kembalikan tiap generalized node sebagai data records
		return sliceDataRegion(dataRegion);
	}
	
	/**
	 * Method untuk membagi tiap generalized node pada dataRegion menjadi data record.
	 * 
	 * @param dataRegion
	 * @return
	 */
	private DataRecord[] sliceDataRegion(DataRegion dataRegion)
	{
		TagNode parentNode = dataRegion.getParent();
		int combinationSize = dataRegion.getCombinationSize();
		int startPoint = dataRegion.getStartPoint();
		int nodesCovered = dataRegion.getNodesCovered();
		DataRecord[] dataRecords = new DataRecord[ nodesCovered / combinationSize ];
		
		int arrayCounter = 0;
		for (int childCounter = startPoint; childCounter + combinationSize <= startPoint + nodesCovered; childCounter += combinationSize)
		{
			TagNode[] recordElements = new TagNode[combinationSize];
			
			int tagNodeCounter = 0;
			for(int generalizedNodeChildCounter = childCounter; generalizedNodeChildCounter < childCounter + combinationSize; generalizedNodeChildCounter++ )
			{
				recordElements[tagNodeCounter] = parentNode.getChildAtNumber( generalizedNodeChildCounter ); 
				tagNodeCounter++;
			}

			DataRecord dataRecord = new DataRecord( recordElements );
			dataRecords[ arrayCounter ] = dataRecord;
			arrayCounter++;
		}

		return dataRecords;
	}
}