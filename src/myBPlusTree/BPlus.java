package myBPlusTree;

public interface BPlus {
    public Object get(Comparable key);   //查询
    public void remove(Comparable key);    //移除
    public void insert(Comparable key, Object obj); //插入或者更新，如果已经存在，就更新，否则插入
    public void set(Comparable key,Object obj);

}
