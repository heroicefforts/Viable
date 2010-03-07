/*
 *  Copyright 2010 Heroic Efforts, LLC
 *  
 *  This file is part of Viable.
 *
 *  Viable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Viable is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Viable.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.heroicefforts.viable.android.content;

import android.database.AbstractCursor;

/**
 * A safe empty cursor representing zero elements.
 * 
 * @author jevans
 *
 */
public class NullCursor extends AbstractCursor
{
	@Override
	public String[] getColumnNames()
	{
		return new String[] { "_id" };
	}

	@Override
	public int getCount()
	{
		return 0;
	}

	@Override
	public double getDouble(int column)
	{
		return 0;
	}

	@Override
	public float getFloat(int column)
	{
		return 0;
	}

	@Override
	public int getInt(int column)
	{
		return 0;
	}

	@Override
	public long getLong(int column)
	{
		return 0;
	}

	@Override
	public short getShort(int column)
	{
		return 0;
	}

	@Override
	public String getString(int column)
	{
		return null;
	}

	@Override
	public boolean isNull(int column)
	{
		return true;
	}

}
