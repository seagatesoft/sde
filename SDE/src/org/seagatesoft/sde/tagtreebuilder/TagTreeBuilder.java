/*
 *  TagTreeBuilder.java  - Interface yang digunakan untuk membangun TagTree dari InputSource.
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

import org.seagatesoft.sde.TagTree;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Interface yang digunakan untuk membangun TagTree dari InputSource.
 * 
 * @author seagate
 *
 */
public interface TagTreeBuilder
{
	/**
	 * Membangun TagTree dari InputSource
	 * 
	 * @param inputSource
	 * @return TagTree dari InputSource
	 */
	public TagTree buildTagTree(InputSource inputSource) throws IOException, SAXException;
	
	/**
	 * Membangun TagTree dari system identifier yang diberikan. Method ini equivalent dengan 
	 * <code>parse(new InputSource(htmlDocument));</code>
	 * 
	 * @param htmlDocument
	 * @return
	 */
	public TagTree buildTagTree(String htmlDocument) throws IOException, SAXException;
	
	public TagTree buildTagTree(InputSource inputSource, boolean ignoreFormattingTags) throws IOException, SAXException;
	
	public TagTree buildTagTree(String htmlDocument, boolean ignoreFormattingTags) throws IOException, SAXException;
}