package com.yang.kingofbotsserver.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx; // 行数
    private Integer sy; // 列数
    private List<Integer> steps;

    private boolean checkTailIncreasing(int steps) {
        if (steps <= 10) return true;
        return steps % 3 == 1;
    }

    public List<SnakeCell> getCells() {
        List<SnakeCell> res = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int step = 0;
        int x = sx, y = sy;
        res.add(new SnakeCell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new SnakeCell(x, y));
            if(!checkTailIncreasing(++step)){
                res.remove(0);
            }
        }
        return res;
    }
    public String getStepsString(){
        StringBuilder res = new StringBuilder();
        for(int d :steps)
            res.append(d);
        return res.toString();
    }
}
