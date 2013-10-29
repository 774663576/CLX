package com.example.clx.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * 异步下载图片类
 * 
 * @author teeker_bin
 * 
 */
public class AsyncImageLoader {
	private MemoryCache cache;
	private static final String SUFFIX = ".cach";// 后缀名
	private Bitmap bmp;

	public AsyncImageLoader() {
		cache = new MemoryCache();
	}

	public Bitmap loaDrawable(final String imgUrl,
			final ImageCallback imageCallback) {
		if (imgUrl.equals("")) {
			Logger.debug(this, "null");
			return null;
		}

		/**
		 * 先从缓存中查找
		 */
		bmp = cache.get(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "缓存");
			return bmp;
		}
		/**
		 * 从文件缓存中获取
		 */
		bmp = getImage(imgUrl);
		if (bmp != null) {
			Logger.debug(this, "本地");
			return bmp;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageCallback.imageLoaded((Bitmap) msg.obj, imgUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				Bitmap bmp = loadImageFromUrl(imgUrl);
				cache.put(imgUrl, bmp);
				saveBitmap(bmp, imgUrl);
				Message message = handler.obtainMessage(0, bmp);
				handler.sendMessage(message);
			}
		}.start();

		return null;
	};

	/**
	 * 根据url转换本地文件名
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + SUFFIX;
	}

	/** 将图片存入文件缓存 **/
	private void saveBitmap(Bitmap bm, String url) {
		if (bm == null) {
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = Environment.getExternalStorageDirectory() + File.separator
				+ "clxcache";
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Logger.error(this, e);
		} catch (IOException e) {
			Logger.error(this, e);
		}
	}

	/** 从缓存中获取图片 **/
	private Bitmap getImage(String url) {
		final String path = Environment.getExternalStorageDirectory()
				+ File.separator + "clxcache" + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				cache.put(url, bmp);
				return bmp;
			}
		}
		return null;
	}

	/**
	 * 从网络获取图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageFromUrl(String url) {
		Logger.debug(this, "网络");
		Bitmap bmp = HttpUtil.GetBitmapFromUrl(url);
		// Bitmap smallBmp = small(bmp);
		// 将原来的位图缩小
		Bitmap rbnmp = BitmapUtils.toRoundBitmap(bmp);
		bmp.recycle();
		// smallBmp.recycle();
		return rbnmp;
	}

	/**
	 * 对图片进行缩放
	 * 
	 * @param bitmap
	 * @return
	 */
	private Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.3f, 0.3f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap imageDrawable, String imageUrl);
	}
}
