package indi.augusttheodor.aurora.tools;

import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import indi.augusttheodor.aurora.inter.AdapterItems;

public class RecyclerViewAdapter<T extends RecyclerView.ViewHolder,V> extends RecyclerView.Adapter<T> {

    public AdapterItems<V> item; //绑定的Item组
    public Fragment fragment;

    public RecyclerViewAdapter(AdapterItems<V> item, Fragment fragment) {
        this.item = item;
        this.fragment = fragment;
    }
    
    
    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) { }


    @Override //这要自己写
    public int getItemViewType(int position) {
        return 0;
    }

    @CallSuper
    @Override
    public int getItemCount() { return this.item.getItems().size(); }
}
