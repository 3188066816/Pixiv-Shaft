package ceui.lisa.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ceui.lisa.R;
import ceui.lisa.activities.BaseActivity;
import ceui.lisa.activities.ImageDetailActivity;
import ceui.lisa.core.UrlFactory;
import ceui.lisa.database.IllustTask;
import ceui.lisa.download.FileCreator;
import ceui.lisa.download.GifQueue;
import ceui.lisa.download.IllustDownload;
import ceui.lisa.http.ErrorCtrl;
import ceui.lisa.interfaces.OnItemClickListener;
import ceui.lisa.models.GifResponse;
import ceui.lisa.models.IllustsBean;
import ceui.lisa.utils.Common;
import ceui.lisa.utils.GlideUtil;
import ceui.lisa.utils.PixivOperate;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


/**
 * 作品详情页竖向多P列表
 */
public class IllustDetailAdapter extends AbstractIllustAdapter<RecyclerView.ViewHolder> {

    public IllustDetailAdapter(IllustsBean list, Context context, boolean isForceOriginal) {
        mContext = context;
        allIllust = list;
        this.isForceOriginal = isForceOriginal;
        imageSize = (mContext.getResources().getDisplayMetrics().widthPixels -
                2 * mContext.getResources().getDimensionPixelSize(R.dimen.twelve_dp));
    }

    public IllustDetailAdapter(IllustsBean list, Context context) {
        this(list, context, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagHolder(
                LayoutInflater.from(mContext).inflate(
                        R.layout.recy_illust_grid, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final TagHolder currentOne = (TagHolder) holder;
        Common.showLog("IllustDetailAdapter onBindViewHolder 000");
        if (position == 0) {
            ViewGroup.LayoutParams params = currentOne.illust.getLayoutParams();
            params.height = imageSize * allIllust.getHeight() / allIllust.getWidth();
            params.width = imageSize;
            currentOne.illust.setLayoutParams(params);
            Glide.with(mContext)
                    .asDrawable()
                    .load(isForceOriginal ?
                            GlideUtil.getOriginalImage(allIllust, position) :
                            GlideUtil.getLargeImage(allIllust, position))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            currentOne.illust.setImageDrawable(resource);
                        }
                    });
        } else {
            Glide.with(mContext)
                    .asBitmap()
                    .load(isForceOriginal ?
                            GlideUtil.getOriginalImage(allIllust, position) :
                            GlideUtil.getLargeImage(allIllust, position))
                    .transition(withCrossFade())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ViewGroup.LayoutParams params = currentOne.illust.getLayoutParams();
                            params.width = imageSize;
                            params.height = imageSize * resource.getHeight() / resource.getWidth();
                            currentOne.illust.setLayoutParams(params);
                            currentOne.illust.setImageBitmap(resource);
                        }
                    });
        }
    }

    public static class TagHolder extends RecyclerView.ViewHolder {
        ImageView illust;

        TagHolder(View itemView) {
            super(itemView);
            illust = itemView.findViewById(R.id.illust_image);
        }
    }
}
