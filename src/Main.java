import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static Graph graph;

    // 定义一个内部静态类用于全局标志
    public static class GlobalFlags {
        // 声明一个静态标志变量
        public static int flag = 0;

        // 其他代码...
    }

    public static void main(String[] args) {
        // 使用 SwingUtilities.invokeLater 确保 GUI 创建和更新在事件调度线程中进行
        SwingUtilities.invokeLater(() -> {
            // 创建主窗口框架
            JFrame frame = new JFrame("Text to Graph");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // 获取内容面板并设置布局管理器
            Container container = frame.getContentPane();
            container.setLayout(new BorderLayout());

            // 初始化 Graph 对象
            graph = new Graph();

            // 创建控制面板，包括文件路径输入框和加载按钮
            JPanel controlPanel = new JPanel();
            JTextField filePathField = new JTextField(20);
            JButton loadButton = new JButton("Load File");
            controlPanel.add(new JLabel("File Path:"));
            controlPanel.add(filePathField);
            controlPanel.add(loadButton);
            container.add(controlPanel, BorderLayout.NORTH);

            // 创建 GraphPanel 对象用于绘制图形
            GraphPanel graphPanel = new GraphPanel(graph);
            container.add(graphPanel, BorderLayout.CENTER);

            // 创建文本区域用于显示结果
            JTextArea resultArea = new JTextArea(10, 50);
            container.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

            // 为加载按钮添加事件监听器
            loadButton.addActionListener(e -> {
                String filePath = filePathField.getText();
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    try (Scanner scanner = new Scanner(file)) {
                        StringBuilder text = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            text.append(scanner.nextLine()).append(" ");
                        }
                        // 构建图
                        graph.buildGraph(text.toString());
                        graphPanel.repaint();
                        resultArea.setText("Graph loaded successfully.");
                        GlobalFlags.flag = 1; // 更新全局标志
                    } catch (FileNotFoundException ex) {
                        resultArea.setText("File not found.");
                        GlobalFlags.flag = 0; // 更新全局标志
                    }
                } else {
                    resultArea.setText("Invalid file path.");
                    GlobalFlags.flag = 0; // 更新全局标志
                }
            });

            // 显示主窗口框架
            frame.setVisible(true);
        });

        // 命令行界面用户交互部分
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Show directed graph");
            System.out.println("2. Generate new text");
            System.out.println("3. Calculate shortest path");
            System.out.println("4. Random walk");
            System.out.println("5. Query bridge words");
            System.out.println("6. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 消除换行符

            switch (choice) {
                case 1:
                    System.out.println("Look at the window");
                    break;
                case 2:
                    System.out.println("Enter new text:");
                    String inputText = scanner.nextLine();
                    System.out.println("Generated text: " + graph.generateNewText(inputText));
                    break;
                case 3:
                    System.out.println("Enter two words:");
                    String word1 = scanner.next();
                    String word2 = scanner.next();
                    System.out.println("Shortest path: " + graph.calcShortestPath(word1, word2));
                    break;
                case 4:
                    System.out.println("Random walk: " + graph.randomWalk());
                    break;
                case 5:
                    System.out.println("Enter two words:");
                    String bridgeWord1 = scanner.next();
                    String bridgeWord2 = scanner.next();
                    System.out.println(graph.queryBridgeWords(bridgeWord1, bridgeWord2));
                    break;
                case 6:
                    return; // 退出程序
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
