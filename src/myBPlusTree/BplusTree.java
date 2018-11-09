package myBPlusTree;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BplusTree {

    protected  Node root;

    /** 阶数，M值 */
    protected int order;

    /** 叶子节点的链表头*/
    protected Node head;


    void insert(Comparable key,Object obj)
    {
        root.insertNode(key,obj,this);
    }

  Object search(Comparable key)
  {
      return root.search(key);
  }
    BplusTree(int order)
    {
        this.order = order;
        root = new Node(true,true);
        head = root;

    }
    /*以层次遍历的顺序输出Nde*/
    void printTree()
    {
        Queue<Node>  q = new LinkedList<>();
        q.add(root);
        while(!q.isEmpty())
        {
            int m = q.size();

            for (int i=0;i<m;i++)
            {
                Node tmp = q.poll();
                System.out.println(tmp);
                if(!tmp.isLeaf)
                {
                    for (Node child : tmp.children)
                    {
                        q.add(child);
                    }
                }
            }
        }
    }

    void printList()
    {
        Node tmp = head;
        StringBuffer  sb = new StringBuffer();
        while(tmp!=null)
        {
            sb.append(tmp+"-->");
            tmp = tmp.next;
        }
        System.out.println(sb.toString());
    }
}
