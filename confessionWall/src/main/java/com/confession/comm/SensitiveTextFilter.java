package com.confession.comm;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 敏感词过滤器配置
 */
@Component
public class SensitiveTextFilter {
    private AhoCorasickDoubleArrayTrie<String> trie;

    public AhoCorasickDoubleArrayTrie<String> getTrie() {
        return trie;
    }

    @javax.annotation.Resource
    private ResourceLoader resourceLoader;



    public SensitiveTextFilter()  {
        trie = new AhoCorasickDoubleArrayTrie<>();
    }


    @PostConstruct
    public void init() throws IOException {
        List<String> fileNames = new ArrayList<>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Resource[] resources = resolver.getResources("classpath:sensitiveText/*.txt");
        for (Resource resource : resources) {
            fileNames.add(resource.getFilename());
        }

        System.out.println(fileNames);

        Map<String, String> sensitiveWords = new HashMap<>();
        for (String fileName : fileNames) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("sensitiveText/"+fileName).getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sensitiveWords.put(line, line);  // 将敏感词作为key和value添加到Map中
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        trie.build(sensitiveWords);  // 构建ACDAT树结构
    }

}
