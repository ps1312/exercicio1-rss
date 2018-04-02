package br.ufpe.cin.if1001.rss;

import android.view.View;
import android.widget.TextView;

public class ItemRSSHolder {

    //View holder usado para os items
    TextView titulo;
    TextView dados;

    public ItemRSSHolder(View row){
        this.titulo = (TextView) row.findViewById(R.id.item_titulo);
        this.dados = (TextView) row.findViewById(R.id.item_data);
    }
}
