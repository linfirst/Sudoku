package com.linfirst.sudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SudokuView extends View {

// "360000000004230800000004200" + "070460003820000014500013020" + "001900000007048300000000045";

    private int MAX_X_Y=9;
    public String[] STR = new String[81];

    private Context mContext;
    public float width;
    public float height;
    public boolean flag = false;
    public boolean[][] original = new boolean[9][9];
    int x;
    int y;
    /**
     * 用一个 三维数组 存放已经对应的 x 轴 y 轴 网格 已经有的数据
     */
    private int [][][]usedNumber = new int[9][9][];

    /**
     * 用 Button 数组将按键记录下来
     */


    public SudokuView(Context context) {
        super(context);
        mContext = context;
        initSTR();
        getUsedArray();

    }

    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initSTR();
        getUsedArray();

    }

    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initSTR();
        getUsedArray();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initSTR();
        getUsedArray();

    }


    private void initSTR() {
        for (int i = 0; i < STR.length; i++) {
            STR[i]="0";
        }
        SudokuComplete sudokuComplete = new SudokuComplete();
        int[][] randomMatrix = sudokuComplete.generatePuzzleMatrix();
        int m = 0;

        //将生成的终盘写入数组中
        for (int i = 0; i < randomMatrix.length; i++) {
            for (int j = 0; j < randomMatrix.length; j++) {
                STR[m++] = String.valueOf(randomMatrix[i][j]);
            }
        }
        int empty=42;
        //随机置0（挖空）
        for (int rand = 0; rand < empty; rand++) {
            int temp=(int) ((Math.random() * 81));
            if ("0".equals(STR[temp]) ){
                empty=empty+1;
            }else {
                STR[temp] = "0";
            }
        }
//        int n=0;
//        for (int i=0;i<81;i++){
//            if("0".equals(STR[i])){
//                n++;
//            }
//
//        }
//        Log.e("NNNNNNN nn",n+"");
    }

    /**
     * 重新绘制View
     */
    public void reDraw() {
        // 进行数据刷新
        getUsedArray();
        invalidate();
    }

    /**
     *  得到对应 x y 的值
     * @param x
     * @param y
     * @return
     */
    public int getNumber(int x, int y) {
        return Integer.valueOf(STR[getIndex(x,y)]);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
        // 得到 九宫格 格子的高度和宽度,高和宽长度一样（正方形）
        width = (w - 12) / 9f;
        height = (w - 12) / 9f;
    }

    /**
     * 确定View尺寸
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //获取模式，一共有三种模式
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //获取模式
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);

        int size = 0;
        //如果是 UNSPECIFIED 模式
        //UNSPECIFIED：将视图按照自己的意愿设置成任意的大小，没有任何限制。
        if (widthModel == MeasureSpec.UNSPECIFIED) {
            size = heightSize;
        } else if (heightModel == MeasureSpec.UNSPECIFIED) {
            size = widthSize;
        } else {
            size = Math.min(widthSize, heightSize);
        }
        //告诉父布局在三种模式下需要的尺寸
        setMeasuredDimension(size, size);
    }

    /**
     * 绘制View
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        //消除锯齿一样的边缘
        paint.setAntiAlias(true);

        // 先画一个空心的方形 作为外背景
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.gray));


        // 画9条横轴 纵轴
        for (int i = 0; i < 10; i++) {
            paint.setStrokeWidth(3);
            paint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
            // 横轴
            canvas.drawLine(6, i * height + 6, getWidth() - 6, i * height + 6, paint);
            // 纵轴
            canvas.drawLine(i * width + 6, 6, i * width + 6, getWidth() - 6, paint);
        }
        //将目前的 九宫格 分为 3 块
        for (int i = 0; i < 10; i=i+3) {
                paint.setStrokeWidth(4);
                paint.setColor(ContextCompat.getColor(mContext, R.color.black));
                // 横轴
                canvas.drawLine(6, i * height + 6, getWidth() - 6, i * height + 6, paint);
                // 纵轴
                canvas.drawLine(i * width + 6, 6, i * width + 6, getWidth() - 6, paint);
        }

        //进行 写入 数字操作 9 * 9
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((float) (height * 0.75));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float x1 = width / 2;
        float y1 = height / 2 - (fontMetrics.ascent + fontMetrics.descent) / 2;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int point = getNumber(i, j);
                //设置原有的数字和手动输入的数字颜色
                if (point != 0 && original[i][j]) {
                    paint.setColor(Color.BLACK);
                    paint.setTypeface(Typeface.SANS_SERIF);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawText(String.valueOf(point), i * width + x1 + 6, j * height + y1 + 6, paint);

                } else if (point != 0 && !original[i][j]) {
                    paint.setColor(Color.BLUE);
                    canvas.drawText(String.valueOf(point), i * width + x1 + 6, j * height + y1 + 6, paint);
                }
            }
        }
        super.onDraw(canvas);
    }

    /**
     * 得到坐标(x,y)x轴，y轴，方格中含有的数字
     * @param x：x坐标
     * @param y：y坐标
     * @return 返回x轴，y轴，方格中含有的数字的数组
     */
    public int[] getUsedToArray(int x, int y) {
        return usedNumber[x][y];
    }
    int k=0;
    /**
     * 查找对应的 行、列、方格已有的数字
     * @param x：x轴
     * @param y：y轴
     * @return ：返回对应的 行、列、方格已有的数字
     */
    public int[] computerXAndYUsed(int x, int y) {
        //初始化int数组，默认值全为0;
        int[] used = new int[9];

        // 查找横轴 (X)
        for (int i = 0; i < MAX_X_Y; i++) {
            int point = getNumber(i, y);
            if (point != 0) {
                //将对应的下标-1后，置为对应的数值
                used[point - 1] = point;
            }
        }

        // 查找纵轴 (Y)
        for (int i = 0; i < MAX_X_Y; i++) {
            int point = getNumber(x, i);
            if (point != 0) {
                //将对应的下标-1后，置为对应的数值
                used[point - 1] = point;
            }
        }

        // 查找网格
        // 计算出 x y 在网格中的最初位置（以一个3*3网格为单位，确定这个点在3*3网格中的哪一个格子）
        int xStart = (x / 3) * 3;
        int yStart = (y / 3) * 3;

        Log.e("UUUUUUUU",xStart+"   "+yStart+"   "+k++);
        for (int i = xStart; i < xStart + 3; i++) {
            for (int j = yStart; j < yStart + 3; j++) {
                int point = getNumber(i, j);
                if (point != 0) {
                    used[point - 1] = point;
                }
            }
        }
//        for (int i=0;i<used.length;i++){
//            Log.e("CCCCCC",used[i]+"");
//        }
        return used;
    }

    /**
     * 得到所有 x y 已有的数据
     */
    public void getUsedArray() {
        for (int x = 0; x < MAX_X_Y; x++) {
            for (int y = 0; y < MAX_X_Y; y++) {
                //flag:标识这是初始化读入数据，只在初始化的时候执行一次
                // 如果不为0，标记这点为true，表示是初始化的数据
                if ((!flag) && (getNumber(x, y) != 0)) {
                    original[x][y] = true;
                }
                //获取所有对应空格(x,y)对应的 行、列、方格已有的数字
                if (getNumber(x,y)==0){
                    usedNumber[x][y] = computerXAndYUsed(x, y);
//                Log.e("UUUUUUU",usedArray[i][j]+"");
                }

            }
        }
    }

    /**
     * 根据x,y（格子）坐标计算索引
     * @param x ：x坐标
     * @param y：y坐标
     * @return :返回索引
     */
    private int getIndex(int x,int y){
        return y * 9 + x;
    }

    /**
     * 修改对应格子的数字
     * @param x
     * @param y
     * @param number ：需要修改的新数字
     */
    public void refreshNumber(int x, int y, char number) {
        //判断是否是原有固定数据
        if(!original[x][y]){
            //将旧数字替换成新数字
            STR[getIndex(x,y)] = String.valueOf(number);
        }
    }

    /**
     * 清除输入
     * @param x
     * @param y
     */
    public void clearNumber(int x, int y) {
        //判断是否是原有固定数据
        if(!original[x][y]&&getNumber(x,y)!=0) {
            // 将对应下标置0
            STR[getIndex(x, y)] = "0";
        }
    }
}
