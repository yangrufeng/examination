package org.chm.excel;

import org.chm.bean.Question;
import org.chm.common.Common;
import org.chm.common.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by pc on 2016/7/11.
 */
public class QuestionExcel {
    private static final int FIRST_QUESTION_ROW = 1;
    private static final int TEXT_COLUMN = 2;
    private static final int TYPE_COLUMN = 1;
    public static List<Question> readExcel_(String file) {
        List<Question> qList = new ArrayList<>();
        try {
            File sdcardDir = FileUtils.getExternalSdCardFile();
            String path0 = sdcardDir.getPath() + Common.MAIN_FOLDER;

            String path = path0 + Common.QUESTION_FOLDER;
            Workbook book = Workbook.getWorkbook(new File(path+"/"+file));

            for (int a = 0; a < 26; a++) {
                // 获得第一个工作表对象
                Sheet sheet = book.getSheet(a);
                // 得到第一列第一行的单元格
                // 得到第一列第一行的单元格
                int columnum = sheet.getColumns();// 得到列数
                if (TEXT_COLUMN > columnum) {
                    throw new IllegalArgumentException("题库数据破损！");
                }
                int rownum = sheet.getRows();// 得到行数
                for (int i = FIRST_QUESTION_ROW; i < rownum; i++)// 循环进行读写
                {// 行
                    Question q = new Question();
                    q.setId(i+"");
                    for (int j = 0; j <= TEXT_COLUMN; j++) {// 列
                        Cell cell1 = sheet.getCell(j, i);
                        String result = cell1.getContents();
                        if (j == TEXT_COLUMN) {
                            q.setText(result);
                        } else if (j == TYPE_COLUMN){
                            switch (result) {
                                case "单项选择":
                                    q.setType(Common.Q_TYPE_SINGLE);
                                    break;
                                case "多项选择":
                                    q.setType(Common.Q_TYPE_MULTIPLE);
                                    break;
                                case "判断":
                                    q.setType(Common.Q_TYPE_JUDGE);
                                    break;
                                default:
                                    throw new IllegalArgumentException("题库数据破损！");
                            }

                        }
                    }
                    qList.add(q);
                }
            }

            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return qList;
    }
}
