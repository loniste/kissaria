package com.ma.kissairaproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

class SellerRecyclerViewAdapter extends RecyclerView.Adapter<SellerRecyclerViewAdapter.MyViewHolder> {

    private static final int TRANSITION_DURATION = 200;
    private ArrayList<SellerSingleRow> mDataset;
    private static long prev_expanded=-1;
    private Context context;
    private RecyclerView recycler;
    private String userId;
    private boolean isOnClicksDisabled;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;

        public TextView cmd;
        public ImageView statusIV;
        View   statusRibbon;
        TextView statusTV;
        public TextView price;
        public TextView afterCommaPrice;
        public ImageView callButton;
        public RecyclerView rv_detail_cmd;
        TextView address;
        String phone_number;
        TextView shipTime;
        TextView shipDate;
        TextView creation_time;
        TextView creation_date;
        TextView full_name;
        CircleImageView profil;



        public TextView bouton1;
        public TextView bouton2;

        View v;


        public MyViewHolder(View view) {

            super(view);

            this.container= (ConstraintLayout) view.findViewById(R.id.hidable_view);

            this.cmd = view.findViewById(R.id.commande);
            this.statusIV = view.findViewById(R.id.status_iv);
            this.statusRibbon=view.findViewById(R.id.status_ribbon);
            this.statusTV=view.findViewById(R.id.status_tv);
            this.price = view.findViewById(R.id.price);
            this.afterCommaPrice = view.findViewById(R.id.afterCommaPrice);

            this.bouton1 = view.findViewById(R.id.txtView1);
            this.bouton2 = view.findViewById(R.id.txtView2);
            this.callButton = view.findViewById(R.id.call);

            this.address = view.findViewById(R.id.address);
            this.shipTime = view.findViewById(R.id.customer_ship_time);
            this.shipDate = view.findViewById(R.id.customer_ship_date);
            this.creation_date = view.findViewById(R.id.creation_date);
            this.creation_time = view.findViewById(R.id.creation_time);
            this.full_name = view.findViewById(R.id.full_name);
            this.rv_detail_cmd = view.findViewById(R.id.rv_detail_cmd);
            this.v = view;
            this.profil=view.findViewById(R.id.profile_image);
        }
        View getView() {
            return v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    SellerRecyclerViewAdapter(ArrayList<SellerSingleRow> myDataset, Context c, String userId) {
        this.context = c;
        mDataset = myDataset;
        this.userId=userId;
        isOnClicksDisabled=false;
        this.setHasStableIds(true);
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public SellerRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_seller, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recycler = recyclerView;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.setIsRecyclable(false);
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                //TODO your background code
//            }
//        });
        holder.container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));


        holder.cmd.setText(mDataset.get(position).getCmd());
        holder.statusIV.setImageResource(mDataset.get(position).getStatusCode());
        switch (mDataset.get(position).getStatusCode()){

            case    R.drawable.ic_pending_icon:
                holder.statusTV.setText("EN COURS");
                holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.pending));
                holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.pending));
                break;
            case R.drawable.ic_ready_icon:
                holder.statusTV.setText("PRÊT");
                holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.ready));
                holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.ready));
                break;
            case R.drawable.ic_delivered_icon:
                holder.statusTV.setText("Livré");
                holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.delivered));
                holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.delivered));
                break;
            case R.drawable.ic_received_icon:
                mDataset.get(position).setStatusCode(R.drawable.ic_delivered_icon);
                holder.statusIV.setImageResource(R.drawable.ic_delivered_icon);
                holder.statusTV.setText("Livré");
                holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.delivered));
                holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.delivered));
                break;
            case R.drawable.ic_canceled_icon:
                holder.statusTV.setText("Annulé");
                holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.canceled));
                holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.canceled));
                break;
        }
        holder.price.setText(mDataset.get(position).getPrice());
        holder.afterCommaPrice.setText(mDataset.get(position).getAfterCommaPrice());

        holder.address.setText(mDataset.get(position).getAddress());
        holder.creation_time.setText(mDataset.get(position).getCreation_time());
        holder.creation_date.setText(mDataset.get(position).getCreation_date());
        holder.shipTime.setText(mDataset.get(position).getShip_time());
        holder.shipDate.setText(mDataset.get(position).getShip_date());
        holder.full_name.setText(mDataset.get(position).getFull_name());
        holder.phone_number=mDataset.get(position).getPhone_number();
//        holder.profil.setBackground(mDataset.get(position).getDrawableProfile());
        holder.profil.setImageDrawable(mDataset.get(position).getDrawableProfile());
        Log.d("holder.getItemId()",holder.getItemId()+"hasStableIds"+ this.hasStableIds());
        if (holder.getItemId()==prev_expanded){
            displayHidableViewOnRegistred(holder, holder.getLayoutPosition());
            Log.d("display_hidable", prev_expanded+ "");
        } else  {
            holder.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);
        }
        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+holder.phone_number));
                holder.getView().getContext().startActivity(intent);

            }
        });
        holder.address.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Toast.makeText(holder.address.getContext(), holder.address.getText(), Toast.LENGTH_SHORT).show();
              }
        });
        final int GRAY = 0xFFf0f0f0;
        CardView cv= holder.itemView.findViewById(R.id.cv);
        /**normally processing for recent items, but here we just set apart even and odd positions**/
        if (mDataset.get(position).getRecent()) {
            cv.setCardBackgroundColor(GRAY);
        } else {
            cv.setCardBackgroundColor(Color.WHITE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayHidableViewonClick(holder, position);
            }
        });
    }
    private void displayHidableViewonClick(MyViewHolder holder, int position) {
        if (!isOnClicksDisabled) {
            //while here, disable all click listeners, because an exception will be made otherwise*/
            isOnClicksDisabled = true;


            /** expand singleRow */
            ConstraintLayout constraintLayout = holder.getView().findViewById(R.id.hidable_view);
            final boolean visibility = constraintLayout.getVisibility() == View.VISIBLE;
            if (!visibility) {
                //textView.setActivated(true);
                constraintLayout.setVisibility(View.VISIBLE);
                //process buttons because this item view is henceforth visible
                sellerButtonsProcessing(holder, position);
                //load data only when this itemView become visible:

                holder.rv_detail_cmd.setLayoutManager(new LinearLayoutManager(context));
                holder.rv_detail_cmd.setAdapter(new RvCmdDetailsAdapter(mDataset.get(position).getDetailCmdsList()));

                if (prev_expanded != -1 && prev_expanded != holder.getItemId()) {
                    //recycler.findViewHolderForLayoutPosition(prev_expanded).itemView.setActivated(false);
                    MyViewHolder vh = (MyViewHolder) recycler.findViewHolderForItemId(prev_expanded);
                    if (vh != null) {
                        vh.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);
                    } else {
                        Log.d("qsd", "NULL");
                    }
                }
                prev_expanded = holder.getItemId();
            } else {
                Log.d("qsd", "the view is already visible");
                //holder.itemView.setActivated(false);
                prev_expanded=-1;
                constraintLayout.setVisibility(View.GONE);
            }
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recycler.getLayoutManager();
//            View someView = linearLayoutManager.getChildAt(0);
//            int top = (someView == null) ? 0 : (someView.getTop() - linearLayoutManager.getPaddingTop());
            //                linearLayoutManager.scrollToPositionWithOffset(linearLayoutManager.findFirstVisibleItemPosition(), top);
            linearLayoutManager.scrollToPositionWithOffset(position, 0);
            //                holder.itemView.setOnClickListener(null);
            CustomTransition customTransition = new CustomTransition();
            customTransition.setDuration(TRANSITION_DURATION);

            TransitionManager.beginDelayedTransition(recycler, customTransition);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isOnClicksDisabled = false;
                }
            }, TRANSITION_DURATION);
        }
    }
    private void displayHidableViewOnRegistred(MyViewHolder holder, int position) {

            /** expand singleRow */
            ConstraintLayout constraintLayout = holder.getView().findViewById(R.id.hidable_view);
            final boolean visibility = constraintLayout.getVisibility() == View.VISIBLE;
            if (!visibility) {
                //textView.setActivated(true);
                constraintLayout.setVisibility(View.VISIBLE);
                //process buttons because this item view is henceforth visible
                sellerButtonsProcessing(holder, position);
                //load data only when this itemView become visible:

                holder.rv_detail_cmd.setLayoutManager(new LinearLayoutManager(context));
                holder.rv_detail_cmd.setAdapter(new RvCmdDetailsAdapter(mDataset.get(position).getDetailCmdsList()));

                Log.d("qsd", "prev_expanded: " + String.valueOf(prev_expanded));

                if (prev_expanded != -1 && prev_expanded != holder.getItemId()) {
                    //recycler.findViewHolderForLayoutPosition(prev_expanded).itemView.setActivated(false);
                    MyViewHolder vh = (MyViewHolder) recycler.findViewHolderForItemId(prev_expanded);
                    Log.d("qsd", "prev_expanded: " + String.valueOf(prev_expanded));
                    if (vh != null) {
                        vh.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);
                        Log.d("vh", "NOT NULL");

                    } else {
                        Log.d("qsd", "NULL");
                    }
                }
                prev_expanded = holder.getItemId();
            } else {
                prev_expanded=-1;
                constraintLayout.setVisibility(View.GONE);
            }
    }
    private void sellerButtonsProcessing(MyViewHolder holder, int position) {
        Log.d("test123", "ze");
        /**processing buttons in a single item when statusIV is pending*/
        if (mDataset.get(position).getStatusCode() == R.drawable.ic_pending_icon) {
            holder.bouton1.setVisibility(View.VISIBLE);
            holder.bouton1.setText("Prêt");
            holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.pending));

            setButtonBg(holder.bouton1, R.drawable.ready_bg);
            holder.bouton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // change statusIV icon
                    holder.statusIV.setImageResource(R.drawable.ic_ready_icon);
                    holder.statusTV.setText("Prêt");
                    holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.ready));

                    holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.ready));
                    mDataset.get(position).setStatusCode(R.drawable.ic_ready_icon);
                    holder.bouton1.setOnClickListener(null);
                    holder.bouton2.setOnClickListener(null);
                    Log.d("inside" , "pending");
                    sellerButtonsProcessing(holder, position);


                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    try {
                        String result=backgroundWorker.execute("post_status_seller",userId,mDataset.get(position).getCmd(),"ready").get();
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.getString("status").equals("success")){
                            //TODO: setting back the previous buttons statusIV
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.bouton2.setVisibility(View.VISIBLE);
            holder.bouton2.setText("Annuler");
            setButtonBg(holder.bouton2,R.drawable.canceled_bg);

            holder.bouton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // change statusIV icon
                    holder.statusIV.setImageResource(R.drawable.ic_canceled_icon);
                    holder.statusTV.setText("Annulé");
                    holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.canceled));

                    holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.canceled));
                    mDataset.get(position).setStatusCode(R.drawable.ic_canceled_icon);
                    holder.bouton1.setOnClickListener(null);
                    holder.bouton2.setOnClickListener(null);
                    Log.d("inside" , "pending");

                    sellerButtonsProcessing(holder, position);


                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    try {
                        String result=backgroundWorker.execute("post_status_seller",userId,mDataset.get(position).getCmd(),"canceled").get();
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.getString("status").equals("success")){
                            //TODO: setting back the previous buttons statusIV
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });



        }

        /**processing buttons in a single item when statusIV is ready*/
        if (mDataset.get(position).getStatusCode() == R.drawable.ic_ready_icon) {
            holder.bouton1.setVisibility(View.VISIBLE);
            holder.bouton1.setText("Livrer");
            setButtonBg(holder.bouton1, R.drawable.delivered_bg);
            holder.bouton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // change statusIV icon
                    holder.statusIV.setImageResource(R.drawable.ic_delivered_icon);
                    holder.statusTV.setText("Livré");
                    holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.delivered));

                    holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.delivered));
                    mDataset.get(position).setStatusCode(R.drawable.ic_delivered_icon);
                    holder.bouton1.setOnClickListener(null);
                    holder.bouton2.setOnClickListener(null);
                    Log.d("inside" , "ready");
                    sellerButtonsProcessing(holder, position);

                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    try {
                        String result=backgroundWorker.execute("post_status_seller",userId,mDataset.get(position).getCmd(),"delivered").get();
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.getString("status").equals("success")){
                            //TODO: setting back the previous buttons statusIV
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.bouton2.setVisibility(View.VISIBLE);
            holder.bouton2.setText("Annuler");
            setButtonBg(holder.bouton2,R.drawable.canceled_bg);

            holder.bouton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // change statusIV icon
                    holder.statusIV.setImageResource(R.drawable.ic_canceled_icon);
                    holder.statusTV.setText("Annulé");
                    holder.statusTV.setTextColor(ContextCompat.getColor(holder.statusIV.getContext(),R.color.canceled));
                    holder.statusRibbon.setBackgroundColor(ContextCompat.getColor(holder.statusRibbon.getContext(), R.color.canceled));
                    mDataset.get(position).setStatusCode(R.drawable.ic_canceled_icon);
                    holder.bouton1.setOnClickListener(null);
                    holder.bouton2.setOnClickListener(null);
                    Log.d("inside" , "ready");

                    sellerButtonsProcessing(holder, position);

                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    try {
                        String result=backgroundWorker.execute("post_status_seller",userId,mDataset.get(position).getCmd(),"canceled").get();
                        JSONObject jsonObject = new JSONObject(result);
                        if (!jsonObject.getString("status").equals("success")){
                            //TODO: setting back the previous buttons statusIV
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**processing buttons in a single item when statusIV is delivered or canceled*/
        else if (mDataset.get(position).getStatusCode()== R.drawable.ic_delivered_icon || mDataset.get(position).getStatusCode()== R.drawable.ic_canceled_icon){

            holder.bouton1.setVisibility(View.GONE);
            holder.bouton2.setVisibility(View.GONE);
        }
        else {
            Log.d("status_problem", "Unknown statusIV code");
//                Toast.makeText(holder.bouton1.getContext(), "Unknown statusIV code", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public long getItemId(int position) {
        String stringId=mDataset.get(position).getCmd();
        int id =Integer.parseInt(stringId);
        return id;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }
}
class RvCmdDetailsAdapter extends RecyclerView.Adapter<RvCmdDetailsAdapter.MyViewHolder> {
    private ArrayList<SingleRowProduct> mDataSet;
    public static class MyViewHolder extends RecyclerView.ViewHolder {//Why static?
        TextView label;
        TextView qty;
        TextView qty2;
        TextView price;
        TextView price2;
        View v;
        MyViewHolder(View view) {
            super(view);
            this.label = view.findViewById(R.id.label);
            this.qty = view.findViewById(R.id.qty);
            this.qty2 = view.findViewById(R.id.qty2);
            this.price = view.findViewById(R.id.price);
            this.price2 = view.findViewById(R.id.afterCommaPrice);
            this.v=view;
        }
        View getView(){
            return v;
        }
    }

    RvCmdDetailsAdapter(ArrayList<SingleRowProduct> list){
        mDataSet=list;
        //Log.d("mDataSet", String.valueOf(mDataSet.get(1).getArticle()));
    }
    @NonNull
    @Override
    public RvCmdDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_detail_cmd, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull RvCmdDetailsAdapter.MyViewHolder holder, int position) {
            holder.label.setText(mDataSet.get(position).getArticle());
            holder.qty.setText(mDataSet.get(position).getQuantity());
            holder.qty2.setText(mDataSet.get(position).getQuantity2());
            holder.price.setText(mDataSet.get(position).getPrice());
            holder.price2.setText(mDataSet.get(position).getPrice2());
    }
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}