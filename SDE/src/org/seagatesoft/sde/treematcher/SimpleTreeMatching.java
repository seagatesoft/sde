package org.seagatesoft.sde.treematcher;

import org.seagatesoft.sde.TagNode;
import org.seagatesoft.sde.TagTree;
import org.seagatesoft.sde.TreeAlignment;

public class SimpleTreeMatching implements TreeMatcher
{
	private static final short TRACKBACK_DIAGONAL = 0;
	private static final short TRACKBACK_UP = 1;
	private static final short TRACKBACK_LEFT = 2;

	public double matchScore(TagNode A, TagNode B)
	{	
		if ( A.getTagElement() != B.getTagElement() )
		{
			return 0.00;
		}
		else
		{
			double[][] matchMatrix = new double[ A.childrenCount() + 1 ] [ B.childrenCount() + 1 ];
			
			for (int i = 1; i < matchMatrix.length; i++)
			{
				for (int j = 1; j < matchMatrix[i].length; j++)
				{
					matchMatrix[i][j] = Math.max( matchMatrix[i][j-1], matchMatrix[i-1][j]);
					matchMatrix[i][j] = Math.max( matchMatrix[i][j], matchMatrix[i-1][j-1] + matchScore( A.getChildAtNumber(i), B.getChildAtNumber(j) ));
				}
			}
			
			return 1.00 + matchMatrix[ matchMatrix.length - 1 ] [ matchMatrix[0].length - 1];
		}
	}

	public double matchScore(TagNode[] A, TagNode[] B)
	{
		double[][] matchMatrix = new double[ A.length + 1 ] [ B.length + 1 ];
		
		for (int i = 1; i < matchMatrix.length; i++)
		{
			for (int j = 1; j < matchMatrix[i].length; j++)
			{
				matchMatrix[i][j] = Math.max( matchMatrix[i][j-1], matchMatrix[i-1][j]);
				matchMatrix[i][j] = Math.max( matchMatrix[i][j], matchMatrix[i-1][j-1] + matchScore( A[i-1], B[j-1] ));
			}
		}
		
		return 1.00 + matchMatrix[ matchMatrix.length - 1 ] [ matchMatrix[0].length - 1];
	}
	
	public double matchScore(TagTree A, TagTree B)
	{
		return matchScore(A.getRoot(), B.getRoot());
	}

	public double normalizedMatchScore(TagNode A, TagNode B)
	{
		return matchScore(A, B) / ( ( A.subTreeSize() + B.subTreeSize() ) / 2.0);
	}
	
	public double normalizedMatchScore(TagNode[] A, TagNode[] B)
	{
		int sizeA = 1;
		
		for (TagNode tagNode: A)
		{
			sizeA += tagNode.subTreeSize();
		}
		
		int sizeB = 1;
		
		for (TagNode tagNode: B)
		{
			sizeB += tagNode.subTreeSize();
		}
		
		return matchScore(A, B) / ( ( sizeA + sizeB ) / 2.0);
	}
	
	public double normalizedMatchScore(TagTree A, TagTree B)
	{
		return matchScore(A, B) / ( ( A.size() + B.size() ) / 2.0);
	}
	
	public TreeAlignment align(TagNode[] A, TagNode[] B)
	{
		TreeAlignment returnAlignment = new TreeAlignment();
		double[][] matchMatrix = new double[ A.length + 1 ] [ B.length + 1 ];
		TreeAlignment[][] alignmentMatrix = new TreeAlignment[ A.length ] [ B.length ];
		short[][] trackbackMatrix = new short[ A.length ] [ B.length ];
		
		// dapatkan skor penjajaran maksimum dan buat matriks untuk trackback-nya
		for (int i = 1; i < matchMatrix.length; i++)
		{
			for (int j = 1; j < matchMatrix[i].length; j++)
			{
				if ( matchMatrix[i][j-1] > matchMatrix[i-1][j] )
				{
					matchMatrix[i][j] = matchMatrix[i][j-1];
					trackbackMatrix[i-1][j-1] = TRACKBACK_LEFT;
				}
				else
				{
					matchMatrix[i][j] = matchMatrix[i-1][j];
					trackbackMatrix[i-1][j-1] = TRACKBACK_UP;
				}
				
				alignmentMatrix[i-1][j-1] = align( A[i-1], B[j-1] );
				double diagonalScore = matchMatrix[i-1][j-1] + alignmentMatrix[i-1][j-1].getScore();
				
				if ( diagonalScore > matchMatrix[i][j] )
				{
					matchMatrix[i][j] = diagonalScore;
					trackbackMatrix[i-1][j-1] = TRACKBACK_DIAGONAL;
				}
			}
		}
		
		// lakukan trackback
		int trackbackRow = trackbackMatrix.length-1;
		int trackbackColumn = trackbackMatrix[0].length-1;
		
		while ( trackbackRow >= 0 && trackbackColumn >= 0)
		{
			// jika ada node yang match
			if ( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_DIAGONAL )
			{
				returnAlignment.add( alignmentMatrix[ trackbackRow ][ trackbackColumn ] );
				trackbackRow--;
				trackbackColumn--;
			}
			else if( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_UP )
			{
				trackbackRow--;
			}
			else if( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_LEFT )
			{
				trackbackColumn--;
			}
		}

		returnAlignment.setScore( 1.00 + matchMatrix[ matchMatrix.length - 1 ] [ matchMatrix[0].length - 1] );
		
		return returnAlignment;
	}
	
	public TreeAlignment align(TagNode A, TagNode B)
	{
		TreeAlignment returnAlignment;

		if ( A.getTagElement() != B.getTagElement() )
		{
			returnAlignment = new TreeAlignment();
			returnAlignment.setScore( 0.00 );
			
			return returnAlignment;
		}
		else
		{
			returnAlignment = new TreeAlignment(A, B);
			double[][] matchMatrix = new double[ A.childrenCount() + 1 ] [ B.childrenCount() + 1 ];
			TreeAlignment[][] alignmentMatrix = new TreeAlignment[ A.childrenCount() ] [ B.childrenCount() ];
			short[][] trackbackMatrix = new short[ A.childrenCount() ] [ B.childrenCount() ];
			
			// dapatkan skor penjajaran maksimum dan buat matriks untuk trackback-nya
			for (int i = 1; i < matchMatrix.length; i++)
			{
				for (int j = 1; j < matchMatrix[i].length; j++)
				{
					if ( matchMatrix[i][j-1] > matchMatrix[i-1][j] )
					{
						matchMatrix[i][j] = matchMatrix[i][j-1];
						trackbackMatrix[i-1][j-1] = TRACKBACK_LEFT;
					}
					else
					{
						matchMatrix[i][j] = matchMatrix[i-1][j];
						trackbackMatrix[i-1][j-1] = TRACKBACK_UP;
					}
					
					alignmentMatrix[i-1][j-1] = align( A.getChildAtNumber( i ), B.getChildAtNumber( j ) );
					double diagonalScore = matchMatrix[i-1][j-1] + alignmentMatrix[i-1][j-1].getScore();
					
					if ( diagonalScore > matchMatrix[i][j] )
					{
						matchMatrix[i][j] = diagonalScore;
						trackbackMatrix[i-1][j-1] = TRACKBACK_DIAGONAL;
					}
				}
			}
			
			// lakukan trackback
			int trackbackRow = trackbackMatrix.length-1;
			int trackbackColumn = -1;

			if ( trackbackRow >= 0)
			{
				trackbackColumn = trackbackMatrix[0].length-1;
			}
			
			while ( trackbackRow >= 0 && trackbackColumn >= 0)
			{
				// jika ada node yang match
				if ( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_DIAGONAL )
				{
					returnAlignment.add( alignmentMatrix[ trackbackRow ][ trackbackColumn ] );
					trackbackRow--;
					trackbackColumn--;
				}
				else if( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_UP )
				{
					trackbackRow--;
				}
				else if( trackbackMatrix[ trackbackRow ][ trackbackColumn ] == TRACKBACK_LEFT )
				{
					trackbackColumn--;
				}
			}

			returnAlignment.setScore( 1.00 + matchMatrix[ matchMatrix.length - 1 ] [ matchMatrix[0].length - 1] );

			return returnAlignment;
		}
	}
}