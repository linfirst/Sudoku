package com.linfirst.sudoku;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    SudokuView sudokuView;
    private Button bt[] = new Button[9];
    private Button bt_clear;
    private int MAX_GRID = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sudokuView = findViewById(R.id.sudoView);

        initView();
    }





    // 重写 触摸 事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //点击的x方向绝对距离/每个格子的长度=格子x坐标
            sudokuView.x = (int) ((event.getX()) / sudokuView.width);
            sudokuView.y = (int) ((event.getY()) / sudokuView.height)-2;

            Log.e("XXXXXX", sudokuView.x+"   "+  sudokuView.y);
            Log.e("XXXXXX",event.getX()+"   "+ event.getY());
            Log.e("XXXXXX", sudokuView.width+"   "+  sudokuView.height);
            //如果点击范围超过格子范围则不做处理
            if (sudokuView.y > MAX_GRID || sudokuView.x > MAX_GRID) {
                return true;
            }
            Log.e("XXXXXX", "啊啊啊啊");
            if (!sudokuView.original[sudokuView.x][sudokuView.y]) {
                //手动输入。将flag置为true，让org在之后不再执行。
                sudokuView.flag = true;
                int []used = sudokuView.getUsedToArray(sudokuView.x, sudokuView.y);

                for (int i = 0; i < 9; i++) {
                    bt[i].setVisibility(View.GONE);
                    bt[i].setTextColor(Color.BLACK);
                }

                //用来设置点击后和格子里相同数字的button可见并且为蓝色，提醒用户
                int clickNumber = sudokuView.getNumber(sudokuView.x, sudokuView.y);
                Log.e("AAAAAA", clickNumber + "");

                // 将对应的 x y 轴、方格上已经出现的数字进行屏蔽
                for (int i = 0; i < 9; i++) {
                    if (used[i] != 0 && (i != clickNumber - 1)) {
                        Log.e("UUUUUU", used[i] + "");
                    } else {
                        bt[i].setVisibility(View.VISIBLE);
                    }
                }
                if (clickNumber != 0) {
                    bt[clickNumber - 1].setTextColor(Color.BLUE);
                }


                // 设置点击事件
                for (int i = 0; i < 9; i++) {
                    final char t = (char) (i + 1 + '0');
                    bt[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (sudokuView.y <= MAX_GRID && sudokuView.x <= MAX_GRID) {
                                sudokuView.refreshNumber(sudokuView.x, sudokuView.y, t);
                                sudokuView.reDraw();
                            }

                        }
                    });
                }
//
//
            }
        }
        return super.onTouchEvent(event);
    }

    // 查找所有的 Button
    public void initView() {
        bt[0] = findViewById(R.id.one);
        bt[1] = findViewById(R.id.two);
        bt[2] = findViewById(R.id.three);
        bt[3] = findViewById(R.id.four);
        bt[4] = findViewById(R.id.five);
        bt[5] = findViewById(R.id.six);
        bt[6] = findViewById(R.id.seven);
        bt[7] = findViewById(R.id.eight);
        bt[8] = findViewById(R.id.nine);
        bt_clear=findViewById(R.id.clear);

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sudokuView.y <= MAX_GRID && sudokuView.x <= MAX_GRID) {
                    sudokuView.clearNumber(sudokuView.x, sudokuView.y);
                    sudokuView.reDraw();
                }
            }
        });
    }

}
