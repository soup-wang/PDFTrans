package com.wmt;

import com.swjtu.lang.LANG;
import com.swjtu.querier.Querier;
import com.swjtu.trans.AbstractTranslator;
import com.swjtu.trans.impl.BaiduTranslator;
import com.swjtu.trans.impl.GoogleTranslator;
import com.swjtu.trans.impl.SogouTranslator;
import com.swjtu.trans.impl.YoudaoTranslator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class getPDFWords {

    public static void main(String[] args) {
        File filePath = new File("E:\\MyFile\\毕业论文\\参考文献");
        for (File file : filePath.listFiles()) {
            try {
                //加载文件
                PDDocument load = PDDocument.load(file);
                PDFTextStripper stripper = new PDFTextStripper();
                //创建要写入的txt文件
                BufferedWriter writer1 = new BufferedWriter(new FileWriter("E:\\MyFile\\毕业论文\\txt英汉\\" + file.getName().replaceAll(".pdf", ".txt")));
                BufferedWriter writer2 = new BufferedWriter(new FileWriter("E:\\MyFile\\毕业论文\\txt汉\\" + file.getName().replaceAll(".pdf", ".txt")));

                String s = getWord(load, stripper);
                //按照一页一页的切分
                int i = 1;
                for (String s1 : s.split("@--》 @--》")) {
//                System.out.println(s1);
                    //按照一段一段的切分
                    writer1.write("--------------------------第 " + i + " 页--------------------------");
                    writer1.newLine();
                    writer2.write("--------------------------第 " + i + " 页--------------------------");
                    writer2.newLine();
                    for (String s2 : s1.split("\n\n")) {
                        String s3 = s2.replaceAll("\n", "").replaceAll("@--》", "");
                        //按照一段一段的进行翻译
                        List<String> result = getTrans(s3);

                        for (String str : result) {
                            //将文字写入到TXT文档中
                            writer1.newLine();
                            writer1.write(s3.replaceAll("\"",""));
                            writer1.newLine();
                            writer1.write("..........................................................");
                            writer1.newLine();
                            writer1.write(str.replaceAll("\"",""));
                            writer1.newLine();
                            writer2.newLine();
                            writer2.write(str.replaceAll("\"",""));
                            writer2.newLine();
                        }

                    }
                    writer1.newLine();
                    writer1.flush();
                    writer2.newLine();
                    writer2.flush();
                    i++;
                }
                writer1.close();
                writer2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static List<String> getTrans(String s3) throws InterruptedException {
        Querier<AbstractTranslator> querierTrans = new Querier<>();                   // 获取查询器
        querierTrans.setParams(LANG.EN, LANG.ZH, s3);    // 设置参数
        //休眠100毫秒以防被拉黑
        Thread.sleep(100L);
        querierTrans.attach(new BaiduTranslator());                                  // 向查询器中添加 Google 翻译器
        return querierTrans.execute();
    }

    private static String getWord(PDDocument load, PDFTextStripper stripper) throws IOException {
        //按照顺序读取文件
        stripper.setSortByPosition(true);
        stripper.setStartPage(1);
        stripper.setEndPage(load.getNumberOfPages());
        //将段落开头结尾替换为回车
        stripper.setArticleStart("@--》");
        stripper.setArticleEnd("@--》");
        //将页头页尾替换
        stripper.setParagraphStart("\n");
        stripper.setParagraphEnd("\n");
        String text = stripper.getText(load);
        return text.replaceAll("\r\n", " ");
    }
}
