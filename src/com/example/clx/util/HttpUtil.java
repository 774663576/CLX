package com.example.clx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * http操作类
 * 
 * @author teeker_bin
 * 
 */
public class HttpUtil {
	private static final int IO_BUFFER_SIZE = 4 * 1024;

	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}

	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	public static String queryStringForPost(String url) {
		HttpPost request = HttpUtil.getHttpPost(url);
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			Logger.error("HttpUtil.queryStringForPost", e);

			e.printStackTrace();
			result = "网络异常！111";
			return result;
		} catch (IOException e) {
			Logger.error("HttpUtil.queryStringForPost", e);

			e.printStackTrace();
			result = "网络异常！";
			return result;
		}
		return null;
	}

	public static String queryStringForPost(HttpPost request) {
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.queryStringForPost", e);

			result = "网络异常！";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.queryStringForPost", e);

			result = "网络异常！";
			return result;
		}
		return null;
	}

	public static String queryStringForGet(String url) {
		HttpGet request = HttpUtil.getHttpGet(url);
		String result = null;
		try {
			HttpResponse response = HttpUtil.getHttpResponse(request);
			Logger.debug(HttpUrlHelper.class, "###:"
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.queryStringForGet", e);

			result = "网络异常！123";
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.queryStringForGet", e);

			result = "网络异常！456";
			return result;
		}
		return null;
	}

	public static String getResultForHttpGet(String url) {

		// 注意字符串连接时不能带空格

		String result = "";

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, HTTP.UTF_8);
			}
			return result;

		} catch (ClientProtocolException e) {
			Logger.debug(HttpUrlHelper.class, "ClientProtocolException");
			Logger.error("HttpUtil.getResultForHttpGet", e);
			e.printStackTrace();
		} catch (IOException e) {
			Logger.debug(HttpUrlHelper.class, "IOException");
			Logger.error("HttpUtil.getResultForHttpGet", e);
			e.printStackTrace();
		}
		return null;

	}

	public static Bitmap GetNetBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new URL(url).openStream(),
					IO_BUFFER_SIZE);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			Bitmap rounbitmap = BitmapUtils.toRoundBitmap(bitmap);// 将原来的位图缩小
			bitmap.recycle();// 释放内存
			return rounbitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.GetNetBitmap", e);
			return null;
		}
	}

	/**
	 * 通过网络获取图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap GetBitmapFromUrl(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;
		try {
			in = new BufferedInputStream(new URL(url).openStream(),
					IO_BUFFER_SIZE);
			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			copy(in, out);
			out.flush();
			byte[] data = dataStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			data = null;
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Logger.error("HttpUtil.GetBitmapFromUrl", e);
			return null;
		}
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

}
