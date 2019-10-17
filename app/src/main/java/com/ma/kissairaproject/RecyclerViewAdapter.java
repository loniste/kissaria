package com.ma.kissairaproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.checkSelfPermission;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<SingleRow> mDataset;
    static int prev_expanded=-1;
    Context context;
    RecyclerView recycler;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cmd;
        public ImageView status;
        public TextView price;
        public TextView afterCommaPrice;
        public TextView bouton1;
        public TextView bouton2;
        public TextView bouton3;
        public ImageView callButton;

        public int visibilityState=0;

        public RecyclerView rv_detail_cmd;
        View v;

        public MyViewHolder(View view) {
            super(view);
            this.cmd = view.findViewById(R.id.commande);
            this.status = view.findViewById(R.id.status);
            this.price = view.findViewById(R.id.price);
            this.afterCommaPrice = view.findViewById(R.id.afterCommaPrice);

            this.bouton1 = view.findViewById(R.id.txtView1);
            this.bouton2 = view.findViewById(R.id.txtView2);
            this.bouton3 = view.findViewById(R.id.txtView3);
            this.callButton = view.findViewById(R.id.call);


            this.rv_detail_cmd = view.findViewById(R.id.rv_detail_cmd);
            this.v = view;
        }

        View getView() {
            return v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<SingleRow> myDataset, Context c) {
        this.context = c;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row, parent, false);
        MyViewHolder vh = new MyViewHolder(v);



        return vh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recycler = recyclerView;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.setIsRecyclable(false);
//        recycler.getRecycledViewPool().setMaxRecycledViews(holder.getItemViewType(), 0);

        holder.cmd.setText(mDataset.get(position).getCmd());
        holder.status.setImageResource(mDataset.get(position).getStatusCode());
        holder.price.setText(mDataset.get(position).getPrice());
        holder.afterCommaPrice.setText(mDataset.get(position).getAfterCommaPrice());

        if (holder.visibilityState==1){
            holder.getView().findViewById(R.id.hidable_view).setVisibility(View.VISIBLE);
        } else  {
            holder.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);

        }



        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:123456789"));
                holder.getView().getContext().startActivity(intent);
            }
        });

        /**processing buttons in a single item when status is pending*/
        if (mDataset.get(position).getStatusCode() == R.drawable.ic_prepared_ribbon) {


            holder.bouton1.setText("Livrer");
            setButtonBg(holder.bouton1, R.drawable.delivered_bg);



            holder.bouton2.setText("Annuler");
            setButtonBg(holder.bouton2,R.drawable.canceled_bg);

            holder.bouton3.setVisibility(View.GONE);
        }
        /**processing buttons in a single item when status is something else*/
        else if (mDataset.get(position).getStatusCode()!= R.drawable.ic_prepared_ribbon){

            holder.bouton1.setVisibility(View.GONE);
            holder.bouton2.setVisibility(View.GONE);
            holder.bouton3.setVisibility(View.GONE);
        }
        holder.rv_detail_cmd.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_detail_cmd.setAdapter(new RvCmdDetailsAdapter(mDataset.get(position).getDetailCmdsList()));

        final int GRAY = 0xFFf0f0f0;
        CardView cv= holder.itemView.findViewById(R.id.cv);

        /**normally processing for recent itmes, but here we just set apart even and odd positions**/
        if (mDataset.get(position).getRecent()) {
            cv.setCardBackgroundColor(GRAY);
        } else {
            cv.setCardBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** expand singleRow */
                LinearLayout linearLayout= v.findViewById(R.id.hidable_view);
                final boolean visibility = linearLayout.getVisibility()==View.VISIBLE;

                if (!visibility)
                {
                    //textView.setActivated(true);
                    linearLayout.setVisibility(View.VISIBLE);
                    Log.d("qsd", "prev_expanded: "+String.valueOf( prev_expanded));

                    if (prev_expanded!=-1 && prev_expanded!=position)
                    {
                        //recycler.findViewHolderForLayoutPosition(prev_expanded).itemView.setActivated(false);
                        MyViewHolder vh= (MyViewHolder) recycler.findViewHolderForLayoutPosition(prev_expanded);
                        Log.d("qsd", "prev_expanded: "+String.valueOf( prev_expanded));
                        if (vh!=null){
                            vh.visibilityState=0;
                            vh.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);
                            Log.d("vh", "NOT NULL");

                        } else{
                            Log.d("qsd", "NULL");
                        }
                    }
                    prev_expanded = position;
                }
                else
                {
                    Log.d("qsd", "the view is already visible");
                    //holder.itemView.setActivated(false);
                    linearLayout.setVisibility(View.GONE);
                }
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recycler.getLayoutManager();
                View someView = linearLayoutManager.getChildAt(0);
                int top = (someView == null) ? 0 : (someView.getTop() - linearLayoutManager.getPaddingTop());

//                linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.findFirstVisibleItemPosition(), top);
                linearLayoutManager.scrollToPositionWithOffset(position,0);



                AutoTransition autoTransition = new AutoTransition();
                autoTransition.setDuration(200);
                //autoTransition.excludeChildren(recycler, true);
                TransitionManager.beginDelayedTransition(recycler,autoTransition);
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void setButtonBg(TextView bouton, int bg_code) {
        int pl = bouton.getPaddingLeft();
        int pt = bouton.getPaddingTop();
        int pr = bouton.getPaddingRight();
        int pb = bouton.getPaddingBottom();
        bouton.setBackground(ContextCompat.getDrawable(context, bg_code));
        bouton.setPadding(pl, pt, pr, pb);
    }
}
class RvCmdDetailsAdapter extends RecyclerView.Adapter<RvCmdDetailsAdapter.MyViewHolder> {
    ArrayList<SingleRowDetailCmd> mDataSet;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView label;
        public TextView qty;
        public TextView price;
        View v;
        public MyViewHolder(View view) {
            super(view);
            this.label = view.findViewById(R.id.label);
            this.qty = view.findViewById(R.id.qty);
            this.price = view.findViewById(R.id.price);
            this.v=view;
        }
        View getView(){
            return v;
        }
    }
    public RvCmdDetailsAdapter(ArrayList<SingleRowDetailCmd> list){
        mDataSet=list;
        Log.d("mDataSet", String.valueOf(mDataSet.get(1).getArticle()));
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_detail_cmd, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.label.setText(mDataSet.get(position).getArticle());
        holder.qty.setText(mDataSet.get(position).getQuantity());
        holder.price.setText(mDataSet.get(position).getPrice());
        if (position==0){
            holder.label.setTextColor(ContextCompat.getColor(holder.getView().getContext(), R.color.gray));
            holder.qty.setTextColor(ContextCompat.getColor(holder.getView().getContext(), R.color.gray));
            holder.price.setTextColor(ContextCompat.getColor(holder.getView().getContext(), R.color.gray));
        }
    }
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}