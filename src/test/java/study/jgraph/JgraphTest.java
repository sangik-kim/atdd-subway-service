package study.jgraph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JgraphTest {

    WeightedMultigraph<String, DefaultWeightedEdge> graph;
    private static final String V1 = "v1";
    private static final String V2 = "v2";
    private static final String V3 = "v3";
    private static final String V4 = "v4";

    @BeforeEach
    void setUp(){
        graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(V1);
        graph.addVertex(V2);
        graph.addVertex(V3);
        graph.setEdgeWeight(graph.addEdge(V1, V2), 2);
        graph.setEdgeWeight(graph.addEdge(V2, V3), 2);
        graph.setEdgeWeight(graph.addEdge(V1, V3), 100);
    }

    @Test
    @DisplayName("경로가 없는 경우")
    void noPath(){
        String source = V1;
        String target = V4;
        graph.addVertex(V4);
        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(source, target);
        assertThat(paths).isEmpty();
    }

    @Test
    @DisplayName("출발지와 목적지가 같은 경우")
    void sameSourceSink(){
        String source = V1;
        String target = V1;
        ShortestPathAlgorithm dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath<String,DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
        assertThat(shortestPath.getLength()).isZero();
    }

    @Test
    @DisplayName("존재하지 않는 목적지")
    void notExistVertex(){
        String source = V1;
        String target = V4;
        KShortestPaths<String,DefaultWeightedEdge> kShortestPaths = new KShortestPaths(graph, 100);

        assertThatThrownBy(()->{
           kShortestPaths.getPaths(source, target);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Dijkstra 최단경로까지 찾기")
    void getDijkstraShortestPath() {
        String source = V3;
        String target = V1;

        ShortestPathAlgorithm dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();

        assertThat(shortestPath).hasSize(3);
    }

    @Test
    @DisplayName("100번째 최단경로까지 찾기")
    void getKShortestPaths() {
        String source = V3;
        String target = V1;
        int kth = 100;
        List<GraphPath> paths = new KShortestPaths(graph, kth).getPaths(source, target);

        assertThat(paths).hasSize(2);
        paths.stream()
                .forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
                });
    }
}
