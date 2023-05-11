package indi.augusttheodor.aurora.inter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public interface HttpInterface{
    void onResponse(Call call, Response response, String tag) throws IOException; //处理响应的
}
