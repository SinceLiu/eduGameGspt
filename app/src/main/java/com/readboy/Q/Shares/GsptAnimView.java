package com.readboy.Q.Shares;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.readboy.Q.Gspt.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
 * �ؿ���ʾ���࣬�������ƹؿ�״̬��������Ӧ��ʵ�������������ŵ���{@link #setmCurLevel(int)}��
 * {@link #setmTotalLevel(int)}�����ùؿ����Թ���ʼ��
 * 
 * @author css
 * @version 1.0
 */
public class GsptAnimView extends ReadboySurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "QQView";
	private SurfaceHolder mSHolder;
	private Timer timer = new Timer();
	private MyTimerTask task = null;
	
	/** ˢͼ��� */
	private int timeout = 150;
	/** ���鵱ǰˢ����һ��ͼƬ */
	private int mTimeCount = 0;
	/** ������ͼƬ�� *///���Ѹ�Ϊ��һ��ͼƬ��ID
	private int mTotalPic = 0;
	/** ͼƬĿ¼ *///���Ѹ�Ϊ��һ��ͼƬ��ID
	private int mPicDir = 0;
	/** ��ǰ�ǵڼ��鶯�������ڼ�����ע����1��ʼ */
	// private int mCurStep;
	/** icon����������ȷ��ͼƬĿ¼ */
	// private int mIconIndex;
	/** �Ƿ��Ѿ���ʼˢ������ */
	// private boolean isRunning;
	/**
	 * ��ǰ��ͼƬ����
	 */
	private int mCBTotalPic = 0;
	/**
	 * ��ǰ��ͼƬ·�������Ѹ�Ϊ��һ��ͼƬ��ID
	 */
	private int mCBPicDir = 0;
	/**
	 * �ص�����
	 */
	private OwnerActivtiyState ownerStateCallback = null;
	/**
	 * ������Context����
	 */
	private Context mContext;
	/** Ϊ�˼ӿ춯���ٶȣ�ʹ���߳̽�ͼ���ѽ������ͼȫ�����浽�ڴ��� */
	// private ArrayList<SoftReference<Bitmap>> imageCache = new
	// ArrayList<SoftReference<Bitmap>>();
	// private ArrayList<Bitmap> imageCache = new ArrayList<Bitmap>();

	public GsptAnimView(Context context) {
		super(context);
		init(context, null);
	}

	/**
	 * ����view������xml������ʱ�����ṩ�˹��캯��
	 * 
	 * @param context
	 *            ������
	 * @param attrs
	 *            �Զ�������
	 */
	public GsptAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 * ��ʼ��surfaceView����Ҫ��ʹ��͸��
	 */
	private void init(Context context, AttributeSet attrs) {
		// Log.i(TAG, "init");

		
		// ���resFolder���Ե���ԴID��ͼƬ��Դ·��
		mPicDir = attrs.getAttributeResourceValue(null, "resStartId", 0);
		if (mPicDir == 0) {
			mPicDir = R.drawable.gspt_anim_bee_000;
			throw new RuntimeException("��������resFolder���ԣ�����ͼƬ����·����");
		}
		// ���resPicNumber���Ե���ԴID��ͼƬ��Դ����
		mTotalPic = attrs.getAttributeIntValue(null, "resPicNumber", 0);
		if (mTotalPic == 0) {
			mTotalPic = 29;
			throw new RuntimeException("��������resPicNumber���ԣ�����ͼƬ������");
		}
		// ���resFolder���Ե���ԴID��ͼƬ��Դ·��
		mCBPicDir = attrs.getAttributeResourceValue(null, "resCBStartId", 0);
		if (mCBPicDir == 0) {
			mCBPicDir = R.drawable.gspt_anim_bee_000;
			throw new RuntimeException("��������resCBFolder���ԣ�����ͼƬ����·����");
		}		
		// ���resPicNumber���Ե���ԴID��ͼƬ��Դ����
		mCBTotalPic = attrs.getAttributeIntValue(null, "resCBPicNumber", 0);
		if (mCBTotalPic == 0) {
			mCBTotalPic = 29;
			throw new RuntimeException("��������resCBPicNumber���ԣ�����ͼƬ������");
		}

		// ��¼������Context����
		mContext = context;
		mSHolder = this.getSurface();
		// ˢ�������
		setZOrderOnTop(true);
		// ʹ����͸��
		mSHolder.setFormat(PixelFormat.TRANSLUCENT);
		mSHolder.addCallback(this);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ��ʼ���Ŷ���
		beginAnim(true);
		/*
		 * //��������SurfaceView Canvas canvas = mSHolder.lockCanvas(); //���Ʊ���
		 * Bitmap bk =
		 * DataManager.decodeBitmapFromAsset(Constant.LAUNCH_BK_TOTAL_PIC_DIR
		 * +"finish_state_bar.png"); canvas.drawBitmap(bk, 0, 0, null);
		 * //������ɣ��ͷŻ������ύ���� mSHolder.unlockCanvasAndPost(canvas);
		 */
		// ������һ�Σ����ⱻ�´��ڵ�
		// mSHolder.lockCanvas(new Rect(0, 0, 0, 0));
		// mSHolder.unlockCanvasAndPost(canvas);

		// ˢ�ؿ�
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
//	 * ���ⲿ���ã����ݵ�ǰ״̬���õ�ǰ״̬��ͼƬ����
//	 * 
//	 * @param fairyState
//	 *            ��ǰ״̬
//	 */
//	public void setTotalPic() {
//		this.mTotalPic = getOneStepTotalPic();
//	}

//	/**
//	 * ��õ�ǰ״̬���ж��ٶ���ͼƬ
//	 * 
//	 * @return ��ǰ״̬�Ķ���ͼƬ����
//	 */
//	private int getOneStepTotalPicxa() {
//		int picNum = Constant.QQVIEW_TOTAL_PIC;
//		/*
//		 * int picNum = 0; //�������ô˷���ȥ����ͼƬ������̫��ʱ�� try {
//		 * //ע��AssertManager��list(path)�����е�path������'/'��β String[] files =
//		 * DataManager.getAssetManager().list(mPicDir); if(files != null) picNum
//		 * = files.length; } catch (IOException e) {
//		 * catch block e.printStackTrace(); }
//		 */
//		// Log.i(TAG, "picNum="+picNum);
//		return picNum;
//	}

	/**
	 * �ж�����activity�Ƿ��Ѿ���ͣ��
	 * 
	 * @return ��ͣ����true������false
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
	 * �ṩ���ⲿ�����Կ�ʼ����
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
	 * �ṩ���ⲿ������ֹͣ����
	 */
	public void stopAnim() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}
	
	/**
	 * @aim ������Դid����ͼƬ���ļ���
	 * @param resID ��ԴID��
	 * @return ����bitmap����
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
	 * ����ͼƬ·��ˢһ��ͼ��������
	 * 
	 * @param picPath
	 *            assert�е�ͼƬ·��
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
				// �����������
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
				Bitmap bitmap = loadResById(resId);;
				if (bitmap != null) {
					canvas.drawBitmap(bitmap, 0, 0, null);
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
	 * @aim ���ûص�����
	 * @param callback
	 */
	public void setOnOwnerActivtiyStateCallback(OwnerActivtiyState callback) {
		ownerStateCallback = callback;
	}

	/**
	 * @aim ��ȡ��ǰ�ĸ�Activity�Ƿ���Pause״̬
	 * @author Administrator
	 * 
	 */
	public interface OwnerActivtiyState {

		/**
		 * �ж��Ƿ��ļ���ѡ��
		 * 
		 * @return true ѡ�� false δѡ��
		 */
		public boolean bOwnerActPause();
	}

	/**
	 * ͳһ��timer���������ڲ���
	 * 
	 * @author css
	 * 
	 */
	class MyTimerTask extends TimerTask {

		/**
		 * ����ID
		 */
		private boolean bNormal = true;
		
		/**
		 * @aim ��ȡ��ǰ������
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
