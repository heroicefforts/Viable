package net.heroicefforts.viable.android;

import java.lang.ref.SoftReference;
import java.util.Set;
import java.util.WeakHashMap;

import net.heroicefforts.viable.android.rep.IssueResource;
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

public class IssueSelectionAdapter extends ArrayAdapter<IssueResource>
{
	private static final int ICON_WIDTH = 48;
	private static final int ICON_HEIGHT = 56;

	private Context ctx;
	private WeakHashMap<IssueResource,SoftReference<Drawable>> iconCache = new WeakHashMap<IssueResource,SoftReference<Drawable>>();
	
	public IssueSelectionAdapter(Context ctx, Set<? extends IssueResource> resources)
	{
		super(ctx, R.layout.plain_text_view);
		this.ctx = ctx;
        for(IssueResource resource : resources)
			add(resource);
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
		
		IssueResource resource = getItem(position);
		h.nameView.setTextColor(Color.BLACK);
		h.nameView.setText(resource.getName(ctx));
		h.iconView.setImageDrawable(getResizedIcon(resource));
		
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
		
		IssueResource resource = getItem(position);
		h.nameView.setTextColor(Color.BLACK);
		h.nameView.setText(resource.getName(ctx));
		h.descView.setText(resource.getDescription(ctx));
		h.iconView.setImageDrawable(resource.getIcon(ctx));
		
		return convertView;
	}

	private Drawable getResizedIcon(IssueResource resource)
	{
		Drawable retVal = null;
		SoftReference<Drawable> ref = iconCache.get(resource);
		if(ref != null)
			retVal = ref.get();
		
		if(retVal == null)
		{
			Drawable src = resource.getIcon(ctx);
			if(src instanceof BitmapDrawable)
			{
				retVal = resize((BitmapDrawable)src, ICON_WIDTH, ICON_HEIGHT);
				iconCache.put(resource, new SoftReference<Drawable>(retVal));
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
