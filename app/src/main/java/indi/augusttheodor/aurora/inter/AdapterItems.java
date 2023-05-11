package indi.augusttheodor.aurora.inter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterItems<T> { //小小泛型类

    private List<T> items;

    public AdapterItems(){ this.items=new ArrayList<T>(); }

    public void clear(){ this.items.clear(); }

    public void add(T o){
        this.items.add(o);
    }

    public void add(int m,@NonNull T o){this.items.add(m,o);}

    public List<T> getItems(){
        return this.items;
    }

}
