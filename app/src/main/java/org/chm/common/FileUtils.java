package org.chm.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

/**
 * @author pc
 *
 */
public class FileUtils {
	private static File sdCardFile = null;
	public static void setSharedPreference(final SharedPreferences sp, final String key, final boolean value) {
		new Thread() {
			@Override
			public void run() {
				sp.edit().putBoolean(key, value).commit();
			}
		}.start();
	}

	public static void setSharedPreference(final SharedPreferences sp, final String key, final String value) {
		new Thread() {
			@Override
			public void run() {
				sp.edit().putString(key, value).commit();
			}
		}.start();
	}

	/**
	 * 在SD卡上创建一个文件夹
	 *
	 * @param subPath
	 */
	public static void createSDCardDir(String subPath) {
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path0 = Environment.getExternalStorageDirectory() + Common.MAIN_FOLDER;
		File path1 = new File(path0);
		System.out.println(path1);
		String path = path0 += subPath;
		if (!path1.exists()) {
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();

		}
		File path2 = new File(path);
		if (!path2.exists()) {
			path2.mkdirs();
		}

	}

	/**
	 * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
	 *
	 * @return
	 */
	private static ArrayList<String> getDevMountList() {
//        String[] toSearch = FileUtils.readFileByLines("/etc/vold.fstab").split(" ");
		ArrayList<String> out = new ArrayList<String>();
//        for (int i = 0; i < toSearch.length; i++) {
//            if (toSearch[i].contains("dev_mount")) {
//                if (new File(toSearch[i + 2]).exists()) {
//                    out.add(toSearch[i + 2]);
//                }
//            }
//        }
		out.add("/mnt/sdcard");
		return out;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	private static String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		String tempString = "";
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));

			StringBuffer sb = new StringBuffer();
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				sb.append(tempString);
			}
			tempString = sb.toString();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return tempString;
	}
	public static File getExternalSdCardFile() {
		if (null == sdCardFile) {
			getExternalSdCardPath();
		}
		return sdCardFile;
	}
	public static File getExternalSdCard() {

		String path = null;

		File sdCardFile = null;

		ArrayList<String> devMountList = getDevMountList();

		for (String devMount : devMountList) {
			File file = new File(devMount);

			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();

                /*
                 * 检验对目录是否有可写权限
                 */
				String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
				File testWritable = new File(path, "test_" + timeStamp);

				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}

		if (path != null) {
			sdCardFile = new File(path);
			return sdCardFile;
		} else if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
			return sdCardFile;
		}

		return null;
	}
	/**
	 * 获取扩展SD卡存储目录
	 *
	 * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
	 * 否则：返回内置SD卡目录
	 *
	 * @return
	 */
	public static String getExternalSdCardPath() {



		String path = null;

		File sdCardFile = null;

		ArrayList<String> devMountList = getDevMountList();

		for (String devMount : devMountList) {
			File file = new File(devMount);

			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();

                /*
                 * 检验对目录是否有可写权限
                 */
				String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
				File testWritable = new File(path, "test_" + timeStamp);

				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}

		if (path != null) {
			sdCardFile = new File(path);
			FileUtils.sdCardFile = sdCardFile;
			return sdCardFile.getAbsolutePath();
		} else if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
			FileUtils.sdCardFile = sdCardFile;
			return sdCardFile.getAbsolutePath();
		}

		return null;
	}

	/**
	 * 通过指定文件名获取路径下匹配的文件名（包括扩展名）
	 *
	 * @param path
	 * @param keyword
	 * @return
	 */
	public static String searchFile(String path, String keyword) {
		String result = "";
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.getName().indexOf(keyword) >= 0) {
				result = file.getPath();
				break;
			}
		}
		if (result.equals("")){
			result = "找不到文件!!";
		}
		return result;
	}

	/**
	 * 复制单个文件
	 *
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(String oldPath, String newPath) throws IOException {
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);

			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}

			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		} finally {
			if (inStream != null)
				inStream.close();
			if (fs != null)
				fs.close();
		}

	}

	/**
	 * 复制整个文件夹内容
	 *
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 获取指定路径下所有文件的文件名（不包含子文件夹下的文件的文件名）
	 *
	 * @param path
	 * @return
	 */
	public static String[] getFilesNameUnderPath(File path) {

		String[] resultList = {};
		if (path.isDirectory()) {
			File[] files = path.listFiles();

//			/*
//			 * 按照文件大小排序
//			 */
//			Arrays.sort(files, new Comparator<File>() {
//				public int compare(File f1, File f2) {
//					long diff = f1.length() - f2.length();
//					if (diff > 0)
//						return 1;
//					else if (diff == 0)
//						return 0;
//					else
//						return -1;
//				}
//
//				public boolean equals(Object obj) {
//					return true;
//				}
//			});
//
//			/*
//			 * 按照文件名称排序
//			 */
//			Arrays.sort(files, new Comparator<File>() {
//				@Override
//				public int compare(File o1, File o2) {
//					if (o1.isDirectory() && o2.isFile())
//						return -1;
//					if (o1.isFile() && o2.isDirectory())
//						return 1;
//					return o1.getName().compareTo(o2.getName());
//				}
//			});

			/*
			 * 按照最后修改日期排序
			 */
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff < 0)
						return 1;
					else if (diff == 0)
						return 0;
					else
						return -1;
				}

				public boolean equals(Object obj) {
					return true;
				}

			});
			resultList = new String[files.length];
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile())
					resultList[i] = file.getName();
			}
		}

		return resultList;
	}

	public static String getPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}finally{
				if (cursor != null) {
					cursor.close();
				}
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}
}
