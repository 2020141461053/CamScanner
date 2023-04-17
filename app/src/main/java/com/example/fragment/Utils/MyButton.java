package com.example.fragment.Utils;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;
import lombok.Getter;

@Getter
public class MyButton extends AppCompatButton {
    String name;//唯一
    String myText;//显示字段
    int con;//图片id
    String url;//后端接口url
    String document_class;//文件种类
    String parent_layout;

    String desc_;

    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context,
                    String name,
                    String text,
                    int    con,
                    String url,
                    String document_class,
                    String parent_layout,
                    String desc_) {
        super(context);
        this.name=name;
        this.myText =text;
        this.con=con;
        this.url=url;
        this.document_class=document_class;
        this.parent_layout= parent_layout;
        this.desc_=desc_;
    }


}