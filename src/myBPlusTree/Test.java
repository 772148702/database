package myBPlusTree;

public class Test {


    public static void main(String[] args) {
        //4阶
        BplusTree bPlusTree = new BplusTree(4);
        for(int i=1;i<16;i++) {
            bPlusTree.insert(i, i+10);
            System.out.println("第"+i+"次插入");
            bPlusTree.printTree();
        }
        bPlusTree.printList();

        System.out.println(bPlusTree.search(10));
    }
}
