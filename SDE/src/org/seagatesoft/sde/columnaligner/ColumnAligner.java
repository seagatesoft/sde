package org.seagatesoft.sde.columnaligner;

import org.seagatesoft.sde.DataRecord;

public interface ColumnAligner
{
	public String[][] alignDataRecords(DataRecord[] dataRecords);
}