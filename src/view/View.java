package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import path.finder.a_star.Edge;
import path.finder.a_star.Graph;
import path.finder.a_star.Node;
import static path.finder.a_star.Node.State.*;

/**
 * A* Path Finding Algorithm Test.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com);
 */
public class View extends JPanel {

    private static final int TILE_SIZE = 40;
    
    private static final int GRID_COLS = 15;
    private static final int GRID_ROWS = 15;
    
    private Node[][] grid;
    
    private Graph<Point> graph;
    
    private Node<Point> startNode;
    private Node<Point> targetNode;
    
    private List<Node<Point>> path;
    
    private final Stroke stroke = new BasicStroke(10
        , BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    private final Font smallFont = new Font("Arial", Font.BOLD, 10);
    
    public View() {
    }
    
    public void start() {
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        
        path = new ArrayList<>();
        grid = new Node[GRID_ROWS][GRID_COLS];
        
        graph = new Graph<>((start, target, current) -> {
            // --- implement your heuristic here ---
            
            // heuristic = manhattan distance
            //int dx = Math.abs(target.getObj().x - current.getObj().x);
            //int dy = Math.abs(target.getObj().y - current.getObj().y);
            //return dx + dy;

            // heuristic = linear distance
            int dx = target.getObj().x - current.getObj().x;
            int dy = target.getObj().y - current.getObj().y;
            return Math.sqrt(dx * dx + dy * dy);
            
            // heuristic = 0 -> equivalent to Dijkstra
            //return 0;
        });
        
        createGrid();
        
        startNode = grid[1][1];
        targetNode = grid[GRID_ROWS - 2][GRID_COLS - 2];
    }

    private void createGrid() {
        
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                int nx = x * TILE_SIZE;
                int ny = y * TILE_SIZE;
                Node node = new Node(new Point(nx, ny));
                graph.addNode(node);
                grid[y][x] = node;
            }
        }
        
        // link all nodes
        
        double diagonalG = Math.sqrt(TILE_SIZE * TILE_SIZE 
            + TILE_SIZE * TILE_SIZE);
        
        for (int y = 0; y < GRID_ROWS - 1; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                // vertical '|'
                Node top = grid[y][x];
                Node bottom = grid[y + 1][x];
                graph.link(top, bottom, TILE_SIZE);
                
                // diagonals 'X'
                if (x < GRID_COLS - 1) {
                    
                    // diagonal '\'
                    top = grid[y][x];
                    bottom = grid[y + 1][x + 1];
                    graph.link(top, bottom, diagonalG);
                    
                    // diagonal '/'
                    top = grid[y][x + 1];
                    bottom = grid[y + 1][x];
                    graph.link(top, bottom, diagonalG);
                }
            }
        }

        for (int x = 0; x < GRID_COLS - 1; x++) {
            for (int y = 0; y < GRID_ROWS; y++) {
                // horizontal '-'
                Node left = grid[y][x];
                Node right = grid[y][x + 1];
                graph.link(left, right, TILE_SIZE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }
    
    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        
        path.clear();
        graph.findPath(startNode, targetNode, path);
        
        graph.getNodes().forEach(node -> {
            drawNode(g, node);
        });
        
        drawPath(g);
        drawExplanations(g);
        
        g.setColor(Color.BLACK);
        g.drawString("Walk Length: " + ((int) targetNode.getG()), 620, 20);
    }

    private void drawNode(Graphics2D g, Node<Point> node) {
        Color color = Color.BLACK;
        
        switch (node.getState()) {
            case OPEN:
                color = Color.CYAN;
                break;
            case CLOSED:
                color = Color.ORANGE;
                break;
            case UNVISITED:
                color = Color.WHITE;
                break;
        }

        if (node == startNode) {
            color = Color.BLUE;
        }
        else if (node == targetNode) {
            color = Color.RED;
        }
        else if (node.isBlocked()) {
            color = Color.BLACK;
        }

        g.setColor(color);
        int rx = node.getObj().x;
        int ry = node.getObj().y;
        g.fillRect(rx, ry, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(rx, ry, TILE_SIZE, TILE_SIZE);
    }

    private void drawPath(Graphics2D g) {
        Stroke originalStroke = g.getStroke();
        g.setColor(Color.BLUE);
        g.setStroke(stroke);
        for (int i = 0; i < path.size() - 1; i++) {
            Node<Point> a = path.get(i);
            Node<Point> b = path.get(i + 1);
            int x1 = a.getObj().x + TILE_SIZE / 2;
            int y1 = a.getObj().y + TILE_SIZE / 2;
            int x2 = b.getObj().x + TILE_SIZE / 2;
            int y2 = b.getObj().y + TILE_SIZE / 2;
            g.drawLine(x1, y1, x2, y2);
        }
        g.setStroke(originalStroke);
    }

    private void drawExplanations(Graphics2D g) {
        int ex = 620;
        
        g.setColor(Color.BLUE);
        g.fillRect(ex, 50, 20, 20);
        g.setColor(Color.RED);
        g.fillRect(ex, 80, 20, 20);
        g.setColor(Color.BLACK);
        g.fillRect(ex, 110, 20, 20);
        g.setColor(Color.CYAN);
        g.fillRect(ex, 140, 20, 20);
        g.setColor(Color.ORANGE);
        g.fillRect(ex, 170, 20, 20);
        g.setColor(Color.WHITE);
        g.fillRect(ex, 200, 20, 20);

        g.setColor(Color.BLACK);
        g.drawRect(ex, 50, 20, 20);
        g.drawRect(ex, 80, 20, 20);
        g.drawRect(ex, 110, 20, 20);
        g.drawRect(ex, 140, 20, 20);
        g.drawRect(ex, 170, 20, 20);
        g.drawRect(ex, 200, 20, 20);

        int tx = 25;
        int ty = 15;
        g.drawString("START", ex + tx, 50 + ty);
        g.drawString("TARGET", ex + tx, 80 + ty);
        g.drawString("WALL", ex + tx, 110 + ty);
        g.drawString("OPEN", ex + tx, 140 + ty);
        g.drawString("CLOSED", ex + tx, 170 + ty);
        g.drawString("UNVISITED", ex + tx, 200 + ty);
        
        g.setFont(smallFont);
        g.drawString("LEFT MOUSE BUTTON = START", ex, 260 + ty);
        g.drawString("RIGHT MOUSE BUTTON = TARGET", ex, 290 + ty);
        g.drawString("MIDDLE MOUSE BUTTON = WALL", ex, 320 + ty);
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            int row = e.getY() / TILE_SIZE;
            int col = e.getX() / TILE_SIZE;
            
            if (col < 0 || row < 0 
                || col > GRID_COLS - 1 || row > GRID_COLS - 1) {
                
                return;
            }
            
            if (SwingUtilities.isLeftMouseButton(e)) {
                startNode = grid[row][col];
            }
            else if (SwingUtilities.isRightMouseButton(e)) {
                targetNode = grid[row][col];
            }
            else if (SwingUtilities.isMiddleMouseButton(e)) {
                if (!grid[row][col].isBlocked()) {
                    grid[row][col].setBlocked(true);
                }
                else {
                    grid[row][col].setBlocked(false);
                }                
            }
            
            repaint();
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("Java A* Path Finding Algorithm Test");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }
    
}
