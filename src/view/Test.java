package view;


import java.util.ArrayList;
import java.util.List;
import path.finder.a_star.Graph;
import path.finder.a_star.Node;

/**
 * Test class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Test {
    
    public static void main(String[] args) {
        
        // Test Graph:
        //
        //     9______ B 
        //     /         \ 6 
        //    /  2    11  \
        //   A ---- C ---- E target 
        //    \  9/  \_10 /
        //  14 \ /     \ / 15
        //      D ----- F
        // start    7
        
        Node<String> a = new Node<>("A");
        Node<String> b = new Node<>("B");
        Node<String> c = new Node<>("C");
        Node<String> d = new Node<>("D");
        Node<String> e = new Node<>("E");
        Node<String> f = new Node<>("F");
        
        Graph<String> graph = new Graph<>((start, target, current) -> {
            return 0; // heuristic = 0 -> equivalent to Dijkstra's algorithm
        });
        
        graph.addNode(a);
        graph.addNode(b);
        graph.addNode(c);
        graph.addNode(d);
        graph.addNode(e);
        graph.addNode(f);
        
        graph.link(a, b, 9);
        graph.link(a, c, 2);
        graph.link(a, d, 14);
        graph.link(b, e, 6);
        graph.link(c, e, 11);
        graph.link(d, c, 9);
        graph.link(d, f, 7);
        graph.link(c, f, 10);
        graph.link(f, e, 15);        
        
        List<Node<String>> path = new ArrayList<>();
        graph.findPath(d, e, path);

        System.out.println("Found path:");
        path.forEach(node -> System.out.println(node.getObj()));
    }
    
}
