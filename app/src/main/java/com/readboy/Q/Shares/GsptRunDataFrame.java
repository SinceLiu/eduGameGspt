/**
 * @aim GsptRunDataFrame类 
 * 实现初始化一些数据，读取配置文件初始化动画相关信息
 * 以及ViewPager中的按钮点击的相关跳转，胜利动画等信息
 * 定义自动获取图片切块的函数，把背景图片分年级划分为
 * 不同的块数与不同的形状组合
 * 
 * GsptRunDataFrame的实现：
 * 1、通过读取XML文件夹下是settings.xml中的配置信息
 * 2、初始化插图动画的相关信息，实例化每一个插图动画Interlude
 * 3、初始图片的基准信息
 * 4、读取ViewPager中每一屏对应的按钮的跳转及胜利后的动画信息
 * 
 * 不同的图形的实现：
 * 1、先把图片等分成3+1分，比如3:3:3:1  最后1份为随机宽度，随机分配到前边3份当中
 * 2、根据难度级别分割不同的图形，都是以9宫格为基础，每个格大小不定
 * 但是横向或者纵向必有一个方向是固定的，即横向为两根直线或者纵向为两根直线
 * 切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
 * a、低难度的时候，随机横向或者纵向只分成两份，大小随机。变成6个格
 * b、中等难度的时候，在9宫格里合并矩形或者切割三角形，随机左切割或者右切割
 * c、高等难度的时候，3个格合并然后切割出梯形或者平行四边形，以及三角形
 * 其他区域可以是长方形或者切割成两个三角形
 * 3、根据切割好的图形的路径，采用setXfermode方法绘制出不同的形状来
 * 
 * 配置信息的保存：
 * 记录在/data/data/com.readboy.Q.Gspt/shared_prefs目录下生成了一个GsptSpCfg.xml文件里
 * 把用户完成了哪些拼图，哪些拼图没有完成记录下来，没有完成的加锁，完成了的解锁 
 * 
 * @time 2013.08.10;
 * @author divhee
 * @modify by
 */
package com.readboy.Q.Shares;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.readboy.Q.Activity.GsptMainActivity;
import com.readboy.Q.Gspt.R;
import com.readboy.Q.app.App;
import com.readboy.Q.db.MessageCenter;
import com.readboy.Q.db.SystemDataBase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GsptRunDataFrame {

	/**
	 * 默认用户ID
	 */
	private static final int USER_ID_DEFAULT = 1;
	/**
	 * 数据库的Uri
	 */
	public static final Uri databaseUri = Uri.parse("content://com.readboy.Q.share/userdata");	
	
	/**
	 * 打印标记
	 */
	public static final String Loginfo = "GsptInfo";
	
	/**
	 * 记录用户是否完成过拼图的配置信息文件"GsptSpCfg"
	 */
	public static final String gstpSpWinPassConfig = "GsptSpCfg";
	
	/**
	 * 进入的模式
	 */
	private int systemEnterMode = ENTER_NORMAL;
	
	/**
	 * 当前的状态，进入onResume与否
	 */
	public static boolean bMainCurrentOnResumed = false;
	public static boolean bIngameCurrentOnResumed = false;
	
	/**
	 * 第一次进入游戏主界面，一些耗时的操作，后边做
	 */
	public EgameStep enumEnterMain = EgameStep.STEP_0;
	
	/**
	 * 第一次进入游戏Ingame界面，一些耗时的操作，后边做
	 */
	public EgameStep enumEnterIngame = EgameStep.STEP_0;
	
	/**
	 * 进入拼图后的游戏状态
	 * 
	 * STEP_0没有游戏
	 * STEP_1刚进入游戏onCreat跑完
	 * STEP_2跑完onResum
	 * STEP_3进入了onPause
	 * STEP_4没有跑完onResum就跑onPause
	 * @author divhee
	 *
	 */
	public static enum EgameStep {
		STEP_0, STEP_1, STEP_2, STEP_3, STEP_4;
	}	
	
	/**
	 * 游戏的所有状态:
	 * 
	 * STEP_STARTIN 刚进入动画阶段
	 * STEP_INOPT 选择图片阶段
	 * STEP_WINNER 拼图完成阶段
	 * STEP_INGAME 拼图游戏过程阶段
	 * STEP_OTHER 其他阶段
	 * 
	 * @author divhee
	 * 
	 */
	public static enum GameStep {
		STEP_STARTIN, STEP_INOPT, STEP_ZY_INOPT, STEP_WINNER, STEP_INGAME, STEP_OTHER;
	}

	/**
	 * 当前游戏状态在第几步
	 */
	public GameStep GsptCurrentStep = GameStep.STEP_OTHER;

	/**
	 * 需要切换到下一个状态
	 */
	public GameStep GsptNeedJumpStep = GameStep.STEP_OTHER;
	
	/**
	 * 游戏胜利后要播放的声音以及动作的步凑
	 * 这个主要是要监听声音播放结束事件
	 */
	public int bGsPlayEndShowNext = 0;

	/**
	 * SurfaceView通过线程刷新的快慢，就是要Delay的时间长短
	 */
	public static final int myThreadDelayTime = 80;	
	
	/**
	 * 两种状态，在选择界面或者在游戏界面
	 */
	public static final int WM_OPTION_GAME = 0;
	public static final int WM_PLAYING_GAME1 = 0x900;
	public static final int WM_PLAYING_GAME2 = 0x901;
	
	/**
	 * IngameActivity播放胜利音1以及1结束
	 */
	public static final int WM_PLAY_WIN_SND1 = 0x1002;
	public static final int WM_END_WIN_SND1 = 0x1003;
	
	/**
	 * IngameActivity播放胜利音2以及2结束
	 */
	public static final int WM_PLAY_WIN_SND2 = 0x1004;
	public static final int WM_END_WIN_SND2 = 0x1005;
	
	/**
	 * 胜利撒花
	 */
	public static final int WM_PLAY_WIN_SND3 = 0x1006;
	public static final int WM_END_WIN_SND3 = 0x1007;	
	
	/**
	 * 记录线条的值，横向同X轴
	 */
	public PointF [] prepareBaseH = null;

	/**
	 * 记录线条的值，竖向同Y轴
	 */
	public PointF [] prepareBaseV = null;	
	
	/**
	 * 记录凹陷或者突出区域的坐标，横向(同X轴线上)
	 */
	public Point [] preparePtH = null;

	/**
	 * 记录凹陷或者突出区域的坐标，竖向(同Y轴线上)
	 */
	public Point [] preparePtV = null;

	/**
	 * 图片的基准宽信息
	 */
	public static int baseWidth = 1280;
	
	/**
	 * 图片的基准高信息
	 */	
	public static int baseHeight = 800;
	
	/**
	 * 普通模式进入
	 */
	public static final int ENTER_NORMAL = 0x03;
	
	/**
	 * 其他模块调用
	 */
	public static final int ENTER_EXTCALL = 0x02;
	
	/**
	 * 游戏类型
	 */
	public boolean bGsptGameStyle = false;
	
	/**
	 * 当前要播放的动画名
	 */
	public String[] GsptInterludeName = null;
	
	/**
	 * SharedPreferences实例用于读取配置信息，是否完成过该拼图
	 * 如果没有完成的拼图，要显示加锁的背景，如果完成了的拼图
	 * 图片按钮不加锁，我们通过gsptSharedPreferences记录这些信息
	 */
	public SharedPreferences gsptSharedPreferences = null;
	
	/**
	 * 插图动画信息的ArrayList
	 */
	public ArrayList<Interlude> gsptInterlude = new ArrayList<Interlude>();
	
	/**
	 * 图片按钮自动跳转信息
	 */
	private ImgBtnAuto gsptImgBtnAuto = new ImgBtnAuto();

	/**
	 * 上下文Context
	 */
	private Context frameContext = null;
	
	/**
	 * 数据结构实例
	 */
	private static GsptRunDataFrame gsptRunData = null;

	/**
	 * 记录上一次按钮点击的时间
	 */
	private static long lastClickTime = 0;
	
	/**
	 * @aim 判断是否是快速点击
	 * @param 无
	 * @return true 非快速点击
	 * 			false 快速点击
	 */
	public static boolean bNotFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 <= timeD && timeD <= 500) {
			return false;
		}
		lastClickTime = time;
		return true;
	}	
	
	/**
	 * 构造初始化
	 */
	public GsptRunDataFrame(Context context) {
		this.frameContext = context;
	}

	/**
	 * @aim 获取实例GsptDataPagerAdapter
	 * @param 无
	 * @return GsptRunDataFrame实例，唯一实例
	 */
	public static GsptRunDataFrame getInstance(Context context) {
		if (gsptRunData == null) {
			gsptRunData = new GsptRunDataFrame(context);
		}
		return gsptRunData;
	}

	/**
	 * @aim 获取总共有几页
	 * @return
	 */
	public static int getPagerTotal() {
		if (gsptRunData != null) {
			switch (gsptRunData.systemEnterMode) {
			case ENTER_EXTCALL:
				return 1;
			case ENTER_NORMAL:
				return 3;
			default:
				break;
			}
		}
		return 1;
	}
	
	/**
	 * @aim 获取总共有几页
	 * @return
	 */
	public static boolean bEnterModeExtCall() {
		if (gsptRunData != null) {
			return (gsptRunData.systemEnterMode == ENTER_EXTCALL);
		}
		return false;
	}	

	/**
	 * @aim 设置总共有几页
	 * @return
	 */
	public static void setAppEnterMode(int mode) {
		if (gsptRunData != null) {
			gsptRunData.systemEnterMode = mode;
			if (gsptRunData.systemEnterMode != ENTER_EXTCALL && gsptRunData.systemEnterMode != ENTER_NORMAL) {
				gsptRunData.systemEnterMode = ENTER_NORMAL;
			}
			// 读取XML文件夹下settings.xml文件中的配置信息
			// 初始化诸多信息
			gsptRunData.initXmlConfigInfo();				
		}
	}
	
	/**
	 * @aim 设置游戏模式，三角形或者不规则图形
	 * @return
	 */
	public static void setAppGameStyle(boolean bStyle) {
		if (gsptRunData != null) {
			gsptRunData.bGsptGameStyle = bStyle;
		}
	}	
	
	/**
	 * @aim 声音播放结束后，监听结束
	 * @param 无
	 * @return 无
	 */
	public void setGsptMediaPlayEnd() {
		if (bGsPlayEndShowNext == WM_OPTION_GAME){
			GsptRunDataFrame.getInstance(frameContext).GsptNeedJumpStep = GameStep.STEP_ZY_INOPT;
		} else {
			if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND1){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND1;
			} else if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND2){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND2;
			} else if (bGsPlayEndShowNext == GsptRunDataFrame.WM_PLAY_WIN_SND3){
				bGsPlayEndShowNext = GsptRunDataFrame.WM_END_WIN_SND3;
			}
		}
	}	
	
	/**
	 * 从数据库获取key对应的数据
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getDataFromDb(String key, int defValue){
		int value = 0;
		Cursor cursor = frameContext.getContentResolver().query(databaseUri, new String[] { "key", "value" }, "key" + "=?", new String[] {key}, null);
		if (cursor != null && cursor.moveToFirst()) {
			value = cursor.getInt(cursor.getColumnIndexOrThrow("value"));
			Log.i("edugame", key + "-----------value="+value);  
		} else {  
		    Log.i("edugame", key + "----------query failure!");  
		    value = defValue;
		} 
		if(cursor != null){
			cursor.close();
		}
		
		return value;
	}
	
	/**
	 * @aim 获取当前游戏状态
	 * @param 无
	 * @return GameStep 游戏状态的 GsptCurrentStep
	 */
	public GameStep getCrrentGameStep() {
		return GsptCurrentStep;
	}

	/**
	 * @aim 获取图片的基准宽
	 * @param 无
	 * @return int 返回图片的基准宽
	 */
	public static int getBaseWidth() {
		return baseWidth;
	}

	/**
	 * @aim 获取图片的基准高
	 * @param 无
	 * @return int 返回图片的基准高
	 */
	public static int getBaseHeight() {
		return baseHeight;
	}

	/**
	 * @aim 获取当前选中的是哪一个ImageButton
	 * @param 无
	 * @return 当前选中的按钮，就是要玩的游戏
	 */
	public int getImgBtnAutoIndex() {
		if (gsptImgBtnAuto != null) {
			return gsptImgBtnAuto.currentindex;
		}
		
		return -1;
	}

	/**
	 * @aim 获取总共有多少按钮
	 * @param 无
	 * @return int 按钮总数
	 */
	public int getImgBtnAutoSum() {
		if (gsptImgBtnAuto != null) {
			return gsptImgBtnAuto.imgbtnsum;
		}
		
		return -1;
	}

	/**
	 * @aim 获取按钮信息实例
	 * @param 无
	 * @return 按钮信息实例
	 */
	public ImgBtnAuto getImgBtnAuto() {
		return gsptImgBtnAuto;
	}
	
	/**
	 * @aim 获取按钮应该显示的状态
	 * @param index
	 * @return
	 */
	public boolean getImgBtnPassState(int index) {
		if (gsptImgBtnAuto != null && gsptImgBtnAuto.nowinpass != null 
				&& index < gsptImgBtnAuto.nowinpass.length) {
			return gsptImgBtnAuto.nowinpass[index];
		}
		return false;
	}

	/**
	 * @aim 设置当前选中的是哪一个ImageButton
	 * @param tag
	 *            要设置的序号
	 * @return true 成功 false 不成功
	 */
	public boolean setImgBtnAutoIndex(int tag) {
		
		if (gsptImgBtnAuto != null) {
			if (tag >= 0 && tag < gsptImgBtnAuto.imgbtnsum) {
				gsptImgBtnAuto.currentindex = tag;
				return true;
			}
		}
		return false;
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
		InputStream is = frameContext.getResources().openRawResource(resID);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public Bitmap loadResById(int resID,float scale) {
		return loadResById(resID, scale,  scale);
	}

	public Bitmap loadResById(int resID,float scalex, float scaley) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = false;
		InputStream is = frameContext.getResources().openRawResource(resID);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		if (scalex>1.0f) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			if (scalex==scaley){
				matrix.postScale(scalex, scalex, 0, 0);
			}else {
				matrix.postScale(scalex, scaley, 0, 0);
			}
			Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
			if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
			bitmap = bmp;
		}
		return bitmap;
	}

//	/**
//	 * @aim 动态加载
//	 * @param idNormal 平常状态的图片
//	 * @param idPressed 按下状态的图片
//	 * @param idSelected 选中状态的图片
//	 * @return
//	 */
//	public StateListDrawable createSelector(int idNormal, int idPressed, int idSelected) { 
//		StateListDrawable bg = new StateListDrawable(); 
//		BitmapDrawable normal = idNormal == -1 ? null : new BitmapDrawable(loadResById(idNormal)); 
//		BitmapDrawable pressed = idPressed == -1 ? null : new BitmapDrawable(loadResById(idPressed)); 
//		BitmapDrawable selected = idSelected == -1 ? null : new BitmapDrawable(loadResById(idSelected)); 
//		// View.PRESSED_ENABLED_STATE_SET 
//		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed); 
//		// View.ENABLED_STATE_SET -表示false状态
//		bg.addState(new int[] { android.R.attr.state_selected }, selected); 
//		// View.EMPTY_STATE_SET 
//		bg.addState(new int[] {}, normal);
//
//		return bg; 
//	}

	/**
	 * @aim 获取资源 资源按照 string+num顺序命名
	 * @param name 资源名
	 * @param index 资源名后带的数字
	 * @return 返回bitmap
	 */
	public Bitmap loadResByName(String name, int index) {
		int resID = frameContext.getResources().getIdentifier(name + index, "drawable",
				frameContext.getPackageName());
		return loadResById(resID);
	}	
	
	/**
	 * @aim ViewPager中的每一个ImageButton对应的相关信息
	 * 
	 * 			currentindex 当前选中的是哪一个按钮
	 * 			imgbtnsum 所有的按钮总共数量
	 * 			imgbtnid 按钮ID名 
	 * 			imgdstid 按钮对应的图片名，就是要拼图的图片名
	 * 			InterludeInfoName 胜利动画名
	 * 			alreadypass 是否已经通过了
	 * 
	 * @author divhee
	 */
	public class ImgBtnAuto {

		/**
		 * 当前选中的是哪一个图片
		 */
		public int currentindex = 0;

		/**
		 * ViewPager所有页面上的图片按钮的总和，一共有多少个按钮
		 */
		public int imgbtnsum = 0;

		/**
		 * 图片按钮名集合
		 */
		public int[] imgbtnid = null;

		/**
		 * 对应的要跳转的图片名集合
		 */
		public int[] imgdstptid = null;

		/**
		 * 对应的游戏胜利动画名集合
		 */
		public String[] InterludeInfoName = null;

		/**
		 * 该图片是否已经成功完成拼图
		 */
		public boolean [] nowinpass = null;

		/**
		 * 游戏胜利后故事情节声音集合
		 */
		public int [] gssndid = null;
		
		/**
		 * 构造
		 */
		public ImgBtnAuto() {
		}
	}

	/**
	 * @aim 每个动画对应的一些特性 
	 * 
	 * 			包括名字，ID号，图片，声音，显示与否，循环与否
	 * 			播放声音，以及位置宽高等信息
	 * 
	 * @author divhee
	 */
	public class Interlude {

		/**
		 * 对应的Index名字便于查找
		 */
		public String myIndex = null;

		/**
		 * 该动画要动画的名字便于查找
		 */
		public String name = null;

		/**
		 * 该动画要要显示的图片总数
		 */
		public int ImgSumNumber = 0;

		/**
		 * 该动画要图片开始的ID号，这个连续着来
		 */
		public int ImgStartId = 0;

		/**
		 * 该动画要显示的图片当前的序号
		 */
		public int ImgCurrentNumber = 0;

		/**
		 * 该动画要要播放的声音总数
		 */
		public int SndSumNumber = 0;

		/**
		 * 该动画要播放声音的ID号
		 */
		public int SndPlayerID[] = null;

		/**
		 * 该动画要播放的声音的实例
		 */
		public MediaPlayer interludeMediaPlayer = null;

		/**
		 * 该动画是否隐藏不显示
		 * true 隐藏
		 * false 显示
		 */
		public boolean isHide = true;

		/**
		 * 该动画是否循环显示播放
		 * true 循环
		 * false 不循环
		 */
		public boolean isLooping = false;

		/**
		 * 图片的显示位置及图片的宽高属性的String
		 */
		public String imgSzPtInfo = null;
		
		/**
		 * 图片的显示位置及图片的宽高的Rect
		 * left和top表示位置的X和Y
		 * right和bottom表示宽和高
		 */
		public Rect imgSzPtRect = new Rect(0, 0, 0, 0);
		
		/**
		 * 构造
		 */
		public Interlude() {
		}
	}

	/**
	 * @aim 读取XML文件夹下的settings.xml中的配置信息
	 * 
	 * 			1、初始化插图动画的相关信息，实例化每一个插图动画Interlude
	 * 			并且把所有的插图动画存入一个ArrayList(gsptInterlude)中
	 * 			name 动画的名字
	 * 			isLooping 是否循环播放显示
	 * 			ImgSumNumber 要显示的图片总数
	 *          ImgStartId 图片开始的ID号，这个连续着来 SndSumNumber要播放的声音总数
	 *          SndPlayerID 播放声音的ID号
	 *          
	 *          2、初始图片的基准信息
	 *          baseWidth 图片的基准宽，默认是800
	 *          baseHeight 图片的基准高，默认是480
	 *          
	 *          3、读取ViewPager中每一屏对应的按钮的跳转及胜利后的动画信息
	 *          imgbtnsum 图片按钮的总数
	 *          imgbtnid 图片按钮的ID号
	 *          imgdstptid 对应要拼图的图片ID号
	 *          InterludeInfoName 对应拼图成功后要显示的动画的动画名
	 *          alreadypass 是否已经通过了
	 *          
	 * @param 无
	 * @return 无
	 */
	public void initXmlConfigInfo() {

		// 获取SharedPreferences对象
		if (gsptSharedPreferences == null){
			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
		}
		// 初始化个数及表
		if (gsptInterlude != null && gsptInterlude.size() > 0) {
			gsptInterlude.clear();
		}
		Resources res = frameContext.getResources();
		XmlResourceParser xrp = res.getXml(R.xml.settings);
		try {
			// 判断是否到了文件的结尾
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				// 文件的内容的起始标签开始，注意这里的起始标签是settings.xml文件里面的
				// <resources>标签下面的标签的起始名字
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String tagname = xrp.getName();
					if (tagname.equals("InterludeInfo")) {
						int count = 0;
						Interlude tmpInterlude = new Interlude();
						tmpInterlude.name = xrp.getAttributeValue(count++);
						tmpInterlude.imgSzPtInfo = xrp.getAttributeValue(count++);
						tmpInterlude.isLooping = xrp.getAttributeBooleanValue(count++, false);
						tmpInterlude.ImgSumNumber = xrp.getAttributeIntValue(count++, 0);
						tmpInterlude.ImgStartId = xrp.getAttributeResourceValue(count++, 0);
						tmpInterlude.SndSumNumber = xrp.getAttributeIntValue(count++, 0);
						if (tmpInterlude.SndSumNumber > 0) {
							tmpInterlude.SndPlayerID = new int[tmpInterlude.SndSumNumber];
							for (int number = 0; number < tmpInterlude.SndSumNumber; number++) {
								tmpInterlude.SndPlayerID[number] = xrp.getAttributeResourceValue(count++, 0);
							}
						}
						tmpInterlude.isHide = true;
						tmpInterlude.ImgCurrentNumber = 0;
						if (tmpInterlude.imgSzPtInfo != null) {
							int[] szpt = new int[] { 0, 0, 0, 0 };
							String[] tmpSzPt = tmpInterlude.imgSzPtInfo.split("\\|");
							for (int number = 0; number < szpt.length
									&& number < tmpSzPt.length; number++) {
								szpt[number] = Integer.valueOf(tmpSzPt[number]).intValue();
							}
							tmpInterlude.imgSzPtRect.set(szpt[0], szpt[1], szpt[2], szpt[3]);
						}

						// 添加进ArrayList表
						gsptInterlude.add(tmpInterlude);
						
					} else if (tagname.equals("gamebaseinfo")) {
						// 获取图片基准信息，所有图片都是以这个基准来配置的
						// 如果读取不到默认是800 * 480大小
						int count = 0;
						baseWidth = xrp.getAttributeIntValue(count++, 1280);
						baseHeight = xrp.getAttributeIntValue(count++, 800);
						
					} else if (tagname.equals("imgbtnatuoinfo") && ENTER_NORMAL == systemEnterMode) {
						// 获取图片按钮的相关配置信息
						// imgbtnsum 图片按钮的总数
						// imgbtnid 图片按钮的ID号
						// imgdstptid 对应要拼图的图片ID号
						// InterludeInfoName 对应拼图成功后要显示的动画的动画名
						// nowinpass 是否已经通过了
						int count = 0;
						gsptImgBtnAuto.currentindex = -1;
						gsptImgBtnAuto.imgbtnsum = xrp.getAttributeIntValue(
								count++, 0);
						if (gsptImgBtnAuto.imgbtnsum > 0) {
							gsptImgBtnAuto.imgbtnid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.imgdstptid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.InterludeInfoName = new String[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.nowinpass = new boolean[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.gssndid = new int[gsptImgBtnAuto.imgbtnsum];
							for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
								gsptImgBtnAuto.imgbtnid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.imgdstptid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.InterludeInfoName[number] = xrp.getAttributeValue(count++);
								gsptImgBtnAuto.gssndid[number] = xrp.getAttributeResourceValue(count++, 0);

								MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
								gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//								Log.d("", number+"======divhee=========read==1=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "======nowinpass====" + gsptImgBtnAuto.nowinpass[number]);
//								if (gsptSharedPreferences != null){
//									gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//											getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//								}
							}
						}
					} else if (tagname.equals("imgbtnatuoinfoext") && ENTER_EXTCALL == systemEnterMode) {
						// 获取图片按钮的相关配置信息
						// imgbtnsum 图片按钮的总数
						// imgbtnid 图片按钮的ID号
						// imgdstptid 对应要拼图的图片ID号
						// InterludeInfoName 对应拼图成功后要显示的动画的动画名
						// nowinpass 是否已经通过了
						int count = 0;
						gsptImgBtnAuto.currentindex = -1;
						gsptImgBtnAuto.imgbtnsum = xrp.getAttributeIntValue(
								count++, 0);
						if (gsptImgBtnAuto.imgbtnsum > 0) {
							gsptImgBtnAuto.imgbtnid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.imgdstptid = new int[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.InterludeInfoName = new String[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.nowinpass = new boolean[gsptImgBtnAuto.imgbtnsum];
							gsptImgBtnAuto.gssndid = new int[gsptImgBtnAuto.imgbtnsum];
							for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
								gsptImgBtnAuto.imgbtnid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.imgdstptid[number] = xrp.getAttributeResourceValue(count++, 0);
								gsptImgBtnAuto.InterludeInfoName[number] = xrp.getAttributeValue(count++);
								gsptImgBtnAuto.gssndid[number] = xrp.getAttributeResourceValue(count++, 0);

								MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
								gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//								Log.d("", number+"======divhee=========read==2=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "======nowinpass====" + gsptImgBtnAuto.nowinpass[number]);
//								if (gsptSharedPreferences != null){
//									gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//											getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//								}
							}
						}
					}
				} else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
				} else if (xrp.getEventType() == XmlResourceParser.TEXT) {
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xrp != null) {
				xrp.close();
			}
		}
	}
	
	/**
	 * @aim 获取上一次保存的用户难度值
	 * @param 无
	 * @return 难度值
	 */
	public int readSPUserAgeLevelConfig() {
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		if (gsptSharedPreferences != null) {
//			return gsptSharedPreferences.getInt("FormerUserLevel", 1);
//		}
//		return 1;
		Log.d("", "======divhee=========readSPUserAgeLevelConfig====");
		try {
			MessageCenter msgCenter = SystemDataBase.queryMsgCS("FormerUserLevel", USER_ID_DEFAULT);
			if (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content)) {
				return Integer.parseInt(msgCenter.msgc_content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}
	
	/**
	 * @aim 年级改变，读取当前年级的配置值
	 * @param 无
	 * @return 无
	 */
	public void readSPConfigLevelChange() {
//		// 获取SharedPreferences对象
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		if (gsptSharedPreferences != null){
//			for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
//				gsptImgBtnAuto.nowinpass[number] = gsptSharedPreferences.
//						getBoolean(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), true);
//			}
//		}
//		Log.d("", "======divhee=========readSPConfigLevelChange====");
		for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
			MessageCenter msgCenter = SystemDataBase.queryMsgCS(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), USER_ID_DEFAULT);
			gsptImgBtnAuto.nowinpass[number] = (msgCenter == null || (msgCenter != null && !TextUtils.isEmpty(msgCenter.msgc_content) && msgCenter.msgc_content.equals("1")));
//			Log.d("", number+"======divhee=========read==3=="+(gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal")) + "====nowinpass==" + gsptImgBtnAuto.nowinpass[number] + "===" + msgCenter.msgc_content);
		}
	}
	
	/**
	 * @aim 记录在/data/data/com.readboy.Q.Gspt/shared_prefs目录下生成了一个GsptSpCfg.xml文件里
	 * 			把用户完成了哪些拼图，哪些拼图没有完成记录下来，没有完成的加锁，完成了的解锁
	 *          
	 * @param 无
	 * @return 无
	 */
	public void saveSPPlayerConfig(boolean bLastExit) {
		try {
//			Log.d("", GsptMainActivity.getSystemUserAge()+"======divhee=========saveSPPlayerConfig===="+bLastExit);
			if (bLastExit){
				MessageCenter msgCenter = new MessageCenter();
				msgCenter.msgc_vid = "FormerUserLevel";
				msgCenter.msgc_content = GsptMainActivity.getSystemUserAge()+"";
				msgCenter.uid = USER_ID_DEFAULT;
				SystemDataBase.insertMsgCS(msgCenter);
			} else {
				// 存入数据
				MessageCenter msgCenter = null;
				for (int number = 0; number < gsptImgBtnAuto.imgbtnsum ; number++) {
					msgCenter = new MessageCenter();
					msgCenter.msgc_vid = gsptImgBtnAuto.InterludeInfoName[number] + GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal");
					msgCenter.msgc_content = gsptImgBtnAuto.nowinpass[number] ? "1" : "0";
					msgCenter.uid = USER_ID_DEFAULT;
					if (SystemDataBase.queryMsgCS(msgCenter.msgc_vid, USER_ID_DEFAULT) == null) {
						SystemDataBase.insertMsgCS(msgCenter);
					} else {
						SystemDataBase.updateMsgCS(msgCenter);
					}
//					Log.d("", number+"==divhee==save==18=="+msgCenter.msgc_vid  + "==nowinpass==" + msgCenter.msgc_content);
				}
				msgCenter = new MessageCenter();
				msgCenter.msgc_vid = "FormerUserLevel";
				msgCenter.msgc_content = GsptMainActivity.getSystemUserAge()+"";
				msgCenter.uid = USER_ID_DEFAULT;
				SystemDataBase.insertMsgCS(msgCenter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		// 获取SharedPreferences对象
//		if (gsptSharedPreferences == null){
//			gsptSharedPreferences = frameContext.getSharedPreferences(gstpSpWinPassConfig, Context.MODE_PRIVATE);
//		}
//		try {
//			if (gsptSharedPreferences != null){
//				if (bLastExit){
//					Editor editor = gsptSharedPreferences.edit();
////					editor.clear();
////					editor.commit();
//					long ltime = System.currentTimeMillis();
//					editor.putLong("LastDestory", ltime);
//					editor.putInt("FormerUserLevel", GsptMainActivity.getSystemUserAge());
////					Log.w("edugame", "===saveSPPlayerConfig===" + ltime);
//					editor.commit();
//				} else {
//					// 存入数据
//					Editor editor = gsptSharedPreferences.edit();
////					editor.clear();
////					editor.commit();
//					for (int number = 0; number < gsptImgBtnAuto.imgbtnsum; number++) {
//						editor.putBoolean(gsptImgBtnAuto.InterludeInfoName[number]
//								+ GsptMainActivity.getSystemUserAge() + (bGsptGameStyle ? "hard" : "normal"), gsptImgBtnAuto.nowinpass[number]);
//					}
//					editor.putInt("FormerUserLevel", GsptMainActivity.getSystemUserAge());
//					editor.commit();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * @aim 获取插图动画的实例信息
	 * @param name
	 *            插图动画名
	 * @return Interlude 相应的插图类实例信息
	 */
	public Interlude getInterludeByName(String name) {

		if (gsptInterlude != null && gsptInterlude.size() > 0) {
			for (Interlude item : gsptInterlude) {
				if (item.name.equals(name)) {
					return item;
				}
			}
		}

		return null;
	}

	/**
	 * @aim 绘制曲线用的类
	 * @author Administrator
	 *
	 */
	public class Cubic {

		/**
		 * 重要参数，两点之间分为几段描画，数字愈大分段越多，描画的曲线就越精细.
		 */
		public static final int STEPS = 12;		
		
		/**
		 * a + b*u + c*u^2 +d*u^3
		 */
		private float a = 0, b = 0, c = 0, d = 0;

		/**
		 * @aim 构造函数
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 */
		public Cubic(float a, float b, float c, float d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		/**
		 * evaluate cubic
		 */
		public float eval(float u) {
			return (((d * u) + c) * u + b) * u + a;
		}
	}	
	
	/**
	 * @aim 把一张长方形或者矩形的图片按游戏等级切割成6块、8块、12快等不均的块数
	 * 			切割出来的图片分为长方形，正方形，三角形，平行四边形，等边梯形等。
	 * 			这里保存这这些形状的信息
	 * 			srcSharpPath 图片显示路径
	 * 			srcSharpRect 图片矩形区域大小
	 * 			smlSharpPath 显示小图片的时候的路径
	 * 			smlSharpRect 显示小图片的矩形区域
	 * 			srcPoint 图片在大图片里的重心位置
	 * 			smlPoint 在小图片里的重心位置
	 * 			imgViewInScrollView 要创建的小图片实例等
	 * @author divhee
	 *
	 */
	public class GameSharps {

		/**
		 * 图形的矩形
		 */
		public Rect srcSharpRect = new Rect();

		/**
		 * 图形的路径
		 */
		public Path srcSharpPath = new Path();

		/**
		 * 重心位置
		 */
		public Point srcPoint = new Point();

		/**
		 * 重心位置
		 */
		public Point smlPoint = new Point();

		/**
		 * 绘制小图的时候的路径
		 */
		public Path smlSharpPath = new Path();

		/**
		 * 绘制小图的时候的区域
		 */
		public Rect smlSharpRect = new Rect();

		/**
		 * 对应的View在ScrollView当中
		 */
		public ImageView imgViewInScrollView = null;

	}
	
	/**
	 * 年龄段，图片区分用
	 */
	public static final int levelSml = 0;
	public static final int levelMid = 1;
	public static final int levelBig = 2;
	public static final int levelSum = 3;
	
	/**
	 * 随机数
	 */
	private static Random rand = new Random(System.currentTimeMillis());	
	
	/**
	 * 要切割出来的区域信息多少个区域
	 */
	private ArrayList<GameSharps> areaSharps = new ArrayList<GameSharps>();	
	
	/**
	 * 拼图图片的宽度
	 */
	private static int gsptBmpWidth = 776;
	
	/**
	 * 拼图图片的高度
	 */
	private static int gsptBmpHeight = 566;	
	
	/**
	 * 年龄大小
	 */
	private static int userAgeLevel = 0;

	/**
	 * 哪些要切成特殊图形，梯形或者平行四边形
	 */
	private int[] espsharpstwo = new int[3];
	
	/**
	 * 哪些要切成三角形
	 */
	private int[] espsharpsthree = new int[6];
	
	/**
	 * 特殊图形顺序
	 */
	private int espsharpsthreeindex = 0;	
	
	/**
	 * @aim 获取随即数
	 * @param 无
	 * @return int 型随机数，取绝对值，全是正数
	 */
	public static int getRandom() {
		return Math.abs(rand.nextInt());
	}
	
	/**
	 * @aim 获取所有形状的形状列表
	 * @param 无
	 * @return ArrayList<GameSharps>形状列表
	 */
	public ArrayList<GameSharps> getGameSharps(){
		
		return areaSharps;
	}
	
	/**
	 * @aim 真正通过给定矩形块，生成一个矩形形状，记录该形状的相关信息
	 * 			生成的图形信息记录于ArrayList变量areaSharps中
	 * @param rtF 源矩形用来生成矩形形状
	 * @return 无
	 */
	public void getRealRectangleSharp_Son(RectF rtF) {
		
		// 产生矩形图形
		GameSharps ngameSharps = new GameSharps();
		ngameSharps.srcSharpPath.addRect(rtF, Path.Direction.CW);
		ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
		ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
		ngameSharps.smlSharpPath.addRect(new RectF(ngameSharps.smlSharpRect), Path.Direction.CW);
		ngameSharps.srcPoint.set((int) (rtF.left + rtF.width() / 2),
				(int) (rtF.top + rtF.height() / 2));
		ngameSharps.smlPoint.set((int) (rtF.width() / 2),
				(int) (rtF.height() / 2));
		
		// 随即存入areaSharps中，不要顺序存取，否则到时候还需要打乱
		// 拼图的图形列表不能有规律的顺序摆放
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
	}

	/**
	 * @aim 真正通过给定矩形块，生成一个三角形形状，记录该形状的相关信息
	 * 			切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
	 * 			切割线是"/"则切割线左边的为第一个三角形，右边为第二个三角形
	 * 			切割线是"\"则切割线左边的为第一个三角形，右边为第二个三角形
	 * @param rtF 源矩形用来生成三角形
	 * @param leftRight 切割线是"/"还是"\"
	 * @param cutLeftRight 取第一个三角形还是第二个三角形
	 * @return 无
	 */
	public void getRealTriangleSharp_Son(RectF rtF, int leftRight, int cutLeftRight) {
		
		if ((leftRight == 1 || leftRight == 2)
				&& (cutLeftRight == 1 || cutLeftRight == 2)) {
			if (leftRight == 1) {
				GameSharps ngameSharps = new GameSharps();
				if (cutLeftRight == 1) {
					// 左切第一个三角形
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 1 / 4),
							(int) (rtF.top + rtF.height() * 1 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 1 / 4),
							(int) (rtF.height() * 1 / 4));
				} else if (cutLeftRight == 2) {
					// 左切第二个三角形
					ngameSharps.srcSharpPath.moveTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(rtF.right - rtF.left, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 3 / 4),
							(int) (rtF.top + rtF.height() * 3 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 3 / 4),
							(int) (rtF.height() * 3 / 4));
				}
				ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
				ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
				
				// 随即存入areaSharps中，不要顺序存取，否则到时候还需要打乱
				// 拼图的图形列表不能有规律的顺序摆放				
				areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
			} else if (leftRight == 2) {
				GameSharps ngameSharps = new GameSharps();
				if (cutLeftRight == 1) {
					// 右切第一个三角形
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.left, rtF.bottom);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(0, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 1 / 4),
							(int) (rtF.top + rtF.height() * 3 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 1 / 4),
							(int) (rtF.height() * 3 / 4));
				} else if (cutLeftRight == 2) {
					// 右切第二个三角形
					ngameSharps.srcSharpPath.moveTo(rtF.left, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.top);
					ngameSharps.srcSharpPath.lineTo(rtF.right, rtF.bottom);
					ngameSharps.srcSharpPath.close();
					ngameSharps.smlSharpPath.moveTo(0, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left, 0);
					ngameSharps.smlSharpPath.lineTo(rtF.right - rtF.left,
							rtF.bottom - rtF.top);
					ngameSharps.smlSharpPath.close();
					ngameSharps.srcPoint.set(
							(int) (rtF.left + rtF.width() * 3 / 4),
							(int) (rtF.top + rtF.height() * 1 / 4));
					ngameSharps.smlPoint.set((int) (rtF.width() * 3 / 4), 
							(int) (rtF.height() * 1 / 4));
				}
				ngameSharps.srcSharpRect.set((int) rtF.left, (int) rtF.top, (int) rtF.right, (int) rtF.bottom);
				ngameSharps.smlSharpRect.set(0, 0, (int) rtF.width(), (int) rtF.height());
				
				// 随即存入areaSharps中，不要顺序存取，否则到时候还需要打乱
				// 拼图的图形列表不能有规律的顺序摆放				
				areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
			}
		}
	}

	/**
	 * @aim 真正通过给定矩形块，生成两个三角形形状，以及一个梯形或者平行四边形记录该形状的相关信息
	 * 			切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
	 * @param rtF1 给定左边矩形用于生成左边的第二个三角形、合并到梯形或者平行四边形中
	 * @param rtF2 给定中间矩形用于生成梯形或者平行四边形
	 * @param rtF3 给定左边矩形用于生成右边的第一个三角形、合并到梯形或者平行四边形中
	 * @param leftRight 做切还是又切
	 * @param cutLeftRight
	 */
	public void getRealQuadrangleSharp_Son(RectF rtF1, RectF rtF2, RectF rtF3,
			int dirleft, int dirRight) {

		GameSharps ngameSharps = new GameSharps();
		if (dirleft == 1 && dirRight == 1) {
			ngameSharps.srcSharpPath.moveTo(rtF1.right, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.left, rtF1.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.right - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.left - rtF2.left, rtF1.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 1 && dirRight == 2) {
			ngameSharps.srcSharpPath.moveTo(rtF1.right, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.left, rtF1.bottom);
			if (rtF1.left == rtF3.left) {
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
			}
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.right - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.left - rtF2.left, rtF1.bottom - rtF2.top);
			if (rtF1.left == rtF3.left) {
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
			}
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 2 && dirRight == 1) {
			ngameSharps.srcSharpPath.moveTo(rtF1.left, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.right, rtF1.bottom);
			if (rtF1.left == rtF3.left) {
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.bottom);
				ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.top);
			}
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.left - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.right - rtF2.left, rtF1.bottom - rtF2.top);
			if (rtF1.left == rtF3.left) {
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
			} else if (rtF1.top == rtF3.top) {
				ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.bottom - rtF2.top);
				ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.top - rtF2.top);
			}
			ngameSharps.smlSharpPath.close();
		}
		if (dirleft == 2 && dirRight == 2) {
			ngameSharps.srcSharpPath.moveTo(rtF1.left, rtF1.top);
			ngameSharps.srcSharpPath.lineTo(rtF1.right, rtF1.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.right, rtF3.bottom);
			ngameSharps.srcSharpPath.lineTo(rtF3.left, rtF3.top);
			ngameSharps.srcSharpPath.close();
			ngameSharps.smlSharpPath.moveTo(rtF1.left - rtF2.left, rtF1.top - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF1.right - rtF2.left, rtF1.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.right - rtF2.left, rtF3.bottom - rtF2.top);
			ngameSharps.smlSharpPath.lineTo(rtF3.left - rtF2.left, rtF3.top - rtF2.top);
			ngameSharps.smlSharpPath.close();
		}
		ngameSharps.srcSharpRect.set((int) rtF2.left, (int) rtF2.top, (int) rtF2.right, (int) rtF2.bottom);
		ngameSharps.smlSharpRect.set(0, 0, (int) rtF2.width(), (int) rtF2.height());
		ngameSharps.srcPoint.set((int) (rtF2.left + rtF2.width() / 2), (int) (rtF2.top + rtF2.height() / 2));
		ngameSharps.smlPoint.set((int) (rtF2.width() / 2), (int) (rtF2.height() / 2));
		
		// 随即存入areaSharps中，不要顺序存取，否则到时候还需要打乱
		// 拼图的图形列表不能有规律的顺序摆放		
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
		
		// Log.w("divhee_edugame", "==getQuadrangleSharp==15=(" + rtF1.left +
		// "," + rtF1.top + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==16=(" + rtF1.right +
		// "," + rtF1.bottom + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==17=(" + rtF3.right +
		// "," + rtF3.bottom + ")===");
		// Log.w("divhee_edugame", "==getQuadrangleSharp==18=(" + rtF3.left +
		// "," + rtF3.top + ")===");
	}

	/**
	 * @aim 低难度，非空矩形全部生成矩形形状
	 * @param rtF1 第一个源矩形
	 * @param rtF2 第二个源矩形
	 * @param rtF3 第三个源矩形
	 * @return 无
	 */
	public void getSharpTypeRectangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		// 生成第一个矩形形状
		if (rtF1 != null && !rtF1.isEmpty()) {
			getRealRectangleSharp_Son(rtF1);
		}
		
		// 生成第二个矩形形状
		if (rtF2 != null && !rtF2.isEmpty()) {
			getRealRectangleSharp_Son(rtF2);
		}
		
		// 生成第三个矩形形状
		if (rtF3 != null && !rtF3.isEmpty()) {
			getRealRectangleSharp_Son(rtF3);
		}
	}

	/**
	 * @aim 生产三角形和矩形图形
	 * @param rtF1 源矩形用于生成两个三角形形状
	 * @param rtF2 源矩形用于生成矩形形状
	 * @param leftRight 三角形切割线是"/"还是"\"(注：1表"/" 2表"\")
	 * @return 无
	 */
	public void getSharpTypeTriangleRectangle(RectF rtF1, RectF rtF2, int leftRight) {
		
		// 切割与否,0不,1左切割,2右切割
		if (leftRight == 1) {
			if (rtF1 != null && !rtF1.isEmpty()) {
				// 左切第一个三角形
				getRealTriangleSharp_Son(rtF1, leftRight, 1);
				// 左切第二个三角形
				getRealTriangleSharp_Son(rtF1, leftRight, 2);
			}
			// 长方形
			if (rtF2 != null && !rtF2.isEmpty()) {
				getRealRectangleSharp_Son(rtF2);
			}
		} else if (leftRight == 2) {
			if (rtF1 != null && !rtF1.isEmpty()) {
				// 右切第一个三角形
				getRealTriangleSharp_Son(rtF1, leftRight, 1);
				// 右切第二个三角形
				getRealTriangleSharp_Son(rtF1, leftRight, 2);
			}
			// 长方形
			if (rtF2 != null && !rtF2.isEmpty()) {
				getRealRectangleSharp_Son(rtF2);
			}
		}
	}

	/**
	 * @aim 通过三个矩形，切割成链两个三角行形状和一个梯形形状(或平行四边形形状)记录且保存形状信息
	 * 			这三个矩形要么top一致，要么left一致，出中间矩形外，另外两个矩形一样大小，
	 * 			保证切出来的平行四边形或者梯形是平行的或者等边的，图片比较美观
	 * 			切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
	 * 			切割线是"/"则切割线左边的为第一个三角形，右边为第二个三角形
	 * 			切割线是"\"则切割线左边的为第一个三角形，右边为第二个三角形
	 * @param rtF1 第三个源矩形
	 * @param rtF2 第三个源矩形
	 * @param rtF3 第三个源矩形
	 * @return 无
	 */
	public void getSharpTypeTrapeziaTriangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		int dirLeft = (getRandom() % 2) + 1;
		int dirRight = (getRandom() % 2) + 1;

		if (rtF1 != null && !rtF1.isEmpty() && rtF2 != null && !rtF2.isEmpty()
				&& rtF3 != null && !rtF3.isEmpty()) {
			// 最后的参数表示选第一个还是第二个三角形
			if (rtF1.left == rtF3.left) {
				if (dirLeft == 1) {
					// rtF1左切第一个
					getRealTriangleSharp_Son(rtF1, dirLeft, 1);
				} else if (dirLeft == 2) {
					// rtF1右切第二个
					getRealTriangleSharp_Son(rtF1, dirLeft, 2);
				}
				if (dirRight == 1) {
					// rtF3 左切第二个
					getRealTriangleSharp_Son(rtF3, dirRight, 2);
				} else if (dirRight == 2) {
					// rtF3右切第一个
					getRealTriangleSharp_Son(rtF3, dirRight, 1);
				}
			} else if (rtF1.top == rtF3.top) {
				// 左切左第一个三角行或者右切左第一个三角形
				getRealTriangleSharp_Son(rtF1, dirLeft, 1);

				// 左切右第二个三角形或者右切右第二个三角形
				getRealTriangleSharp_Son(rtF3, dirRight, 2);
			}

			// 生产四角行，等边梯形，平行四边形
			getRealQuadrangleSharp_Son(rtF1, rtF2, rtF3, dirLeft, dirRight);
		}
	}

	/**
	 * @aim 通过三个矩形，切割成链两个三角行形状或者矩形形状，记录且保存形状信息
	 * 			具体要生成些什么形状要根据espsharpsthree初始化的时候设定的值来生成
	 * 			初始化的时候以保证了形状的个数，以及随即性
	 * 			切割三角形分切割线是"/"或者"\"(注：1表"/" 2表"\")
	 * 			切割线是"/"则切割线左边的为第一个三角形，右边为第二个三角形
	 * 			切割线是"\"则切割线左边的为第一个三角形，右边为第二个三角形 
	 * @param rtF1 第三个源矩形
	 * @param rtF2 第三个源矩形
	 * @param rtF3 第三个源矩形
	 * @return 无
	 */
	public void getSharpTypeRandomTriangleOrRectangle(RectF rtF1, RectF rtF2, RectF rtF3) {
		
		// 随即左切割还是右切割
		int leftRight = 0;
		for (int index = 0; index < 3; index++) {
			leftRight = (getRandom() % 2) + 1;
			if (index == 0) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF1, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF1, leftRight);
				}
			} else if (index == 1) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF2, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF2, leftRight);
				}
			} else if (index == 2) {
				if (espsharpsthree[espsharpsthreeindex] == 1) {
					getSharpTypeTriangleRectangle(rtF3, null, leftRight);
				} else {
					getSharpTypeTriangleRectangle(null, rtF3, leftRight);
				}
			}
		}
	}

	/**
	 * @aim 通过玩家年龄基本划分游戏难度，获取不同难度下的拼图形状
	 * 			levelSml 6个形状左右，全是长方形(可能有正方形)，形状位置大小随机
	 * 			levelMid 9个形状左右，分长方形(可能有正方形)、三角形，都必须有，个数位置大小随机
	 * 			levelBig 12个形状左右，分平行四边形或者梯形、三角形、长方形(正方形)，
	 * 			三角形和平行四边形或者梯形必须有，个数位置大小随机
	 * 
	 * @param rtF1 第一个源矩形
	 * @param rtF2 第二个源矩形
	 * @param rtF3 第三个源矩形
	 * @return 无
	 */
	public void getSharpsByLevel(RectF rtF1, RectF rtF2, RectF rtF3, int needespsharp) {

		if (userAgeLevel == levelSml) {
			// 最简单的模式
			getSharpTypeRectangle(rtF1, rtF2, rtF3);

		} else if (userAgeLevel == levelMid) {
			// 合并与否,0不,1最左边2最右边3全部
			int needadd = 0;
			if (espsharpstwo[needespsharp] != 0) {
				needadd = (getRandom() % 3) + 1;
			}
			// 切割与否,1左切割,2右切割，合并后必定切
			int needcut = getRandom() % 3;
			if (needadd == 0) {
				getSharpTypeRectangle(rtF1, rtF2, rtF3);
			} else if (needadd == 1) {
				if (rtF1.left == rtF2.left) {
					rtF1.bottom = rtF2.bottom;
					rtF2.set(0, 0, 0, 0);
				} else if (rtF1.top == rtF2.top) {
					rtF1.right = rtF2.right;
					rtF2.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF1, rtF3, needcut);
				}
			} else if (needadd == 2) {
				if (rtF2.left == rtF3.left) {
					rtF2.bottom = rtF3.bottom;
					rtF3.set(0, 0, 0, 0);
				} else if (rtF2.top == rtF3.top) {
					rtF2.right = rtF3.right;
					rtF3.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF2, rtF1, needcut);
				}
			} else if (needadd == 3) {
				if (rtF1.left == rtF3.left) {
					rtF1.bottom = rtF3.bottom;
					rtF2.set(0, 0, 0, 0);
					rtF3.set(0, 0, 0, 0);
				} else if (rtF1.top == rtF3.top) {
					rtF1.right = rtF3.right;
					rtF2.set(0, 0, 0, 0);
					rtF3.set(0, 0, 0, 0);
				}
				if (needcut == 0) {
					getSharpTypeRectangle(rtF1, rtF2, rtF3);
				} else {
					getSharpTypeTriangleRectangle(rtF1, rtF2, needcut);
				}
			}
		} else if (userAgeLevel == levelBig) {
			// 特殊平行四边形 特殊梯形
			// 切割与否,1左切割,2右切割，特殊的时候是上切割，下切割
			if ((needespsharp == 0 && espsharpstwo[0] == 1)
					|| (needespsharp == 1 && espsharpstwo[1] == 1)
					|| (needespsharp == 2 && espsharpstwo[2] == 1)) {
				if (rtF1.left == rtF3.left) {
					rtF2.top = rtF1.top;
					rtF2.bottom = rtF3.bottom;
					if ((getRandom() % 2) == 0) {
						rtF3.top = rtF3.bottom - rtF1.height();
					} else {
						rtF1.bottom = rtF1.top + rtF3.height();
					}
					// 生成平行四边形或者梯形以及三角形
					getSharpTypeTrapeziaTriangle(rtF1, rtF2, rtF3);
				} else if (rtF1.top == rtF3.top) {
					rtF2.left = rtF1.left;
					rtF2.right = rtF3.right;
					if ((getRandom() % 2) == 0) {
						rtF3.left = rtF3.right - rtF1.width();
					} else {
						rtF1.right = rtF1.left + rtF3.width();
					}
					// 生成平行四边形或者梯形以及三角形					
					getSharpTypeTrapeziaTriangle(rtF1, rtF2, rtF3);
				}
			} else {
				
				// 随即生成三角形形状或者矩形形状
				getSharpTypeRandomTriangleOrRectangle(rtF1, rtF2, rtF3);
			}
		}
	}

	/**
	 * @aim 初始化路径，及其记录的路径信息
	 * 			切割出来的图形形状信息记录于areaSharps当中
	 * 			思路：固定两行或者两列，然后再切割，分成不等的九宫格，然后根据难度
	 * 			合并或者切割合并的九宫格，切割完后拼接成平行四边形或者梯形
	 * @param 无
	 * @return 无
	 */
	public void initRandomSharpsPath() {
		// 游戏年级等级
//		if (GsptMainActivity.getSystemUserAge() == 1){
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelSml;
//		} else {
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelBig;
//		}
		switch (GsptMainActivity.getSystemUserAge()) {
		case 3:
			userAgeLevel = levelBig;
			break;
		case 2:
			userAgeLevel = levelMid;
			break;
		case 1:
		default:
			userAgeLevel = levelSml;
			break;
		}
		// 图形集合
		if (areaSharps != null) {
			if (areaSharps.size() > 0) {
				areaSharps.clear();
			}
		} else {
			areaSharps = new ArrayList<GameSharps>();
		}

		// 初始化哪些要切割成梯形或平行四边形
		espsharpstwo[0] = (getRandom() % 2);
		espsharpstwo[1] = (getRandom() % 2);
		espsharpstwo[2] = (getRandom() % 2);
		int esptwonumber = espsharpstwo[2] + espsharpstwo[1] + espsharpstwo[0];
		if (esptwonumber == 0) {
			espsharpstwo[getRandom() % 3] = 1;
			esptwonumber++;
		}
		
		// 初始化哪些矩形要切割成三角行
		espsharpsthreeindex = 0;
		for (int index = 0; index < espsharpsthree.length; index++) {
			if (esptwonumber > 1) {
				if (index < 3) {
					espsharpsthree[index] = 1;
				} else {
					espsharpsthree[index] = 0;
				}
			} else if (esptwonumber == 1) {
				espsharpsthree[index] = 1;
			}
		}
		if (esptwonumber == 1) {
			int deletethree = getRandom() % 4;
			for (int num = 0; num < deletethree; num++) {
				espsharpsthree[getRandom() % espsharpsthree.length] = 0;
			}
		}

		// 0横向，1竖向
		RectF pathRect = new RectF(0, 0, 0, 0);
		RectF tmpRectF1 = new RectF(0, 0, 0, 0);
		RectF tmpRectF2 = new RectF(0, 0, 0, 0);
		RectF tmpRectF3 = new RectF(0, 0, 0, 0);
		
		// 横向固定还是竖向固定
		int dirVHfinal = getRandom() % 2;
		
		// 有几根固定用于年龄小的时候，图片分割数为6块矩形用
		// dirLost == 0或者 1，分成9宫格
		int dirHold = 2;
		int dirTimes = 2;
		if (userAgeLevel == levelSml) {
			if ((getRandom() % 2) == 0) {
				dirHold -= 1;
			} else {
				dirTimes -= 1;
			}
		}
		if (App.getInstance().mScale>1.0f){
			float w = 776*App.getInstance().mScale;
			float h = 566*App.getInstance().mScale;
			gsptBmpWidth=(int)w;
			gsptBmpHeight=(int)h;
		}
		// 初始化要切割的基准宽高，能随机多宽和多高 
		int basew0 = (gsptBmpWidth * 9 / 10) / 3;
		int basew1 = (gsptBmpWidth * 1 / 10) / 2;
		int baseh0 = (gsptBmpHeight * 9 / 10) / 3;
		int baseh1 = (gsptBmpHeight * 1 / 10) / 2;
		// 以前是随即，根本不会对齐，现在会对齐了。固定随机
		int [] baserand = {getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom()};
		
		pathRect.set(0, 0, 0, 0);
		if (dirVHfinal == 0) {
			// 竖向固定
			for (int addx = 0; addx <= dirHold; addx++) {
				pathRect.left = pathRect.right;
				if (addx == dirHold) {
					pathRect.right = gsptBmpWidth;
				} else {
					pathRect.right = pathRect.left + basew0 + (getRandom() % basew1);
					// 只分两列，简单模式有效
					if (dirHold < dirTimes) {
						pathRect.right += ((basew0 + (getRandom() % basew1)) / 2);
					}
				}
				pathRect.top = 0;
				pathRect.bottom = 0;
				for (int addy = 0; addy <= dirTimes; addy++) {
					pathRect.top = pathRect.bottom;
					if (addy == dirTimes) {
						pathRect.bottom = gsptBmpHeight;
					} else {
						pathRect.bottom = pathRect.top + baseh0 + (baserand[addy] % baseh1);
						// 只分两行，简单模式有效
						if (dirTimes < dirHold) {
							pathRect.bottom += ((baseh0 + (baserand[addy] % baseh1)) / 2);
						}
					}
					if (addy == 0) {
						tmpRectF1.set(pathRect);
					} else if (addy == 1) {
						tmpRectF2.set(pathRect);
					} else {
						tmpRectF3.set(pathRect);
					}
				}
				
				// 根据年龄等级区分游戏难度，根据难度获取真实路径信息，全部存于areaSharps当中
				getSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, addx);
				
			}
		} else {
			// 横向固定
			for (int addy = 0; addy <= dirHold; addy++) {
				pathRect.top = pathRect.bottom;
				if (addy == dirHold) {
					pathRect.bottom = gsptBmpHeight;
				} else {
					pathRect.bottom = pathRect.top + baseh0 + (getRandom() % baseh1);
					// 只分两行，简单模式有效
					if (dirHold < dirTimes) {
						pathRect.bottom += ((baseh0 + (getRandom() % baseh1)) / 2);
					}
				}
				pathRect.left = 0;
				pathRect.right = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				for (int addx = 0; addx <= dirTimes; addx++) {
					pathRect.left = pathRect.right;
					if (addx == dirTimes) {
						pathRect.right = gsptBmpWidth;
					} else {
						pathRect.right = pathRect.left + basew0 + (baserand[addx] % basew1);
						// 只分两列，简单模式有效
						if (dirTimes < dirHold) {
							pathRect.right += ((basew0 + (baserand[addx] % basew1)) / 2);
						}
					}
					if (addx == 0) {
						tmpRectF1.set(pathRect);
					} else if (addx == 1) {
						tmpRectF2.set(pathRect);
					} else {
						tmpRectF3.set(pathRect);
					}
				}
				
				// 根据年龄等级区分游戏难度，根据难度获取真实路径信息，全部存于areaSharps当中
				getSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, addy);
				
			}
		}
	}	
	
	/**
	 * 计算曲线.
	 * 
	 * @param x
	 * @return
	 */
	private List<Cubic> calculate(List<Integer> x) {
		int n = x.size() - 1;
		float[] gamma = new float[n + 1];
		float[] delta = new float[n + 1];
		float[] D = new float[n + 1];
		int i;
		/*
		 * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]|
		 * |3(x[2] - x[0]) | | 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4
		 * 1| | . | |3(x[n] - x[n-2])| [ 1 2] [D[n]] [3(x[n] - x[n-1])]
		 * 
		 * by using row operations to convert the matrix to upper triangular and
		 * then back sustitution. The D[i] are the derivatives at the knots.
		 */

		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
					* gamma[i];
		}
		delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		List<Cubic> cubics = new LinkedList<Cubic>();
		for (i = 0; i < n; i++) {
			Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
					- 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
					+ D[i + 1]);
			cubics.add(c);
		}
		return cubics;
	}	
	
	/**
	 * @aim 获取带凹陷或者突出的矩形的一条边的路径
	 * @param points
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public List<Point> getHardCurvePoint(int x1, int y1, int x2, int y2) {
		List<Point> points = new LinkedList<Point>();
		int midPtSelf = 0, midPtStart = 0;
		boolean bNotEspLine = true;
//		Log.w("edugame", "====getHardCurvePoint=======(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")");
		Point curPoint = null;
		if (x1 == x2) {
			midPtSelf = (y1 + y2) / 2;
			for (int index = 0 ; index < prepareBaseH.length ; index++) {
				if (x1 == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
					midPtStart = (preparePtH[0].y + preparePtH[3].y) / 2;
					if (prepareBaseH[index].y == 1) {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[0].x, midPtSelf + (preparePtH[0].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[1].x, midPtSelf + (preparePtH[1].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[2].x, midPtSelf + (preparePtH[2].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[3].x, midPtSelf + (preparePtH[3].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					} else {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[0].x, midPtSelf + (preparePtH[0].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 - preparePtH[1].x, midPtSelf + (preparePtH[1].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 - preparePtH[2].x, midPtSelf + (preparePtH[2].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x1 + preparePtH[3].x, midPtSelf + (preparePtH[3].y - midPtStart));
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					}
				}
			}
			if (bNotEspLine) {
				curPoint = new Point(x1, y1);
				points.add(curPoint);
				curPoint = new Point(x2, y2);
				points.add(curPoint);
				bNotEspLine = false;				
			}
		} else if (y1 == y2) {
			midPtSelf = (x1 + x2) / 2;
			for (int index = 0; index < prepareBaseV.length ; index++) {
				if (y1 == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
					midPtStart = (preparePtV[0].x + preparePtV[3].x) / 2;
					if (prepareBaseV[index].x == 1) {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[0].x - midPtStart), y1 + preparePtV[0].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[1].x - midPtStart), y1 + preparePtV[1].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[2].x - midPtStart), y1 + preparePtV[2].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[3].x - midPtStart), y1 + preparePtV[3].y);
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					} else {
						curPoint = new Point(x1, y1);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[0].x - midPtStart), y1 + preparePtV[0].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[1].x - midPtStart), y1 - preparePtV[1].y);
						points.add(curPoint);
						curPoint = new Point(midPtSelf + (preparePtV[2].x - midPtStart), y1 - preparePtV[2].y);
						points.add(curPoint);						
						curPoint = new Point(midPtSelf + (preparePtV[3].x - midPtStart), y1 + preparePtV[3].y);
						points.add(curPoint);
						curPoint = new Point(x2, y2);
						points.add(curPoint);
						bNotEspLine = false;
					}
				}
			}
			if (bNotEspLine) {
				curPoint = new Point(x1, y1);
				points.add(curPoint);
				curPoint = new Point(x2, y2);
				points.add(curPoint);
				bNotEspLine = false;				
			}			
		}
//		Log.w("edugame", "=======points==========" + points.size() + "," + points);
		return points;
	}
	
	/**
	 * @aim 获取X和Y延伸的宽和高
	 * @param bExpandX X方向上的延伸
	 * @return int 延伸的值
	 */
	public int getHardExpandXY(boolean bExpandX) {
		if (bExpandX) {
			return Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
		} else {
			return Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
		}
	}
	
	/**
	 * @aim 获取要扩张的宽和高
	 * @param rtF
	 * @return
	 */
	public RectF getHardExpandWidthHeight(RectF rtF) {
		RectF rectF = new RectF(0, 0, 0, 0);
		for (int index = 0 ; index < prepareBaseH.length ; index++) {
			if (rtF.left == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
				if (prepareBaseH[index].y == 1) {
					rectF.left = 0;
				} else {
					rectF.left = Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
				}
			} else if (rtF.right == prepareBaseH[index].x && prepareBaseH[index].x != 0) {
				if (prepareBaseH[index].y == 1) {
					rectF.right = Math.abs(Math.max(preparePtH[1].x, preparePtH[2].x));
				} else {
					rectF.right = 0;
				}					
			}
		}
		for (int index = 0; index < prepareBaseV.length ; index++) {
			if (rtF.top == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
				if (prepareBaseV[index].x == 1) {
					rectF.top = 0;
				} else {
					rectF.top = Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
				}
			} else if (rtF.bottom == prepareBaseV[index].y && prepareBaseV[index].y != 0) {
				if (prepareBaseV[index].x == 1) {
					rectF.bottom = Math.abs(Math.max(preparePtV[1].y, preparePtV[2].y));
				} else {
					rectF.bottom = 0;
				}
			}
		}
		
		return rectF;
	}
	
	/**
	 * @aim 获取当前的路径
	 * @param rtF
	 * @param bSmallMode 小图模式
	 * @return
	 */
	public Path getCurveDrawPath(RectF rtF, boolean bSmallMode) {
		// RectF传入进来的改变了会影响外部的，汗
		Path curvePath = new Path();
		List<PointF> sumListPts = new LinkedList<PointF>();
		List<Point> points = null;
		List<Integer> points_x = new LinkedList<Integer>();
		List<Integer> points_y = new LinkedList<Integer>();
		List<PointF> points_xy = new LinkedList<PointF>();
		RectF expandRectF = getHardExpandWidthHeight(rtF);
		// 路径初始化
		for (int iSide = 0 ; iSide < 4 ; iSide++) {
			switch (iSide) {
			case 0:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.right, (int)rtF.top);
				break;
			case 1:
				points = getHardCurvePoint((int)rtF.right, (int)rtF.top, (int)rtF.right, (int)rtF.bottom);
				break;
			case 2:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.bottom, (int)rtF.right, (int)rtF.bottom);
				break;
			case 3:
				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.left, (int)rtF.bottom);
				break;
			}
			if (bSmallMode) {
				// 做一个平移处理，因为是曲线，可能会绘制出去，所以平移一下
				for (int i = 0 ; i < points.size() ; i++) {
					points.get(i).set(points.get(i).x - (int)rtF.left + (int)expandRectF.left + getHardExpandXY(true) / 2, 
							points.get(i).y - (int)rtF.top + (int)expandRectF.top + getHardExpandXY(false) / 2);
				}
			}
			points_x.clear();
			points_y.clear();
			points_xy.clear();
			if (points.size() > 2) {
				for (int i = 0; i < points.size() ; i++) {
					points_x.add(points.get(i).x);
					points_y.add(points.get(i).y);
				}
				List<Cubic> calculate_x = calculate(points_x);
				List<Cubic> calculate_y = calculate(points_y);
				points_xy.add(new PointF(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0)));
				for (int i = 0; i < calculate_x.size(); i++) {
					for (int j = 1; j <= Cubic.STEPS; j++) {
						float u = j / (float) Cubic.STEPS;
						points_xy.add(new PointF(calculate_x.get(i).eval(u), calculate_y.get(i).eval(u)));
					}
				}
			} else {
				points_xy.add(new PointF(points.get(0)));
				points_xy.add(new PointF(points.get(1)));
			}
			// 先把所有的点记录下来，然后最后添加路径
			switch (iSide) {
			case 0:
			case 1:
				for (int i = 0; i < points_xy.size() ; i++) {
					sumListPts.add(points_xy.get(i));
				}
//				points = getHardCurvePoint((int)rtF.left, (int)rtF.top, (int)rtF.left, (int)rtF.bottom);
				break;
			case 2:
			case 3:
				for (int i = points_xy.size() - 1 ; i >= 0 ; i--) {
					sumListPts.add(points_xy.get(i));
				}				
				break;
			}			
		}
		// 真正开始添加path
		for (int index = 0; index < sumListPts.size() ; index++) {
			if (index == 0) {
				curvePath.moveTo((int)sumListPts.get(index).x, (int)sumListPts.get(index).y);
			} else {
				curvePath.lineTo((int)sumListPts.get(index).x, (int)sumListPts.get(index).y);
			}
			if (index == sumListPts.size() - 1) {
				// 构成一个封闭曲线
				curvePath.close();
			}
		}
		
		return curvePath;
	}
	
	/**
	 * @aim 真正通过给定矩形块，生成一个矩形形状，记录该形状的相关信息
	 * 			生成的图形信息记录于ArrayList变量areaSharps中
	 * 		新的小矩形区域包括原来的大小+上下或者左右突出的部分+曲线浮动的宽高
	 * 		(4个方向上的浮动部分不超过突出点的距离的一半)
	 * @param rtF 源矩形用来生成矩形形状
	 * @return 无
	 */
	public void getHardRealRectangleSharp_Son(RectF rtF) {
		// 产生突出或者凹陷的矩形图形
		GameSharps ngameSharps = new GameSharps();
		RectF expandRectF = getHardExpandWidthHeight(rtF);
		ngameSharps.srcSharpPath.addPath(getCurveDrawPath(rtF, false));
		ngameSharps.srcSharpRect.set((int) (rtF.left - expandRectF.left) - getHardExpandXY(true) / 2, 
				(int) (rtF.top - expandRectF.top) - getHardExpandXY(false) / 2, 
				(int) (rtF.right + expandRectF.right) + getHardExpandXY(true) / 2, 
				(int) (rtF.bottom + expandRectF.bottom) + getHardExpandXY(false) / 2);
		ngameSharps.smlSharpRect.set(0, 0, (int) (rtF.width() + expandRectF.left + expandRectF.right + getHardExpandXY(true)), 
				(int) (rtF.height() + expandRectF.top + expandRectF.bottom + getHardExpandXY(false)));
		ngameSharps.smlSharpPath.addPath(getCurveDrawPath(rtF, true));
		ngameSharps.srcPoint.set((int) (rtF.left + rtF.width() / 2),
				(int) (rtF.top + rtF.height() / 2));
		// 这里要注意取到真正的中心点，与原图重合的中心点
		// 原来的中心点为，原来的矩形宽(高)的一半+扩展的宽(高)的一半+左(上)边突出的部分
		ngameSharps.smlPoint.set((int) ((rtF.width() + getHardExpandXY(true)) / 2 + expandRectF.left),
				(int) ((rtF.height() + getHardExpandXY(false)) / 2 + expandRectF.top));
		// 随即存入areaSharps中，不要顺序存取，否则到时候还需要打乱
		// 拼图的图形列表不能有规律的顺序摆放
		areaSharps.add(getRandom() % (areaSharps.size() + 1), ngameSharps);
	}	
	
	/**
	 * @aim 低难度，非空矩形全部生成矩形形状
	 * @param rtF1 第一个源矩形
	 * @param rtF2 第二个源矩形
	 * @param rtF3 第三个源矩形
	 * @return 无
	 */
	public void getHardSharpTypeRectangle(RectF rtF1, RectF rtF2, RectF rtF3, RectF rtF4) {
		
		// 生成第一个矩形形状
		if (rtF1 != null && !rtF1.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF1);
		}
		
		// 生成第二个矩形形状
		if (rtF2 != null && !rtF2.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF2);
		}
		
		// 生成第三个矩形形状
		if (rtF3 != null && !rtF3.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF3);
		}
		
		// 生成第四个矩形形状
		if (rtF4 != null && !rtF4.isEmpty()) {
			getHardRealRectangleSharp_Son(rtF4);
		}
		
	}	
	
	/**
	 * @aim 通过玩家年龄基本划分游戏难度，获取不同难度下的拼图形状
	 * 			levelSml 6个形状左右，全是长方形(可能有正方形)，形状位置大小随机
	 * 			levelMid 9个形状左右，分长方形(可能有正方形)、三角形，都必须有，个数位置大小随机
	 * 			levelBig 12个形状左右，分平行四边形或者梯形、三角形、长方形(正方形)，
	 * 			三角形和平行四边形或者梯形必须有，个数位置大小随机
	 * 
	 * @param rtF1 第一个源矩形
	 * @param rtF2 第二个源矩形
	 * @param rtF3 第三个源矩形
	 * @param rtF4 第四个源矩形
	 * @return 无
	 */
	public void getHardSharpsByLevel(RectF rtF1, RectF rtF2, RectF rtF3, RectF rtF4) {

		getHardSharpTypeRectangle(rtF1, rtF2, rtF3, rtF4);
	}	
	
	/**
	 * @aim 初始化路径，及其记录的路径信息
	 * 			切割出来的图形形状信息记录于areaSharps当中
	 * 			思路：固定两行或者两列，然后再切割，分成不等的九宫格，然后根据难度
	 * 			合并或者切割合并的九宫格，切割完后拼接成平行四边形或者梯形
	 * @param 无
	 * @return 无
	 */
	public void initHardRandomSharpsPath() {
		// 游戏年龄等级
//		if (GsptMainActivity.getSystemUserAge() == 1){
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelSml;
//		} else {
//			userAgeLevel = (getRandom() % 10 <= 2) ? levelMid : levelBig;
//		}
		switch (GsptMainActivity.getSystemUserAge()) {
		case 3:
			userAgeLevel = levelBig;
			break;
		case 2:
			userAgeLevel = levelMid;
			break;
		case 1:
		default:
			userAgeLevel = levelSml;
			break;
		}		
		// 图形集合
		if (areaSharps != null) {
			if (areaSharps.size() > 0) {
				areaSharps.clear();
			}
		} else {
			areaSharps = new ArrayList<GameSharps>();
		}

		// 初始化哪些要切割成梯形或平行四边形
		espsharpstwo[0] = (getRandom() % 2);
		espsharpstwo[1] = (getRandom() % 2);
		espsharpstwo[2] = (getRandom() % 2);
		int esptwonumber = espsharpstwo[2] + espsharpstwo[1] + espsharpstwo[0];
		if (esptwonumber == 0) {
			espsharpstwo[getRandom() % 3] = 1;
			esptwonumber++;
		}
		
		// 初始化哪些矩形要切割成三角行
		espsharpsthreeindex = 0;
		for (int index = 0; index < espsharpsthree.length; index++) {
			if (esptwonumber > 1) {
				if (index < 3) {
					espsharpsthree[index] = 1;
				} else {
					espsharpsthree[index] = 0;
				}
			} else if (esptwonumber == 1) {
				espsharpsthree[index] = 1;
			}
		}
		if (esptwonumber == 1) {
			int deletethree = getRandom() % 4;
			for (int num = 0; num < deletethree; num++) {
				espsharpsthree[getRandom() % espsharpsthree.length] = 0;
			}
		}
		
		// 0横向，1竖向
		RectF pathRect = new RectF(0, 0, 0, 0);
		RectF tmpRectF1 = new RectF(0, 0, 0, 0);
		RectF tmpRectF2 = new RectF(0, 0, 0, 0);
		RectF tmpRectF3 = new RectF(0, 0, 0, 0);
		RectF tmpRectF4 = new RectF(0, 0, 0, 0);
		
		// 横向固定还是竖向固定
		int dirVHfinal = getRandom() % 2;
		
		// 有几根固定用于年龄小的时候，图片分割数为6块矩形用
		// dirLost == 0或者 1，分成12,16宫格
		int dirHoldX = 3;
		int dirHoldY = 3;
		if (userAgeLevel == levelSml) {
			dirHoldX -= 1;
			dirHoldY -= 1;
		} else if (userAgeLevel == levelMid){
			switch ((getRandom() % 2)) {
			case 1:
				dirHoldX -= 1;
				break;
			case 0:
			default:
				dirHoldY -= 1;
				break;
			}			
		}
		// 初始化突出或者凹陷部位
		prepareBaseH = new PointF[dirHoldX];
		for (int num = 0 ; num < dirHoldX ; num++) {
			prepareBaseH[num] = new PointF(0, 0);
		}
		prepareBaseV = new PointF[dirHoldY];
		for (int num = 0 ; num < dirHoldY ; num++) {
			prepareBaseV[num] = new PointF(0, 0);
		}		
		preparePtH = new Point[]{new Point(0, 30), new Point(30, 35), new Point(30, 75), new Point(0, 80) };
		preparePtV = new Point[]{new Point(30, 0), new Point(35, 30), new Point(75, 30), new Point(80, 0) };

		if (App.getInstance().mScale>1.0f){
			float w = 776*App.getInstance().mScale;
			float h = 566*App.getInstance().mScale;
			gsptBmpWidth=(int)w;
			gsptBmpHeight=(int)h;
		}
		// 初始化要切割的基准宽高，能随机多宽和多高 
		int basew0 = (gsptBmpWidth * 94 / 100) / (dirHoldX + 1);
		int basew1 = (gsptBmpWidth * 6 / 100) / (dirHoldX + 1);
		int baseh0 = (gsptBmpHeight * 94 / 100) / (dirHoldY + 1);
		int baseh1 = (gsptBmpHeight * 6 / 100) / (dirHoldY + 1);
		// 以前是随即，根本不会对齐，现在会对齐了。固定随机
		int [] baserand = {getRandom(), getRandom(), getRandom(), getRandom(), getRandom(), getRandom()};
		pathRect.set(0, 0, 0, 0);
		if (dirVHfinal == 0) {
			// 竖向固定
			for (int addx = 0; addx <= dirHoldX; addx++) {
				pathRect.left = pathRect.right;
				if (addx == dirHoldX) {
					pathRect.right = gsptBmpWidth;
				} else {
					pathRect.right = pathRect.left + basew0 + (getRandom() % basew1);
					if (addx < prepareBaseH.length) {
						// 记录固定线条值及方向
						prepareBaseH[addx].set((int)pathRect.right, getRandom()%2);
					}
				}
				pathRect.top = 0;
				pathRect.bottom = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				tmpRectF4.set(0, 0, 0, 0);				
				for (int addy = 0; addy <= dirHoldY; addy++) {
					pathRect.top = pathRect.bottom;
					if (addy == dirHoldY) {
						pathRect.bottom = gsptBmpHeight;
					} else {
						pathRect.bottom = pathRect.top + baseh0 + (baserand[addy] % baseh1);
						if (addy < prepareBaseV.length && addx == 0) {
							// 记录固定线条值及方向
							prepareBaseV[addy].set(getRandom()%2, (int)pathRect.bottom);
						}					
					}
					if (addy == 0) {
						tmpRectF1.set(pathRect);
					} else if (addy == 1) {
						tmpRectF2.set(pathRect);
					} else if (addy == 2){
						tmpRectF3.set(pathRect);
					} else {
						tmpRectF4.set(pathRect);
					}
				}
				// 根据年龄等级区分游戏难度，根据难度获取真实路径信息，全部存于areaSharps当中
				getHardSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, tmpRectF4);
				
			}
		} else {		
			// 横向固定
			for (int addy = 0; addy <= dirHoldY; addy++) {
				pathRect.top = pathRect.bottom;
				if (addy == dirHoldY) {
					pathRect.bottom = gsptBmpHeight;
				} else {
					pathRect.bottom = pathRect.top + baseh0 + (getRandom() % baseh1);
					if (addy < prepareBaseV.length) {
						// 记录固定线条值及方向
						prepareBaseV[addy].set(getRandom()%2, (int)pathRect.bottom);
					}				
				}
				pathRect.left = 0;
				pathRect.right = 0;
				tmpRectF1.set(0, 0, 0, 0);
				tmpRectF2.set(0, 0, 0, 0);
				tmpRectF3.set(0, 0, 0, 0);
				tmpRectF4.set(0, 0, 0, 0);
				for (int addx = 0; addx <= dirHoldX; addx++) {
					pathRect.left = pathRect.right;
					if (addx == dirHoldX) {
						pathRect.right = gsptBmpWidth;
					} else {
						pathRect.right = pathRect.left + basew0 + (baserand[addx] % basew1);
						if (addx < prepareBaseH.length && addy == 0) {
							// 记录固定线条值及方向
							prepareBaseH[addx].set((int)pathRect.right, getRandom()%2);
						}					
					}
					if (addx == 0) {
						tmpRectF1.set(pathRect);
					} else if (addx == 1) {
						tmpRectF2.set(pathRect);
					} else if (addx == 2) {
						tmpRectF3.set(pathRect);
					} else {
						tmpRectF4.set(pathRect);
					}
				}
				// 根据年龄等级区分游戏难度，根据难度获取真实路径信息，全部存于areaSharps当中
				getHardSharpsByLevel(tmpRectF1, tmpRectF2, tmpRectF3, tmpRectF4);
				
			}
		}
	}	
	
	/**
	 * @aim 隐藏控件并且释放资源
	 * @param v 要销毁的View
	 * @return 无
	 */
	public static void setViewGoneDestroy(View v) {
		try {
			if (v != null) {
				// 释放背景资源
				Drawable vDb = v.getBackground();
				if (vDb != null) {
					vDb.setCallback(null);
					v.unscheduleDrawable(vDb);
					// v.setBackground(null);
					// BitmapDrawable bdb = (BitmapDrawable)vDb;
					// v.setBackground(null);
					// bdb.setCallback(null);
					// if (!bdb.getBitmap().isRecycled()){
					// bdb.getBitmap().recycle();
					// Log.w("divhee_edugame",
					// "=======setViewGoneDestroy=====3====");
					// }
				}
				
				// 如果设置过背景图片，释放图片资源，清除设置不重复释放资源
				Bitmap bgBitmap = (Bitmap) v.getTag(R.id.tag_second_bgbitmap);			
				v.setTag(R.id.tag_second_bgbitmap, null);
				
				// 如果设置过前景图片，释放图片资源，清除设置不重复释放资源
				Bitmap imgBitmap = (Bitmap) v.getTag(R.id.tag_third_smallbitmap);
				v.setTag(R.id.tag_third_smallbitmap, null);			
				
				// 如果设置过灰度图片，释放图片资源，清除设置不重复释放资源
				Bitmap grayBitmap = (Bitmap) v.getTag(R.id.tag_four_graybitmap);
				v.setTag(R.id.tag_four_graybitmap, null);				
				
				// 隐藏View，释放该View的资源
				v.setVisibility(View.GONE);
				
				// 释放资源
				if (bgBitmap != null) {
					if (!bgBitmap.isRecycled()) {
						bgBitmap.recycle();
						System.gc();
					}
				}
				// 释放资源
				if (imgBitmap != null) {
					if (!imgBitmap.isRecycled()) {
						imgBitmap.recycle();
						System.gc();
					}
				}
				// 释放资源
				if (grayBitmap != null) {
					if (!grayBitmap.isRecycled()) {
						grayBitmap.recycle();
						System.gc();
					}
				}
			}
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * GsptRunDataFrame类结束 end 
	 */
}
