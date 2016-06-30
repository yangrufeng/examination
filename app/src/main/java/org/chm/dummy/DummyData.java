package org.chm.dummy;

import org.chm.bean.Qid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/6/29.
 */
public class DummyData {

    /*
     * 获取题号列表
     */
    public static List<Qid> getQList() {
        List<Qid> qList = new ArrayList<Qid>();
        for (int i = 0; i < 100; i++) {
            Qid q = new Qid();
            q.setId(i+1+"");
            q.setParentId("0");
            if (i < 3)
            q.setFinished(true);
            else
            q.setFinished(false);
            qList.add(q);
        }
        return qList;
    }
}
