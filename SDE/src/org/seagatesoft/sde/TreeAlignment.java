package org.seagatesoft.sde;

import java.util.ArrayList;
import java.util.List;

public class TreeAlignment
{
	private double score;
	private TagNode firstNode;
	private TagNode secondNode;
	private List<TreeAlignment> subTreeAlignment = new ArrayList<TreeAlignment>();
	
	public TreeAlignment()
	{
		
	}

	public TreeAlignment(double score, TagNode firstNode, TagNode secondNode)
	{
		setScore( score );
		setFirstNode( firstNode );
		setSecondNode( secondNode );
	}
	
	public TreeAlignment(TagNode firstNode, TagNode secondNode)
	{
		setFirstNode( firstNode );
		setSecondNode( secondNode );
	}

	public void setScore(double score)
	{
		this.score = score;
	}
	
	public void setFirstNode(TagNode firstNode)
	{
		this.firstNode = firstNode;
	}
	
	public void setSecondNode(TagNode secondNode)
	{
		this.secondNode = secondNode;
	}
	
	public void add(TreeAlignment alignment)
	{
		subTreeAlignment.add( alignment );

		if ( alignment.getSubTreeAlignment().size() != 0)
		{
			subTreeAlignment.addAll( alignment.getSubTreeAlignment() );
		}
	}

	public void addSubTreeAlignment(List<TreeAlignment> listAlignment)
	{
		subTreeAlignment.addAll( listAlignment );
	}
	
	public double getScore()
	{
		return score;
	}
	
	public TagNode getFirstNode()
	{
		return firstNode;
	}
	
	public TagNode getSecondNode()
	{
		return secondNode;
	}
	
	public List<TreeAlignment> getSubTreeAlignment()
	{
		return subTreeAlignment;
	}
}