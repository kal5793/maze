import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class CreateMaze extends MyFrame{
	private int[][] Maze;
    public int Width =15;
    public int Height=15;

    // 乱数生成用
    private Random Random;
    // 現在拡張中の壁情報を保持
    private Stack<Cell> CurrentWallCells;
    // 壁の拡張を行う開始セルの情報
    private List<Cell> StartCells;

    // 通路・壁情報
    private static final int Path = 0;
    private static final int Wall = 1;

    // コンストラクタ
    public CreateMaze(int width, int height) {
        // 5未満のサイズや偶数では生成できない
        if (width < 15 || height < 15) ;
        if (width % 2 == 0) width++;
        if (height % 2 == 0) height++;

        // 迷路情報を初期化
        this.Width = width;
        this.Height = height;
        Maze = new int[width][height];
        StartCells = new ArrayList<>();
        CurrentWallCells = new Stack<>();
        this.Random = new Random();
    }

    public int[][] createMaze() {
        // 各マスの初期設定を行う
        for (int y = 0; y < this.Height; y++) {
            for (int x = 0; x < this.Width; x++) {
                // 外周のみ壁にしておき、開始候補として保持
                if (x == 0 || y == 0 || x == this.Width - 1 || y == this.Height - 1) {
                    this.Maze[x][y] = Wall;
                } else {
                    this.Maze[x][y] = Path;
                    // 外周ではない偶数座標を壁伸ばし開始点にしておく
                    if (x % 2 == 0 && y % 2 == 0) {
                        // 開始候補座標
                        StartCells.add(new Cell(x, y));
                    }
                }
            }
        }

        // 壁が拡張できなくなるまでループ
        while (!StartCells.isEmpty()) {
            // ランダムに開始セルを取得し、開始候補から削除
            int index = Random.nextInt(StartCells.size());
            Cell cell = StartCells.get(index);
            StartCells.remove(index);
            int x = cell.x;
            int y = cell.y;

            // すでに壁の場合は何もしない
            if (this.Maze[x][y] == Path) {
                // 拡張中の壁情報を初期化
                CurrentWallCells.clear();
                extendWall(x, y);
            }
        }
        return this.Maze;
    }

    // 指定座標から壁を生成拡張する
    private void extendWall(int x, int y) {
        // 伸ばすことができる方向(1マス先が通路で2マス先まで範囲内)
        // 2マス先が壁で自分自身の場合、伸ばせない
        List<Direction> directions = new ArrayList<>();
        if (this.Maze[x][y - 1] == Path && !isCurrentWall(x, y - 2))
            directions.add(Direction.Up);
        if (this.Maze[x + 1][y] == Path && !isCurrentWall(x + 2, y))
            directions.add(Direction.Right);
        if (this.Maze[x][y + 1] == Path && !isCurrentWall(x, y + 2))
            directions.add(Direction.Down);
        if (this.Maze[x - 1][y] == Path && !isCurrentWall(x - 2, y))
            directions.add(Direction.Left);

        // ランダムに伸ばす(2マス)
        if (!directions.isEmpty()) {
            // 壁を作成(この地点から壁を伸ばす)
            setWall(x, y);

            // 伸ばす先が通路の場合は拡張を続ける
            boolean isPath = false;
            int dirIndex = Random.nextInt(directions.size());
            switch (directions.get(dirIndex)) {
                case Up:
                    isPath = (this.Maze[x][y - 2] == Path);
                    setWall(x, --y);
                    setWall(x, --y);
                    break;
                case Right:
                    isPath = (this.Maze[x + 2][y] == Path);
                    setWall(++x, y);
                    setWall(++x, y);
                    break;
                case Down:
                    isPath = (this.Maze[x][y + 2] == Path);
                    setWall(x, ++y);
                    setWall(x, ++y);
                    break;
                case Left:
                    isPath = (this.Maze[x - 2][y] == Path);
                    setWall(--x, y);
                    setWall(--x, y);
                    break;
            }
            if (isPath) {
                // 既存の壁に接続できていない場合は拡張続行
                extendWall(x, y);
            }
        } else {
            // すべて現在拡張中の壁にぶつかる場合、バックして再開
            Cell beforeCell = CurrentWallCells.pop();
            extendWall(beforeCell.x, beforeCell.y);
        }
    }

    // 壁を拡張する
    private void setWall(int x, int y) {
        this.Maze[x][y] = Wall;
        if (x % 2 == 0 && y % 2 == 0) {
            CurrentWallCells.push(new Cell(x, y));
        }
    }

    // 拡張中の座標かどうか判定
    private boolean isCurrentWall(int x, int y) {
        return CurrentWallCells.contains(new Cell(x, y));
    }

    // デバッグ用処理
    public static void debugPrint(int[][] maze) {
        System.out.println("Width: " + maze.length);
        System.out.println("Height: " + maze[0].length);
        for (int y = 0; y < maze[0].length; y++) {
            for (int x = 0; x < maze.length; x++) {
                System.out.print(maze[x][y] == Wall ? "■" : "　");
            }
            System.out.println();
        }
    }

    // セル情報
    private static class Cell {
        int x, y;
        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    // 方向
    private enum Direction {
        Up, Right, Down, Left
    }
    
    private void Draw() {
        int drawXpos = 10;
        int drawYpos = 40;
        int startY=40;
        int wallSize =30;

        for (int i = 0; i < Width; i++) {
            for (int j = 0; j < Height; j++) {

                fillRect(drawXpos, drawYpos,30 ,30 );
                setColor(0,0,0);
                drawYpos += wallSize;
            }
            drawXpos += wallSize;
            drawYpos = startY;
        }
    }
}
