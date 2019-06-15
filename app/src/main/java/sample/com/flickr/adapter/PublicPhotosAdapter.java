package sample.com.flickr.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sample.com.flickr.R;
import sample.com.flickr.activity.AuthorPublisedActivity;
import sample.com.flickr.responce.model.PublicPhotosModel;

public class PublicPhotosAdapter extends RecyclerView.Adapter<PublicPhotosAdapter.MyViewHolder> {
    private List<PublicPhotosModel> publicPhotosList;
    private Context context;

    public PublicPhotosAdapter(Context mContext, List<PublicPhotosModel> photosList) {
        this.context = mContext;
        this.publicPhotosList = photosList;
    }

    @NonNull
    @Override
    public PublicPhotosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.public_photos_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicPhotosAdapter.MyViewHolder myViewHolder, int i) {

        Picasso.with(context).load(publicPhotosList.get(myViewHolder.getAdapterPosition()).getMedia().getM()).fit().centerCrop()
                .placeholder(R.drawable.flickr)
                .into(myViewHolder.publicPhoto);

        myViewHolder.authorName.setText(publicPhotosList.get(myViewHolder.getAdapterPosition()).getAuthor().
                replace("nobody@flickr.com", "").replaceAll("([^\\p{L}\\p{Z}])", ""));

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPreview(publicPhotosList.get(myViewHolder.getAdapterPosition()).getMedia().getM());
                return true;
            }
        });

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AuthorPublisedActivity.class);
                intent.putExtra("authorId", publicPhotosList.get(myViewHolder.getAdapterPosition()).getAuthorId());
                intent.putExtra("authorName", myViewHolder.authorName.getText());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicPhotosList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView publicPhoto;
        TextView authorName;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            publicPhoto = itemView.findViewById(R.id.iv_public_photo);
            authorName = itemView.findViewById(R.id.author_name);
        }
    }

    private void showPreview(String imageUrl) {
        final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.dailog_preview);
        Button btnClose = (Button) nagDialog.findViewById(R.id.btnIvClose);
        ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);
        Picasso.with(context).load(imageUrl).fit()
                .into(ivPreview);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }


}
