package org.chm.dummy;

import org.chm.bean.Answer;
import org.chm.bean.Qid;
import org.chm.bean.Question;
import org.chm.common.Common;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试数据
 * Created by pc on 2016/6/29.
 */
public class DummyData {

    /*
     * 获取题号列表
     */
    public static List<Qid> getQList() {
        List<Qid> qList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Qid q = new Qid();
            q.setId(i+1+"");
            q.setAnswerId(i+1+"");
            q.setParentId("0");
            if (i < 3)
            q.setFinished(true);
            else
            q.setFinished(false);
            qList.add(q);
        }
        return qList;
    }

    public static Question getQuestionById(String qId) {
        Question q = new Question();
        q.setText("这个问题是什么呢？"+qId);
        q.setType(Common.Q_TYPE_SINGLE);
        return q;
    }

    public static Qid getPrevious() {
        Qid q = new Qid();
        q.setAnswerId("1");
        q.setId("1");
        return q;
    }

    public static Qid getNext() {
        Qid q = new Qid();
        q.setAnswerId("3");
        q.setId("3");
        return q;
    }

    public static Answer getAnswerById(String aId) {
        Answer a = new Answer();
        a.setId(aId);
        a.setType(Common.Q_TYPE_SINGLE);
        a.setAns1("A：选项"+aId);
        a.setAns2("B：选项"+aId);
        a.setAns3("C：选项"+aId);
        a.setAns4("D：选项"+aId);
        return a;
    }

    public static String getCurrentQuestionId() {
        return "1";
    }
    public static String getCurrentAnswerId() {
        return "1";
    }
    public static void setCurrentQuestionAnswer(String answer) {
        System.out.println(answer);
        return;
    }
}
