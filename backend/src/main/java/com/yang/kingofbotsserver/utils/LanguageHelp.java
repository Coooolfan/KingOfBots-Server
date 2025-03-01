package com.yang.kingofbotsserver.utils;

import java.util.List;

public class LanguageHelp {
    public static String isStatic(String language) {
        List<String> staticLanguage = List.of("java", "cpp");
        if (staticLanguage.contains(language))
            return "uncompile";
        return "noneed";
    }
}
