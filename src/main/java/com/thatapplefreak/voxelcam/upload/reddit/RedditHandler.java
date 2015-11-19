package com.thatapplefreak.voxelcam.upload.reddit;

import java.io.File;

import com.thatapplefreak.voxelcam.VoxelCamCore;
import com.thatapplefreak.voxelcam.net.Callback;
import com.thatapplefreak.voxelcam.net.imgur.ImgurUpload;
import com.thatapplefreak.voxelcam.net.imgur.ImgurUploadResponse;

public abstract class RedditHandler {
	
	//protected static User reddit;
	
	/**
	 * user is logged into reddit
	 */
	protected static boolean loggedIn = false;
	
	/**
	 * Post image to reddit
	 * @param postTitle
	 * @param screenshot
	 */
	public static void doRedditPost(final String postTitle, final String subreddit, final File screenshot, final IRedditPostCallback callback) {
		final ImgurUpload poster = new ImgurUpload(screenshot, new Callback<ImgurUploadResponse>() {
			@Override
			public void onCompleted(ImgurUploadResponse response) {
				
				ImgurUploadResponse uploadResponse = response;
				if (uploadResponse.isSuccessful()) {
					try {
//						Method m = User.class.getDeclaredMethod("submit", String.class, String.class, boolean.class, String.class);
//						m.setAccessible(true);
//						Object obj = m.invoke(reddit, postTitle, uploadResponse.getLink(), false, subreddit);
//						JSONObject jobj = (JSONObject) obj;
//						callback.onPostSuccess(((JSONArray) ((JSONArray) ((JSONArray) jobj.get("jquery")).get(16)).get(3)).get(0).toString());
					} catch (Exception e) {
						e.printStackTrace();
						callback.onPostFailure();
					}
				} else {
					callback.onPostFailure();
				}
			}
		});
		try {
			VoxelCamCore.instance().getImagePoster().post(poster);
		} catch (Exception e) {
			e.printStackTrace();
			callback.onPostFailure();
		}
	}
	
	/**
	 * Log the user into reddit
	 * @param username
	 * @param password
	 * @return
	 */
	public static void login(final String username, final String password, final ILoginCallback logincallback) {
//		reddit = new User(new HttpRestClient(), username, password);
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					reddit.connect();
//					logincallback.onLoginSuccess();
//					loggedIn = true;
//				} catch (Exception e) {
//					logincallback.onLoginFailure();
//				}			
//			}
//		}.start();
	}
	
	/**
	 * @return True if the user is logged in
	 */
	public static boolean isLoggedIn() {
		return loggedIn;
	}
	
}
