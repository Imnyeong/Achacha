package com.mindpass.achacha;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kj.lee on 2017. 9. 26..
 */

public class MemoAdapter extends BaseAdapter
{
    LayoutInflater inflater = null;
    private ArrayList<MemoData> m_oData = null;
    private int nListCnt = 0;

    public MemoAdapter(ArrayList<MemoData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_memo, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textTitle);
        TextView oTextMemo = (TextView) convertView.findViewById(R.id.textMemo);
        TextView oTextDate = (TextView) convertView.findViewById(R.id.textDate);
        Button oBtn = (Button) convertView.findViewById(R.id.btnDelete);

        oTextTitle.setText(m_oData.get(position).strTitle);
        oTextMemo.setText(m_oData.get(position).strMemo);
        oTextDate.setText(m_oData.get(position).strDate);
        oBtn.setOnClickListener(m_oData.get(position).onClickListener);

        convertView.setTag(""+position);
        return convertView;
    }
}
