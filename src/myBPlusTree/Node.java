package myBPlusTree;

import java.util.AbstractMap.SimpleEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
public class Node {
    boolean isRoot;
    boolean isLeaf;

    protected Node parent;

    /** 叶节点的前节点*/
    protected Node previous;

    /** 叶节点的后节点*/
    protected  Node next;

    /** 节点的关键字 */
    /**
     * 按照从小到大的关系进行排列，父节点取子节点中最大的关键字作为键值。
     */
    protected List<Entry<Comparable, Object>> entries;

    /** 子节点 */
    protected List<Node> children;



    public Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        entries = new ArrayList<Map.Entry<Comparable, Object>>();

        if (!isLeaf) {
            children = new ArrayList<Node>();
        }
    }

    public Node(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }
    // g更新父节点中的keys
    void update(Node node)
    {

        node.entries.clear();
        for(Node son:node.children)
        {
            int lo = son.entries.size()-1;
            node.entries.add(son.entries.get(lo));
        }
        if(node.isRoot==false)
        update(node.parent);
    }

    void insertNode(Comparable key,Object obj,BplusTree tree)
    {
        //在树的叶子节点的部位，可以直接插入
        if(isLeaf&& entries.size()<tree.order)
        {
            for(Entry<Comparable,Object> entity:entries)
            {
                if(entity.getKey().compareTo(key)>0)
                {
                   int i =  entries.indexOf(entity);
                    entries.add(i,new SimpleEntry(key,obj) );
                    return;
                }
            }

            entries.add(new SimpleEntry(key,obj));
            //是键值比较大的值了，要跟新父节点。
            if(this.isRoot==false)
            update(this.parent);
            return;
        }
        //在叶子节点部分但是需要分裂才能够插入。
        if(isLeaf&&entries.size()>=tree.order)
        {
            boolean flag = false;
            for(Entry<Comparable,Object> entity:entries)
            {
                if(entity.getKey().compareTo(key)>0)
                {
                    flag  = true;
                    int i =  entries.indexOf(entity);
                    entries.add(i,new SimpleEntry(key,obj) );

                }
            }
            if(flag==false)   entries.add(new SimpleEntry(key,obj));
            Node left = new Node(true,false);
            Node right = new Node(true,false);
            left.previous = this.previous;
            left.next = right;
            right.previous = left;
            right.next = this.next;

            int l = entries.size()/2+entries.size()%2;
            int r = entries.size()/2;

            for (int i=0;i<l;i++)
                left.entries.add(this.entries.get(i));
            for (int i=0;i<r;i++)
                right.entries.add(this.entries.get(l+i));

            if(isRoot==false)
            {
                int lo = parent.children.indexOf(this);
                parent.children.remove(this);
                parent.children.add(lo, right);
                right.parent = parent;
                parent.children.add(lo, left);
                left.parent = parent;
                updateNoRecursive(parent);
                checkOverflow(parent, tree);
            } else
            {
                Node root = new Node(false,true);
                tree.root = root;
                root.children.add(left);
                right.parent = root;
                root.children.add(right);
                left.parent = root;
                root.parent = null;
                updateNoRecursive(root);
            }
           return;
        }
        if(isLeaf==false)
        {
            boolean flag = false;
            Node tmp = null;
            for(Entry<Comparable,Object> entity:entries)
            {
                if(entity.getKey().compareTo(key)>0) {
                    flag = true;
                    int i = entries.indexOf(entity);
                    tmp = children.get(i);


                }
            }
            if(flag==false)
            {
                tmp = children.get(entries.size()-1);
            }
            tmp.insertNode(key,obj,tree);
        }
    }
    void updateNoRecursive(Node node)
    {

        node.entries.clear();
        for(Node son:node.children)
        {
            int lo = son.entries.size()-1;
            node.entries.add(son.entries.get(lo));
        }

    }

    void checkOverflow(Node node,BplusTree tree)
    {
        if(node.isRoot==true&&node.entries.size()>tree.order)
        {
            Node left = new Node(false,false);
            Node right = new Node(false,false);
            Node root = new Node(false,true);
            int l = tree.order/2+tree.order%2;
            int r = tree.order/2;


            for (int i=0;i<l;i++)
                left.entries.add(node.entries.get(i));
            for (int i=0;i<r;i++)
                right.entries.add(node.entries.get(l+i));

            int lo = node.parent.children.indexOf(node);
          //  node.parent.children.remove(node);
            root.children.add(lo,right);
            right.parent = root;
            root.children.add(lo,left);
            left.parent = root;

            updateNoRecursive(node.parent);
            tree.head = root;
        }

        if(node.entries.size()>tree.order)
        {
            Node left = new Node(false,false);
            Node right = new Node(false,false);

            int l = tree.order/2+tree.order%2;
            int r = tree.order/2;

            for (int i=0;i<l;i++)
                left.entries.add(node.entries.get(i));
            for (int i=0;i<r;i++)
                right.entries.add(node.entries.get(l+i));

            int lo = node.parent.children.indexOf(node);
            node.parent.children.remove(node);
            node.parent.children.add(lo,right);
            right.parent = node.parent;
            node.parent.children.add(lo,left);
            left.parent = node.parent;
            updateNoRecursive(node.parent);
            checkOverflow(node.parent,tree);
        }

    }

    @Override
    public String toString() {
        return "Node{" +
                "isRoot=" + isRoot +
                ", isLeaf=" + isLeaf +

                ", entries=" + entries +
                ", children=" + children +
                '}';
    }

    void release()
    {
        children = null;
        entries = null;
        next = null;
        previous  = null;
    }
}