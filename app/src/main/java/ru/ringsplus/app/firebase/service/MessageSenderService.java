package ru.ringsplus.app.firebase.service;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MessageSenderService {

    public void sendPost(String title, String body) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    httpURLConnection.setRequestProperty("Accept","application/json");
                    httpURLConnection.setRequestProperty("Authorization", "Bearer AAAAF2YHVtc:APA91bEho4zO8zAEHttlRFsgIvJqWO11SjpL2PaUZImVrayrXOEXY_MbVgMSSPshZ_rSdP3vjblX6_UCYtS3rnakCd21vbSYQvxcrJrnUJZGGa_u2a6pSrYx1lFtg4sSz7l5AV6qZzqf");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    String jsonInit = "{\n" +
                            "    \"to\": \"/topics/all_dev\",\n" +
                            "    \"notification\": {\n" +
                            "        \"title\": \"" + title + "\",\n" +
                            "        \"body\": \"" + body + "\"\n" +
                            "    }\n" +
                            "}";

                    JSONObject jsonParam = new JSONObject(jsonInit);

                    byte[] jsonByteArray = jsonParam.toString().getBytes(StandardCharsets.UTF_8);

                    DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                    try {
                        os.write(jsonByteArray);
                    } finally {
                        os.flush();
                        os.close();
                    }
                } finally {
                    if (httpURLConnection != null)
                        httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
    }

}
