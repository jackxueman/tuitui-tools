package com.tuitui.tool.emoji;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 特殊处理表情的工具类
 */
public class EmojiUtils {

    private static Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    public static boolean hasEmoji(String source) {
        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            return true;
        }
        return false;
    }

    public static boolean hasEmoji(String[] strings) {
        for (String str : strings) {
            if (hasEmoji(str)) {
                return true;
            }
        }
        return false;
    }
}
