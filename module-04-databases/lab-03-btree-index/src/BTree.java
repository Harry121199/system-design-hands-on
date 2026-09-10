import java.util.ArrayList;
import java.util.List;

public class BTree {

    private final int order;       // max children per node
    private final int maxKeys;     // max keys per node = order - 1
    private Node root;
    private int height;
    private int size;

    // Each node holds keys and child pointers
    static class Node {
        List<Entry> entries = new ArrayList<>();
        List<Node> children = new ArrayList<>();

        boolean isLeaf() {
            return children.isEmpty();
        }
    }

    // Each entry is a key-value pair (like priority → row pointer)
    static class Entry {
        String key;
        String value;  // in a real DB, this would be a pointer to the row on disk

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "→" + value;
        }
    }

    public BTree(int order) {
        if (order < 3) throw new IllegalArgumentException("Order must be >= 3");
        this.order = order;
        this.maxKeys = order - 1;
        this.root = new Node();
        this.height = 0;
        this.size = 0;
    }

    // ============ SEARCH ============

    public String search(String key) {
        return search(root, key, 0);
    }

    private String search(Node node, String key, int depth) {
        System.out.println("  Level " + depth + ": checking node with keys " + nodeKeys(node));

        if (node.isLeaf()) {
            // Linear scan through leaf entries
            for (Entry entry : node.entries) {
                if (entry.key.equals(key)) {
                    return entry.value;
                }
            }
            return null; // not found
        }

        // Internal node — find which child to follow
        for (int i = 0; i < node.entries.size(); i++) {
            int cmp = key.compareTo(node.entries.get(i).key);
            if (cmp < 0) {
                return search(node.children.get(i), key, depth + 1);
            }
            if (cmp == 0) {
                return node.entries.get(i).value;
            }
        }
        // Key is greater than all entries — go to last child
        return search(node.children.get(node.children.size() - 1), key, depth + 1);
    }

    // ============ INSERT ============

    public void insert(String key, String value) {
        Entry newEntry = new Entry(key, value);
        SplitResult result = insert(root, newEntry);

        if (result != null) {
            // Root was split — create a new root
            Node newRoot = new Node();
            newRoot.entries.add(result.promotedEntry);
            newRoot.children.add(result.left);
            newRoot.children.add(result.right);
            root = newRoot;
            height++;
        }
        size++;
    }

    private SplitResult insert(Node node, Entry entry) {
        if (node.isLeaf()) {
            // Insert into leaf in sorted order
            insertSorted(node.entries, entry);

            if (node.entries.size() > maxKeys) {
                return split(node);
            }
            return null;
        }

        // Find which child to insert into
        int childIndex = 0;
        for (int i = 0; i < node.entries.size(); i++) {
            if (entry.key.compareTo(node.entries.get(i).key) >= 0) {
                childIndex = i + 1;
            }
        }

        SplitResult result = insert(node.children.get(childIndex), entry);

        if (result != null) {
            // Child was split — absorb promoted key
            node.children.remove(childIndex);
            insertSorted(node.entries, result.promotedEntry);

            int insertPos = node.entries.indexOf(result.promotedEntry);
            node.children.add(insertPos, result.left);
            node.children.add(insertPos + 1, result.right);

            if (node.entries.size() > maxKeys) {
                return split(node);
            }
        }
        return null;
    }

    // ============ SPLIT ============

    private SplitResult split(Node node) {
        int mid = node.entries.size() / 2;
        Entry promoted = node.entries.get(mid);

        Node left = new Node();
        Node right = new Node();

        // Distribute entries
        left.entries.addAll(node.entries.subList(0, mid));
        right.entries.addAll(node.entries.subList(mid + 1, node.entries.size()));

        // Distribute children if internal node
        if (!node.isLeaf()) {
            left.children.addAll(node.children.subList(0, mid + 1));
            right.children.addAll(node.children.subList(mid + 1, node.children.size()));
        }

        return new SplitResult(promoted, left, right);
    }

    static class SplitResult {
        Entry promotedEntry;
        Node left;
        Node right;

        SplitResult(Entry promotedEntry, Node left, Node right) {
            this.promotedEntry = promotedEntry;
            this.left = left;
            this.right = right;
        }
    }

    // ============ HELPERS ============

    private void insertSorted(List<Entry> entries, Entry newEntry) {
        int i = 0;
        while (i < entries.size() && newEntry.key.compareTo(entries.get(i).key) > 0) {
            i++;
        }
        entries.add(i, newEntry);
    }

    private String nodeKeys(Node node) {
        List<String> keys = new ArrayList<>();
        for (Entry e : node.entries) {
            keys.add(e.key);
        }
        return keys.toString();
    }

    // ============ PRINT TREE ============

    public void printTree() {
        System.out.println("\n=== B-Tree (order=" + order + ", size=" + size + ", height=" + height + ") ===");
        printNode(root, 0);
    }

    private void printNode(Node node, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "Node: " + nodeKeys(node) + (node.isLeaf() ? " [leaf]" : ""));
        for (Node child : node.children) {
            printNode(child, depth + 1);
        }
    }

    public int getHeight() { return height; }
    public int getSize() { return size; }
}