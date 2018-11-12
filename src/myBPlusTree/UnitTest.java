package myBPlusTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;

class People implements Serializable
{
    String name;
    String id;
    People(int key)
    {
        name = String.valueOf(key);
        id = String.valueOf(key);
    }
}
public class UnitTest {



    @Test
    void testStore() throws ClassNotFoundException {
       Table a = new Table("People",4);
        for (int i=1;i<500;i++)
        {
            a.bplusTree.insert(i,new People(i));
        }

        a.store();

        BplusTree b = a.fetch();
        b.printTree();
    }
}
