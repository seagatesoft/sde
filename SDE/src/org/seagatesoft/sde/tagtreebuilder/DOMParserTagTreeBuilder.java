/*
 *  TagTreeBuilder.java  - Kelas yang digunakan untuk membangun TagTree dari InputSource menggunakan 
 *  parser DOM. Parser DOM yang digunakan adalah NekoHTML.
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
package org.seagatesoft.sde.tagtreebuilder;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.seagatesoft.sde.TagNode;
import org.seagatesoft.sde.TagTree;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.parsers.DOMParser;

/**
 * Kelas yang dapat membangun TagTree dari InputSource menggunakan parser DOM. Parser DOM yang digunakan adalah parser DOM dari NekoHTML.
 * 
 * @author seagate
 *
 */
public class DOMParserTagTreeBuilder implements TagTreeBuilder
{
	/**
	 * array yang menyimpan tag2 yang diabaikan dalam membangun pohon tag
	 */
	private short[] ignoredTags = { HTMLElements.STYLE, HTMLElements.SCRIPT, HTMLElements.APPLET, HTMLElements.OBJECT};
	/**
	 * 
	 */
	private Pattern filterPattern = Pattern.compile("^[\\s\\W]*$");
	/**
	 * 
	 */
	private Pattern absoluteURIPattern = Pattern.compile("^.*:.*$");
	/**
	 * 
	 */
	private String baseURI;
	/**
	 * 
	 */
	private TagNodeCreator tagNodeCreator;
	
	/**
	 * Membangun TagTree dari system identifier yang diberikan. Method ini equivalent dengan 
	 * <code>parse(new InputSource(htmlDocument));</code>
	 * 
	 * @param htmlDocument
	 * @return
	 */
	public TagTree buildTagTree(String htmlDocument)  throws IOException, SAXException
	{
		return buildTagTree(htmlDocument, false);
	}
	
	public TagTree buildTagTree(String htmlDocument, boolean ignoreFormattingTags)  throws IOException, SAXException
	{
		return buildTagTree(new InputSource(htmlDocument), ignoreFormattingTags);
	}
	
	/**
	 * Membangun TagTree dari InputSource. Proses pembangunan TagTree dilakukan menggunakan parser 
	 * DOM. Root dari TagTree adalah elemen BODY.
	 * 
	 * @param inputSource
	 * @return TagTree dari InputSource
	 */
	public TagTree buildTagTree(InputSource inputSource) throws IOException, SAXException
	{
		return buildTagTree(inputSource, false);
	}

	public TagTree buildTagTree(InputSource inputSource, boolean ignoreFormattingTags) throws IOException, SAXException 
	{
		TagTree tree = null;
		
		// jika formatting tags diabaikan dalam pembuatan pohon tag
		if ( ignoreFormattingTags )
		{
			tagNodeCreator = new IgnoreFormattingTagsTagNodeCreator();
		}
		// jika formatting tags tidak diabaikan dalam pembuatan pohon tag
		else
		{
			tagNodeCreator = new DefaultTagNodeCreator();
		}

		// parse dokumen HTML menjadi pohon DOM dan dapatkan Document-nya
		DOMParser parser = new DOMParser();
		parser.parse(inputSource);
		Document documentNode = parser.getDocument();
		// dapatkan BaseURI+nama file dari dokumen HTML ini
		baseURI = documentNode.getBaseURI();
		Pattern baseDirectoryPattern = Pattern.compile("^(.*/)[^/]*$");
		Matcher matcher = baseDirectoryPattern.matcher( baseURI );
			
		// dapatkan BaseURI dari dokumen HTML ini
		if ( matcher.lookingAt() )
		{
			baseURI = matcher.group(1);
		}
			
		// dapatkan node BODY dan salin sebagai root untuk pohon tag
		Node bodyNode = documentNode.getElementsByTagName("BODY").item(0);
		TagNode rootTagNode = new TagNode();
		tree = new TagTree();
		tree.setRoot(rootTagNode);
		rootTagNode.setTagElement(HTMLElements.getElement(bodyNode.getNodeName()).code);
		tree.addTagNodeAtLevel(rootTagNode);
		// salin Node DOM menjadi TagNode untuk anak2 dari root
		Node child = bodyNode.getFirstChild();

		while(child != null)
		{
			tagNodeCreator.createTagNodes(child, rootTagNode, tree);
			child = child.getNextSibling();
		}
			
		// berikan nomor node pada TagNode di TagTree
		tree.assignNodeNumber();
		
		return tree;
	}
	
	/**
	 * Method untuk mengecek apakah array mengandung element
	 * 
	 * @param array array element HTML
	 * @param element element HTML yang akan dicek apakah berada dalam array 
	 * @return hasil pengecekan, true jika element terdapat pada array dan false jika sebaliknya
	 */
	private boolean arrayContains(short[] array, short element)
	{
		for (short e: array)
		{
			if (e == element)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private interface TagNodeCreator
	{
		public void createTagNodes(Node node, TagNode parent, TagTree tagTree);
	}
	
	private class DefaultTagNodeCreator implements TagNodeCreator
	{
		/**
		 * Membuat TagNode yang merupakan node pada TagTree dengan menggunakan informasi dari Node yang 
		 * dihasilkan oleh parser DOM. Method ini dipanggil secara rekursif.
		 * 
		 * @param node node yang akan dijadikan sumber informasi untuk membuat TagNode
		 * @param parent Parent dari TagNode yang akan dibuat
		 * @param tagTree TagTree yang sedang dibuat
		 */
		public void createTagNodes(Node node, TagNode parent, TagTree tagTree)
		{
			// jika node DOM bertipe ELEMENT
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				// dapatkan tagCode-nya (representasi tag dalam short)
				short tagCode = HTMLElements.getElement(node.getNodeName()).code;

				// jika tagCode tidak termasuk dalam daftar tag yang diabaikan
				if ( !arrayContains(ignoredTags, tagCode) )
				{
					// salin node DOM menjadi TagNode (tagCode-nya)
					TagNode tagNode = new TagNode();
					tagNode.setTagElement(tagCode);
					// set parent dari TagNode yang baru dibuat
					tagNode.setParent(parent);
					// tambahkan ke dalam TagTree
					tagTree.addTagNodeAtLevel(tagNode);
					
					// jika tagCode merupakan tag IMG
					if ( tagCode == HTMLElements.IMG )
					{
						// dapatkan nilai atribut src-nya
						NamedNodeMap attributesMap = node.getAttributes();
						String imgURI = attributesMap.getNamedItem("src").getNodeValue();
						Matcher absoluteURIMatcher = absoluteURIPattern.matcher( imgURI );
						
						// jika URI pada atribut src bukan merupakan URI absolut (URI relatif)
						if ( ! absoluteURIMatcher.matches() )
						{
							// tambahkan baseURI sehingga menjadi URI absolut
							imgURI = baseURI + imgURI;
						}
						
						// tambahkan tag IMG dengan src-nya sebagai teks HTML pada TagNode parent-nya
						String imgText = String.format("<img src=\"%s\" />", imgURI);
						tagNode.appendInnerText( imgText );
					}
					// jika tagCode merupakan tag A
					else if( tagCode == HTMLElements.A )
					{
						// append teks di dalam tag A ke innerText milik parent
						parent.appendInnerText( node.getTextContent() );
						// dapatkan map atribut dari tag A ini
						NamedNodeMap attributesMap = node.getAttributes();
						
						// jika tag A ini memiliki atribut href
						if ( attributesMap.getNamedItem("href") != null)
						{
							// dapatkan nilai atribut href-nya
							String linkURI = attributesMap.getNamedItem("href").getNodeValue();
							Matcher absoluteURIMatcher = absoluteURIPattern.matcher( linkURI );
						
							// jika nilai atribut href bukan merupakan URI absolut
							if ( ! absoluteURIMatcher.matches() )
							{
								// tambahkan baseURI sehingga menjadi URI absolut
								linkURI = baseURI + linkURI;
							}
						
							// tambahkan tag A dengan href-nya dan teks Link sebagai teks HTML pada TagNode parent-nya
							String linkText = String.format("<a href=\"%s\">Link&lt;&lt;</a>", linkURI);
							tagNode.appendInnerText( linkText );
						}
					}
					
					// lakukan secara rekursif penyalinan Node DOM menjadi DOM pada anak2 dari node ini (jika memiliki anak)
					Node child = node.getFirstChild();
					
					while(child != null)
					{
						createTagNodes(child, tagNode, tagTree);
						child = child.getNextSibling();
					}
				}
			}
			// jika node DOM bertipe TEXT
			else if (node.getNodeType() == Node.TEXT_NODE)
			{
				Matcher filterMatcher = filterPattern.matcher( node.getNodeValue() );
				
				// kalau Text node ini hanya berisi string yang tidak terbaca, maka tidak usah 
				// disimpan sebagai innerText pada node parent-nya
				if ( ! filterMatcher.matches() )
				{
					// jika mengandung teks yang bisa terbaca, maka di-append pada innerText node parent-nya
					parent.appendInnerText(node.getNodeValue());
				}
			}
			// selain bertipe ELEMENT dan TEXT diabaikan
		}
	}
	
	private class IgnoreFormattingTagsTagNodeCreator implements TagNodeCreator
	{
		/**
		 * array yang menyimpan tag2 formatting
		 */
		private short[] formattingTags = { HTMLElements.B, HTMLElements.I, HTMLElements.U, HTMLElements.STRONG, HTMLElements.STRIKE, HTMLElements.EM, HTMLElements.BIG, HTMLElements.SMALL, HTMLElements.SUP, HTMLElements.SUP, HTMLElements.BDO, HTMLElements.BR};
		
		public void createTagNodes(Node node, TagNode parent, TagTree tagTree)
		{
			// jika node DOM bertipe ELEMENT
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				// dapatkan tagCode-nya (representasi tag dalam short)
				short tagCode = HTMLElements.getElement(node.getNodeName()).code;

				// jika tagCode termasuk dalam daftar formatting tags
				if ( arrayContains(formattingTags, tagCode) )
				{
					// khusus untuk tag BR hanya menambahkan tag <BR /> ke innerText parent TagNode-nya
					if ( tagCode == HTMLElements.BR)
					{
						parent.appendInnerText( "<BR />" );
					}
					else
					{
						// append teks di dalam tagCode (beserta tagCode itu sendiri) ke dalam innerText parent-nya
						parent.appendInnerText( String.format("<%s>%s</%s>", HTMLElements.getElement(tagCode).name, node.getTextContent(), HTMLElements.getElement(tagCode).name) );
					}
				}
				// jika tagCode tidak termasuk dalam daftar tag yang diabaikan
				else if ( !arrayContains(ignoredTags, tagCode) )
				{
					// salin node DOM menjadi TagNode (tagCode-nya)
					TagNode tagNode = new TagNode();
					tagNode.setTagElement(tagCode);
					// set parent dari TagNode yang baru dibuat
					tagNode.setParent(parent);
					// tambahkan ke dalam TagTree
					tagTree.addTagNodeAtLevel(tagNode);
					
					// jika tagCode merupakan tag IMG
					if ( tagCode == HTMLElements.IMG )
					{
						// dapatkan nilai atribut src-nya
						NamedNodeMap attributesMap = node.getAttributes();
						String imgURI = attributesMap.getNamedItem("src").getNodeValue();
						Matcher absoluteURIMatcher = absoluteURIPattern.matcher( imgURI );
						
						// jika URI pada atribut src bukan merupakan URI absolut (URI relatif)
						if ( ! absoluteURIMatcher.matches() )
						{
							// tambahkan baseURI sehingga menjadi URI absolut
							imgURI = baseURI + imgURI;
						}
						
						// tambahkan tag IMG dengan src-nya sebagai teks HTML pada TagNode parent-nya
						String imgText = String.format("<img src=\"%s\" />", imgURI);
						tagNode.appendInnerText( imgText );
					}
					// jika tagCode merupakan tag A
					else if( tagCode == HTMLElements.A )
					{
						// append teks di dalam tag A ke innerText milik parent
						parent.appendInnerText( node.getTextContent() );
						// dapatkan map atribut dari tag A ini
						NamedNodeMap attributesMap = node.getAttributes();
						
						// jika tag A ini memiliki atrbiut href
						if ( attributesMap.getNamedItem("href") != null)
						{
							// dapatkan nilai atribut href-nya
							String linkURI = attributesMap.getNamedItem("href").getNodeValue();
							Matcher absoluteURIMatcher = absoluteURIPattern.matcher( linkURI );
						
							// jika nilai atribut href bukan merupakan URI absolut
							if ( ! absoluteURIMatcher.matches() )
							{
								// tambahkan baseURI sehingga menjadi URI absolut
								linkURI = baseURI + linkURI;
							}
						
							// tambahkan tag A dengan href-nya dan teks Link sebagai teks HTML pada TagNode parent-nya
							String linkText = String.format("<a href=\"%s\">Link&lt;&lt;</a>", linkURI);
							tagNode.appendInnerText( linkText );
						}
					}
					
					// lakukan secara rekursif penyalinan Node DOM menjadi DOM pada anak2 dari node ini (jika memiliki anak)
					Node child = node.getFirstChild();
					
					while(child != null)
					{
						createTagNodes(child, tagNode, tagTree);
						child = child.getNextSibling();
					}
				}
			}
			// jika node DOM bertipe TEXT
			else if (node.getNodeType() == Node.TEXT_NODE)
			{
				Matcher filterMatcher = filterPattern.matcher( node.getNodeValue() );
				
				// kalau Text node ini hanya berisi string yang tidak terbaca, maka tidak usah 
				// disimpan sebagai innerText pada node parent-nya
				if ( ! filterMatcher.matches() )
				{
					// jika mengandung teks yang bisa terbaca, maka di-append pada innerText node parent-nya
					parent.appendInnerText(node.getNodeValue());
				}
			}
		}
	}
}