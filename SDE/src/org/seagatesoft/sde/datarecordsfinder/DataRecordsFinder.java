package org.seagatesoft.sde.datarecordsfinder;

import org.seagatesoft.sde.DataRecord;
import org.seagatesoft.sde.DataRegion;

/**
 * Interface untuk mengidentifikasi data records dari suatu data region.
 * 
 * @author seagate
 *
 */

public interface DataRecordsFinder
{
	public DataRecord[] findDataRecords(DataRegion dataRegion, double similarityTreshold);
}