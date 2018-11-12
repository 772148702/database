package myBPlusTree;

public class Test2 {

    class People
    {
        String name;
        String id;
        People(int key)
        {
            name = String.valueOf(key);
            id = String.valueOf(key);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {

        Table a = new Table("People",4);
        for (int i=1;i<500;i++)
        {
            a.bplusTree.insert(i,new myBPlusTree.People(i));
        }

        a.store();

        BplusTree b = a.fetch();
        b.printTree();
    }
}
