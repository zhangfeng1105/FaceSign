package com.zf.facesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SignlisetAdapter extends BaseAdapter {
    private List<Integer> list = new ArrayList<>();
    private Context context;
    private MySQLite mySQLite = MySQLite.getInstance();
    private Map<Integer, String> nameMap = mySQLite.qurryIdandName();


    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public SignlisetAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.signlist_term,viewGroup,false);
        }

        TextView signtext = (TextView) view.findViewById(R.id.signidandname);
        Button delsiagid = view.findViewById(R.id.del_signid);
        signtext.setText(list.get(i)+nameMap.get(list.get(i)));
        delsiagid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delelte(i);
            }
        });
        return view;
    }

    public abstract void delelte(int i);
}
