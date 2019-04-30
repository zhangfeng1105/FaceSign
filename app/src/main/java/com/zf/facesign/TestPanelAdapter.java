package com.zf.facesign;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kelin.scrollablepanel.library.PanelAdapter;

import java.util.List;


public abstract class TestPanelAdapter extends PanelAdapter {
    private List<List<String>> data;
    private List<String> _mHeaders;
    private static String btnname;

    public void setBtnname(String btnname) {
        this.btnname = btnname;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public void set_mHeaders(List<String> data) {
        this._mHeaders = data;
    }

    @Override
    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.size() + 1;
    }

    @Override
    public int getColumnCount() {
        if (_mHeaders == null) {
            return 0;
        }
        return _mHeaders.size() + 1;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row > 0 && column == getColumnCount() - 1) {
            return 1;
        }
        return super.getItemViewType(row, column);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
        if (data == null || data.size() == 0 || _mHeaders == null || _mHeaders.size() == 0) {
            return;
        }
        if (row == 0) {
            if (column < getColumnCount() - 1) {
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                titleViewHolder.titleTextView.setBackgroundColor(Color.rgb(0xff, 0xff, 0xe0));
                titleViewHolder.titleTextView.getPaint().setFakeBoldText(true);
                String title = _mHeaders.get(column);
                titleViewHolder.titleTextView.setText(title);
            } else {
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                titleViewHolder.titleTextView.setBackgroundColor(Color.rgb(0xff, 0xff, 0xe0));
                titleViewHolder.titleTextView.getPaint().setFakeBoldText(true);
                titleViewHolder.titleTextView.setText("操作");
            }
        } else {
            if (column < getColumnCount() - 1) {
                TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
                String title = data.get(row - 1).get(column);
                titleViewHolder.titleTextView.setText(title);
                titleViewHolder.titleTextView.setTextSize(20);
            } else {
                final BtnViewHolder btnViewHolder = (BtnViewHolder) holder;
                btnViewHolder.btn.setTag(row - 1);
                btnViewHolder.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetFace((Integer) btnViewHolder.btn.getTag());
                    }
                });
            }
        }
    }

    public abstract void resetFace(int tag);


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new TestPanelAdapter.TitleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_title, parent, false));
        } else {
            return new TestPanelAdapter.BtnViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.btnitem, parent, false));
        }

    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public TitleViewHolder(View view) {
            super(view);
            this.titleTextView = (TextView) view.findViewById(R.id.title);
        }
    }

    private static class BtnViewHolder extends RecyclerView.ViewHolder {
        public Button btn;

        public BtnViewHolder(View view) {
            super(view);
            this.btn = (Button) view.findViewById(R.id.doface);
            if (btnname != null && btnname.equals(""))
                this.btn.setText(btnname);
        }
    }
}
