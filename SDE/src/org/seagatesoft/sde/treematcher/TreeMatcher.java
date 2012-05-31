package org.seagatesoft.sde.treematcher;

import org.seagatesoft.sde.TagNode;
import org.seagatesoft.sde.TagTree;
import org.seagatesoft.sde.TreeAlignment;

public interface TreeMatcher
{
	public double matchScore(TagNode A, TagNode B);
	public double matchScore(TagNode[] A, TagNode[] B);
	public double matchScore(TagTree A, TagTree B);
	public double normalizedMatchScore(TagNode A, TagNode B);
	public double normalizedMatchScore(TagNode[] A, TagNode[] B);
	public double normalizedMatchScore(TagTree A, TagTree B);
	public TreeAlignment align(TagNode[] A, TagNode[] B);
	public TreeAlignment align(TagNode A, TagNode B);
}