package com.xidian.miniblog.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qhhu
 * @date 2020/3/29 - 10:04
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private TrieNode rootNode = new TrieNode();

    private static final String REPLACEMENT = "***";

    private class TrieNode {

        private boolean isKeyWordEnd = false;

        // 前缀树的关键就是字符存储在路径上
        private Map<Character, TrieNode> nextNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public TrieNode getNextNode(Character character) {
            return nextNodes.get(character);
        }

        public void addNextNode(Character character, TrieNode trieNode) {
            nextNodes.put(character, trieNode);
        }
    }

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 构建前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        TrieNode curNode = rootNode;
        int begin = 0;
        int position = begin;
        StringBuilder stringBuilder = new StringBuilder();

        while (position < text.length()) {
            char chr = text.charAt(position);

            if (isSymbol(chr)) {
                if (begin == position) {
                    // 敏感词外的字符直接加入 stringBuilder
                    stringBuilder.append(chr);
                    begin++;
                    position = begin;
                } else {
                    // 跳过敏感词中的符号（色*情）
                    position++;
                }

                continue;
            }

            curNode = curNode.getNextNode(chr);
            if (curNode == null) {
                stringBuilder.append(text.charAt(begin));

                begin++;
                position = begin;

                curNode = rootNode;
            } else if (curNode.isKeyWordEnd()) {
                stringBuilder.append(REPLACEMENT);

                position++;
                begin = position;

                curNode = rootNode;
            } else {
                position++;
            }
        }

        // 当 position 到达结尾时结束算法, 这时最后一批 [begin, position] 字符未计入结果
        stringBuilder.append(text.substring(begin));

        return stringBuilder.toString();
    }

    private void addKeyWord(String keyword) {
        TrieNode curNode = rootNode;
        for (char chr : keyword.toCharArray()) {
            TrieNode nextNode = curNode.getNextNode(chr);

            if (nextNode == null) {
                nextNode = new TrieNode();
                curNode.addNextNode(chr, nextNode);
            }

            curNode = nextNode;
        }

        curNode.setKeyWordEnd(true);
    }

    private boolean isSymbol(char chr) {
        // [0x2E80, 0x9FFF]是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(chr) && (chr < 0x2E80 || chr > 0x9FFF);
    }

}
