package org.seagatesoft.sde;

import java.util.HashMap;

/**
 * Kelas yang meng-override method get() dari superclass-nya, yaitu HashMap dengan mengecek 
 * apakah key terdapat dalam keyset menggunakan method equals(), bukan menggunakan hashCode().
 * 
 * @author seagate
 *
 * @param <K>
 * @param <V>
 */
public class MultiKeyMap<K, V> extends HashMap<K, V>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5224421926641367424L;

	/**
	 * 
	 * Meng-override method get() dari superclass-nya, yaitu HashMap dengan mengecek 
	 * apakah key terdapat dalam keyset menggunakan method equals(), bukan menggunakan hashCode().
	 * 
	 */
	public V get(Object key)
	{
		V value = null;
		
		for (K k: keySet())
		{
			if (k.equals(key))
			{
				return super.get(k);
			}
		}
		
		return value;
	}
}