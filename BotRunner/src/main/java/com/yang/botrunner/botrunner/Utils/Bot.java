package com.yang.botrunner.botrunner.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    Integer userId;
    String botCode;
    String input;
    String language;
}
