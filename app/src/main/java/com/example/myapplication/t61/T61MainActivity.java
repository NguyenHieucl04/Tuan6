package com.example.myapplication.t61;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class T61MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProductAdapter adapter;
    private List<Product> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_t61_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new FetchProductsTask().execute();

    }
    private class FetchProductsTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response=new StringBuilder();
            try{
                URL url=new URL("https://hungnttg.github.io/shopgiay.json");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line="";
                while((line=reader.readLine())!=null){
                    response.append(line);
                }
            } catch (MalformedURLException e){
                throw new RuntimeException(e);
            } catch (IOException e){
                throw new RuntimeException();
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null && !s.isEmpty()){
                try {
                    JSONObject json=new JSONObject();
                    JSONArray productArray=json.getJSONArray("products");
                    for(int i=0;i<productArray.length();i++){
                        JSONObject productObject=productArray.getJSONObject(i);
                        String styleID=productObject.getString("styleid");
                        String brand=productObject.getString("brands_filter_facet");
                        String price=productObject.getString("price");
                        String additionalInfo=productObject.getString("product_additional_info");
                        String searchImage=productObject.getString("search_image");
                        Product product=new Product(styleID,brand,price,additionalInfo,searchImage);
                        productList.add(product);

                    }
                    adapter.notifyDataSetChanged();
                }
                catch (JSONException e){
                    throw new RuntimeException(e);
                }
            }
            else{
                Toast.makeText(T61MainActivity.this,"Failed to fetch products!", Toast.LENGTH_LONG).show();
            }
        }
    }
}