import java.util.HashMap;
import java.util.Map;

public class LRUCache<K,V> {

    private final int capacity;
    private final Map<K,Node<K,V>> map;
    private Node<K,V> head;
    private Node<K,V> tail;
    private int hits;
    private int misses;

    static class Node <K,V> {
        K key;
        V value;
        Node<K,V> prev;
        Node<K,V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(null, null);
        this.tail = new Node(null, null);
        this.head.next = tail;
        this.tail.prev = head;
    }

    public V get(K key) {
        Node<K,V> node = map.get(key);
        if(node == null) {
            misses ++;
            return null;
        }
        hits++;
        moveToHead(node);
        return node.value;
    }

    public void put(K key, V value) {
        Node<K,V> existing = map.get(key);

        if(existing != null) {
            existing.value = value;
            moveToHead(existing);
            return;
        }
        Node<K,V> newNode = new Node<>(key, value);
        map.put(key, newNode);
        addToHead(newNode);

        if(map.size()>capacity) {
            Node<K,V> evicted = removeTail();
            map.remove(evicted.key);
            System.out.println(" EVICTED: "+ evicted.key);
        }
    }
    private void addToHead(Node<K,V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    private void removeNode(Node<K,V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node<K,V> node) {
        removeNode(node);
        addToHead(node);
    }
    private Node<K,V> removeTail() {
        Node<K,V> evicted = tail.prev;
        removeNode(evicted);
        return evicted;
    }

    public int size() { return map.size(); }
    public int getHits() { return hits; }
    public int getMisses() { return misses; }

    public double hitRate() {
        int total = hits + misses;
        return total == 0?0:((double)hits / total) *100;
    }
    public void printState(){

        System.out.println(" Cache [");
        Node<K,V> current = head.next;
        while(current != tail){
            System.out.println(current.key+"="+current.value);
            if(current.next != tail) System.out.println("->");
            current = current.next;
        }
        System.out.println("] (Size=" + size() + "/" + capacity + ")");
    }

}