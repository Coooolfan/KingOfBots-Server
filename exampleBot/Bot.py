import sys
from collections import deque

class Cell:
    def __init__(self, x, y):
        self.x = x
        self.y = y

def check_tail_increasing(step):  # 检验当前回合，蛇的长度是否增加
    if step <= 10:
        return True
    return step % 3 == 1

def get_cells(sx, sy, steps):
    steps = steps[1:-1]  # 去掉括号
    res = deque()

    dx = [-1, 0, 1, 0]
    dy = [0, 1, 0, -1]
    x, y = sx, sy
    step = 0
    res.append(Cell(x, y))

    for i in range(len(steps)):
        d = int(steps[i])
        x += dx[d]
        y += dy[d]
        res.append(Cell(x, y))
        step += 1
        if not check_tail_increasing(step):
            res.popleft()

    return res

def next_move(input_str):
    strs = input_str.split('#')
    g = [[0 for _ in range(14)] for _ in range(13)]

    # 解析地图
    for i in range(13):
        for j in range(14):
            k = i * 14 + j
            if strs[0][k] == '1':
                g[i][j] = 1

    # 解析起始位置和步骤
    a_sx, a_sy = int(strs[1]), int(strs[2])
    b_sx, b_sy = int(strs[4]), int(strs[5])

    a_cells = get_cells(a_sx, a_sy, strs[3])
    b_cells = get_cells(b_sx, b_sy, strs[6])

    # 标记蛇身位置
    for c in a_cells:
        g[c.x][c.y] = 1
    for c in b_cells:
        g[c.x][c.y] = 1

    # 寻找可行的移动方向
    dx = [-1, 0, 1, 0]
    dy = [0, 1, 0, -1]

    for i in range(4):
        x = a_cells[-1].x + dx[i]
        y = a_cells[-1].y + dy[i]
        if 0 <= x < 13 and 0 <= y < 14 and g[x][y] == 0:
            return i

    return 0

if __name__ == "__main__":
    if len(sys.argv) > 1:
        input_str = sys.argv[1]
        print(next_move(input_str))
    else:
        print("-1")
