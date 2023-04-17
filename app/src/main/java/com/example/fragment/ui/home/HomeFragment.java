package com.example.fragment.ui.home;


import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import androidx.gridlayout.widget.GridLayout;
import com.example.fragment.R;
import com.example.fragment.Utils.MyButton;
import com.example.fragment.ui.Affair;
import androidx.fragment.app.Fragment;

import java.util.*;

public class HomeFragment extends Fragment {
    List<MyButton> buttonList=new ArrayList<>();
    //4个
    Map<String,GridLayout> layoutMap =new HashMap();

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initLayouMap(rootView);
        addButton();
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        ButtonValue();
        super.onCreate(savedInstanceState);

    }
    private void initLayouMap(View rootView) {
        layoutMap.put("sub2", rootView.findViewById(R.id.sub2));
        layoutMap.put("sub3", rootView.findViewById(R.id.sub3));
        layoutMap.put("sub4", rootView.findViewById(R.id.sub4));
    }

    private void ButtonValue() {
        buttonList.add(new MyButton(getContext(),"文档翻译","文档翻译",R.drawable.trans,"task/translate","","sub2","OCR文档处理过后的文字翻译功能，优于传统机器翻译"));
        buttonList.add(new MyButton(getContext(),"文稿制作","文稿制作",R.drawable.ppt,"task/outline","","sub2","OCR文字处理之后，用于完成ppt大纲以及讲稿"));
        buttonList.add(new MyButton(getContext(),"作业修订","作业修订",R.drawable.hw,"task/correct","","sub2","作业上传后的批改或润色"));


        buttonList.add(new MyButton(getContext(),"数据处理","数据处理",R.drawable.data,"task/analysis","","sub3","数据导入后进行信度、效度等验证"));
        buttonList.add(new MyButton(getContext(),"代码检测","代码检测",R.drawable.code,"task/testCode","","sub3","检测编码或代码是否会出现错误"));
        buttonList.add(new MyButton(getContext(),"文献总结","文献总结",R.drawable.docu,"task/summarize","","sub3","抓取文章的主要思想、创新点、难点等"));
        buttonList.add(new MyButton(getContext(),"语言润色","语言润色",R.drawable.article,"task/modify","","sub3","对上传的文档进行语言上的润色修改"));

        buttonList.add(new MyButton(getContext(),"日程计划","日程计划",R.drawable.plan,"task/schedule","","sub4","上传多项任务及截止时间，由系统给出安排的合理计划"));
        buttonList.add(new MyButton(getContext(),"活动策划","活动策划",R.drawable.scheme,"task/plan","","sub4","上传活动需求、参与者名单等系统进行合理规划安排"));

    }


    private void addButton() {
        // 清空视图中的所有子视图


        Typeface myTypeface = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            myTypeface = getResources().getFont(R.font.button);
        }
        int screenWidth = getResources().getDisplayMetrics().widthPixels; // 获取屏幕宽度
        int gridWidth = (int) (screenWidth / 4.5); // 假设每行有 4 个格子
        int cellWidth = (int) (gridWidth / 2.5); // 假设每个格子占据一行的一半宽度和高度

        for (MyButton myButton:buttonList) {
            if(!layoutMap.containsKey(myButton.getParent_layout()))
                continue;
            GridLayout gridLayout= layoutMap.get(myButton.getParent_layout());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            TextView textView = new TextView(getContext());
            textView.setText(myButton.getMyText());
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(myTypeface);
            textView.setPadding(0,20,0,10);

            if (myButton.getParent() != null) {
                ((ViewGroup) myButton.getParent()).removeView(myButton);
            }

            myButton.setBackgroundResource(myButton.getCon());
            // 设置 Button 的宽度和高度，并让文本居中
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(cellWidth, cellWidth);
            myButton.setLayoutParams(buttonParams);
            myButton.setPadding(5, 5, 5, 5);
            myButton.setGravity(Gravity.CENTER);
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyButton this_Button=(MyButton) view;
                    Intent intent = new Intent(getContext(), Affair.class);
                    intent.putExtra("affair", this_Button.getName());
                    intent.putExtra("url", this_Button.getUrl());
                    intent.putExtra("document_class", this_Button.getDocument_class());
                    intent.putExtra("desc_", this_Button.getDesc_());
                    startActivity(intent);
                }
            });
            // 设置 TextView 的高度为 WRAP_CONTENT
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textParams);

            layout.addView(myButton);
            layout.addView(textView);
            layout.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            layout.setLayoutParams(params);
            gridLayout.addView(layout);
        }

        //填充
        for(GridLayout gridLayout: layoutMap.values()){
            int i=gridLayout.getChildCount();
            while(i<4){
                View view=new View(getContext());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                view.setLayoutParams(params);
                gridLayout.addView(view);
                i++;
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(GridLayout gridLayout: layoutMap.values()) {
            gridLayout.removeAllViews();
        }
    }


}


