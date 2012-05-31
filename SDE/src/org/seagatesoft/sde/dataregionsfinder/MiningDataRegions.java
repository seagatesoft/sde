package org.seagatesoft.sde.dataregionsfinder;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.seagatesoft.sde.DataRegion;
import org.seagatesoft.sde.MultiKeyMap;
import org.seagatesoft.sde.TagNode;
import org.seagatesoft.sde.treematcher.TreeMatcher;

public class MiningDataRegions implements DataRegionsFinder
{
	private TreeMatcher treeMatcher;
	
	public MiningDataRegions(TreeMatcher treeMatcher)
	{
		this.treeMatcher = treeMatcher;
	}
	
	public TreeMatcher getTreeMatcher()
	{
		return treeMatcher;
	}

	public List<DataRegion> findDataRegions(TagNode tagNode, int maxNodeInGeneralizedNodes, double similarityTreshold)
	{
		// List untuk menyimpan seluruh data region yang ditemukan
		List<DataRegion> dataRegions = new ArrayList<DataRegion>();
		// List untuk menyimpan kandidat data region, sepertinya tidak perlu
		List<DataRegion> currentDataRegions = new ArrayList<DataRegion>();
		
		if ( tagNode.subTreeDepth() >= 2 )
		{
			// lakukan pembandingan2 antara generalized nodes yang mungkin dari children milik tagNode
			Map<ComparisonResultKey, Double> comparisonResults = compareGeneralizedNodes(tagNode, maxNodeInGeneralizedNodes);
			// identifikasi data region dari hasil pembandingan generalized nodes
			currentDataRegions = identifyDataRegions(1, tagNode, maxNodeInGeneralizedNodes, similarityTreshold, comparisonResults);
			
			// tambahkan currentDataRegions yang ditemukan jika ada pada dataRegions
			if ( ! currentDataRegions.isEmpty() )
			{
				dataRegions.addAll(currentDataRegions);
			}
			
			// buat array yang menyatakan child mana saja dari tagNode yang termasuk dalam data region 
			boolean[] childCoveredArray = new boolean[tagNode.childrenCount()];

			for (DataRegion dataRegion: currentDataRegions)
			{
				for (int childCounter = dataRegion.getStartPoint(); childCounter < dataRegion.getStartPoint() + dataRegion.getNodesCovered(); childCounter++)
				{
					childCoveredArray[ childCounter - 1 ] = true;
				}
			}
			
			// cari data regions dari children yang tidak termasuk dalam currentDataRegions, rekursif
			for (int childCounter = 0; childCounter < childCoveredArray.length; childCounter++)
			{
				if (! childCoveredArray[childCounter] )
				{
					dataRegions.addAll( findDataRegions(tagNode.getChildAtNumber( childCounter+1 ), maxNodeInGeneralizedNodes, similarityTreshold));
				}
			}
		}

		return dataRegions;
	}

	private Map<ComparisonResultKey, Double> compareGeneralizedNodes(TagNode tagNode, int maxNodeInGeneralizedNodes)
	{
		Map<ComparisonResultKey, Double> comparisonResults = new MultiKeyMap<ComparisonResultKey, Double>();

		// mulai dari setiap node
		for (int childCounter = 1; childCounter <= maxNodeInGeneralizedNodes; childCounter++)
		{
			// membandingkan kombinasi yang berbeda mulai dari childCounter sampai maxNodeInGeneralizedNodes
			for (int combinationSize = childCounter; combinationSize <= maxNodeInGeneralizedNodes; combinationSize++)
			{
				// minimal terdapat sepasang generalized nodes, sepertinya redundan dengan pengecekan di bawah
				// TO BE OPTIMIZED
				if ( tagNode.getChildAtNumber( childCounter + 2*combinationSize - 1) != null )
				{
					int startPoint = childCounter;
					
					// mulai melakukan pembandingan pasangan-pasangan generalized nodes
					// BEDA DENGAN JURNAL, kondisi <=, untuk mengatasi kasus ketika childCounter = 1, combinationSize = 1, dan startPoint = tagNode.childrenCount() - 1 
					for (int nextPoint = childCounter + combinationSize; nextPoint <= tagNode.childrenCount(); nextPoint = nextPoint + combinationSize)
					{
						// lakukan pembandingan jika terdapat generalized nodes selanjutnya dengan ukuran yang sama
						if ( tagNode.getChildAtNumber(nextPoint + combinationSize - 1) != null )
						{
							// buat array dari kedua generalized nodes yang akan dibandingkan
							TagNode[] A = new TagNode[combinationSize];
							TagNode[] B = new TagNode[combinationSize];

							// isi array daftar nomor children dari tagNode yang termasuk dalam generalized node A
							int arrayCounter = 0;
							for (int i = startPoint; i < nextPoint; i++)
							{
								A[arrayCounter] = tagNode.getChildAtNumber(i);
								arrayCounter++;
							}

							// isi array daftar nomor children dari tagNode yang termasuk dalam generalized node A
							arrayCounter = 0;
							for (int i = nextPoint; i < (nextPoint + combinationSize); i++)
							{
								B[arrayCounter] = tagNode.getChildAtNumber(i);
								arrayCounter++;
							}

							// simpan hasil pembandingan
							ComparisonResultKey key = new ComparisonResultKey(tagNode, combinationSize, startPoint);
							comparisonResults.put(key, treeMatcher.normalizedMatchScore(A, B));
							startPoint = nextPoint;
						}
					}
				}
			}
		}
		
		return comparisonResults;
	}
	
	private List<DataRegion> identifyDataRegions(int initStartPoint, TagNode tagNode, int maxNodeInGeneralizedNodes, double similarityTreshold, Map<ComparisonResultKey, Double> comparisonResults)
	{
		List<DataRegion> dataRegions = new ArrayList<DataRegion>();
		DataRegion maxDR = new DataRegion(tagNode, 0, 0, 0);
		DataRegion currentDR = new DataRegion(tagNode, 0, 0, 0);
		
		// mulai dari tiap kombinasi
		for (int combinationSize = 1; combinationSize <= maxNodeInGeneralizedNodes; combinationSize++)
		{
			// mulai dari tiap startPoint
			// BEDA dengan jurnal, <, untuk efisiensi karena perbandingan ke-initStartPoint+combinationSize tidak perlu
			for (int startPoint = initStartPoint; startPoint < initStartPoint+combinationSize; startPoint++)
			{
				boolean flag = true;
				// BEDA dengan jurnal, childNumber+2*combinationSize-1 <=, karena belum tentu setiap ComparisonResultKey(tagNode, combinationSize, childNumber) ada
				for (int childNumber = startPoint; childNumber+2*combinationSize-1 <= tagNode.childrenCount(); childNumber += combinationSize)
				{
					ComparisonResultKey key = new ComparisonResultKey(tagNode, combinationSize, childNumber);

					if ( comparisonResults.get( key ) >= similarityTreshold )
					{
						// jika cocok untuk pertama kali
						if (flag)
						{
							currentDR.setCombinationSize(combinationSize);
							currentDR.setStartPoint(childNumber);
							currentDR.setNodesCovered( 2 * combinationSize );
							flag = false;
						}
						// jika cocok bukan untuk pertama kali
						else
						{
							currentDR.setNodesCovered( currentDR.getNodesCovered() + combinationSize );
						}
					}
					// jika tidak cocok dan sebelumnya cocok
					else if ( ! flag )
					{
						break;
					}
				}
				
				// jika currentDR yang baru ditemukan mencakup lebih banyak nodes dan dimulai dari posisi yang lebih awal atau sama dengan maxDR 
				if ( ( maxDR.getNodesCovered() < currentDR.getNodesCovered() ) && ( maxDR.getStartPoint() == 0 || currentDR.getStartPoint() <= maxDR.getStartPoint() ) )
				{
					maxDR.setCombinationSize( currentDR.getCombinationSize() );
					maxDR.setStartPoint( currentDR.getStartPoint() );
					maxDR.setNodesCovered( currentDR.getNodesCovered() );
				}
			}
		}
		
		// jika ditemukan data region
		if ( maxDR.getNodesCovered() != 0)
		{
			dataRegions.add(maxDR);
			
			// jika data region yang ditemukan masih menyisakan children yang belum dicari, 
			// maka cari data region mulai dari child setelah child terakhir dari data region
			if ( maxDR.getStartPoint() + maxDR.getNodesCovered() - 1 != tagNode.childrenCount() )
			{
				dataRegions.addAll( identifyDataRegions(maxDR.getStartPoint() + maxDR.getNodesCovered(), tagNode, maxNodeInGeneralizedNodes, similarityTreshold, comparisonResults) );
			}
		}

		return dataRegions;
	}
}