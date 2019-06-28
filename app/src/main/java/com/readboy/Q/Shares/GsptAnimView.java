package com.readboy.Q.Shares;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.readboy.Q.Gspt.R;
import com.readboy.Q.app.App;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ReadboySurfaceView;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 关卡提示框类，用来绘制关卡状态，调用者应在实例化该类后紧接着调用{@link #setmCurLevel(int)}和
 * {@link #setmTotalLevel(int)}来设置关卡，以供初始化
 * 
 * @author css
 * @version 1.0
 */
public class GsptAnimView extends ReadboySurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "QQView";
	private SurfaceHolder mSHolder;
	private Timer timer = new Timer();
	private MyTimerTask task = null;
	
	/** 刷图间隔 */
	private int timeout = 150;
	/** 单组当前刷到哪一张图片 */
	private int mTimeCount = 0;
	/** 单组总图片数 *///现已改为第一张图片的ID
	private int mTotalPic = 0;
	/** 图片目录 *///现已改为第一张图片的ID
	private int mPicDir = 0;
	/** 当前是第几组动画，即第几步，注：从1开始 */
	// private int mCurStep;
	/** icon索引，用于确定图片目录 */
	// private int mIconIndex;
	/** 是否已经开始刷动画了 */
	// private boolean isRunning;
	/**
	 * 当前的图片总数
	 */
	private int mCBTotalPic = 0;
	/**
	 * 当前的图片路径，现已改为第一张图片的ID
	 */
	private int mCBPicDir = 0;
	/**
	 * 回调函数
	 */
	private OwnerActivtiyState ownerStateCallback = null;
	/**
	 * 上下文Context对象
	 */
	private Context mContext;
	/** 为了加快动画速度，使用线程解图，把解出来的图全部缓存到内存中 */
	// private ArrayList<SoftReference<Bitmap>> imageCache = new
	// ArrayList<SoftReference<Bitmap>>();
	// private ArrayList<Bitmap> imageCache = new ArrayList<Bitmap>();

	public GsptAnimView(Context context) {
		super(context);
		init(context, null);
	}

	/**
	 * 将此view放置于xml布局中时必须提供此构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param attrs
	 *            自定义属性
	 */
	public GsptAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 * 初始化surfaceView，主要是使其透明
	 */
	private void init(Context context, AttributeSet attrs) {
		// Log.i(TAG, "init");

		
		// 获得resFolder属性的资源ID，图片资源路径
		mPicDir = attrs.getAttributeResourceValue(null, "resStartId", 0);
		if (mPicDir == 0) {
			mPicDir = R.drawable.gspt_anim_bee_000;
			throw new RuntimeException("必须设置resFolder属性，标明图片所在路径！");
		}
		// 获得resPicNumber属性的资源ID，图片资源个数
		mTotalPic = attrs.getAttributeIntValue(null, "resPicNumber", 0);
		if (mTotalPic == 0) {
			mTotalPic = 29;
			throw new RuntimeException("必须设置resPicNumber属性，标明图片总数！");
		}
		// 获得resFolder属性的资源ID，图片资源路径
		mCBPicDir = attrs.getAttributeResourceValue(null, "resCBStartId", 0);
		if (mCBPicDir == 0) {
			mCBPicDir = R.drawable.gspt_anim_bee_000;
			throw new RuntimeException("必须设置resCBFolder属性，标明图片所在路径！");
		}		
		// 获得resPicNumber属性的资源ID，图片资源个数
		mCBTotalPic = attrs.getAttributeIntValue(null, "resCBPicNumber", 0);
		if (mCBTotalPic == 0) {
			mCBTotalPic = 29;
			throw new RuntimeException("必须设置resCBPicNumber属性，标明图片总数！");
		}

		// 记录上下文Context对象
		mContext = context;
		mSHolder = this.getSurface();
		// 刷新在最顶层
		setZOrderOnTop(true);
		// 使背景透明
		mSHolder.setFormat(PixelFormat.TRANSLUCENT);
		mSHolder.addCallback(this);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 开始播放动画
		beginAnim(true);
		/*
		 * //锁定整个SurfaceView Canvas canvas = mSHolder.lockCanvas(); //绘制背景
		 * Bitmap bk =
		 * DataManager.decodeBitmapFromAsset(Constant.LAUNCH_BK_TOTAL_PIC_DIR
		 * +"finish_state_bar.png"); canvas.drawBitmap(bk, 0, 0, null);
		 * //绘制完成，释放画布，提交更改 mSHolder.unlockCanvasAndPost(canvas);
		 */
		// 重新锁一次，避免被下次遮挡
		// mSHolder.lockCanvas(new Rect(0, 0, 0, 0));
		// mSHolder.unlockCanvasAndPost(canvas);

		// 刷关卡
		// drawQstLevel(mCurLevel, mTotalLevel);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (timer != null) {
			timer.cancel();
		}
		// freeImageCache();
	}

//	/**
//	 * 供外部调用，根据当前状态设置当前状态的图片总数
//	 * 
//	 * @param fairyState
//	 *            当前状态
//	 */
//	public void setTotalPic() {
//		this.mTotalPic = getOneStepTotalPic();
//	}

//	/**
//	 * 获得当前状态中有多少动画图片
//	 * 
//	 * @return 当前状态的动画图片总数
//	 */
//	private int getOneStepTotalPicxa() {
//		int picNum = Constant.QQVIEW_TOTAL_PIC;
//		/*
//		 * int picNum = 0; //不建议用此方法去搜索图片总数，太耗时了 try {
//		 * //注：AssertManager的list(path)函数中的path不能以'/'结尾 String[] files =
//		 * DataManager.getAssetManager().list(mPicDir); if(files != null) picNum
//		 * = files.length; } catch (IOException e) {
//		 * catch block e.printStackTrace(); }
//		 */
//		// Log.i(TAG, "picNum="+picNum);
//		return picNum;
//	}

	/**
	 * 判断所属activity是否已经暂停了
	 * 
	 * @return 暂停返回true，否则false
	 */
	private boolean bMyOwnerActPause() {
		boolean ret = false;
		if (ownerStateCallback != null) {
			ret = ownerStateCallback.bOwnerActPause();
			if (getVisibility() != View.VISIBLE) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 提供给外部调用以开始动画
	 */
	public boolean beginAnim(boolean bAnimNormal) {
		synchronized (GsptViewPagerAdapter.class) {
			if (bAnimNormal) {
				if (task != null && task.getTaskStyle()) {
					return false;
				}				
				if (task != null) {
					task.cancel();
					task = null;
				}
				mTimeCount = 0;
				task = new MyTimerTask(bAnimNormal);
				timer.schedule(task, 0, timeout);
			} else {
				if (task != null && !task.getTaskStyle()) {
					return false;
				}
				if (task != null) {
					task.cancel();
					task = null;
				}
				mTimeCount = 0;
				task = new MyTimerTask(bAnimNormal);
				timer.schedule(task, 0, timeout);
			}
			return true;
		}
	}
	
	/**
	 * 提供给外部调用以停止动画
	 */
	public void stopAnim() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}
	
	/**
	 * @aim 根据资源id加载图片到文件流
	 * @param resID 资源ID号
	 * @return 返回bitmap对象
	 */
	public Bitmap loadResById(int resID) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		InputStream is = mContext.getResources().openRawResource(resID);
		return BitmapFactory.decodeStream(is, null, opt);
	}	
	
	/**
	 * 根据图片路径刷一张图到画布上
	 * 
	 * @param picPath
	 *            assert中的图片路径
	 */
	private void drawOnePic(int resId) {
		if (bMyOwnerActPause())
			return;

		Canvas canvas = null;
		try {
			canvas = mSHolder.lockCanvas();
			if (canvas == null) {
				Log.e(TAG, "mTimeCount=" + mTimeCount + ",mSHolder=" + mSHolder + ",drawOnePic canvas=null");
			} else {
				// 清除画布内容
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				Bitmap bitmap = loadResById(resId);;
				if (bitmap != null) {
					if(App.getInstance().mScale>1){
						int w = bitmap.getWidth();
						int h = bitmap.getHeight();
						float scaleX = App.getInstance().mScale;
						Matrix matrix = new Matrix();
						matrix.postScale(scaleX, scaleX, 0, 0);
						canvas.drawBitmap(bitmap, matrix, null);
					}else{
						canvas.drawBitmap(bitmap, 0, 0, null);
					}
					if (!bitmap.isRecycled()) {
						bitmap.recycle();
					}
					bitmap = null;
				}
				mSHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "mTimeCount=" + mTimeCount + ",mSHolder=" + mSHolder + ",drawOnePic error!");
		} finally {
			if (canvas != null) {
				mSHolder.unlockCanvasAndPost(canvas);
				canvas = null;
			}
		}
	}	

	/**
	 * @aim 设置回调函数
	 * @param callback
	 */
	public void setOnOwnerActivtiyStateCallback(OwnerActivtiyState callback) {
		ownerStateCallback = callback;
	}

	/**
	 * @aim 获取当前的父Activity是否是Pause状态
	 * @author Administrator
	 * 
	 */
	public interface OwnerActivtiyState {

		/**
		 * 判断是否文件被选中
		 * 
		 * @return true 选中 false 未选中
		 */
		public boolean bOwnerActPause();
	}

	/**
	 * 统一的timer调度任务内部类
	 * 
	 * @author css
	 * 
	 */
	class MyTimerTask extends TimerTask {

		/**
		 * 任务ID
		 */
		private boolean bNormal = true;
		
		/**
		 * @aim 获取当前的类型
		 * @return
		 */
		public boolean getTaskStyle() {
			return bNormal;
		}
		
		public MyTimerTask(boolean bNormalState) {
			this.bNormal = bNormalState;
		}
		
		@Override
		public void run() {
			synchronized (GsptViewPagerAdapter.class) {
				if (bNormal) {
					if (mTimeCount >= mTotalPic) {
						mTimeCount = 0;
					}
					//drawOnePic(mPicDir + "/" + mTimeCount + ".png");
					drawOnePic(mPicDir + mTimeCount);
					mTimeCount++;
				} else {
					if (mTimeCount >= mCBTotalPic) {
						mTimeCount = 0;
					}				
					//drawOnePic(mCBPicDir + "/" + mTimeCount + ".png");
					drawOnePic(mCBPicDir + mTimeCount);
					mTimeCount++;
				}
			}
		}
	}

}
