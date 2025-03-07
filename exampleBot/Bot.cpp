#include <iostream>
#include <vector>
#include <deque>
#include <string>

class Cell {
public:
    int x, y;

    Cell(int x, int y) : x(x), y(y) {}
};

bool check_tail_increasing(int step) {  // 检验当前回合，蛇的长度是否增加
    if (step <= 10) return true;
    return step % 3 == 1;
}

std::deque<Cell> getCells(int sx, int sy, const std::string& steps) {
    std::string stepsProcessed = steps.substr(1, steps.length() - 2);  // 移除括号
    std::deque<Cell> res;

    int dx[4] = {-1, 0, 1, 0}, dy[4] = {0, 1, 0, -1};
    int x = sx, y = sy;
    int step = 0;
    res.push_back(Cell(x, y));
    for (char c : stepsProcessed) {
        int d = c - '0';
        x += dx[d];
        y += dy[d];
        res.push_back(Cell(x, y));
        if (!check_tail_increasing(++step)) {
            res.pop_front();
        }
    }
    return res;
}

int nextMove(const std::string& input) {
    // 分割输入字符串
    std::vector<std::string> strs;
    size_t start = 0, end = 0;
    while ((end = input.find('#', start)) != std::string::npos) {
        strs.push_back(input.substr(start, end - start));
        start = end + 1;
    }
    strs.push_back(input.substr(start));

    // 解析地图
    int g[13][14] = {0};
    for (int i = 0, k = 0; i < 13; i++) {
        for (int j = 0; j < 14; j++, k++) {
            if (strs[0][k] == '1') {
                g[i][j] = 1;
            }
        }
    }

    // 解析坐标和步骤
    int aSx = std::stoi(strs[1]), aSy = std::stoi(strs[2]);
    int bSx = std::stoi(strs[4]), bSy = std::stoi(strs[5]);

    std::deque<Cell> aCells = getCells(aSx, aSy, strs[3]);
    std::deque<Cell> bCells = getCells(bSx, bSy, strs[6]);

    // 更新地图
    for (const Cell& c : aCells) g[c.x][c.y] = 1;
    for (const Cell& c : bCells) g[c.x][c.y] = 1;

    // 找到可行的下一步
    int dx[4] = {-1, 0, 1, 0}, dy[4] = {0, 1, 0, -1};
    for (int i = 0; i < 4; i++) {
        int x = aCells.back().x + dx[i];
        int y = aCells.back().y + dy[i];
        if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] == 0) {
            return i;
        }
    }

    return 0;
}

int main(int argc, char* argv[]) {
    std::string input;
    if (argc > 1) {
        input = argv[1];
    } else {
        input = "11111111111111110010000000011010000011000110001000000001100000000000011110001000001110000000000001110000010001111000000000000110000000010001100011000001011000000001001111111111111111#11#11#(00)#1#12#(23)";
    }

    std::cout << nextMove(input) << std::endl;
    return 0;
}