package com.kineticcafe.kcpmall.searchIndex;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.kineticcafe.kcpmall.constants.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ImmutableArrayValue;
import org.msgpack.value.ImmutableMapValue;
import org.msgpack.value.ImmutableStringValue;
import org.msgpack.value.MapValue;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableLongValueImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-08-29.
 */
public class IndexManager {


    protected Handler mHandler;
    protected Logger logger = null;
    protected Context mContext;
    protected IndexService mIndexService;
    protected int mLoadingLayout;
    private Map<String, Object> mResult;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    private static final String KEY_TAGS = "tags";
    private static final String KEY_CATEGORIES = "categories";

    public static Map<String, ArrayList<Integer>> sTagsMap = new HashMap<>();
    public static Map<String, ArrayList<Integer>> sCategoriesMap = new HashMap<>();

    private static final String HEADER_KEY_CONTENT_TYPE     = "Content-Type";



    public IndexService getKcpService(){
        ServiceFactory serviceFactory = new ServiceFactory();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_CONTENT_TYPE,       Constants.HEADER_VALUE_CONTENT_TYPE_MESSAGE_PACK);

        if(mIndexService == null) mIndexService = serviceFactory.createRetrofitService(mContext, headers, IndexService.class, HeaderFactory.SEARCH_INDEX_URL_BASE);
        return mIndexService;
    }

    public IndexManager(Context context, int loadingLayout, Handler handler) {
        mContext = context;
        mHandler = handler;
        mLoadingLayout = loadingLayout;
        logger = new Logger(getClass().getName());
    }

    public void downloadSearchIndexes(){
        DownloadBinaryTask downloadImageTask = new DownloadBinaryTask(HeaderFactory.SEARCH_INDEX_URL_BASE + HeaderFactory.getSearchIndexUrl());
        downloadImageTask.execute();
    }

    private void handleState(int state){
        handleState(state, null);
    }

    private void handleState(int state, @Nullable String mode){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = mode;
        switch (state){
            case DOWNLOAD_STARTED:
                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, true);
                break;
            case DOWNLOAD_FAILED:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DOWNLOAD_COMPLETE:
//                ProgressBarWhileDownloading.showProgressDialog(mContext, mLoadingLayout, false);
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }


    public interface IndexService {
        @GET
        Call<Map<String, Object>> getParkings(@Url String url);
    }


    public class DownloadBinaryTask extends AsyncTask<String, Integer, byte[]> {

        private String stringUrl;
        private URL url;
        public DownloadBinaryTask(String url) {
            super();
            this.stringUrl = url;
            try {
                this.url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        protected void onPreExecute() {
        }

        protected byte[] doInBackground(String... urls) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                InputStream stream = url.openStream();
                byte [] buffer = new byte[1024];
                int bytesRead = 0;
                while((bytesRead = stream.read(buffer)) != -1) {
                    outputStream .write(buffer, 0, bytesRead);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byte[] searchIndexBytes = outputStream.toByteArray();
            prepareIndexes(searchIndexBytes);
            return searchIndexBytes;
        }
        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(byte[] result) {
            Message message = new Message();
            message.arg1 = DOWNLOAD_COMPLETE;
            mHandler.sendMessage(message);
        }

    }


    public void prepareIndexes(byte[] result){
        try {
            if(result == null) return;
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(result);
            MapValue mv = (MapValue) unpacker.unpackValue();
            Map<Value, Value> map = mv.map();
            for (Map.Entry<Value, Value> entry : map.entrySet()) {
                ImmutableStringValue key = (ImmutableStringValue) entry.getKey();
                ImmutableMapValue value = (ImmutableMapValue) entry.getValue();

                for (Map.Entry<Value, Value> item : value.entrySet()) {
                    ImmutableStringValue tagName = (ImmutableStringValue) item.getKey();
                    ImmutableArrayValue idArrays = (ImmutableArrayValue) item.getValue();

                    ArrayList<Integer> tagsIds = new ArrayList<>();
                    for(int i = 0; i < idArrays.size(); i++){
                        if(idArrays.get(i) instanceof ImmutableLongValueImpl) {
                            ImmutableLongValueImpl id = (ImmutableLongValueImpl) idArrays.get(i);
                            tagsIds.add(id.asInt());
                        }
                    }
                    if(tagsIds.size() > 0) {
                        if(key.asString().equals(KEY_TAGS)) {
                            sTagsMap.put(tagName.asString(), tagsIds);
                        } else if(key.asString().equals(KEY_CATEGORIES)) {
                            sCategoriesMap.put(tagName.asString(), tagsIds);
                        }
                    }
                }
            }

            unpacker.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}



