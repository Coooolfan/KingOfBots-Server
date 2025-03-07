class Cell {
  constructor(x, y) {
    this.x = x;
    this.y = y;
  }
}

function check_tail_increasing(step) {  // 检验当前回合，蛇的长度是否增加
  if (step <= 10) return true;
  return step % 3 === 1;
}

function getCells(sx, sy, steps) {
  steps = steps.substring(1, steps.length - 1);
  const res = [];

  const dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
  let x = sx, y = sy;
  let step = 0;
  res.push(new Cell(x, y));
  for (let i = 0; i < steps.length; i++) {
    const d = parseInt(steps.charAt(i));
    x += dx[d];
    y += dy[d];
    res.push(new Cell(x, y));
    if (!check_tail_increasing(++step)) {
      res.shift();
    }
  }
  return res;
}

function nextMove(input) {
  const strs = input.split("#");
  const g = Array(13).fill().map(() => Array(14).fill(0));

  for (let i = 0, k = 0; i < 13; i++) {
    for (let j = 0; j < 14; j++, k++) {
      if (strs[0].charAt(k) === '1') {
        g[i][j] = 1;
      }
    }
  }

  const aSx = parseInt(strs[1]), aSy = parseInt(strs[2]);
  const bSx = parseInt(strs[4]), bSy = parseInt(strs[5]);

  const aCells = getCells(aSx, aSy, strs[3]);
  const bCells = getCells(bSx, bSy, strs[6]);

  for (const c of aCells) g[c.x][c.y] = 1;
  for (const c of bCells) g[c.x][c.y] = 1;

  const dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
  for (let i = 0; i < 4; i++) {
    const x = aCells[aCells.length - 1].x + dx[i];
    const y = aCells[aCells.length - 1].y + dy[i];
    if (x >= 0 && x < 13 && y >= 0 && y < 14 && g[x][y] === 0) {
      return i;
    }
  }

  return 0;
}

function main() {
  // 从命令行参数获取输入
  let args = process.argv.slice(2);

  if (args.length === 0) {
    args = ["11111111111111110010000000011010000011000110001000000001100000000000011110001000001110000000000001110000010001111000000000000110000000010001100011000001011000000001001111111111111111#11#11#(00)#1#12#(23)"];
  }

  console.log(nextMove(args[0]));
}

main();