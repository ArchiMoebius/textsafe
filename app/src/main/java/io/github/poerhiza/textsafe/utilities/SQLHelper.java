package io.github.poerhiza.textsafe.utilities;

import java.util.ArrayList;
import java.util.List;

import io.github.poerhiza.textsafe.valueobjects.AutoResponse;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "message_manager.db";

	private static final String TABLE_RESPONSES = "responses";
	private static final String TABLE_RESPONSES_ID = "id";
	private static final String TABLE_RESPONSES_TITLE = "title";
	private static final String TABLE_RESPONSES_RESPONSE = "response";
	private static final String TABLE_RESPONSES_FREQUENCY = "frequency";

	public SQLHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_RESPONSES_TABLE = 
			"CREATE TABLE " + 
				TABLE_RESPONSES + "("
					+ TABLE_RESPONSES_ID + " INTEGER PRIMARY KEY,"
				    + TABLE_RESPONSES_TITLE + " TEXT,"
					+ TABLE_RESPONSES_RESPONSE + " TEXT," 
				    + TABLE_RESPONSES_FREQUENCY + " INTEGER " + 
				")";
		db.execSQL(CREATE_RESPONSES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
		onCreate(db);
	}

    // Adding new autoResponse
	public long addAutoResponse(AutoResponse autoResponse)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(TABLE_RESPONSES_TITLE, autoResponse.getTitle());
		values.put(TABLE_RESPONSES_RESPONSE, autoResponse.getResponse());

		long result = db.insert(TABLE_RESPONSES, null, values);
		db.close();
		return result;
	}

    // Getting single autoResponse
	public AutoResponse getAutoResponse(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		AutoResponse autoResponse = null;

		Cursor cursor = db.query(TABLE_RESPONSES, new String[] { TABLE_RESPONSES_ID,
				TABLE_RESPONSES_TITLE, TABLE_RESPONSES_RESPONSE, TABLE_RESPONSES_FREQUENCY }, TABLE_RESPONSES_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if(cursor != null)
		{
			if(cursor.moveToFirst())
			{
				autoResponse = new AutoResponse(
					Integer.parseInt(cursor.getString(0)),
					cursor.getString(1), 
					cursor.getString(2)
				);
				autoResponse.setFreq(cursor.getInt(3));
				cursor.close();
			}
		}

		db.close();
		return autoResponse;
	}

	 // Getting all autoResponses
	 public List<AutoResponse> getAllAutoResponses()
	 {
		List<AutoResponse> autoResponseList = new ArrayList<AutoResponse>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_RESPONSES + " ORDER BY " + TABLE_RESPONSES_FREQUENCY + " DESC ";
	
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
	
		// looping through all rows and adding to list
		if (cursor.moveToFirst())
		{
			do
			{
				AutoResponse autoResponse = new AutoResponse(
					Integer.parseInt(cursor.getString(0)),
					cursor.getString(1),
					cursor.getString(2)
				);
				autoResponse.setFreq(cursor.getInt(3));
				autoResponseList.add(autoResponse);
			}
			while (cursor.moveToNext());
		} else {
            Log.d(AutoResponse.TAG, "cursor move to first fail");
        }
		cursor.close();
		db.close();
		// return contact list
		return autoResponseList;
	}

	// Getting autoResponses Count
	public int getAutoResponsesCount()
	{
		String countQuery = "SELECT  * FROM " + TABLE_RESPONSES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		int count = cursor.getCount();
		return count;
	}

    // Updating single autoResponse
	public int updateAutoResponse(AutoResponse autoResponse)
	{
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(TABLE_RESPONSES_TITLE, autoResponse.getTitle());
		values.put(TABLE_RESPONSES_RESPONSE, autoResponse.getResponse());
		values.put(TABLE_RESPONSES_FREQUENCY, autoResponse.getFreq());

		// updating row
		int affected = db.update(TABLE_RESPONSES, values, TABLE_RESPONSES_ID + " = ?",
				new String[] { String.valueOf(autoResponse.getID()) });
		db.close();
		return affected;
	}

	// Deleting single autoResponse
	public void deleteAutoResponse(AutoResponse autoResponse)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RESPONSES, TABLE_RESPONSES_ID + " = ?",
				new String[] { String.valueOf(autoResponse.getID()) });
		db.close();
	}

	
}
