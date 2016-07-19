package org.chm.dummy;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.chm.bean.Answer;
import org.chm.bean.Qid;
import org.chm.bean.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试数据
 * Created by pc on 2016/6/29.
 */
public class DummyData {
    private static ContentResolver resolver;
    private static int currentIndex = 0;
    private static int max = 0;
    public static void setContentResolver(ContentResolver resolver) {
        DummyData.resolver = resolver;
    }

    public static void setCurrentIndex(int index) {
        currentIndex = index;
    }
    /*
     * 获取题号列表
     */
    public static List<Qid> getQList() {
        List<Qid> qList = new ArrayList<>();
        Cursor cursor = resolver.query(Uri.parse("content://org.chm.provider.myprovider/qid"), null, null, null, "id asc");

        if (null == cursor) {
            throw new RuntimeException("无法获取cursor");
        }
        max = cursor.getCount();
        while (cursor.moveToNext()) {
            Qid qid = new Qid();
            qid.setParentId(cursor.getString(cursor.getColumnIndex("parentId")));
            qid.setId(cursor.getString(cursor.getColumnIndex("id")));
            qid.setAnswerId(cursor.getString(cursor.getColumnIndex("answerId")));
            qid.setFinished(cursor.getString(cursor.getColumnIndex("isFinished")).equals("1")); // 从sqlite取出的boolean为1或0

            qList.add(qid);
        }
        cursor.close();


        return qList;
    }

    public static Question getQuestionById(String qId) {
        Question q = new Question();
        Uri uri = Uri.parse("content://org.chm.provider.myprovider/question");
        uri = ContentUris.withAppendedId(uri, Long.parseLong(qId));
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (null == cursor) {
            throw new RuntimeException("无法获取cursor");
        }
        if(cursor.moveToFirst()) {
            q.setId(cursor.getString(cursor.getColumnIndex("id")));
            q.setText(cursor.getString(cursor.getColumnIndex("text")));
            q.setType(cursor.getString(cursor.getColumnIndex("type")));
        }


        cursor.close();
        return q;
    }

    public static Qid getPrevious() {
        if (currentIndex <= 1) {
            currentIndex = 1;
        } else {
            currentIndex--;
        }
        Uri uri = Uri.parse("content://org.chm.provider.myprovider/qid");
        uri = ContentUris.withAppendedId(uri, currentIndex);
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (null == cursor) {
            throw new RuntimeException("无法获取cursor");
        }
        Qid qid = new Qid();
        if(cursor.moveToFirst()){

            qid.setParentId(cursor.getString(cursor.getColumnIndex("parentId")));
            qid.setId(cursor.getString(cursor.getColumnIndex("id")));
            qid.setAnswerId(cursor.getString(cursor.getColumnIndex("answerId")));
            qid.setFinished(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isFinished"))));
        }

        cursor.close();

        return qid;
    }

    public static Qid getNext() {
        if (currentIndex >= max) {
            currentIndex = max;
        } else {
            currentIndex++;
        }
        Uri uri = Uri.parse("content://org.chm.provider.myprovider/qid");
        uri = ContentUris.withAppendedId(uri, currentIndex);
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (null == cursor) {
            throw new RuntimeException("无法获取cursor");
        }
        Qid qid = new Qid();
        if(cursor.moveToFirst()) {
            qid.setParentId(cursor.getString(cursor.getColumnIndex("parentId")));
            qid.setId(cursor.getString(cursor.getColumnIndex("id")));
            qid.setAnswerId(cursor.getString(cursor.getColumnIndex("answerId")));
            qid.setFinished(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isFinished"))));
        }
        cursor.close();

        return qid;
    }

    public static Answer getAnswerById(String aId) {
        Uri uri = Uri.parse("content://org.chm.provider.myprovider/answer");
        uri = ContentUris.withAppendedId(uri, Long.parseLong(aId));
        Cursor cursor = resolver.query(uri, null, null, null, null);

        if (null == cursor) {
            throw new RuntimeException("无法获取cursor");
        }
        Answer a = new Answer();
        if (cursor.moveToFirst()){
            a.setId(cursor.getString(cursor.getColumnIndex("id")));
//            a.setRightAns(cursor.getString(cursor.getColumnIndex("rightAns")));
            a.setType(cursor.getString(cursor.getColumnIndex("type")));
            a.setAns1(cursor.getString(cursor.getColumnIndex("ans1")));
            a.setAns2(cursor.getString(cursor.getColumnIndex("ans2")));
            a.setAns3(cursor.getString(cursor.getColumnIndex("ans3")));
            a.setAns4(cursor.getString(cursor.getColumnIndex("ans4")));
            a.setAns(cursor.getString(cursor.getColumnIndex("ans")));
        }

        cursor.close();

        return a;
    }

    public static String getCurrentQuestionId() {
        return currentIndex+"";
    }
    public static String getCurrentAnswerId() {
        return "1";
    }
    public static void setCurrentQuestionAnswer(String answer) {
        ContentValues updateValues = new ContentValues();
        updateValues.put("ans", answer);
        Uri uri = Uri.parse("content://org.chm.provider.myprovider/answer");
        Uri updateIdUri = ContentUris.withAppendedId(uri, currentIndex);
        int rowNum;
        rowNum = resolver.update(updateIdUri, updateValues, null, null);
        if (rowNum <= 0) {
            throw new RuntimeException("更新失败");
        }

        Uri uri2 = Uri.parse("content://org.chm.provider.myprovider/qid");
        ContentValues updateValues2 = new ContentValues();
        updateValues2.put("isFinished", true);
        Uri updateIdUri2 = ContentUris.withAppendedId(uri2, currentIndex);
        rowNum = resolver.update(updateIdUri2, updateValues2, null, null);
        if (rowNum <= 0) {
            throw new RuntimeException("更新失败");
        }
        return;
    }
}
