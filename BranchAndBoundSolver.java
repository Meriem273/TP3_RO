import java.util.*;


public class BranchAndBoundSolver extends KnapsackSolver {

   private class Node implements Comparable<Node> {
      
      public int k;
      List<Item> taken;
      public double bound;
      public double value;
      public double weight;
      
      public Node() {
         taken = new ArrayList<Item>();
      }
      
      public Node(Node parent) {
         k = parent.k + 1;
         taken = new ArrayList<Item>(parent.taken);
         bound = parent.bound;
         value = parent.value;
         weight = parent.weight;
      }
      
      
      public int compareTo(Node other) {
         return (int) (other.bound - bound);
      }
      
      public void computeBound() {
         int i = k;
         double w = weight;
         bound = value;
         Item item;
         do {
            item = items.get(i);
            if (w + item.weight > capacity) break;
            w += item.weight;
            bound += item.value;
            i++;
         } while (i < items.size());
         bound += (capacity - w) * (item.value / item.weight);
      }
   }
   
   public BranchAndBoundSolver(List<Item> items, int capacity) {
      super(items, capacity);
   }
   
   @Override
   public KnapsackSolution solve() {
      
      Collections.sort(items, Item.byRatio());
      
      Node best = new Node();
      Node root = new Node();
      root.computeBound();
      
      PriorityQueue<Node> q = new PriorityQueue<Node>();
      q.offer(root);
      
      while (!q.isEmpty()) {
         Node node = q.poll();
         
         if (node.bound > best.value && node.k < items.size() - 1) {
            
            Node with = new Node(node);
            Item item = items.get(node.k);
            with.weight += item.weight;
            
            if (with.weight <= capacity) {
            
               with.taken.add(items.get(node.k));
               with.value += item.value;
               with.computeBound();
               
               if (with.value > best.value) {
                  best = with;
               }
               if (with.bound > best.value) {
                  q.offer(with);
               }
            }
            
            Node without = new Node(node);
            without.computeBound();
            
            if (without.bound > best.value) {
               q.offer(without);
            }
         }
      }
      
      KnapsackSolution solution = new KnapsackSolution();
      solution.value = best.value;
      solution.weight = best.weight;
      solution.items = best.taken;
      solution.approach = "Using Branch and Bound the best feasible solution found";
      
      return solution;
   }
}
