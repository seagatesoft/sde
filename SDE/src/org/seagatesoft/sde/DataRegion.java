package org.seagatesoft.sde;

public class DataRegion
{
	private TagNode parent;
	private int combinationSize;
	private int startPoint;
	private int nodesCovered;
	
	public DataRegion(TagNode parent, int combinationSize, int startPoint, int nodesCovered)
	{
		setParent(parent);
		setCombinationSize(combinationSize);
		setStartPoint(startPoint);
		setNodesCovered(nodesCovered);
	}

	public void setParent(TagNode parent)
	{
		this.parent = parent; 
	}

	public void setCombinationSize(int combinationSize)
	{
		this.combinationSize = combinationSize;
	}
	
	public void setStartPoint(int startPoint)
	{
		this.startPoint = startPoint;
	}
	
	public void setNodesCovered(int nodesCovered)
	{
		this.nodesCovered = nodesCovered;
	}
	
	public TagNode getParent()
	{
		return parent;
	}

	public int getCombinationSize()
	{
		return combinationSize;
	}
	
	public int getStartPoint()
	{
		return startPoint;
	}
	
	public int getNodesCovered()
	{
		return nodesCovered;
	}
}