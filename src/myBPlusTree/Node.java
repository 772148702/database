package myBPlusTree;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
public class Node implements Serializable{
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
    //根据键值来进行查找。
    Object search(Comparable key)
    {
        if(isLeaf){
            for(Entry entry:entries){
                if(key.compareTo(entry.getKey())==0)
                {
                    return entry.getValue();
                }
            }
        }

        for(Entry entry:entries)
        {
            if(key.compareTo(entry.getKey())<=0) {
                int i = entries.indexOf(entry);
                return children.get(i).search(key);
            }
        }
        return children.get(children.size()-1).search(key);
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
            if(this.previous!=null)
            this.previous.next = left;
            left.next = right;
            right.previous = left;
            right.next = this.next;
            if(this.next!=null)
            this.next.previous = right;
            if(this.previous==null) {
                tree.head = left;
            }
            int l = entries.size()/2+entries.size()%2;
            int r = entries.size()/2;

            for (int i=0;i<l;i++) {
                left.entries.add(this.entries.get(i));

            }
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
                update(parent);
               Node tmp =  checkOverflow(parent, tree);
               //right.parent = left.parent = tmp;

            } else
            {
                Node root = new Node(false,true);
                tree.root = root;
                left.parent = root;
                right.parent = root;
                root.children.add(left);

                root.children.add(right);

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
        if(node==null) return;
        if(node.entries!=null)
        node.entries.clear();
        for(Node son:node.children)
        {
            int lo = son.entries.size()-1;
            node.entries.add(son.entries.get(lo));
        }

    }

    Node checkOverflow(Node node,BplusTree tree)
    {
        if(node.isRoot==true&&node.children.size()>tree.order)
        {
            Node left = new Node(false,false);
            Node right = new Node(false,false);
            Node root = new Node(false,true);
            int l = node.entries.size()/2+node.entries.size()%2;
            int r = node.entries.size()/2;


            for (int i=0;i<l;i++) {
                left.children.add(node.children.get(i));
                node.children.get(i).parent = left;
            }
            for (int i=0;i<r;i++) {
                right.children.add(node.children.get(l + i));
                node.children.get(i+l).parent = right;
            }
          updateNoRecursive(left);
          updateNoRecursive(right);
         //   int lo = node.parent.children.indexOf(node);
          //  node.parent.children.remove(node);
            root.children.add(left);
            right.parent = root;
            root.children.add(right);
            left.parent = root;
            node.release();
            updateNoRecursive(root);
            tree.root = root;
            return root;
        }

        if(node.children.size()>tree.order)
        {
            Node left = new Node(false,false);
            Node right = new Node(false,false);

            int l =  node.entries.size()/2+ node.entries.size()%2;
            int r =  node.entries.size()/2;

            for (int i=0;i<l;i++) {
                left.children.add(node.children.get(i));
                node.children.get(i).parent = left;
            }
            for (int i=0;i<r;i++) {
                right.children.add(node.children.get(l + i));
                node.children.get(i+l).parent = right;
            }
            updateNoRecursive(left);
            updateNoRecursive(right);
            int lo = node.parent.children.indexOf(node);
            node.parent.children.remove(node);
            node.parent.children.add(lo,right);
            right.parent = node.parent;
            node.parent.children.add(lo,left);
            left.parent = node.parent;
            updateNoRecursive(node.parent);
            checkOverflow(node.parent,tree);
            return node;
        }
    return node;
    }

    @Override
    public String toString() {
        if(this.parent!=null)
        return "Node{" +
                "this"+this.hashCode()+
                "isRoot=" + isRoot +
                ", isLeaf=" + isLeaf +
                ",parent="+parent.hashCode()+
                ", entries=" + entries +

                '}';
        else
        return "Node{" +
                "this"+this.hashCode()+
                ",isRoot=" + isRoot +
                ", isLeaf=" + isLeaf +
             //   ",parent="+parent+
                ", entries=" + entries +

                '}';

    }

    Object remove(Comparable key,BplusTree tree)
    {
        /**
         * 叶子节点
         * 1.如果当前节点中的元素的个数大于 [oder/2]  就直接移除
         * 2.如果左节点的元素的个数大于[order/2],则可以从左节点中借到 最大的元素
         * 通过父节点来索引左节点。 然后跟新父节点的值----这个要递归地调用
         *
         * 3 右节点与2相同
         * 4 如果都不满足要求，我们可以合并左节点，成为一个新的节点，然后更新父节点，注意是下溢。因为不同与2.3这个是子节点的数目变少了。
         */
        if(isLeaf){
            if(entries.size()>tree.order/2+tree.order%2||isRoot)
            {
                for(Entry entry:entries){
                    if(key.compareTo(entry.getKey())==0)
                    {
                        Object tmp = entry.getValue();
                        entries.remove(entry);
                        if(!isRoot)
                        update(parent);
                        return  tmp;
                    }
                }
            } else
            {
            //移除节点
                for(Entry entry:entries) {
                    if (key.compareTo(entry.getKey()) == 0) {
                        Object tmp = entry.getValue();
                        entries.remove(entry);
                        break;
                    }
                }

                int index = parent.children.indexOf(this);
                Node left;
                //可以从左边的来借。
                if(index>0)
                {
                    left = parent.children.get(index-1);
                    if(left.entries.size()>tree.order/2+tree.order%2)
                    {
                        Entry obj = left.entries.get(left.entries.size() - 1);
                        left.entries.remove(left.entries.size() - 1);
                        this.entries.add(0, obj);
                        update(this.parent);
                        return obj.getValue();
                    }
                }

                if(index<parent.children.size()-1)
                {
                    Node right = parent.children.get(index+1);
                    if(right.entries.size()>tree.order/2+tree.order%2)
                    {
                        Entry obj = right.entries.get(0);
                        right.entries.remove(0);
                        this.entries.add(obj);
                        update(this.parent);
                        return obj.getValue();
                    }
                }
                //左右的数目都不够

                //合并左边的的节点，左边存在节点

                if(index>0)
                {
                    left = parent.children.get(index-1);
                    for(Entry entry:entries)
                    {
                        left.entries.add(entry);
                    }
                    //节点链表
                    left.next = this.next;
                    if(this.next!=null)
                    this.next.previous = left;

                    //断绝关系
                    parent.children.remove(this);
                    this.parent = null;
                    //非循环下溢
                    updateNoRecursive(left.parent);
                    if(left.parent.isRoot&&left.parent.children.size()==1)
                    {
                        tree.root = left;
                        left.isRoot = true;
                        return null;
                    }
                    updateNoRecursive(left.parent);
                    flowdown(left.parent,tree);
                    return null;
                }
                if(index<parent.children.size()-1)
                {

                    Node right = parent.children.get(index-1);
                    for(Entry entry:entries)
                    {
                        right.entries.add(entry);
                    }
                    //节点链表
                    right.previous = this.previous;
                    if(this.previous!=null)
                        this.previous.next = right;

                    //断绝关系
                    parent.children.remove(this);
                    this.parent = null;
                    //非循环下溢
                    updateNoRecursive(right.parent);
                    if(right.parent.isRoot&&right.parent.children.size()==1)
                    {
                        tree.root = right;
                        right.isRoot = true;
                        return null;
                    }
                    updateNoRecursive(right.parent);
                    flowdown(right.parent,tree);
                    return null;
                }

                tree.root  = this;
                this.isRoot = true;
                this.parent = null;
                return null;
            }


        }

        //内部节点

        for(Entry entry:entries)
        {
            if(key.compareTo(entry.getKey())<=0) {
                int i = entries.indexOf(entry);
                return children.get(i).remove(key,tree);
            }
        }
        return children.get(children.size()-1).remove(key,tree);



    }

    void  flowdown(Node node,BplusTree tree)
    {
        //*内部节点也就是非叶子节点，因为数目的关系来调整拓扑关系。
        int m = node.children.size();
        if(m<tree.order/2+tree.order%2)
        {

            int index = node.parent.children.indexOf(node);
            Node left;
            //可以从左边的来借。
            if(index>0)
            {
                left = node.parent.children.get(index-1);
                if(left.children.size()>tree.order/2+tree.order%2)
                {
                    Node obj = left.children.get(left.children.size() - 1);
                    obj.parent = node;
                    left.children.remove(left.children.size() - 1);

                   // left.parent.children.remove(obj);
                    node.children.add(0, obj);
                    updateNoRecursive(left);
                    update(node);

                    return;
                }
            }
            //可以从右边来借
            if(index<node.parent.children.size()-1)
            {
                Node right = node.parent.children.get(index+1);
                if(right.children.size()>tree.order/2+tree.order%2)
                {
                    Node obj = right.children.get(0);
                    obj.parent = node;
                    right.children.remove(0);
                    right.parent.children.remove(obj);
                    updateNoRecursive(right);
                    node.children.add( obj);
                    update(node.parent);
                    return;
                }
            }
            //左右的数目都不够

            //合并左边的的节点，左边存在节点

            if(index>0)
            {
                left = node.parent.children.get(index-1);
                for(Node child:node.children)
                {
                    left.children.add(child);
                    child.parent = left;
                }
                //节点链表

                //断绝关系
                node.parent.children.remove(node);
                if(node.parent.children.size()==1)
                {
                    tree.root = left;
                    node.parent.children.clear();
                    node.parent = null;
                    updateNoRecursive(left);
                    return;
                }
                node = null;
                //非循环下溢
                updateNoRecursive(left);
                flowdown(left.parent,tree);
                return;
            }

            if(index<node.parent.children.size()-1)
            {

                Node right = node.parent.children.get(index-1);
                for(Node child: node.children)
                {
                    right.children.add(child);
                    child.parent = right;
                }
                //节点链表
//                right.previous = this.previous;
//                if(this.previous!=null)
//                    this.previous.next = right;

                //断绝关系

                node.parent.children.remove(node);
                if(node.parent.children.size()==1)
                {
                    tree.root = right;
                    node.parent.children.clear();
                    node.parent = null;
                    return;
                }
                    node = null;

                //非循环下溢
                updateNoRecursive(right);
                flowdown(right.parent,tree);
                return;

            }
//
//            node.isRoot = true;
//            tree.root = node;
//            return ;
        }

    }


    void release()
    {
        children = null;
        entries = null;
        next = null;
        previous  = null;
    }
}
