/*
 *  TagTree.java  - Kelas untuk merepresentasikan pohon tag pada HTML Document Object Model (DOM)
 *  Copyright (C) 2009 Sigit Dewanto
 *  sigitdewanto11@yahoo.co.uk
 *  http://sigit.web.ugm.ac.id
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.seagatesoft.sde;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
 * Kelas untuk merepresentasikan pohon tag pada HTML Document Object Model (DOM).
 * 
 * @author seagate
 *
 */
public class TagTree
{
	/**
	 * Referensi ke root TagTree ini.
	 */
	private TagNode root;
	
	/**
	 * Map untuk menyimpan List<TagNode> pada tiap-tiap level TagTree secara urut (preorder).
	 */
	private Map<Integer, List<TagNode>> tagNodeMap;
	
	/**
	 * List untuk menyimpan semua TagNode dalam TagTree ini secara urut (preorder).
	 */
	private List<TagNode> tagNodeList;
	
	/**
	 * Konstruktur default. Dalam implementasinya akan menginstanisasi private field tagNodeMap dan tagNodeList.
	 *
	 */
	public TagTree()
	{
		tagNodeMap = new HashMap<Integer, List<TagNode>>();
		tagNodeList = new ArrayList<TagNode>();
	}

	/**
	 * Mengeset TagNode root untuk TagTree ini.
	 * 
	 * @param root TagNode root untuk TagTree ini
	 */
	public void setRoot(TagNode root)
	{
		this.root = root;
	}

	/**
	 * Mengembalikan root TagNode pada TagTree ini.
	 * 
	 * @return root TagNode pada TagTree ini
	 */
	public TagNode getRoot()
	{
		return root;
	}
	
	/**
	 * Menambahkan tag node pada daftar tag node dengan level yang sama dengan level tag node yang dimasukkan.
	 * Level dihitung dari root dengan indeks 0.
	 * 
	 * @param tagNode TagNode yang dimasukkan dalam daftar 
	 * @return
	 */
	public boolean addTagNodeAtLevel(TagNode tagNode)
	{
		int level = tagNode.getLevel();
		if (tagNodeMap.get(level) == null)
		{
			tagNodeMap.put(level, new ArrayList<TagNode>());
			return tagNodeMap.get(level).add(tagNode);
		}
		else
		{
			return tagNodeMap.get(level).add(tagNode);
		}
	}
	
	/**
	 * Mendapatkan List<TagNode> dengan level <code>level</code>.
	 * Level dihitung dari root dengan indeks 0.
	 * 
	 * @param level level pada TagTree ini
	 * @return List<TagNode> dengan level <code>level</code>
	 */
	public List<TagNode> getTagNodesAtLevel(int level)
	{
		return tagNodeMap.get(level);
	}
	
	/**
	 * Mengeset seluruh nomor tag node yang ada dalam TagTree ini secara preorder.
	 * Urutan secara preorder dimulai dari root dengan indeks 0.
	 *
	 */
	public void assignNodeNumber()
	{
		for (int level: tagNodeMap.keySet())
		{
			for (TagNode tagNode: tagNodeMap.get(level))
			{
				tagNodeList.add(tagNode);
				tagNode.setTagNodeNumber( tagNodeList.size()-1 );
			}
		}
	}
	
	/**
	 * Mendapatkan TagNode dengan nomor tagNodeNumber dalam TagTree ini.
	 * Urutan secara preorder dimulai dari root dengan indeks 0.
	 * 
	 * @param tagNodeNumber nomor tag node dalam TagTree ini
	 * @return TagNode dengan nomor tagNodeNumber dalam TagTree ini
	 */
	public TagNode getTagNodeAtNumber(int tagNodeNumber)
	{
		return tagNodeList.get(tagNodeNumber);
	}
	
	/**
	 * Mendapatkan banyaknya node dalam TagTree ini.
	 * 
	 * @return banyaknya node dalam TagTree ini
	 */
	public int size()
	{
		return tagNodeList.size();
	}
	
	/**
	 * Mendapatkan tingkat kedalaman maksimum TagTree ini. Mengembalikan 0 jika TagTree ini hanya terdiri dari root.
	 * 
	 * @return tingkat kedalaman maksimum TagTree ini
	 */
	public int depth()
	{
		return tagNodeMap.size()-1;
	}
}