import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Map;
import java.util.HashMap;

public class GraphPanel extends JPanel {
    private final Graph graph;

    // 构造函数，接收一个 Graph 对象
    public GraphPanel(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // 获取图的邻接表
        Map<String, Map<String, Integer>> adjList = graph.getAdjList();

        // 设置图形渲染的抗锯齿特性
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 计算绘制圆形布局的半径
        double radius = Math.min(width, height) / 2.5;
        int n = adjList.size();
        // 计算每个节点之间的角度增量
        double angleIncrement = 2 * Math.PI / n;

        // 计算圆形布局的中心点
        int centerX = width / 2;
        int centerY = height / 2;

        // 存储每个节点的位置
        Map<String, Point> nodePositions = new HashMap<>();
        int i = 0;
        // 遍历邻接表，为每个节点计算并存储其位置
        for (String node : adjList.keySet()) {
            double angle = i * angleIncrement;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            nodePositions.put(node, new Point(x, y));
            i++;
        }

        // 绘制边
        for (Map.Entry<String, Map<String, Integer>> entry : adjList.entrySet()) {
            String from = entry.getKey();
            Map<String, Integer> neighbors = entry.getValue();
            Point fromPos = nodePositions.get(from);
            if (fromPos != null) { // 添加条件检查
                for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                    String to = neighborEntry.getKey();
                    Point toPos = nodePositions.get(to);
                    if (toPos != null) { // 添加条件检查
                        // 画箭头线条
                        drawArrowLine(g2d, fromPos.x, fromPos.y, toPos.x, toPos.y, 10, 5);
                        // 在边的中点绘制权重
                        g2d.drawString(String.valueOf(neighborEntry.getValue()),
                                (fromPos.x + toPos.x) / 2, (fromPos.y + toPos.y) / 2);
                    }
                }
            }
        }

        // 绘制节点
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String node = entry.getKey();
            Point pos = entry.getValue();
            // 绘制节点为圆形
            g2d.fillOval(pos.x - 10, pos.y - 10, 20, 20);
            // 在节点旁边绘制节点名称
            g2d.drawString(node, pos.x - 10, pos.y - 15);
        }
    }

    // 绘制箭头线条的方法
    private void drawArrowLine(Graphics2D g2d, int x1, int y1, int x2, int y2, int d, int h) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx * dx + dy * dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        // 计算箭头左侧点的坐标
        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        // 计算箭头右侧点的坐标
        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        // 绘制箭头线条
        g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        g2d.fillPolygon(xpoints, ypoints, 3);
    }
}
