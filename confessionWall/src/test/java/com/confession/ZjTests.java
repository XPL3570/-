package com.confession;

import com.confession.comm.SensitiveTextFilter;
import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collection;

@SpringBootTest
public class ZjTests {

//    @Resource
    private SensitiveTextFilter sensitiveTextFilter;


//    @Test
    public void aaa() {
//        long startTime = System.currentTimeMillis();
        AhoCorasickDoubleArrayTrie<String> trie = sensitiveTextFilter.getTrie();
        String inputText = "敏感字测试,我操,abcdefg 荭志";
        for (int i = 0; i < 10; i++) {
            inputText=inputText+inputText;
        }
            Collection<AhoCorasickDoubleArrayTrie.Hit<String>> sensitiveWordHits = trie.parseText(inputText);
            StringBuilder filteredTextBuilder = new StringBuilder(inputText);
            for (AhoCorasickDoubleArrayTrie.Hit<String> hit : sensitiveWordHits) {
                String sensitiveWord = hit.value;
    //            System.out.println(sensitiveWord);
                int start = hit.begin; // 敏感词在输入文本中的起始位置
                int end = hit.end; // 敏感词在输入文本中的结束位置

                for (int i = start; i < end; i++) {
                    filteredTextBuilder.replace(i, i +1, "*");
                }

            }
//        System.out.println(filteredTextBuilder);

//        long endTime = System.currentTimeMillis();
//        System.out.println("花费时间:"+(endTime-startTime)+"ms"+"，字符数："+inputText.length());
    }

}
