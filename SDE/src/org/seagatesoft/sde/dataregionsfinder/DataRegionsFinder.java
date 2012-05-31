/*
 *  DataRegionFinder.java  - Interface untuk mencari data region pada tag tree
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
package org.seagatesoft.sde.dataregionsfinder;

import java.util.List;

import org.seagatesoft.sde.DataRegion;
import org.seagatesoft.sde.TagNode;

/**
 * Interface untuk mencari data region pada tag tree.
 * 
 * @author seagate
 *
 */
public interface DataRegionsFinder
{
	public List<DataRegion> findDataRegions(TagNode tagNode, int maxNodeInGeneralizedNodes, double similarityTreshold);
}