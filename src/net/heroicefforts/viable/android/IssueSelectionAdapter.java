package net.heroicefforts.viable.android;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueSelectionAdapter extends ArrayAdapter<IssueState>
{
	private static final int ICON_WIDTH = 48;
	private static final int ICON_HEIGHT = 56;

	private Context ctx;
	protected boolean bugsOnly;
	private HashMap<Integer,SoftReference<Drawable>> iconCache = new HashMap<Integer,SoftReference<Drawable>>();
	
	public IssueSelectionAdapter(Context ctx, boolean bugsOnly)
	{
		super(ctx, R.layout.plain_text_view);
		this.ctx = ctx;
		this.bugsOnly = bugsOnly;
        for(IssueState state : getStates())
			add(state);
	}
		
	protected Set<IssueState> getStates()
	{
		LinkedHashSet<IssueState> retVal = new LinkedHashSet<IssueState>();
		retVal.addAll(IssueState.DEFAULT_BUG_STATES);
		if(!bugsOnly)
			retVal.addAll(IssueState.DEFAULT_FEATURE_STATE);
		
		return retVal;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Holder h = null;
		if(convertView == null)
		{
			convertView = View.inflate(ctx, R.layout.issue_type_select, null);
			h = new Holder(((TextView) convertView.findViewById(R.id.IssueIconNameTextView)), null,
				(ImageView) convertView.findViewById(R.id.IssueIconImageView));
			convertView.setTag(h);
		}
		else
			h = (Holder) convertView.getTag();
		
		IssueState state = getItem(position);
		h.nameView.setTextColor(Color.BLACK);
		h.nameView.setText(ctx.getString(state.getNameRes()));
		h.iconView.setImageDrawable(getResizedIcon(state.getIconRes()));
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		Holder h = null;
		if(convertView == null)
		{
			convertView = View.inflate(ctx, R.layout.issue_type_select_dd, null);
			h = new Holder((TextView)convertView.findViewById(R.id.include01), 
				((TextView) convertView.findViewById(R.id.IssueIconDescTextView)),
				(ImageView) convertView.findViewById(R.id.IssueIconImageView));
			convertView.setTag(h);
		}
		else
			h = (Holder) convertView.getTag();
		
		IssueState state = getItem(position);
		h.nameView.setTextColor(Color.BLACK);
		h.nameView.setText(ctx.getString(state.getNameRes()));
		h.descView.setText(ctx.getString(state.getDescRes()));
		h.iconView.setImageDrawable(getIcon(state.getIconRes()));
		return convertView;
	}

	private Drawable getIcon(int resId)
	{
		return ctx.getResources().getDrawable(resId);
	}
	
	private Drawable getResizedIcon(int resId)
	{
		Drawable retVal = null;
		SoftReference<Drawable> ref = iconCache.get(resId);
		if(ref != null)
			retVal = ref.get();
		
		if(retVal == null)
		{
			Drawable d = ctx.getResources().getDrawable(resId);

			if(d instanceof BitmapDrawable)
			{
				retVal = resize((BitmapDrawable)d, ICON_WIDTH, ICON_HEIGHT);
				iconCache.put(resId, new SoftReference<Drawable>(retVal));
			}
		}

		return retVal;
		
	}

	static class Holder
	{
		public ImageView iconView;
		public TextView descView;
		private TextView nameView;
		
		public Holder(TextView nameView, TextView descView, ImageView iconView)
		{
			super();
			this.nameView = nameView;
			this.iconView = iconView;
			this.descView = descView;
		}
		
	}
	
	private Drawable resize(BitmapDrawable src, int newWidth, int newHeight)
	{
		Bitmap bitmapOrg = src.getBitmap();

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// createa matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// rotate the Bitmap
		//matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

		// make a Drawable from Bitmap to allow to set the BitMap
		// to the ImageView, ImageButton or what ever
		return new BitmapDrawable(resizedBitmap);
	}
	
}
