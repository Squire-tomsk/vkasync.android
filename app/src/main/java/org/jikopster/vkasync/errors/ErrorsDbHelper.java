/*
 * Copyright (c) 2014-2016 Jikopster Orglobster.
 *
 * This file is part of Jikopster vk a sync
 *
 * Jikopster vk a sync is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jikopster vk a sync is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jikopster vk a sync.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jikopster.vkasync.errors;

import static org.jikopster.vkasync.errors.ErrorsContract.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ErrorsDbHelper extends SQLiteOpenHelper
{
    public static final int VERSION = 1;
    public static final String NAME = "errors.db";

    public ErrorsDbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    private static final String
            SQL_CREATE =
                    "CREATE TABLE " + ErrorEntry.TABLE_NAME + " (" +
                            ErrorEntry._ID + " INTEGER PRIMARY KEY," +
                            ErrorEntry.COLUMN_NAME_TITLE       + " TEXT," +
                            ErrorEntry.COLUMN_NAME_DESCRIPTION + " TEXT" +
                    ")",
            SQL_DELETE =
                 "DROP TABLE IF EXISTS " + ErrorEntry.TABLE_NAME;


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);
        db.execSQL(SQL_CREATE);
    }
}
