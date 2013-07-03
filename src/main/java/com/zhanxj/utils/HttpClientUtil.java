package com.zhanxj.utils;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * HttpClientUtil.java
 *
 * @author catty
 * @version 1.0, Created on 2008/2/20
 */
public class HttpClientUtil {

    protected static HttpClient httpclient = null;
    /**
     * 最大连接数
     */
    public final static int MAX_TOTAL_CONNECTIONS = 800;
    /**
     * 获取连接的最大等待时间
     */
    public final static int WAIT_TIMEOUT = 60000;
    /**
     * 每个路由最大连接数
     */
    public final static int MAX_ROUTE_CONNECTIONS = 400;
    /**
     * 连接超时时间
     */
    public final static int CONNECT_TIMEOUT = 10000;
    /**
     * 读取超时时间
     */
    public final static int READ_TIMEOUT = 10000;
    protected static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7";
    private static HttpParams httpParams;
    private static ClientConnectionManager connectionManager;

    static {
        httpParams = new BasicHttpParams();
        // 设置最大连接数  
        ConnManagerParams.setMaxTotalConnections(httpParams, MAX_TOTAL_CONNECTIONS);
        // 设置获取连接的最大等待时间  
        ConnManagerParams.setTimeout(httpParams, WAIT_TIMEOUT);
        // 设置每个路由最大连接数  
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        // 设置连接超时时间  
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
        // 设置读取超时时间  
        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        connectionManager = new ThreadSafeClientConnManager(httpParams, registry);
        if (httpclient == null) {
            httpclient = new DefaultHttpClient(connectionManager, httpParams);
        }
    }

    /**
     * <pre>下載後回傳Inputstream</pre>
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static InputStream downloadAsStream(String url) throws Exception {
        InputStream is = (InputStream) download(url, null, null, false);
        return is;
    }

    /**
     * <pre>下載後儲存到File</pre>
     *
     * @param url
     * @param saveFile
     * @throws Exception
     */
    public static void download(String url, File saveFile) throws Exception {
        download(url, saveFile, null, false);
    }

    /**
     * <pre>下載</pre>
     *
     * @param url
     * @param saveFile
     * @param params
     * @param isPost
     * @return 如果saveFile==null則回傳inputstream, 否則回傳saveFile
     * @throws Exception
     */
    @SuppressWarnings("finally")
    public static Object download(final String url, final File saveFile, final Map<String, String> params,
                                  final boolean isPost) throws Exception {

        boolean saveToFile = saveFile != null;

        // check dir exist ??
        if (saveToFile && saveFile.getParentFile().exists() == false) {
            saveFile.getParentFile().mkdirs();
        }

        Exception err = null;
        HttpRequestBase request = null;
        HttpResponse response = null;
        HttpEntity entity = null;
        FileOutputStream fos = null;
        Object result = null;

        try {
            // create request
            if (isPost) {
                request = new HttpPost(url);
            } else {
                request = new HttpGet(url);
            }

            // add header & params
            addHeaderAndParams(request, params);

            // connect
            response = httpclient.execute(request);
            entity = response.getEntity();
            entity = new BufferedHttpEntity(entity);

            // get result
            if (saveToFile) {// save to disk
                fos = new FileOutputStream(saveFile);
                IOUtils.copy(entity.getContent(), fos);
                result = saveFile;
            } else { // warp to inpustream
                result = new BufferedInputStream(entity.getContent());
            }

        } catch (Exception e) {
            err = e;
        } finally {
            // close
            IOUtils.closeQuietly(fos);
            // clear
            request = null;
            response = null;
            entity = null;
            if (err != null) {
                throw err;
            }
            return result;
        }

    }

    protected static void addHeaderAndParams(final HttpRequestBase request, final Map<String, String> params) {
        // add default header
        request.addHeader("User-Agent", userAgent);

        // add params
        if (params != null) {

            // map --> HttpParams
            BasicHttpParams myParams = new BasicHttpParams();
            for (String key : params.keySet()) {
                myParams.setParameter(key, params.get(key));
            }

            request.setParams(myParams);
        }
    }

    public static HttpClient getHttpclient() {
        return httpclient;
    }

    public static void setHttpclient(HttpClient httpclient) {
        HttpClientUtil.httpclient = httpclient;
    }


    public static String getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(String userAgent) {
        HttpClientUtil.userAgent = userAgent;
    }

}
