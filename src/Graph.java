import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
public class Graph {
    // 邻接表，用于存储图的结构。键是单词，值是一个哈希表，表示该单词与其他单词的边及其权重。
    private final Map<String, Map<String, Integer>> adjList = new HashMap<>();
    private String[] words;

    // 构建图的方法，从给定的文本中提取单词并构建邻接表
    public void buildGraph(String text) {
        // 去除文本中的非字母字符，并将文本转为小写
        String sanitizedText = text.replaceAll("[\\W_]+", " ").toLowerCase();
        words = sanitizedText.split("\\s+");

        // 遍历单词数组，构建邻接表
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            // 如果邻接表中没有 word1，则添加一个新的条目
            adjList.putIfAbsent(word1, new HashMap<>());
            // 增加 word1 到 word2 的边，如果已经存在则增加权重
            adjList.get(word1).put(word2, adjList.get(word1).getOrDefault(word2, 0) + 1);
        }
        int j = words.length - 1;
        // 确保最后一个单词也在邻接表中
        adjList.putIfAbsent(words[j], new HashMap<>());
    }

    // 获取邻接表的方法
    public Map<String, Map<String, Integer>> getAdjList() {
        return adjList;
    }

    // 查询桥接词的方法，即在 word1 和 word2 之间存在的中间词
    public String queryBridgeWords(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        // 查找从 word1 出发可以到达并且能继续到达 word2 的所有单词
        for (String neighbor : adjList.get(word1).keySet()) {
            if (adjList.containsKey(neighbor) && adjList.get(neighbor).containsKey(word2)) {
                bridgeWords.add(neighbor);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }
        return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords) + ".";
    }

    // 生成新文本的方法，通过插入桥接词生成新的文本
    public String generateNewText(String inputText) {
        String sanitizedText = inputText.replaceAll("[\\W_]+", " ").toLowerCase();
        String[] words = sanitizedText.split("\\s+");

        StringBuilder newText = new StringBuilder(words[0]);
        Random rand = new Random();

        // 遍历单词数组，为相邻的单词对寻找桥接词并插入新文本
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            Set<String> bridgeWords = new HashSet<>();
            if (adjList.containsKey(word1)) {
                for (String neighbor : adjList.get(word1).keySet()) {
                    if (adjList.containsKey(neighbor) && adjList.get(neighbor).containsKey(word2)) {
                        bridgeWords.add(neighbor);
                    }
                }
            }
            // 如果找到桥接词，从中随机选择一个插入新文本
            if (!bridgeWords.isEmpty()) {
                String bridgeWord = bridgeWords.toArray(new String[0])[rand.nextInt(bridgeWords.size())];
                newText.append(" ").append(bridgeWord);
            }
            newText.append(" ").append(word2);
        }
        return newText.toString();
    }

    // 计算两个单词之间的最短路径的方法
    public String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (!adjList.containsKey(word1) || !adjList.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : adjList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(word1, 0);
        pq.add(word1);

        // 使用 Dijkstra 算法计算最短路径
        while (!pq.isEmpty()) {
            String current = pq.poll();
            int currentDist = distances.get(current);

            for (Map.Entry<String, Integer> neighbor : adjList.get(current).entrySet()) {
                int newDist = currentDist + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        if (distances.get(word2) == Integer.MAX_VALUE) {
            return "No path from " + word1 + " to " + word2 + "!";
        }

        // 构建从 word1 到 word2 的最短路径
        StringBuilder path = new StringBuilder();
        for (String at = word2; at != null; at = previous.get(at)) {
            path.insert(0, at + " ");
        }
        return "Shortest path from " + word1 + " to " + word2 + " is: " + path.toString().trim() + ". Path length: " + distances.get(word2) + ".";
    }

    // 随机游走方法，从图中的一个随机顶点开始，随机选择一条边走下去，原神，启动！
    public String randomWalk() {
        Random random = new Random();
        List<String> vertices = new ArrayList<>(adjList.keySet());
        String current = vertices.get(random.nextInt(vertices.size()));
        StringBuilder path = new StringBuilder(current);

        Set<String> visitedEdges = new HashSet<>();

        while (true) {
            Map<String, Integer> neighbors = adjList.get(current);
            if (neighbors == null || neighbors.isEmpty()) break;

            List<String> nextVertices = new ArrayList<>(neighbors.keySet());
            String next = nextVertices.get(random.nextInt(nextVertices.size()));

            String edge = current + "->" + next;
            if (visitedEdges.contains(edge)) break;

            visitedEdges.add(edge);
            path.append(" -> ").append(next);
            current = next;
        }
        String resultPath = path.toString();


        // 将结果写入名为 random.txt 的文件中，启动！
        try (FileWriter writer = new FileWriter("random.txt")) {
            writer.write(resultPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path.toString();
    }

    // 内部类，用于表示图中的节点及其权重
    private static class Node {
        String word;
        int weight;

        Node(String word, int weight) {
            this.word = word;
            this.weight = weight;
        }
    }
}
