package org.chm.provider;

import android.provider.BaseColumns;

/**
 * Created by pc on 2016/7/12.
 */
public class ContentProviderData {
    public static final class TableMetaData implements BaseColumns {
        public static final String CREATE_TABLE_QUESTION = "CREATE TABLE [Question] (\n" +
                "  [id] INTEGER NOT NULL ON CONFLICT FAIL, \n" +
                "  [text] TEXT(2000), \n" +
                "  [type] CHAR(1));";
        public static final String CREATE_TABLE_ANSWER = "CREATE TABLE [Answer] (\n" +
                "  [id] INTEGER NOT NULL ON CONFLICT FAIL, \n" +
                "  [type] CHAR(1), \n" +
                "  [ans] TEXT(200), \n" +
                "  [ans1] TEXT(200), \n" +
                "  [ans2] TEXT(200), \n" +
                "  [ans3] TEXT(200), \n" +
                "  [ans4] TEXT(200));";
        public static final String CREATE_TABLE_QID = "CREATE TABLE [Qid] (\n" +
                "  [parentId] INTEGER NOT NULL ON CONFLICT FAIL, \n" +
                "  [id] INTEGER NOT NULL ON CONFLICT FAIL, \n" +
                "  [answerId] INTEGER NOT NULL ON CONFLICT FAIL, \n" +
                "  [isFinished] BOOLEAN DEFAULT false);";

        public static final String DROP_TABLE_QUESTION = "DROP TABLE IF EXISTS [Question]";
        public static final String DROP_TABLE_ANSWER= "DROP TABLE IF EXISTS [Answer]";
        public static final String DROP_TABLE_QID= "DROP TABLE IF EXISTS [Qid]";
        public static final int QUERY_QUESTION_ALL = 1;
        public static final int QUERY_QUESTION_BY_ID = 2;
        public static final int QUERY_QID_ALL = 3;
        public static final int QUERY_ANSWER_BY_ID = 4;
        public static final int QUERY_QID_BY_ID = 5;
        public static final int INSERT_QUESTION = 1;
        public static final int INSERT_ANSWER = 2;
        public static final int INSERT_QID= 3;
        public static final int UPDATE_QUESTION = 1;
        public static final int UPDATE_ANSWER = 2;
        public static final int UPDATE_QID= 3;
        public static final int DEL_QUESTION = 1;
        public static final int DEL_ANSWER = 2;
        public static final int DEL_QID= 3;
    }
}
