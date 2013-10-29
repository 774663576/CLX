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
 * �й�http����������
 * 
 * @author teeker_bin
 * 
 */
public class HttpUrlHelper {
	public static final String TAG = "HttpUrlHelper";
	public static final String strUrl = "http://clx.jieme.com";// ��������ַ

	/**
	 * ��ȡ�������˵ķ����������������������շ������˷��ص��������
	 * 
	 * get �ύ��ʽ
	 * 
	 * @param urlStr
	 *            URL ����
	 * @return
	 * @throws IOException
	 */
	public static String getUrlData(String urlStr) {
		String strResult = "";
		try {
			HttpGet httpRequest = new HttpGet(urlStr);
			HttpClient httpclient = new DefaultHttpClient();

			// ����ʱ
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			// ��ȡ��ʱ
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000); // 6��
			// �ж��Ƿ�ɹ�
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
	 * POST ����ʽ
	 * 
	 * @param urlStr
	 *            URL ����
	 * @param pairs
	 *            ���ݵĲ���
	 * @return
	 */
	public static String postUrlData(String urlStr, List<NameValuePair> pairs) {
		String strPostResult = "����ʧ��";
		// ����HTTPost����
		HttpPost httpPost = new HttpPost(urlStr);
		try {

			// �жϴ���Ĳ����Ƿ�Ϊ��
			if (pairs != null) {
				HttpEntity httpentity = new UrlEncodedFormEntity(pairs, "UTF-8");
				httpPost.setEntity(httpentity);

			}
			HttpClient httpclient = new DefaultHttpClient();
			// ����ʱ
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			// ��ȡ��ʱ
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10000);
			try {
				HttpResponse httpResponse = httpclient.execute(httpPost);
				// �ж��Ƿ�ɹ�
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					strPostResult = EntityUtils.toString(httpResponse
							.getEntity());
					Logger.debug(HttpUrlHelper.class, "strPostResult:"
							+ strPostResult);
					return strPostResult;
				} else {
					Logger.debug(HttpUrlHelper.class, "�����룺"
							+ httpResponse.getStatusLine().getStatusCode());
					return strPostResult;
				}
			} catch (ConnectTimeoutException e) {// ��ʱ
				Logger.debug(HttpUrlHelper.class, "http ���ӳ�ʱ =" + e.toString());
				Logger.error("HttpUrlHelper.postUrlData", e);

				return strPostResult;
			}
		} catch (Exception e) {
			Logger.debug(HttpUrlHelper.class, "http ���� =" + e.toString());
			Logger.error("HttpUrlHelper.postUrlData", e);

			return strPostResult;
		}
	}

	// ����HttpClientʵ��
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

	// �����ṩHttpClientʵ��
	public static HttpClient getHttpClient() {
		HttpClient httpClient = createHttpClient();
		return httpClient;
	}

	/**
	 * ��֯��������
	 * 
	 * @param map
	 *            ��Ҫ���Ĳ���
	 * @param url
	 *            Ҫ���ʵ�url
	 * @return
	 */
	public static String postData(Map<String, Object> map, String url) {
		Logger.debug(HttpUrlHelper.class, "---------");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<?> mapite = map.entrySet().iterator();
		while (mapite.hasNext()) {// ѭ��������Ҫ���ݸ����������������
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
	 * �ϴ�ͼƬ�ӿ�
	 * 
	 * @param url
	 *            ��������ַ
	 * @param file
	 *            �ϴ���ͼƬ�ļ�
	 * @param cid
	 *            Ȧ��id
	 * @param uid
	 *            �û�id
	 * @param gid
	 *            ��¼id
	 * @param token
	 * @return
	 */
	public static String postDataFile(String url, File file, String cid,
			String uid, String gid, String token) {
		String strPostResult = "����ʧ��";
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
	 * @Description �������״̬
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
