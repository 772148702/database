package myBPlusTree;

public class Test {
    public static void main(String[] args) {
        BplusTree bPlusTree = new BplusTree(4);
        for(int i=1;i<10;i++) {

            bPlusTree.insert(i, i);
            System.out.println("第"+i+"次插入");
            bPlusTree.printTree();
        }
       bPlusTree.printTree();
    }
}
