package lv.bestan.android.wear.expensestracker.utils;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stan on 17/10/2014.
 */
public class EmailUtils {

    public static void sendEmail(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://api.sendgrid.com/api/mail.send.json");
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                    nameValuePairs.add(new BasicNameValuePair("api_user", "bestan"));
                    nameValuePairs.add(new BasicNameValuePair("api_key", "C2sEws3FIUdEK83GBD5a"));
                    nameValuePairs.add(new BasicNameValuePair("to", "bestan93@gmail.com"));
                    nameValuePairs.add(new BasicNameValuePair("toname", "bestan"));
                    nameValuePairs.add(new BasicNameValuePair("subject", "Wear expenses crash!"));
                    nameValuePairs.add(new BasicNameValuePair("text", text));
                    nameValuePairs.add(new BasicNameValuePair("from", "bestan93@gmail.com"));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = client.execute(post);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        Log.d("test", line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

}
