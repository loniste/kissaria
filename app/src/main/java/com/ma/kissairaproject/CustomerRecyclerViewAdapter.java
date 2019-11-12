package com.ma.kissairaproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

class CustomerRecyclerViewAdapter extends RecyclerView.Adapter<CustomerRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<CustomerSingleRow> mDataset;
    private static int prev_expanded=-1;
    private Context context;
    private RecyclerView recycler;
    private String userId;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cmd;
        public ImageView status;
        public TextView price;
        public TextView afterCommaPrice;
        public RecyclerView rv_detail_cmd;
        TextView address;
        TextView customer_ship_date;
        TextView creation_time;
        TextView creation_date;
        TextView full_name;

        public int visibilityState=0;
        View v;
        public MyViewHolder(View view) {
            super(view);
            this.cmd = view.findViewById(R.id.commande);
            this.status = view.findViewById(R.id.status);
            this.price = view.findViewById(R.id.price);
            this.afterCommaPrice = view.findViewById(R.id.afterCommaPrice);



            this.address = view.findViewById(R.id.address);
            this.customer_ship_date = view.findViewById(R.id.customer_ship_date);
            this.creation_date = view.findViewById(R.id.creation_date);
            this.creation_time = view.findViewById(R.id.creation_time);
            this.full_name = view.findViewById(R.id.full_name);
            this.rv_detail_cmd = view.findViewById(R.id.rv_detail_cmd);
            this.v = view;
        }

        View getView() {
            return v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    CustomerRecyclerViewAdapter(ArrayList<CustomerSingleRow> myDataset, Context c, String userId) {
        this.context = c;
        mDataset = myDataset;
        this.userId =userId;

    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_customer, parent, false);
        return new MyViewHolder(v);
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
//        recycler.getRecycledViewPool().setMaxRecycledViews(holder.getItemViewType(), 0);

        holder.cmd.setText(mDataset.get(position).getCmd());
        holder.status.setImageResource(mDataset.get(position).getStatusCode());
        holder.price.setText(mDataset.get(position).getPrice());
        holder.afterCommaPrice.setText(mDataset.get(position).getAfterCommaPrice());

        holder.address.setText(mDataset.get(position).getAddress());
        holder.creation_time.setText(mDataset.get(position).getCreation_time());
        holder.creation_date.setText(mDataset.get(position).getCreation_date());
        holder.customer_ship_date.setText(mDataset.get(position).getCustomer_ship_date());
        holder.full_name.setText(mDataset.get(position).getFull_name());

        holder.rv_detail_cmd.setLayoutManager(new LinearLayoutManager(context));
        holder.rv_detail_cmd.setAdapter(new RvShopDetailsAdapter(mDataset.get(position).getDetailShopList(),context, userId));

        if (holder.visibilityState==1){
            holder.getView().findViewById(R.id.hidable_view).setVisibility(View.VISIBLE);
        } else  {
            holder.getView().findViewById(R.id.hidable_view).setVisibility(View.GONE);
        }
        holder.address.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Toast.makeText(holder.address.getContext(), holder.address.getText(), Toast.LENGTH_SHORT).show();
              }
        });


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


//                holder.itemView.setOnClickListener(null);
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


}

class RvShopDetailsAdapter extends RecyclerView.Adapter<RvShopDetailsAdapter.MyViewHolder> {
    private ArrayList<SingleRowShop> shopList;
    private String userId;


    Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder {//Why static?
        TextView shopName;
        View shopStatus;
        RecyclerView productsRecyclerView;

        String phone_number;
        public ImageView callButton;
        public TextView bouton1;
        public TextView bouton2;
        public TextView bouton3;

        View v;
        MyViewHolder(View view) {
            super(view);
            this.shopName = view.findViewById(R.id.shop_name);
            this.shopStatus=view.findViewById(R.id.status_line);

            this.bouton1 = view.findViewById(R.id.txtView1);
            this.bouton2 = view.findViewById(R.id.txtView2);
            this.bouton3 = view.findViewById(R.id.txtView3);
            this.callButton = view.findViewById(R.id.call);


            this.productsRecyclerView = view.findViewById(R.id.shop_recycler_view);
            this.v=view;
        }
        View getView(){
            return v;
        }
    }

    RvShopDetailsAdapter(ArrayList<SingleRowShop> list, Context context, String userId){
        shopList =list;
        this.context=context;
        this.userId=userId;

        //Log.d("shopList", String.valueOf(shopList.get(1).getArticle()));
    }
    @NonNull
    @Override
    public RvShopDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_of_shop, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return (MyViewHolder) vh;
    }
    @Override
    public void onBindViewHolder(@NonNull RvShopDetailsAdapter.MyViewHolder holder, int position) {
            holder.shopName.setText(shopList.get(position).getShopName());
            int statusColor = 0;
            switch (shopList.get(position).getShopStatus()) {
                case "pending":
                    statusColor=ContextCompat.getColor(holder.shopStatus.getContext(), R.color.pending);
                    break;
                case "canceled":
                    statusColor=ContextCompat.getColor(holder.shopStatus.getContext(), R.color.canceled);
                    break;
                case "ready":
                    statusColor=ContextCompat.getColor(holder.shopStatus.getContext(), R.color.ready);
                    break;
                case "received":
                    statusColor=ContextCompat.getColor(holder.shopStatus.getContext(), R.color.received);
                    break;
            }
            holder.shopStatus.setBackgroundColor(statusColor);
            holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.shopName.getContext()));
            holder.productsRecyclerView.setAdapter(new ProductsDetailsAdapter(shopList.get(position).getProductsList()));

            ((MyViewHolder) holder).callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+ holder.phone_number));
                    holder.getView().getContext().startActivity(intent);

                }
            });
            holder.phone_number=shopList.get(position).getPhoneNumber();

            customerButtonsProcessing(holder, position);

    }
    private void customerButtonsProcessing(RvShopDetailsAdapter.MyViewHolder holder, int position) {
        /**processing buttons in a single item when status is ready*/
        if (shopList.get(position).getShopStatus().equals("ready")) {
            holder.bouton1.setVisibility(View.VISIBLE);
            holder.bouton1.setText("Reçu");
            setButtonBg(holder.bouton1, R.drawable.ready_bg);
            holder.bouton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    if (!userId.equals("")){
                        try {
                            String result=backgroundWorker.execute("post_status", userId, shopList.get(position).getShopName(),"received").get();
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("status").equals("success")){
                                // change status icon
                                holder.shopStatus.setBackgroundColor(ContextCompat.getColor(holder.shopStatus.getContext(), R.color.received));
                                //make buttons gone
                                holder.bouton1.setVisibility(View.GONE);
                                holder.bouton2.setVisibility(View.GONE);
                                holder.bouton3.setVisibility(View.GONE);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            holder.bouton2.setVisibility(View.VISIBLE);
            holder.bouton2.setText("Annuler");
            setButtonBg(holder.bouton2,R.drawable.canceled_bg);
            holder.bouton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    if (!userId.equals("")){
                        try {
                            String result=backgroundWorker.execute("post_status",userId,shopList.get(position).getshId(),"canceled").get();
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("status").equals("success")){
                                // change status icon
                                holder.shopStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.canceled));
                                //make buttons gone
                                holder.bouton1.setVisibility(View.GONE);
                                holder.bouton2.setVisibility(View.GONE);
                                holder.bouton3.setVisibility(View.GONE);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            holder.bouton3.setVisibility(View.GONE);
        }
        /**processing buttons in a single item when status is canceled or received*/
        else if (shopList.get(position).getShopStatus().equals("canceled")  || shopList.get(position).getShopStatus().equals("received")){

            holder.bouton1.setVisibility(View.GONE);
            holder.bouton2.setVisibility(View.GONE);
            holder.bouton3.setVisibility(View.GONE);
        }
        if (shopList.get(position).getShopStatus().equals("pending")) {
            holder.bouton1.setVisibility(View.VISIBLE);
            holder.bouton1.setText("Annuler");
            setButtonBg(holder.bouton1,R.drawable.canceled_bg);
            holder.bouton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackgroundWorker backgroundWorker=new BackgroundWorker(holder.bouton1.getContext());
                    if (!userId.equals("")){
                        try {
                            String result=backgroundWorker.execute("post_status",userId,shopList.get(position).getshId(),"canceled").get();
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("status").equals("success")){
                                // change status icon
                                holder.shopStatus.setBackgroundColor(ContextCompat.getColor(context,R.color.canceled));
                                //make buttons gone
                                holder.bouton1.setVisibility(View.GONE);
                                holder.bouton2.setVisibility(View.GONE);
                                holder.bouton3.setVisibility(View.GONE);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            holder.bouton2.setVisibility(View.GONE);
            holder.bouton3.setVisibility(View.GONE);
        }
        else {
            Log.d("status_problem", "status problem");
        }
    }
    @Override
    public int getItemCount() {
        return shopList.size();
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


class ProductsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<SingleRowProduct> productsList;

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
            this.price2 = view.findViewById(R.id.price2);
            this.v=view;
        }
        View getView(){
            return v;
        }
    }
    ProductsDetailsAdapter(ArrayList<SingleRowProduct> list){
        productsList =list;
        //Log.d("shopList", String.valueOf(shopList.get(1).getArticle()));
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_detail_cmd, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return (MyViewHolder) vh;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder) holder).label.setText(productsList.get(position).getArticle());
            ((MyViewHolder) holder).qty.setText(productsList.get(position).getQuantity());
            ((MyViewHolder) holder).qty2.setText(productsList.get(position).getQuantity2());
            ((MyViewHolder) holder).price.setText(productsList.get(position).getPrice());
            ((MyViewHolder) holder).price2.setText(productsList.get(position).getPrice2());
    }
    @Override
    public int getItemCount() {
        return productsList.size();
    }
}