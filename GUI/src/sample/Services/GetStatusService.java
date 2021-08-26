package sample.Services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStatusService extends Service<Boolean> {
    private final String ip;
    private final String port;

    public GetStatusService(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                Boolean result=false;

                try{
                    URL url=new URL("http://"+ip+":"+port+"/getStatus");

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(4500);
                    int status = con.getResponseCode();

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    if(status==HttpURLConnection.HTTP_OK){
                        try{
                            String resultString=content.toString();
                            if(resultString.equals("1")){
                                result=true;
                            }else if(resultString.equals("0")){
                                result=false;
                            }
                        }
                        catch (Exception e){
                            return null;
                        }
                    }else{
                            return null;
                    }
                }
                catch (Exception e){
                    return null;
                }

                return result;
            }
        };
    }
}
