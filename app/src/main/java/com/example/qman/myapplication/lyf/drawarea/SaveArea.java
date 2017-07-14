package com.example.qman.myapplication.lyf.drawarea;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.qman.myapplication.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.example.qman.myapplication.R.string.username;

/**
 * Created by lyf on 13/07/2017.
 */

public class SaveArea extends DialogFragment{
    Button savearea_cancle;
    Button savearea_sure;
    EditText savearea_fieldname;
    CheckBox savearea_Cropkinds_corn;
    CheckBox savearea_Cropkinds_wheat;
    CheckBox savearea_Cropkinds_rice;
    public interface savearea_Listener
    {
        void savearea_sureClick(String fieldname,ArrayList<String> Cropkinds);
        void savearea_cancleClick();
    }
    private savearea_Listener m_savearea_Listener;
    public void setOnButtonClickListener(savearea_Listener mOnButtonClickLitener)
    {
        this.m_savearea_Listener = mOnButtonClickLitener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.savearea, container);
        savearea_cancle=(Button)view.findViewById(R.id.savearea_cancle);
        savearea_sure =(Button)view.findViewById(R.id.savearea_sure);
        savearea_fieldname=(EditText)view.findViewById(R.id.savearea_fieldname);
        savearea_Cropkinds_corn=(CheckBox)view.findViewById(R.id.savearea_Cropkinds_corn);
        savearea_Cropkinds_wheat=(CheckBox)view.findViewById(R.id.savearea_Cropkinds_wheat);
        savearea_Cropkinds_rice=(CheckBox)view.findViewById(R.id.savearea_Cropkinds_rice);

        savearea_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String>cropKinds=new ArrayList<String>();

                if(savearea_Cropkinds_corn.isChecked())
                {
                    cropKinds.add(savearea_Cropkinds_corn.getText().toString());
                }
                if(savearea_Cropkinds_wheat.isChecked())
                {
                    cropKinds.add(savearea_Cropkinds_wheat.getText().toString());;
                }
                if(savearea_Cropkinds_rice.isChecked())
                {
                    cropKinds.add(savearea_Cropkinds_rice.getText().toString());;
                }
                m_savearea_Listener.savearea_sureClick( savearea_fieldname.getText().toString(),cropKinds);
            }
        });
        savearea_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_savearea_Listener.savearea_cancleClick();
            }
        });
        return view;
    }
}
