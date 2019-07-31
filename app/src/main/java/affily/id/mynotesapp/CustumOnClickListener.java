package affily.id.mynotesapp;

import android.view.View;

public class CustumOnClickListener implements View.OnClickListener {
    private int position;
    private OnItemCallback onItemCallback;

    public CustumOnClickListener(int position, OnItemCallback onItemCallback) {
        this.position = position;
        this.onItemCallback = onItemCallback;
    }

    @Override
    public void onClick(View view) {
        onItemCallback.onItemClicked(view,position);
    }

    public interface OnItemCallback{
        void onItemClicked(View view,int position);
    }
}
