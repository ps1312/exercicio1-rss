package br.ufpe.cin.if1001.rss;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    //private final String RSS_FEED = "http://leopoldomt.com/if1001/g1brasil.xml";

    //OUTROS LINKS PARA TESTAR...
    //http://rss.cnn.com/rss/edition.rss
    //http://pox.globo.com/rss/g1/brasil/
    //http://pox.globo.com/rss/g1/ciencia-e-saude/
    //http://pox.globo.com/rss/g1/tecnologia/

    //use ListView ao invés de TextView - deixe o atributo com o mesmo nome
    private ListView conteudoRSS;
    private List<ItemRSS> parsedResponse;
    private SharedPreferences sharedPref;

    // Usado no parse simples
    //private List<String> parsedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //use ListView ao invés de TextView - deixe o ID no layout XML com o mesmo nome conteudoRSS
        //isso vai exigir o processamento do XML baixado da internet usando o ParserRSS

        //No primeiro uso da aplicacao o valor de rssfeed padrao eh carregado
        PreferenceManager.setDefaultValues(this, R.xml.preferencias, false);

        //Load nas preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        conteudoRSS = (ListView) findViewById(R.id.conteudoRSS);

        //Se o user pressionar alguma news..
        conteudoRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Se quiser abrir no browser externo
//
//                Intent seeOnBrowser = new Intent(Intent.ACTION_VIEW);
//                ItemRSS item = (ItemRSS) conteudoRSS.getItemAtPosition(i);
//                Uri itemUrl = Uri.parse(item.getLink());
//                seeOnBrowser.setData(itemUrl);
//                if (seeOnBrowser.resolveActivity(getPackageManager()) != null) {
//                    startActivity(seeOnBrowser);
//                }
//
//              O app vai usar webview
                Intent seeOnWebview = new Intent(getApplicationContext(), WebviewActivity.class);
                ItemRSS item = (ItemRSS) conteudoRSS.getItemAtPosition(i);
                seeOnWebview.putExtra("url", item.getLink());
                if (seeOnWebview.resolveActivity(getPackageManager()) != null) {
                    startActivity(seeOnWebview);
                }
            }
        });
    }

    //Menu na action bar para mudança de url do feed
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se a opcao escolhida for para mudar o feed (unica opcao atualmente)
        if (item.getItemId() == R.id.change_settings) {
            startActivity(new Intent(getApplicationContext(), PreferenciasActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Pegar a string com a key rssfeed na sharedpreference e executa a asynctask
        String RSS_FEED = sharedPref.getString("rssfeed", "WWWWW");
        new CarregaRSStask().execute(RSS_FEED);
    }

    private class CarregaRSStask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String conteudo = "provavelmente deu erro...";
            try {
                conteudo = getRssFeed(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conteudo;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //ajuste para usar uma ListView
            //o layout XML a ser utilizado esta em res/layout/itemlista.xml
            try {
//                Usando o parser simples para teste
//                parsedResponse = ParserRSS.parserSimples(s);
//                conteudoRSS.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, parsedResponse));

                //Real deal
                conteudoRSS.setVisibility(View.VISIBLE);    //Deixar listview visivel quando tudo funcionar direito
                parsedResponse = ParserRSS.parse(s);
                conteudoRSS.setAdapter(new CustomAdapter(getApplicationContext(), parsedResponse));

            } catch (XmlPullParserException e) {
                //Esconder listview do usuario para chamar atencao para o erro
                Toast.makeText(getApplicationContext(), "Ocorreu algum erro no url", Toast.LENGTH_SHORT).show();
                conteudoRSS.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}
