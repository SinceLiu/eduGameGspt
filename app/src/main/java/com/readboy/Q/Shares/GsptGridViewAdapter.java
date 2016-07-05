/**
 * @aim OperateAdapter类，实现记加载用户的早晚听条目
 * 			用户设置早晚听内容的时候，搜索相应的早晚听内容，包括点读、音乐盒、听故事、八戒学唐诗
 * 			搜索出来的条目的Adapter处理
 * 
 * 实现：
 * Adapter回调，获取当前的类型，然后加载不同的布局
 * 
 * 
 * @time 2014.04.10;
 * @author divhee
 * @modify by 
 */
package com.readboy.Q.Shares;

import java.util.List;
import java.util.Map;

import com.readboy.Q.Gspt.R;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class GsptGridViewAdapter extends BaseAdapter{
	
	/**
	 * 加载每个条目的layout用
	 */
	private LayoutInflater listenInflater = null;
	
	/**
	 * 系统的context
	 */
	private Context listenContext = null;
	
	/**
	 * 数据list map
	 */
	private List<Map<String, Object>> listenListItems = null;

	/**
	 * 点击监听的回调
	 */
	private OnClickListener onClickListener = null;
	
	/**
	 * 列表信息
	 */
	private StoryGridViewInfo listInfo = null;
	
	/**
	 * @aim 修改List的Style
	 * @param style
	 */
	public void setListenAdapterInfo(StoryGridViewInfo setInfo) {
		listInfo = setInfo;
	}
	
	public interface StoryGridViewInfo {
		/**
		 * @aim 判断当前按钮的状态，锁，或者开着的
		 * @index 当前的序号
		 * @return true 已锁 false 已经打开
		 */
		public boolean bBtnOfGridItemPassState(int index);
		
		/**
		 * @aim 获取按钮的状态图片StateListDrawable
		 * @param index 按钮序号
		 * @return 状态图片StateListDrawable
		 */
		public StateListDrawable getShowStateListDrawable(int index);
		
	}	
	
	@SuppressWarnings("unchecked")
	public GsptGridViewAdapter(Context context, List<? extends Map<String, ?>> data, OnClickListener clickListener) {
		this.listenContext = context;
		this.listenListItems = (List<Map<String, Object>>) data;
		this.listenInflater = (LayoutInflater) listenContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.onClickListener = clickListener;
	}
	
	@Override
	public int getCount() {
		return listenListItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;
		// 每一个故事按钮
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = listenInflater.inflate(R.layout.gspt_gridview_item, null);
			viewHolder.btnStoryIcon = (Button) convertView.findViewById(R.id.btn_story_icon);
			viewHolder.btnStoryIcon.setOnClickListener(onClickListener);
			convertView.setTag(R.id.tag_grid_holder, viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.tag_grid_holder);
		}
		int gridItemId = (Integer) listenListItems.get(position).get("gridItemId");
		viewHolder.btnStoryIcon.setSelected((listInfo != null) ? listInfo.bBtnOfGridItemPassState(gridItemId) : false);
		viewHolder.btnStoryIcon.setTag(R.id.tag_grid_index, gridItemId);
		int gridItemDb = (Integer) listenListItems.get(position).get("gridItemDb");
//		viewHolder.btnStoryIcon.setBackgroundResource(gridItemDb);
		viewHolder.btnStoryIcon.setBackgroundDrawable((listInfo != null) ? listInfo.getShowStateListDrawable(gridItemDb) : null);

		return convertView;
	}
	
	/**
	 * @aim GridView每个条目显示的图片以及文件名的控件实例；只需要获取一次
	 * @author Administrator
	 *
	 */
	private class ViewHolder {
		public Button btnStoryIcon = null;
	}
}	