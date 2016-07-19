package org.chm.excel;

import org.chm.bean.Answer;
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
public class AnswerExcel {
    private static final int FIRST_QUESTION_ROW = 1;
    private static final int CHOICE_COLUMN = 4;
    private static final int ANSWER_COLUMN = 3;
    private static final int TYPE_COLUMN = 1;
    public static List<Answer> readExcel_(String file) {
        List<Answer> aList = new ArrayList<>();
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
                if (CHOICE_COLUMN > columnum) {
                    throw new IllegalArgumentException("题库数据破损！");
                }
                int rownum = sheet.getRows();// 得到行数
                for (int i = FIRST_QUESTION_ROW; i < rownum; i++)// 循环进行读写
                {// 行
                    Answer ans = new Answer();
                    ans.setId(i+"");
                    for (int j = 0; j <= CHOICE_COLUMN; j++) {// 列
                        Cell cell1 = sheet.getCell(j, i);
                        String result = cell1.getContents();
                        if (j == CHOICE_COLUMN) {
                            System.out.println(result);
                            String[] array = result.split("\\|");
                            if (array.length == 2) {
                                ans.setAns1(array[0]);
                                ans.setAns2(array[1]);
                            } else {
                                ans.setAns1(array[0]);
                                ans.setAns2(array[1]);
                                ans.setAns3(array[2]);
                                ans.setAns4(array[3]);
                            }
                        } else if (j == TYPE_COLUMN){
                            switch (result) {
                                case "单项选择":
                                    ans.setType(Common.Q_TYPE_SINGLE);
                                    break;
                                case "多项选择":
                                    ans.setType(Common.Q_TYPE_MULTIPLE);
                                    break;
                                case "判断":
                                    ans.setType(Common.Q_TYPE_JUDGE);
                                    break;
                                default:
                                    throw new IllegalArgumentException("题库数据破损！");
                            }

                        } else if (j == ANSWER_COLUMN) {
                            ans.setRightAns(result);
                        }
                    }
                    aList.add(ans);
                }
            }

            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return aList;
    }
}
