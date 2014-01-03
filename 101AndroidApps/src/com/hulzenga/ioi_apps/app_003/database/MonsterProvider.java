package com.hulzenga.ioi_apps.app_003.database;

import static com.hulzenga.ioi_apps.app_003.database.MonsterContract.COLUMN_MONSTER_ID;
import static com.hulzenga.ioi_apps.app_003.database.MonsterContract.TABLE_MONSTERS;
import static com.hulzenga.ioi_apps.app_003.database.MonsterContract.VALID_COLUMNS;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Provider for the underlying MonsterDatabase as defined in the contract
 */
public class MonsterProvider extends ContentProvider{

	//our monster database
	private MonsterDatabaseHelper database;
	
	//URI matching constants 
	private static final int MONSTERS 	= 10;
	private static final int MONSTER_ID = 20;
	
	//URI identifiers
	private static final String AUTHORITY 			= "com.hulzenga.ioi_androdiapps.app_003.database.monsterprovider";
	private static final String BASE_PATH 			= "monsters";	
	public  static final Uri 	CONTENT_URI 		= Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);	
	public  static final String CONTENT_TYPE  		= ContentResolver.CURSOR_DIR_BASE_TYPE  + "/monsters";
	public  static final String CONTENT_ITEM_TYPE 	= ContentResolver.CURSOR_ITEM_BASE_TYPE + "/monster";
	
	private static final UriMatcher sURIMatcher		= new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, MONSTERS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", MONSTER_ID);
	}
	
	@Override
	public boolean onCreate() {
		database = new MonsterDatabaseHelper(getContext());
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
				
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		//check if nonexistant columns are queried
		checkColumns(projection);
		
		queryBuilder.setTables(TABLE_MONSTERS);
		
		switch(sURIMatcher.match(uri)) {
		case MONSTERS:
			break;
		case MONSTER_ID:
			queryBuilder.appendWhere(COLUMN_MONSTER_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db 	= database.getWritableDatabase();
		Cursor cursor 		= queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		//ensure all listeners are notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {		
		switch(sURIMatcher.match(uri)) {
		case MONSTERS:
			return CONTENT_TYPE;			
		case MONSTER_ID:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = database.getWritableDatabase();
		long id = 0;
				
		switch(sURIMatcher.match(uri)) {
		case MONSTERS:
			id = db.insert(TABLE_MONSTERS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
				
		switch(sURIMatcher.match(uri)) {
		case MONSTERS:
			rowsDeleted = db.delete(TABLE_MONSTERS, selection, selectionArgs);
			break;
		case MONSTER_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(TABLE_MONSTERS, COLUMN_MONSTER_ID + " = " + id, null);
			} else {
				rowsDeleted = db.delete(TABLE_MONSTERS, COLUMN_MONSTER_ID + " = " + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		
		return rowsDeleted;
	}	

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		
		switch(sURIMatcher.match(uri)) {
		case MONSTERS:
			rowsUpdated = db.update(TABLE_MONSTERS, values, selection, selectionArgs);
			break;
		case MONSTER_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(TABLE_MONSTERS, values, COLUMN_MONSTER_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(TABLE_MONSTERS, values, COLUMN_MONSTER_ID + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	//checks all columns in the projection for invalid columns
	public void checkColumns(String[] projection) {
		for (String column: projection) {
			if (!VALID_COLUMNS.contains(column)) {
				throw new IllegalArgumentException("Invalid columns in projection");
			}
		}
	}
}
