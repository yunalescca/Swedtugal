package com.example.swedtugal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BayesianLocalizationActivity extends AppCompatActivity implements View.OnClickListener {

    private Canvas floorPlan;
    private List<ShapeDrawable> walls;
    private int width, height;
    private Handler offMainHandler;
    private Button locateMeButton;
    TextView textView, textView2, textView3, textView4, textView5;
    private Bayesian bayesian;
    DecimalFormat df;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayesian_localization);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSizeScreen();
        drawMap();

        offMainHandler = new Handler();
        locateMeButton = (Button) findViewById(R.id.bayesianLocateMe);
        textView = (TextView) findViewById(R.id.cellPosition);
        textView2 = (TextView) findViewById(R.id.cellLessLikely);
        textView3 = (TextView) findViewById(R.id.cellMoreLikely);
        textView4 = (TextView) findViewById(R.id.lessLikely);
        textView5 = (TextView) findViewById(R.id.moreLikely);
        setVisibility(false);

        bayesian = new Bayesian(BayesianLocalizationActivity.this);

        df = new DecimalFormat("#.###");
    }

    private void setSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    /**
     * Draws the basic map.
     */
    private void drawMap() {
        this.walls = new ArrayList<>();

        // Outer shape
        drawWall(-605, -340, -603, 92, Color.BLACK); // LEFT
        drawWall(613, -340, 615, 92, Color.BLACK); // RIGHT
        drawWall(-605, 90, 615, 92, Color.BLACK); //BOTTOM
        drawWall(-605, -340, 615, -338, Color.BLACK); // TOP

        // Corridor TOP
        drawWall(-605, -157, -575, -155, Color.BLACK); // TOP 1
        drawWall(-514, -157, -456, -155, Color.BLACK); // TOP 2
        drawWall(-393, -157, -335, -155, Color.BLACK); // TOP 3
        drawWall(-268, -157, 526, -155, Color.BLACK); // TOP 4
        drawWall(583, -157, 615, -155, Color.BLACK); // TOP 5

        // Corridor BOTTOM
        drawWall(-605, -94, -550, -92, Color.BLACK); // BOTTOM 1
        drawWall(-485, -94, 526, -92, Color.BLACK); //BOTTOM 2
        drawWall(583, -94, 615, -92, Color.BLACK); //BOTTOM 3

        // Cell 16
        drawWall(-485, -340, -483, -157, Color.BLACK); // RIGHT

        // Cell 13
        drawWall(-364, -340, -362, -157, Color.BLACK); // RIGHT

        // Cell 11
        drawWall(-243, -338, -241, -157, Color.BLACK); // RIGHT

        // Top gap
        drawWall(-241, -338, -121, -157, Color.LTGRAY); // 1st GRAY
        drawWall(-121, -338, -119, -157, Color.BLACK); // 1st SEP

        drawWall(-119, -338, 2, -157, Color.LTGRAY); // 2nd GRAY
        drawWall(2, -338, 4, -157, Color.BLACK); // 2nd SEP

        drawWall(4, -338, 125, -157, Color.LTGRAY); // 3rd GRAY
        drawWall(125, -338, 127, -157, Color.BLACK); // 3rd SEP

        drawWall(127, -338, 248, -157, Color.LTGRAY); // 4th GRAY
        drawWall(248, -338, 250, -157, Color.BLACK); // 4th SEP

        drawWall(250, -338, 371, -157, Color.LTGRAY); // 5th GRAY
        drawWall(371, -338, 373, -157, Color.BLACK); // 5th SEP

        drawWall(373, -338, 494, -157, Color.LTGRAY); // 6th GRAY
        drawWall(494, -338, 496, -157, Color.BLACK); // 6th SEP

        // Bottom gap
        drawWall(-485, -92, 2, 90, Color.LTGRAY); // EL GRAY
        drawWall(2, -92, 4, 90, Color.BLACK); // EL SEP

        drawWall(4, -92, 125, 90, Color.LTGRAY); // 1st GRAY
        drawWall(125, -92, 127, 90, Color.BLACK); // 1st SEP

        drawWall(127, -92, 248, 90, Color.LTGRAY); // 2nd GRAY
        drawWall(248, -92, 250, 90, Color.BLACK); // 2nd SEP

        drawWall(250, -92, 371, 90, Color.LTGRAY); // 3rd GRAY
        drawWall(371, -92, 373, 90, Color.BLACK); // 3rd SEP

        drawWall(373, -92, 494, 90, Color.LTGRAY); // 4th GRAY
        drawWall(494, -92, 496, 90, Color.BLACK); // 4th SEP

        // Cell 14
        drawWall(-485, -92, -483, 90, Color.BLACK); // LEFT OF EL
        drawWall(-603, -92, -550, -1, Color.LTGRAY); // GRAY SMALL
        drawWall(-552, -92, -550, -1, Color.BLACK); // RIGHT SMALL
        drawWall(-605, -1, -550, 1, Color.BLACK); // BOTTOM SMALL

        ImageView floorPlanView = (ImageView) findViewById(R.id.floorPlan);
        Bitmap blankBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        this.floorPlan = new Canvas(blankBitmap);
        floorPlanView.setImageBitmap(blankBitmap);
        for(ShapeDrawable drawableWall: walls) drawableWall.draw(this.floorPlan);
    }


    /**
     * Draw a wall on the canvas. Default position in center of screen.
     * @param left pixels to add to left bound.
     * @param top pixels to add to top bound.
     * @param right pixels to add to right bound.
     * @param bottom pixels to add to bottom bound.
     * @param color color of wall.
     */
    private void drawWall (int left, int top, int right, int bottom, int color) {
        ShapeDrawable drawableWall = new ShapeDrawable(new RectShape());
        drawableWall.setBounds(this.width / 2 + left, this.height / 2 + top,
                this.width / 2 + right, this.height / 2 + bottom);
        drawableWall.getPaint().setColor(color);

        this.walls.add(drawableWall);
    }

    private void fillCell(int left, int top, int right, int bottom, boolean hide, int color) {
        if (!hide){
            ShapeDrawable cellToFill = new ShapeDrawable(new RectShape());

            cellToFill.setBounds(this.width / 2 + left, this.height / 2 + top,
                    this.width / 2 + right, this.height / 2 + bottom);
            cellToFill.getPaint().setColor(color);
            cellToFill.draw(floorPlan);
        }
    }

    private int selectColor(double probability){
        int color;
        if (probability<0.15){
            color = Color.rgb(248, 233, 232);
        } else if (probability<0.4){
            color = Color.rgb(255,176,169);
        } else if (probability<0.9){
            color = Color.rgb(181,123,128);
        } else{
            color = Color.rgb(107, 77, 87);
        }
        return color;
    }

    @Override
    public void onClick(View v) {
        textView.setText(getString(R.string.locationLoading));
        setVisibility(false);
        locateMeButton.setEnabled(false);
        drawMap();
        Runnable offMainRunnable = new Runnable() {
            @Override
            public void run() { // this thread is not on the main
                final List<Double> posteriorProbability = bayesian.getScan();
                final int maxCell = bayesian.guessLocation();
                final int minCell = posteriorProbability.indexOf(Collections.min(posteriorProbability)) + 1;
                final String minProb = df.format(posteriorProbability.get(minCell-1));
                offMainHandler.post(new Runnable() {
                    @Override
                    public void run() { // main thread
                        fillCells(posteriorProbability);
                        switch (maxCell) {
                            case 1:
                                textView.setText(getString(R.string.in_cell_1));
                                break;
                            case 2:
                                textView.setText(getString(R.string.in_cell_2));
                                break;
                            case 3:
                                textView.setText(getString(R.string.in_cell_3));
                                break;
                            case 4:
                                textView.setText(getString(R.string.in_cell_4));
                                break;
                            case 5:
                                textView.setText(getString(R.string.in_cell_5));
                                break;
                            case 6:
                                textView.setText(getString(R.string.in_cell_6));
                                break;
                            case 7:
                                textView.setText(getString(R.string.in_cell_7));
                                break;
                            case 8:
                                textView.setText(getString(R.string.in_cell_8));
                                break;
                            case 9:
                                textView.setText(getString(R.string.in_cell_9));
                                break;
                            case 10:
                                textView.setText(getString(R.string.in_cell_10));
                                break;
                            case 11:
                                textView.setText(getString(R.string.in_cell_11));
                                break;
                            case 12:
                                textView.setText(getString(R.string.in_cell_12));
                                break;
                            case 13:
                                textView.setText(getString(R.string.in_cell_13));
                                break;
                            case 14:
                                textView.setText(getString(R.string.in_cell_14));
                                break;
                            case 15:
                                textView.setText(getString(R.string.in_cell_15));
                                break;
                            case 16:
                                textView.setText(getString(R.string.in_cell_16));
                                break;
                            default:
                                textView.setText(getString(R.string.in_cell_default));
                                break;
                        }
                        if(minProb.equals("0")){
                            textView2.setText("Cell " + minCell + " with probability " + "~0" +"!");
                        } else {
                            textView2.setText("Cell " + minCell + " with probability " + minProb +"!");
                        }
                        textView3.setText("Cell " + maxCell + " with probability " + df.format(posteriorProbability.get(maxCell-1)) +"!");
                        setVisibility(true);
                        locateMeButton.setEnabled(true);
                    }
                });
            }
        };
        new Thread(offMainRunnable).start();
    }

    public void setVisibility(boolean visible){
        if(visible){
            textView2.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            textView4.setVisibility(View.VISIBLE);
            textView5.setVisibility(View.VISIBLE);
        } else{
            textView2.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);
            textView4.setVisibility(View.INVISIBLE);
            textView5.setVisibility(View.INVISIBLE);
        }
    }

    private void fillCells(List<Double> posteriorProbability){
        fillCell(496,-92,613,90, posteriorProbability.get(0) < 0.1, selectColor(posteriorProbability.get(0)));
        fillCell(496,-155,613,-94,  posteriorProbability.get(1) < 0.1,selectColor(posteriorProbability.get(1)));
        fillCell(496,-338,613,-157, posteriorProbability.get(2) < 0.1, selectColor(posteriorProbability.get(2)));
        fillCell(371,-155,496,-94, posteriorProbability.get(3) < 0.1, selectColor(posteriorProbability.get(3)));
        fillCell(248,-155,373,-94, posteriorProbability.get(4) < 0.1, selectColor(posteriorProbability.get(4)));
        fillCell(125,-155,250,-94, posteriorProbability.get(5) < 0.1, selectColor(posteriorProbability.get(5)));
        fillCell(2,-155,127,-94, posteriorProbability.get(6) < 0.1, selectColor(posteriorProbability.get(6)));
        fillCell(-121,-155,4,-94, posteriorProbability.get(7) < 0.1, selectColor(posteriorProbability.get(7)));
        fillCell(-244,-155,-119,-94, posteriorProbability.get(8) < 0.1, selectColor(posteriorProbability.get(8)));
        fillCell(-364,-155,-242,-94, posteriorProbability.get(9) < 0.1, selectColor(posteriorProbability.get(9)));
        fillCell(-362,-338,-243,-157, posteriorProbability.get(10) < 0.1, selectColor(posteriorProbability.get(10)));
        fillCell(-485,-155,-362,-94, posteriorProbability.get(11) < 0.1, selectColor(posteriorProbability.get(11)));
        fillCell(-483,-338,-364,-157, posteriorProbability.get(12) < 0.1, selectColor(posteriorProbability.get(12)));
        fillCell(-603,0,-485,90, posteriorProbability.get(13) < 0.1, selectColor(posteriorProbability.get(13)));
        fillCell(-603,-155,-483,-94, posteriorProbability.get(14) < 0.1, selectColor(posteriorProbability.get(14)));
        fillCell(-603,-338,-485,-157, posteriorProbability.get(15) < 0.1, selectColor(posteriorProbability.get(15)));
    }
}