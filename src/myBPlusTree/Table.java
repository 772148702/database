package myBPlusTree;

import java.io.*;

public class Table {
    BplusTree bplusTree;
    String name;
    Table(String tmp,int order)
    {
         name = tmp;
        bplusTree = new BplusTree(order);
    }

    Table(String tmp)
    {
         name = tmp;
        bplusTree = new BplusTree(20);
    }

    void store()  {
        try {
            File file = new File(name);
            if(!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream os =  new ObjectOutputStream(fs);
            os.writeObject(bplusTree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    BplusTree fetch() throws ClassNotFoundException {
        try {
            FileInputStream fs = new FileInputStream(name);
            ObjectInputStream os =  new ObjectInputStream(fs);
            bplusTree = (BplusTree) os.readObject();
            return bplusTree;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
