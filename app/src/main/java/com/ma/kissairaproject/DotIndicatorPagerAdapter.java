package com.ma.kissairaproject;

/*import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;*/
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class DotIndicatorPagerAdapter extends PagerAdapter {

  private  ArrayList<SellerSingleRow> list;

  /*private static final List<Item> items =
      Arrays.asList(new Item(R.color.md_indigo_500), new Item(R.color.md_green_500), new Item(R.color.md_red_500), new Item(R.color.md_orange_500),
          new Item(R.color.md_purple_500));*/
  public DotIndicatorPagerAdapter(ArrayList<SellerSingleRow> list){
      this.list=list;
  }
  @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, int position) {
    View item = LayoutInflater.from(container.getContext()).inflate(R.layout.material_page, container, false);
    if (position==0){
      /*int imageCode=R.drawable.rose_jaune;
      ImageView imageView=item.findViewById(R.id.item_image);
      imageView.setImageResource(imageCode);*/
    }

        TextView commande= item.findViewById(R.id.commande);
        ImageView status= item.findViewById(R.id.status_tv);
        TextView price= item.findViewById(R.id.price);


        commande.setText(list.get(position).getCmd());
        status.setImageResource(list.get(position).getStatusCode());
        price.setText(list.get(position).getPrice());

        final int ORANGE = 0xFFf0f0f0;
        CardView cv= (CardView) item.findViewById(R.id.cv);
        if (list.get(position).getRecent()) {
          cv.setCardBackgroundColor(ORANGE);
        } else {
          cv.setCardBackgroundColor(Color.WHITE);
        }




    CardView cardView = item.findViewById(R.id.cv);
    /*cardView.setCardBackgroundColor(
        ContextCompat.getColor(container.getContext(), (items.get(position).color)));*/
    container.addView(item);
    return item;
  }

  @Override public int getCount() {
    return list.size();
  }

  @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
    return view == object;
  }

  @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }

  private static class Item {
    private final int color;

    private Item(int color) {
      this.color = color;
    }
  }
}
