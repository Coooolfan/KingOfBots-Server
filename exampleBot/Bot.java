import java.util.LinkedList;
import java.util.List;

public class Bot {
    public static class Cell {
        public int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static boolean check_tail_increasing(int step) {  // 检验当前回合，蛇的长度是否增加
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public static List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new LinkedList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int i = 0; i < steps.length(); i++) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing(++step)) {
                res.removeFirst();
            }
        }
        return res;
    }

    public static Integer nextMove(String input) {
//      getMapString() + "#" +
//      me.getSx() + "#" +
//      me.getSy() + "#(" +
//      me.getStepsString() + ")#" +
//      you.getSx() + "#" +
//      you.getSy() + "#(" +
//      you.getStepsString() + ")";
        String[] strs = input.split("#");
        int[][] g = new int[13][14];
        for (int i = 0, k = 0; i < 13; i++) {
            for (int j = 0; j < 14; j++, k++) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);

        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        for (Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            int x = aCells.getLast().x + dx[i];
            int y = aCells.getLast().y + dy[i];
            if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
                return i;
            }
        }

        return 0;
    }

    public static void main(String[] args) {
        if (args.length == 0)
            args = new String[]{"11111111111111110010000000011010000011000110001000000001100000000000011110001000001110000000000001110000010001111000000000000110000000010001100011000001011000000001001111111111111111#11#11#(00)#1#12#(23)"};
//        System.out.println("args[0]: " + args[0]);
        System.out.println(nextMove(args[0]));
    }

}