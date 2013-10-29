package com.example.clx.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.example.clxapp.CLXApplication;

/**
 * 有关http辅助操作类
 * 
 * @author teeker_bin
 * 
 */
public class HttpUrlHelper {
	public static final String TAG = "HttpUrlHelper";
	public static final String strUrl = "http://clx.jieme.com";// 服务器地址

	/**
	 * 获取请求服务端的方法，进行流操作，并接收服务器端返回的相关数据
	 * 
	 * get 提交方式
	 * 
	 * @param urlStr
	 *            URL 链接
	 * @return
	 * @throws IOException
	 */
	public static String getUrlData(String urlStr) {
		String strResult = "";
		try {
			HttpGet httpRequest = new HttpGet(urlStr);
			HttpClient httpclient = new DefaultHttpClient();

			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000); // 6秒
			// 判断是否成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
				return strResult;
			} else {
				return strResult;
			}
		} catch (Exception e) {
			Logger.error("HttpUrlHelper.getUrlData", e);

			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * POST 靖求方式
	 * 
	 * @param urlStr
	 *            URL 链接
	 * @param pairs
	 *            传递的参数
	 * @return
	 */
	public static String postUrlData(String urlStr, List<NameValuePair> pairs) {
		String strPostResult = "链接失败";
		// 建立HTTPost对象
		HttpPost httpPost = new HttpPost(urlStr);
		try {

			// 判断传入的参数是否为空
			if (pairs != null) {
				HttpEntity httpentity = new UrlEncodedFormEntity(pairs, "UTF-8");
				httpPost.setEntity(httpentity);

			}
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);
			try {
				HttpResponse httpResponse = httpclient.execute(httpPost);
				// 判断是否成功
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					strPostResult = EntityUtils.toString(httpResponse
							.getEntity());
					Logger.debug(HttpUrlHelper.class, "strPostResult:"
							+ strPostResult);
					return strPostResult;
				} else {
					Logger.debug(HttpUrlHelper.class, "错误码："
							+ httpResponse.getStatusLine().getStatusCode());
					return strPostResult;
				}
			} catch (ConnectTimeoutException e) {// 超时
				Logger.debug(HttpUrlHelper.class, "http 连接超时 =" + e.toString());
				Logger.error("HttpUrlHelper.postUrlData", e);

				return strPostResult;
			}
		} catch (Exception e) {
			Logger.debug(HttpUrlHelper.class, "http 请求 =" + e.toString());
			Logger.error("HttpUrlHelper.postUrlData", e);

			return strPostResult;
		}
	}

	// 创建HttpClient实例
	private static HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}

	// 对外提供HttpClient实例
	public static HttpClient getHttpClient() {
		HttpClient httpClient = createHttpClient();
		return httpClient;
	}

	/**
	 * 组织请求数据
	 * 
	 * @param map
	 *            需要传的参数
	 * @param url
	 *            要访问的url
	 * @return
	 */
	public static String postData(Map<String, Object> map, String url) {
		Logger.debug(HttpUrlHelper.class, "---------");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<?> mapite = map.entrySet().iterator();
		while (mapite.hasNext()) {// 循环遍历需要传递给服务器的请求参数
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) mapite.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			params.add(new BasicNameValuePair(key.toString(), value.toString()));
		}
		String strPostJson = HttpUrlHelper.postUrlData(HttpUrlHelper.strUrl
				+ url, params);
		return strPostJson;
	}

	/**
	 * 上传图片接口
	 * 
	 * @param url
	 *            服务器地址
	 * @param file
	 *            上传的图片文件
	 * @param cid
	 *            圈子id
	 * @param uid
	 *            用户id
	 * @param gid
	 *            记录id
	 * @param token
	 * @return
	 */
	public static String postDataFile(String url, File file, String cid,
			String uid, String gid, String token) {
		String strPostResult = "链接失败";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		MultipartEntity mpEntity = new MultipartEntity();
		try {
			FileBody fileBody = new FileBody(file);
			mpEntity.addPart("img", fileBody);
			mpEntity.addPart("cid", new StringBody(cid));
			mpEntity.addPart("uid", new StringBody(uid));
			mpEntity.addPart("token", new StringBody(token));
			mpEntity.addPart("gid", new StringBody(gid));
			post.setEntity(mpEntity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strPostResult = EntityUtils.toString(response.getEntity(),
						"utf-8");
				Logger.debug(HttpUrlHelper.class, "strPostResult:"
						+ strPostResult);
				return strPostResult;
			} else {
				return strPostResult;
			}
		} catch (Exception e) {
			Logger.error("HttpUrlHelper.postDataFile", e);

		} finally {
			if (mpEntity != null) {
				try {
					mpEntity.consumeContent();
				} catch (UnsupportedOperationException e) {
					Logger.error("HttpUrlHelper.postDataFile", e);

					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Logger.error("HttpUrlHelper.postDataFile", e);

					e.printStackTrace();
				}
			}
			client.getConnectionManager().shutdown();
		}
		return strPostResult;

	}

	/**
	 * 
	 * @Description 检查网络状态
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) CLXApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
