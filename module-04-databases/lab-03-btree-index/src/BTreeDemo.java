public class BTreeDemo {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("  B-Tree Index Demo — How CREATE INDEX works");
        System.out.println("============================================\n");

        // Order 3 = each node holds max 2 keys, 3 children
        BTree tree = new BTree(3);

        // Simulate indexing the "priority" column
        System.out.println("--- Inserting index entries (like CREATE INDEX) ---\n");

        String[][] data = {
                {"HIGH",   "row_001"}, {"LOW",    "row_002"}, {"MEDIUM", "row_003"},
                {"HIGH",   "row_004"}, {"LOW",    "row_005"}, {"HIGH",   "row_006"},
                {"MEDIUM", "row_007"}, {"LOW",    "row_008"}, {"HIGH",   "row_009"},
                {"MEDIUM", "row_010"}, {"LOW",    "row_011"}, {"HIGH",   "row_012"},
        };

        for (String[] row : data) {
            System.out.println("INSERT: " + row[0] + " → " + row[1]);
            tree.insert(row[0], row[1]);
        }

        tree.printTree();

        // Search
        System.out.println("\n--- Searching (like WHERE priority = 'HIGH') ---\n");

        String[] searchKeys = {"HIGH", "LOW", "MEDIUM", "URGENT"};
        for (String key : searchKeys) {
            System.out.println("Searching for: " + key);
            String result = tree.search(key);
            System.out.println("Result: " + (result != null ? result : "NOT FOUND"));
            System.out.println();
        }

        // Scale test
        System.out.println("--- Scale test: 10,000 entries ---\n");

        BTree bigTree = new BTree(4);  // order 4 = max 3 keys per node
        long startInsert = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            bigTree.insert(String.format("key_%05d", i), "row_" + i);
        }
        long insertTime = (System.nanoTime() - startInsert) / 1_000_000;

        System.out.println("Inserted 10,000 entries in " + insertTime + "ms");
        System.out.println("Tree height: " + bigTree.getHeight());
        System.out.println("To find any key: at most " + (bigTree.getHeight() + 1) + " node reads");
        System.out.println();

        // Search in big tree
        System.out.println("Searching for key_05000 in 10,000 entries:");
        long startSearch = System.nanoTime();
        String found = bigTree.search("key_05000");
        long searchTime = System.nanoTime() - startSearch;
        System.out.println("Found: " + found + " in " + searchTime + " nanoseconds");
        System.out.println();

        // Compare: full scan vs index
        System.out.println("--- Full scan vs B-Tree index ---\n");
        System.out.println("Full table scan of 10,000 rows: reads 10,000 entries");
        System.out.println("B-Tree index lookup (height " + bigTree.getHeight() + "): reads " + (bigTree.getHeight() + 1) + " nodes");
        System.out.printf("Index is %.0fx fewer reads%n", 10_000.0 / (bigTree.getHeight() + 1));
    }
}