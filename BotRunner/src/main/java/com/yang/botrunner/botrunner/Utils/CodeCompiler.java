package com.yang.botrunner.botrunner.Utils;

import java.io.IOException;

public interface CodeCompiler {
    void compile(String sourceCode,Integer botId) throws IOException, InterruptedException;
}
