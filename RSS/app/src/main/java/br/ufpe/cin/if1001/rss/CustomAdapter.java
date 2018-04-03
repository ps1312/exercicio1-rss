package br.ufpe.cin.if1001.rss;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<ItemRSS> responseItems;

    public CustomAdapter(Context c, List<ItemRSS> items) {
        this.context = c;
        this.responseItems = items;
    }

    @Override
    public int getCount() {
        return responseItems.size();
    }

    @Override
    public Object getItem(int i) {
        return responseItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        //ItemRSS nao tem id, retorna posicao
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v;
        ItemRSSHolder holder;

        //Se a view nao existe, cria (utilizando o padrao viewholder) e o layout itemlista.xml
        if (view == null) {
            v = LayoutInflater.from(context).inflate(R.layout.itemlista, viewGroup, false);
            holder = new ItemRSSHolder(v);
            v.setTag(holder);
        } else {
            v = view;
            holder = (ItemRSSHolder) v.getTag();
        }

        holder.titulo.setText(responseItems.get(i).getTitle());
        holder.data.setText(responseItems.get(i).getPubDate());

        return v;
    }


}
