package org.chm.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    private static final UriMatcher sMatcher;
    private static final UriMatcher iMatcher;
    private static final UriMatcher uMatcher;
    private static final UriMatcher dMatcher;
    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase getDb() {
        return db;
    }

    private SQLiteDatabase db;
    static {
        //常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //如果match()方法匹配content://org.chm.provider.myprovider/question路径，返回匹配码为1
        sMatcher.addURI("org.chm.provider.myprovider", "question", ContentProviderData.TableMetaData.QUERY_QUESTION_ALL);//添加需要匹配uri，如果匹配就会返回匹配码
        //如果match()方法匹配content://org.chm.provider.myprovider/question/230路径，返回匹配码为2
        sMatcher.addURI("org.chm.provider.myprovider", "question/#", ContentProviderData.TableMetaData.QUERY_QUESTION_BY_ID);//#号为通配符
        sMatcher.addURI("org.chm.provider.myprovider", "qid", ContentProviderData.TableMetaData.QUERY_QID_ALL);
        sMatcher.addURI("org.chm.provider.myprovider", "answer/#", ContentProviderData.TableMetaData.QUERY_ANSWER_BY_ID);
        sMatcher.addURI("org.chm.provider.myprovider", "qid/#", ContentProviderData.TableMetaData.QUERY_QID_BY_ID);

        iMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        iMatcher.addURI("org.chm.provider.myprovider", "question", ContentProviderData.TableMetaData.INSERT_QUESTION);
        iMatcher.addURI("org.chm.provider.myprovider", "answer", ContentProviderData.TableMetaData.INSERT_ANSWER);
        iMatcher.addURI("org.chm.provider.myprovider", "qid", ContentProviderData.TableMetaData.INSERT_QID);

        uMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uMatcher.addURI("org.chm.provider.myprovider", "question/#", ContentProviderData.TableMetaData.UPDATE_QUESTION);
        uMatcher.addURI("org.chm.provider.myprovider", "answer/#", ContentProviderData.TableMetaData.UPDATE_ANSWER);
        uMatcher.addURI("org.chm.provider.myprovider", "qid/#", ContentProviderData.TableMetaData.UPDATE_QID);

        dMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        dMatcher.addURI("org.chm.provider.myprovider", "question", ContentProviderData.TableMetaData.DEL_QUESTION);
        dMatcher.addURI("org.chm.provider.myprovider", "answer", ContentProviderData.TableMetaData.DEL_ANSWER);
        dMatcher.addURI("org.chm.provider.myprovider", "qid", ContentProviderData.TableMetaData.DEL_QID);
    }
    public MyContentProvider() {

    }
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        //构造函数-创建数据库
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /*
         * 在调getReadableDatabase或getWritableDatabase时，
         * 会判断指定的数据库是否存在，不存在则调SQLiteDatabase.create创建，
         * onCreate只在数据库第一次创建时才执行。
         */
        //创建表
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            /*
             * 建表
             */
            //开启事务
            db.beginTransaction();
            try
            {
                db.execSQL(ContentProviderData.TableMetaData.CREATE_TABLE_QUESTION);
                db.execSQL(ContentProviderData.TableMetaData.CREATE_TABLE_ANSWER);
                db.execSQL(ContentProviderData.TableMetaData.CREATE_TABLE_QID);
                //设置事务标志为成功，当结束事务时就会提交事务
                db.setTransactionSuccessful();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally
            {
                //结束事务
                db.endTransaction();
            }
        }

        /*
         * 当系统在构造SQLiteOpenHelper类的对象时，如果发现版本号不一样，就会自动调用onUpgrade函数。
         */
        //更新数据库
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.beginTransaction();
            try
            {
                db.execSQL(ContentProviderData.TableMetaData.DROP_TABLE_QUESTION);
                db.execSQL(ContentProviderData.TableMetaData.DROP_TABLE_ANSWER);
                db.execSQL(ContentProviderData.TableMetaData.DROP_TABLE_QID);
                //设置事务标志为成功，当结束事务时就会提交事务
                db.setTransactionSuccessful();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally
            {
                //结束事务
                db.endTransaction();
            }

            onCreate(db);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        String table;
        switch (dMatcher.match(uri)) {
            case ContentProviderData.TableMetaData.DEL_QUESTION:
                table = "question";
                break;
            case ContentProviderData.TableMetaData.DEL_ANSWER:
                table = "answer";
                break;
            case ContentProviderData.TableMetaData.DEL_QID:
                table = "qid";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return db.delete(table, null, null);
    }

    /*
     * 传进去一个URI，返回一个表示MIME类型的字符串；MIME类型就是用来标识当前的Activity所能打开的文件类型。
     * MIME类型主要是Activity的Intent-filter的data域，例如（AndroidManifest.xml）:
     *  <activity
     *       android:name=".SecondActivity"
     *       android:label="@string/title_activity_second" >
     *       <intent-filter>
     *           <action android:name="harvic.test.qijian"/>
     *           <category android:name="android.intent.category.DEFAULT"/>
     *           <data android:mimeType="image/bmp"/>
     *       </intent-filter>
     *   </activity>
     *   代表隐式匹配Intent的MIMETYPE域来启动Activity时，这个Activity只能打开image/bmp类型的文件。
     *   隐式匹配Intent的MIMETYPE域来启动Activity：
     *   Intent intent = new Intent();
     *   intent.setAction("harvic.test.qijian");
     *   intent.setData(mCurrentURI);
     *   startActivity(intent);
     * 隐式启动Activity时会调用，
     * 根据intent中的uri在AndroidManifest中找到provider(对应xml中android:authorities)，
     * 根据intent里的action与uri通过getType方法返回的data android:mimeType在AndroidManifest中匹配Activity(intent-filter)。
     *
     * android:mimeType：
     * 如果是单条记录应该返回以vnd.android.cursor.item/ 为首的字符串，
     * 如果是多条记录，应该返回vnd.android.cursor.dir/ 为首的字符串;
     */
    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId;
        switch (iMatcher.match(uri)) {
            case ContentProviderData.TableMetaData.INSERT_QUESTION:
                rowId = db.insert("question", null, values);
                break;
            case ContentProviderData.TableMetaData.INSERT_ANSWER:
                rowId = db.insert("answer", null, values);
                break;
            case ContentProviderData.TableMetaData.INSERT_QID:
                rowId = db.insert("qid", null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (rowId != -1) {
            return ContentUris.withAppendedId(uri, rowId); // 返回插入记录id
        }
        return null;
    }

    /*
     * 该方法在ContentProvider创建后就会被调用,
     * 在初次被调用的时候会进入onCreate,以后不会重复调用。
     */
    @Override
    public boolean onCreate() {
        DatabaseHelper dbOpenHelper=new DatabaseHelper(this.getContext());
        this.db = dbOpenHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        String table;

        switch (sMatcher.match(uri)) {
            case ContentProviderData.TableMetaData.QUERY_QUESTION_ALL:
                //获取person表中所有记录

                table = "question";
                break;
            case ContentProviderData.TableMetaData.QUERY_QUESTION_BY_ID:
                table = "question";
                long _id = ContentUris.parseId(uri);
                selection = "id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            case ContentProviderData.TableMetaData.QUERY_QID_ALL:
                table = "qid";
                break;
            case ContentProviderData.TableMetaData.QUERY_QID_BY_ID:
                table = "qid";
                _id = ContentUris.parseId(uri);
                selection = "id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            case ContentProviderData.TableMetaData.QUERY_ANSWER_BY_ID:
                table = "answer";
                _id = ContentUris.parseId(uri);
                selection = "id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;

            default://不匹配
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        String table;
        switch (uMatcher.match(uri)) {
            case ContentProviderData.TableMetaData.UPDATE_QUESTION:
                table = "question";
                break;
            case ContentProviderData.TableMetaData.UPDATE_ANSWER:
                table = "answer";
                long _id = ContentUris.parseId(uri);
                selection = "id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            case ContentProviderData.TableMetaData.UPDATE_QID:
                table = "qid";
                _id = ContentUris.parseId(uri);
                selection = "id = ?";
                selectionArgs = new String[]{String.valueOf(_id)};
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        int result = db.update(table, values, selection, selectionArgs);
//        getResolver().notifyChange(uri,null); //通知在ContentResolver中注册了该URI的ContentObserver，这个URI对应的数据源发生变化了
        return result;
    }
}
