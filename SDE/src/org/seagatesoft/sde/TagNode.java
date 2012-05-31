/*
 *  TagNode.java  - Kelas untuk merepresentasikan tag node pada HTML Document Object Model (DOM)
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
import org.cyberneko.html.HTMLElements;

/**
 * Kelas untuk merepresentasikan tag node dalam HTML Document Object Model (DOM).
 * 
 * @author Sigit Dewanto
 *
 */
public class TagNode
{
	/**
	 * Referensi ke Parent milik TagNode ini.
	 * 
	 */
	private TagNode parent;
	
	/**
	 * Referensi ke seluruh child milik TagNode ini.
	 * 
	 */
	private List<TagNode> children;
	/**
	
	 * Kode elemen HTML yang dimiliki oleh TagNode ini. Merujuk pada daftar di 
	 * org.cyberneko.html.HTMLElements.
	 * 
	 */
	private short tagElement;
	
	/**
	 * Isi teks yang dimiliki oleh TagNode ini.
	 */
	private String innerText;
	
	/**
	 * Nomor TagNode ini dihitung dari root secara preorder.
	 */
	private int tagNodeNumber;
	
	/**
	 * Tingkat kedalaman (level) TagNode ini diukur dari root.
	 * 
	 */
	private int level = 0;
	
	/**
	 * Nomor yang menunjukkan TagNode ini merupakan child keberapa dari Parent-nya. Penomoran mulai dari 1 sampai N, dengan N merupakan jumlah 
	 * child yang dimiliki oleh Parent.
	 * 
	 */
	private int childNumber;

	/**
	 * Konstruktor default. Menginstanisasi private field children dalam implementasinya.
	 *
	 */
	public TagNode()
	{
		children = new ArrayList<TagNode>();
	}

	/**
	 * Menambahkan newChild sebagai child dari TagNode ini. Method ini juga akan mengeset nilai childNumber pada newChild.
	 *  
	 * @param newChild
	 */
	private void addChild(TagNode newChild)
	{
		children.add(newChild);
		newChild.setChildNumber(children.size());
	}
	
	/**
	 * Mengeset nilai childNumber (anak keberapa dari Parent-nya).
	 *  
	 * @param childNumber
	 */
	private void setChildNumber(int childNumber)
	{
		this.childNumber = childNumber;
	}
	
	/**
	 * Menyisipkan sebuah rangkaian tag node sebagai children tag node ini pada mulai dari posisi pos. 
	 * @param pos
	 * @param insertNodes
	 */
	public void insertChildNodes(int pos, List<TagNode> insertNodes)
	{
		for (TagNode insertNode: insertNodes)
		{
			insertNode.parent = this;
			insertNode.setLevel();
		}

		children.addAll(pos-1, insertNodes);
		
		for (int childListCounter=pos-1; childListCounter < children.size(); childListCounter++)
		{
			children.get( childListCounter ).setChildNumber( childListCounter+1 );
		}
	}

	/**
	 * Menghitung tingkat kedalaman (level) dari TagNode ini pada TagTree dan menyimpannya dalam 
	 * field level.
	 */
	private void setLevel()
	{
		level = countLevel();
	}

	/**
	 * Method rekursif untuk menghitung tingkat kedalaman TagNode ini dari root.
	 * 
	 * @return tingkat kedalaman (level) dari TagNode ini pada TagTree
	 */
	private int countLevel()
	{
		if (parent == null)
		{
			return 0;
		}
		else
		{
			return 1 + parent.countLevel();
		}
	}
	
	/**
	 * Method rekursif untuk menghitung tingkat kedalaman subtree dengan TagNode ini sebagai root.
	 * 
	 * @return level terdalam dari descendant TagNode ini
	 */
	private int countSubTreeDepth()
	{
		if (children.isEmpty())
		{
			return level; 
		}
		else
		{
			int subLevel = level + 1;
			int currentSubLevel = subLevel;
			
			for (TagNode child: children)
			{
				currentSubLevel = child.countSubTreeDepth();
				
				if (currentSubLevel > subLevel)
				{
					subLevel = currentSubLevel;
				}
			}
			
			return subLevel;
		}
	}

	/**
	 * Menunjuk suatu TagNode lain untuk dijadikan induk oleh TagNode ini. Versi ini masih belum menghapus referensi parent TagNode sebelumnnya ke TagNode 
	 * ini (jika sebelumnya telah memiliki parent). Method ini juga akan mendaftarkan TagNode ini sebagai child 
	 * pada Parent. TagNode ini juga akan mendapat nomor urut child dari Parent. Selanjutnya nilai level TagNode ini akan diset.
	 * 
	 * @param parent Parent
	 */
	public void setParent(TagNode parent)
	{
		if (parent != null)
		{
			this.parent = parent;
			parent.addChild(this);
			setLevel();
		}
	}

	/**
	 * Mengeset elemen tag yang dimiliki oleh TagNode ini. Merujuk pada daftar di org.cyberneko.html.HTMLElements.
	 * 
	 * @param tagElement elemen tag yang dimiliki oleh TagNode ini. Merujuk pada daftar di org.cyberneko.html.HTMLElements.
	 */
	public void setTagElement(short tagElement)
	{
		this.tagElement = tagElement;
	}
	
	/**
	 * Mengeset isi teks yang dimiliki TagNode ini.
	 * 
	 * @param innerText isi teks yang dimiliki TagNode ini
	 */
	public void setInnerText(String innerText)
	{
		this.innerText = innerText;
	}
	
	/**
	 * Mengeset nomor TagNode ini pada TagTree secara preorder. Nilainya diiisi menggunakan method 
	 * assignNodeNumber() pada TagTree.
	 * 
	 * @param tagNodeNumber nomor TagNode ini dalam TagTree secara preorder.
	 */
	public void setTagNodeNumber(int tagNodeNumber)
	{
		this.tagNodeNumber = tagNodeNumber;
	}
	
	/**
	 * Menggabungkan text dengan innerText yang lama ke dalam innerText yang baru.
	 * 
	 * @param text
	 */
	public void appendInnerText(String text)
	{
		if ( innerText == null )
		{
			innerText = text;
		}
		else
		{
			innerText += text;
		}
	}
	
	/**
	 * Mendapatkan referansi ke Parent dari TagNode ini. Jika tidak memiliki Parent kembaliannya 
	 * adalah null.
	 * 
	 * @return referensi ke Parent dari TagNode ini. null jika TagNode ini tidak memiliki Parent.
	 */
	public TagNode getParent()
	{
		return parent;
	}

	/**
	 * Mendapatkan referensi ke sibling sebelumnya dari TagNode ini. Jika TagNode ini merupakan child 
	 * pertama, maka kembaliannya null.
	 * 
	 * @return referensi ke sibling sebelumnya dari TagNode ini. null jika TagNode ini 
	 * merupakan child pertama.
	 */
	public TagNode getPrevSibling()
	{
		if ( childNumber > 1 && parent != null )
		{
			return parent.getChildAtNumber(childNumber-1);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Mendapatkan referensi ke sibling berikutnya dari TagNode ini. Jika TagNode ini merupakan child 
	 * terakhir, maka kembaliannya null.
	 * 
	 * @return referensi ke sibling berikutnya dari TagNode ini. null jika TagNode ini 
	 * merupakan child terakhir.
	 */
	public TagNode getNextSibling()
	{
		if ( childNumber < parent.childrenCount() && parent != null )
		{
			return parent.getChildAtNumber(childNumber+1);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Mendapatkan List dari children yang dimiliki oleh TagNode ini. Mengembalikan List kosong jika tidak memiliki child.
	 * 
	 * @return List dari children yang dimiliki oleh TagNode ini
	 */
	public List<TagNode> getChildren()
	{
		return children;
	}

	/**
	 * Mendapatkan child pertama yang dimiliki oleh TagNode ini, Mengembalikan null jika TagNode ini tidak memiliki child.
	 * 
	 * @return child pertama yang dimiliki oleh TagNode ini
	 */
	public TagNode getFirstChild()
	{
		if (children.isEmpty())
		{
			return null;
		}
		else
		{
			return children.get(0);
		}
	}
	
	/**
	 * Mendapatkan child terakhir yang dimiliki oleh TagNode ini, Mengembalikan null jika TagNode ini tidak memiliki child.
	 * 
	 * @return child terakhir yang dimiliki oleh TagNode ini
	 */
	public TagNode getLastChild()
	{
		if (children.isEmpty())
		{
			return null;
		}
		else
		{
			return children.get(children.size()-1);
		}
	}
	
	/**
	 * Mengembalikan referensi ke child dengan nomor childNumber pada TagNode ini. Mengembalikan null jika TagNode ini tidak memiliki anak atau 
	 * childNumber di luar nomor indeks child (childNumber < 1 atau childNumber > childrenCount() ) 
	 * 
	 * @param childNumber nomor child pada TagNode ini
	 * @return referensi ke child dengan nomor childNumber pada TagNode ini
	 */
	public TagNode getChildAtNumber(int childNumber)
	{
		if (children.isEmpty() || childNumber < 1 || childNumber > children.size())
		{
			return null;
		}
		else
		{
			return children.get(childNumber-1);
		}
	}

	/**
	 * Mengembalikan kode elemen HTML yang dimiliki oleh TagNode ini. Merujuk pada daftar di 
	 * org.cyberneko.html.HTMLElements.
	 * 
	 * @return kode elemen HTML yang dimiliki oleh TagNode ini
	 */
	public short getTagElement()
	{
		return tagElement;
	}
	
	/**
	 * Mendapatkan isi teks yang dimiliki TagNode ini. Mengembalikan null jika TagNode ini tidak memiliki isi teks. Mengembalikan String 
	 * kosong jika TagNode ini memiliki isi teks yang berupa String kosong.
	 * 
	 * @return isi teks yang dimiliki TagNode ini
	 */
	public String getInnerText()
	{
		return innerText;
	}

	/**
	 * Mendapatkan nomor TagNode ini pada TagTree secara preorder.
	 * 
	 * @return nomor TagNode ini pada TagTree secara preorder
	 */
	public int getTagNodeNumber()
	{
		return tagNodeNumber;
	}
	
	/**
	 * Mengembalikan tingkat kedalaman (level) TagNode ini diukur dari root. Mengembalikan 0 jika TagNode ini merupakan root.
	 * 
	 * @return tingkat kedalaman (level) TagNode ini diukur dari root
	 */
	public int getLevel()
	{
		return level;
	}
	
	/**
	 * Mengembalikan nomor yang menunjukkan TagNode ini merupakan child keberapa dari Parent-nya. 
	 * Penomoran mulai dari 1 sampai N, dengan N merupakan jumlah child yang dimiliki oleh Parent.
	 * 
	 * @return nomor yang menunjukkan TagNode ini merupakan child keberapa dari Parent-nya
	 */
	public int getChildNumber()
	{
		return childNumber;
	}
	
	/**
	 * Mendapatkan jumlah child yang dimiliki oleh TagNode ini.
	 * 
	 * @return jumlah child yang dimiliki oleh TagNode ini
	 */
	public int childrenCount()
	{
		return children.size();
	}
	
	public int subTreeSize()
	{
		int size = 0;
		
		for (TagNode child: children)
		{
			size += child.subTreeSize();
		}
		
		return size + 1;
	}

	/**
	 * Mendapatkan tingkat kedalaman maksimum subtree dengan TagNode ini sebagai root. Mengembalikan 0 jika TagNode ini tidak memiliki child.
	 * 
	 * @return tingkat kedalaman maksimum subtree dengan TagNode ini sebagai root
	 */
	public int subTreeDepth()
	{
		return countSubTreeDepth() - level;
	}
	
	/**
	 * Mengembalikan nama elemen TagNode ini. Menggunakan rujukan pada kelas 
	 * org.cyberneko.HTMLElements.
	 * 
	 * @return nama elemen TagNode ini
	 */
	public String toString()
	{
		return HTMLElements.getElement(tagElement).name;
	}
}