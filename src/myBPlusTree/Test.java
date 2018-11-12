package myBPlusTree;

public class Test {


    public static void main(String[] args) {
        //4阶
        BplusTree bPlusTree = new BplusTree(4);
        int num = 50;
        for(int i=1;i<num;i++) {
            bPlusTree.insert(i, i);
            System.out.println("第"+i+"次插入");
            bPlusTree.printTree();
        }
        bPlusTree.printList();

        for (int i=num-1;i>=1;i--)
        {
            bPlusTree.remove(i);
            System.out.println("删除 "+i);
            bPlusTree.printTree();
        }


    }
}
