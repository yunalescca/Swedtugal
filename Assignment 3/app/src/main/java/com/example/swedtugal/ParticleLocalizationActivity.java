package com.example.swedtugal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ParticleLocalizationActivity extends AppCompatActivity implements SensorEventListener{

    private Canvas floorPlan;
    private List<Particle> particles;
    private List<Wall> walls;
    private List<ShapeDrawable> drawableWalls, drawableParticles;
    private List<Double> magnitudeData;
    private EditText strideField;
    private int width, height;
    private double stride;
    private TextView cellTextView, trueAngleTextView, calibratedAngleTextView;

    private SensorManager sensorManager;
    private Sensor accelerometer,
            magneticField;

    private float[] accelerometerData,
            magneticFieldData;

    private String direction;
    private float calibration;

    private boolean locating;

    private static final float ALPHA = 0.80f;
    private static final int tMax = 14;
    private static final double METER_IN_PIXELS = 30.2;
    private static final double NUMBER_PARTICLES = 2000;
    private static final int WIDTH_HALF = 640;
    private static final int HEIGHT_HALF = 360;
    private static final int
            CELL_1_LEFT_X = WIDTH_HALF + 496,
            CELL_1_RIGHT_X = WIDTH_HALF + 613,
            CELL_1_TOP_Y = HEIGHT_HALF - 92,
            CELL_1_BOTTOM_Y = HEIGHT_HALF + 92,

            CELL_2_LEFT_X = WIDTH_HALF + 496,
            CELL_2_RIGHT_X = WIDTH_HALF + 613,
            CELL_2_TOP_Y = HEIGHT_HALF - 155,
            CELL_2_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_3_LEFT_X = WIDTH_HALF + 496,
            CELL_3_RIGHT_X = WIDTH_HALF + 613,
            CELL_3_TOP_Y = HEIGHT_HALF - 340,
            CELL_3_BOTTOM_Y = HEIGHT_HALF - 157,

            CELL_4_LEFT_X = WIDTH_HALF + 373,
            CELL_4_RIGHT_X = WIDTH_HALF + 494,
            CELL_4_TOP_Y = HEIGHT_HALF - 155,
            CELL_4_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_5_LEFT_X = WIDTH_HALF + 250,
            CELL_5_RIGHT_X = WIDTH_HALF + 371,
            CELL_5_TOP_Y = HEIGHT_HALF - 155,
            CELL_5_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_6_LEFT_X = WIDTH_HALF + 127,
            CELL_6_RIGHT_X = WIDTH_HALF + 248,
            CELL_6_TOP_Y = HEIGHT_HALF - 155,
            CELL_6_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_7_LEFT_X = WIDTH_HALF + 4,
            CELL_7_RIGHT_X = WIDTH_HALF + 125,
            CELL_7_TOP_Y = HEIGHT_HALF - 155,
            CELL_7_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_8_LEFT_X = WIDTH_HALF - 119,
            CELL_8_RIGHT_X = WIDTH_HALF + 2,
            CELL_8_TOP_Y = HEIGHT_HALF - 155,
            CELL_8_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_9_LEFT_X = WIDTH_HALF - 241,
            CELL_9_RIGHT_X = WIDTH_HALF - 121,
            CELL_9_TOP_Y = HEIGHT_HALF - 155,
            CELL_9_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_10_LEFT_X = WIDTH_HALF - 362,
            CELL_10_RIGHT_X = WIDTH_HALF - 243,
            CELL_10_TOP_Y = HEIGHT_HALF - 155,
            CELL_10_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_11_LEFT_X = WIDTH_HALF - 362,
            CELL_11_RIGHT_X = WIDTH_HALF - 243,
            CELL_11_TOP_Y = HEIGHT_HALF - 340,
            CELL_11_BOTTOM_Y = HEIGHT_HALF - 157,

            CELL_12_LEFT_X = WIDTH_HALF - 483,
            CELL_12_RIGHT_X = WIDTH_HALF - 364,
            CELL_12_TOP_Y = HEIGHT_HALF - 155,
            CELL_12_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_13_LEFT_X = WIDTH_HALF - 483,
            CELL_13_RIGHT_X = WIDTH_HALF - 364,
            CELL_13_TOP_Y = HEIGHT_HALF - 340,
            CELL_13_BOTTOM_Y = HEIGHT_HALF - 157,

            CELL_14_LEFT_X = WIDTH_HALF - 605,
            CELL_14_RIGHT_X = WIDTH_HALF - 485,
            CELL_14_TOP_Y = HEIGHT_HALF - 92,
            CELL_14_BOTTOM_Y = HEIGHT_HALF + 92,

            CELL_15_LEFT_X = WIDTH_HALF - 605,
            CELL_15_RIGHT_X = WIDTH_HALF - 485,
            CELL_15_TOP_Y = HEIGHT_HALF - 155,
            CELL_15_BOTTOM_Y = HEIGHT_HALF - 94,

            CELL_16_LEFT_X = WIDTH_HALF - 605,
            CELL_16_RIGHT_X = WIDTH_HALF - 485,
            CELL_16_TOP_Y = HEIGHT_HALF - 340,
            CELL_16_BOTTOM_Y = HEIGHT_HALF - 157;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_localization);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.strideField = (EditText) findViewById(R.id.strideLengthField);
        this.cellTextView = (TextView) findViewById(R.id.cellTextView);
        this.trueAngleTextView = (TextView) findViewById(R.id.trueAngle);
        this.calibratedAngleTextView = (TextView) findViewById(R.id.calibratedAngle);
        setScreenSize();
        drawMap();
        addLogicalWalls();
        initParticles();
        locating = false;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        accelerometerData = new float[3];
        magneticFieldData = new float[3];
        magnitudeData = new ArrayList<>();

        calibration = 0;
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Set width and height of the screen.
     */
    private void setScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    /**
     * Draw the drawable walls on the map.
     */
    private void drawMap() {
        this.drawableWalls = new ArrayList<>();

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

        // Walls surrounding the floor map
        drawWall(-WIDTH_HALF, -HEIGHT_HALF, WIDTH_HALF, -340, Color.WHITE); // TOP
        drawWall(-WIDTH_HALF, -HEIGHT_HALF, -605, HEIGHT_HALF, Color.WHITE); // LEFT
        drawWall(615, -HEIGHT_HALF, WIDTH_HALF, HEIGHT_HALF, Color.WHITE); // RIGHT
        drawWall(-WIDTH_HALF, 92, WIDTH_HALF, HEIGHT_HALF, Color.WHITE); // BOTTOM

        ImageView floorPlanView = (ImageView) findViewById(R.id.floorPlan);
        Bitmap blankBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        this.floorPlan = new Canvas(blankBitmap);
        floorPlanView.setImageBitmap(blankBitmap);
        for(ShapeDrawable drawableWall: drawableWalls) drawableWall.draw(this.floorPlan);
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

        this.drawableWalls.add(drawableWall);
    }

    /**
     * Add logical walls (V = vertical, H = horizontal).
     */
    private void addLogicalWalls() {
        this.walls = new ArrayList<>();
        int w = this.width / 2,
            h = this.height / 2;

        // Outer shape
        this.walls.add(new Wall(w - 604, h - 340, w - 604, h + 92)); // V LEFT
        this.walls.add(new Wall(w + 614, h - 340, w + 614, h + 92)); // V RIGHT
        this.walls.add(new Wall(w - 605, h + 91, w + 615,  h + 91)); // H BOTTOM
        this.walls.add(new Wall(w - 605, h - 339, w + 615, h - 339)); // H TOP

        // Corridor TOP
        this.walls.add(new Wall(w - 605, h - 156, w - 575, h - 156)); // H TOP 1
        this.walls.add(new Wall(w - 514, h - 156, w - 456, h - 156)); // H TOP 2
        this.walls.add(new Wall(w - 393, h - 156, w - 335, h - 156)); // H TOP 3
        this.walls.add(new Wall(w - 268, h - 156, w + 526, h - 156)); // H TOP 4
        this.walls.add(new Wall(w + 583, h - 156, w + 615, h - 156)); // H TOP 5

        // Corridor BOTTOM
        this.walls.add(new Wall(w - 605, h - 93, w - 550, h - 93)); // H BOTTOM 1
        this.walls.add(new Wall(w - 485, h - 93, w + 526, h - 93)); // H BOTTOM 2
        this.walls.add(new Wall(w + 583, h - 93, w + 615, h - 93)); // H BOTTOM 3

        // Cell 16 SEP and Cell 13 SEP
        this.walls.add(new Wall(w - 484, h - 340, w - 484, h - 157)); // V
        this.walls.add(new Wall(w - 363, h - 340, w - 363, h - 157)); // V
    }

    /**
     * Draw 5000 particles randomly across the map. If they are drawn on top of a wall,
     * generate again.
     */
    private void initParticles() {
        this.drawableParticles = new ArrayList<>();
        this.particles = new ArrayList<>();
        ShapeDrawable drawableParticle;
        int x, y;

        for (int i = 0; i < NUMBER_PARTICLES; i++) {
            drawableParticle = new ShapeDrawable(new OvalShape());
            drawableParticle.getPaint().setColor(Color.rgb(107,77,87));
            x = new Random().nextInt(613 + 1 + 603) - 603;
            y = new Random().nextInt(90 + 1 + 338) - 338;
            drawableParticle.setBounds(this.width / 2 + x, this.height / 2 + y,
                    this.width / 2 + x + 4, this.height / 2 + y + 4);

            while (isCollision(drawableParticle)) {
                x = new Random().nextInt(613 + 1 + 603) - 603;
                y = new Random().nextInt(90 + 1 + 338) - 338;
                drawableParticle.setBounds(this.width / 2 + x, this.height / 2 + y,
                        this.width / 2 + x + 4, this.height / 2 + y + 4);
            }
            this.drawableParticles.add(drawableParticle);
            this.particles.add(new Particle(this.width / 2 + x + 2, this.height / 2 + y + 2, 0));
            drawableParticle.draw(this.floorPlan);
        }
    }

    /**
     * Determines if the drawable particle has a direct collision with any of the drawable walls.
     * @param drawableParticle Particle to check if there was a collision.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision(ShapeDrawable drawableParticle) {
        Rect particleRect = drawableParticle.getBounds(),
                wallRect;
        for(ShapeDrawable drawableWall: drawableWalls) {
            wallRect = drawableWall.getBounds();
            if (particleRect.intersect(wallRect)) return true;
        }
        return false;
    }

    /**
     * Check if a particle went across a wall to the other side by checking the orientation
     * of the vectors (wall, and vector created from particle moving).
     * @return True if particle moved across the wall to the other side, False otherwise.
     */
    private boolean isAcrossWall(Particle particle) {
        Location particleCurrent = particle.getCurrentLocation(),
                particlePrevious = particle.getPreviousLocation(),
                wallStart, wallEnd;
        int o1, o2, o3, o4;

        for(Wall wall: walls) {
            wallStart = wall.getStartLocation();
            wallEnd = wall.getEndLocation();
            o1 = orientation(particlePrevious, particleCurrent, wallStart);
            o2 = orientation(particlePrevious, particleCurrent, wallEnd);
            o3 = orientation(wallStart, wallEnd, particlePrevious);
            o4 = orientation(wallStart, wallEnd, particleCurrent);

            if (o1 != o2 && o3 != o4){
                return true;
            }
        }
        return false;
    }

    /**
     * Measure the orientation between points on vector PQ and third point R on a second vector.
     * @param p First point on vector to measure.
     * @param q Second point on vector to measure.
     * @param r Third point on another vector to compare first vector to.
     * @return 0 if points were collinear, 1 if clockwise or 2 if counter-clockwise.
     */
    private int orientation(Location p, Location q, Location r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX())
                - (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (val == 0.0) return 0; // collinear
        return (val > 0) ? 1 : 2; // clock- or counterclockwise
    }

    /**

     * If all particles died, resurrect them. Resample them on their previous
     * location but add some noise as to move them away from the place they died.
     * @param deadDrawables drawable list of particles.
     * @param deadParticles logical list of particles.
     */
    private void resurrectParticles(List<ShapeDrawable> deadDrawables, List<Particle> deadParticles) {
        int oldX, oldY, newX, newY;
        ShapeDrawable dp;
        drawMap();
        for(Particle p: deadParticles) {
            p.setCurrentLocation(p.getPreviousLocation());
            dp = deadDrawables.get(deadParticles.indexOf(p));
            oldX = p.getPreviousLocation().getX();
            oldY = p.getPreviousLocation().getY();

            do {
                newX = new Random().nextInt((oldX + 6) + 1 - (oldX - 6)) + (oldX - 6);
                newY = new Random().nextInt((oldY + 6) + 1 - (oldY - 6)) + (oldY - 6);
                dp.setBounds(newX - 2, newY - 2,
                        newX + 2, newY + 2);
                p.setCurrentLocation(new Location(newX, newY));
            } while(isCollision(dp) || isAcrossWall(p));

            this.particles.add(p);
            this.drawableParticles.add(dp);
            dp.draw(this.floorPlan);
        }
    }

    /**
     * Transform meters to pixels.
     * @param meters Value to transform to pixels.
     * @return Value in pixels.
     */
    private double metersToPixels(double meters) {
        return meters * METER_IN_PIXELS;
    }

    /**
     * Transform pixels to meters.
     * @param pixels value to transform to meters.
     * @return Value in meters.
     */
    private double pixelsToMeters(double pixels) {
        return pixels / METER_IN_PIXELS;
    }

    /**
     * Whenever Initial Belief button is clicked, generate new particles and re-draw the map.
     * @param v The view.
     */
    public void initialBelief(View v) {
        locating = false;
        drawMap();
        initParticles();
        accelerometerData = new float[3];
        magneticFieldData = new float[3];
        magnitudeData = new ArrayList<>();
    }

    /**
     * Pressing Locate Me button will start the scan and make particles move.
     * @param v The view.
     */
    public void locateMe(View v) {
        //TODO: Call logic functions
        stride = Double.valueOf(strideField.getText().toString());
        this.strideField.setFocusable(false);
        locating = true;
    }

    /**
     * Guess which cell we are in depending on coordinates (x,y).
     * @return Between 1-16 if particle is in either of these, 0 otherwise.
     */
    private int guessCell() {
        Map<Integer, Integer> cellCount = new HashMap<>();
        int cell = 0;
        double x, y;

        for(Particle p: this.particles) {
            x = p.getCurrentLocation().getX();
            y = p.getCurrentLocation().getY();
            if (x >= CELL_1_LEFT_X && x <= CELL_1_RIGHT_X
                    && y >= CELL_1_TOP_Y && y <= CELL_1_BOTTOM_Y) cell = 1;
            if (x >= CELL_2_LEFT_X && x <= CELL_2_RIGHT_X
                    && y >= CELL_2_TOP_Y && y <= CELL_2_BOTTOM_Y) cell = 2;
            if (x >= CELL_3_LEFT_X && x <= CELL_3_RIGHT_X
                    && y >= CELL_3_TOP_Y && y <= CELL_3_BOTTOM_Y) cell = 3;
            if (x >= CELL_4_LEFT_X  && x <= CELL_4_RIGHT_X
                    && y >= CELL_4_TOP_Y && y <= CELL_4_BOTTOM_Y) cell =  4;
            if (x >= CELL_5_LEFT_X  && x <= CELL_5_RIGHT_X
                    && y >= CELL_5_TOP_Y && y <= CELL_5_BOTTOM_Y) cell =  5;
            if (x >= CELL_6_LEFT_X  && x <= CELL_6_RIGHT_X
                    && y >= CELL_6_TOP_Y && y <= CELL_6_BOTTOM_Y) cell = 6;
            if (x >= CELL_7_LEFT_X  && x <= CELL_7_RIGHT_X
                    && y >= CELL_7_TOP_Y && y <= CELL_7_BOTTOM_Y) cell =  7;
            if (x >= CELL_8_LEFT_X && x <= CELL_8_RIGHT_X
                    && y >= CELL_8_TOP_Y && y <= CELL_8_BOTTOM_Y) cell = 8;
            if (x >= CELL_9_LEFT_X && x <= CELL_9_RIGHT_X
                    && y >= CELL_9_TOP_Y && y <= CELL_9_BOTTOM_Y) cell = 9;
            if (x >= CELL_10_LEFT_X && x <= CELL_10_RIGHT_X
                    && y >= CELL_10_TOP_Y && y <= CELL_10_BOTTOM_Y) cell =  10;
            if (x >= CELL_11_LEFT_X && x <= CELL_11_RIGHT_X
                    && y >= CELL_11_TOP_Y && y <= CELL_11_BOTTOM_Y) cell = 11;
            if (x >= CELL_12_LEFT_X && x <= CELL_12_RIGHT_X
                    && y >= CELL_12_TOP_Y && y <= CELL_12_BOTTOM_Y) cell = 12;
            if (x >= CELL_13_LEFT_X && x <= CELL_13_RIGHT_X
                    && y >= CELL_13_TOP_Y && y <= CELL_13_BOTTOM_Y) cell = 13;
            if (x >= CELL_14_LEFT_X && x <= CELL_14_RIGHT_X
                    && y >= CELL_14_TOP_Y && y <= CELL_14_BOTTOM_Y) cell = 14;
            if (x >= CELL_15_LEFT_X && x <= CELL_15_RIGHT_X
                    && y >= CELL_15_TOP_Y && y <= CELL_15_BOTTOM_Y) cell = 15;
            if (x >= CELL_16_LEFT_X && x <= CELL_16_RIGHT_X
                    && y >= CELL_16_TOP_Y && y <= CELL_16_BOTTOM_Y) cell = 16;

            if (cellCount.containsKey(cell)) {
                cellCount.put(cell, cellCount.get(cell) + 1);
            } else {
                cellCount.put(cell, 1);
            }
        }

        cellCount = sortMapByValue(cellCount);
        if (cellCount.entrySet().iterator().next().getValue() > (int) (NUMBER_PARTICLES * 0.4)) {
            return cellCount.entrySet().iterator().next().getKey();
        } else {
            return 0;
        }

    }

    private Map<Integer, Integer> sortMapByValue(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer> > list = new LinkedList<>(map.entrySet()); //create list from map
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer> >() { //sort list by value
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }});

        map = new LinkedHashMap<>();

        for (Map.Entry<Integer, Integer> s : list) {
            map.put(s.getKey(), s.getValue());
        }

        return map;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            updateDirection(sensorType, event.values[0], event.values[1], event.values[2]);
        }
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if(locating){
                updateStepCounter(event.values[0], event.values[1], event.values[2]);
            }
            updateDirection(sensorType, event.values[0], event.values[1], event.values[2]);
        }
    }

    public void updateDirection (int sensorType, float value0, float value1 , float value2) {
        float realAngle, calibratedAngle;
        String realDirection;

        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldData[0] = (ALPHA * magneticFieldData[0]) + ((1 - ALPHA) * value0);
            magneticFieldData[1] = (ALPHA * magneticFieldData[1]) + ((1 - ALPHA) * value1);
            magneticFieldData[2] = (ALPHA * magneticFieldData[2]) + ((1 - ALPHA) * value2);
        } else {
            accelerometerData[0] = (ALPHA * accelerometerData[0]) + ((1 - ALPHA) * value0);
            accelerometerData[1] = (ALPHA * accelerometerData[1]) + ((1 - ALPHA) * value1);
            accelerometerData[2] = (ALPHA * accelerometerData[2]) + ((1 - ALPHA) * value2);
        }

        float R[] = new float[9];
        float I[] = new float[9];

        if (SensorManager.getRotationMatrix(R, I, accelerometerData, magneticFieldData)) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);

            realAngle = (((float)Math.toDegrees(orientation[0])) + 360) % 360;
            calibratedAngle = (((float)Math.toDegrees(orientation[0])) + calibration + 360) % 360;
            realDirection = setDirection(realAngle);
            direction = setDirection(calibratedAngle);

            trueAngleTextView.setText("Real angle: " + realDirection + " " + (int)realAngle);
            calibratedAngleTextView.setText("Calibrated angle: " + direction + " " + (int)calibratedAngle);
        }
    }

    private String setDirection(float angle) {
        String dir;
        if (angle < 45 || angle >= 315) {
            dir = "N";
        } else if (angle < 135) {
            dir = "E";
        } else if (angle < 225) {
            dir = "S";
        } else {
            dir = "W";
        }
        return dir;
    }

    public void updateStepCounter (float xData, float yData, float zData) {
        magnitudeData.add(Math.sqrt(Math.pow(xData, 2) + Math.pow(yData, 2) + Math.pow(zData, 2)));
        if (magnitudeData.size() == tMax) {
            List<Double> tmp = new ArrayList<>();
            tmp.addAll(magnitudeData);
            magnitudeData = new ArrayList<>();
            tmp = smoothMagnitude(tmp);
            newMethod(tmp);
        }
    }

    private List<Double> smoothMagnitude(List<Double> tmp) {
        List<Double> smoothTmp = new ArrayList<>();
        for (int i = 1 ; i < tmp.size() - 1; i++) {
            smoothTmp.add((tmp.get(i - 1) + tmp.get(i) + tmp.get(i + 1)) / 3.0);
        }
        return smoothTmp;
    }

    private void newMethod (List<Double> magnitudeSubList) {
        int peakCount = 0, peakAccumulate = 0, stepCount = 0;
        double peakMean, forwardSlope, backwardSlope, mag;
        for (int i = 1; i < magnitudeSubList.size() - 1; i++) {
            mag = magnitudeSubList.get(i);
            forwardSlope = magnitudeSubList.get(i + 1) - mag;
            backwardSlope = mag - magnitudeSubList.get(i - 1);
            if (forwardSlope < 0 && backwardSlope > 0) {
                peakCount++;
                peakAccumulate += mag;
            }
        }
        peakMean = (double) peakAccumulate / (double) peakCount;

        for (int i = 1; i < magnitudeSubList.size() - 1; i++) {
            mag = magnitudeSubList.get(i);
            forwardSlope = magnitudeSubList.get(i + 1) - mag;
            backwardSlope = mag - magnitudeSubList.get(i - 1);
            if (forwardSlope < 0 && backwardSlope > 0 && mag > (0.9 * peakMean) && mag > 11.2) {
                stepCount=1;
            }
        }
        if (stepCount>0){
            moveParticles (direction);
        }
    }

    private void moveParticles(String currDirection) {
        drawMap();
        List<ShapeDrawable> deadDrawables = new ArrayList<>();
        List<Particle> deadParticles = new ArrayList<>();
        int xTemp, yTemp, x, y, rand;
        int tmpStep = (int) metersToPixels(stride);
        int tmpAngle;

        Rect rect;
        Particle p;

        for (ShapeDrawable dp : this.drawableParticles) {
            if (currDirection.equals("N")) {
                tmpAngle = (((int) (0 + (Math.random() * 10 - 5))) + 360) % 360;
            } else if (currDirection.equals("E")) {
                tmpAngle = (int) (90 + (Math.random() * 10 - 5));
            } else if (currDirection.equals("S")) {
                tmpAngle = (int) (180 + (Math.random() * 10 - 5));
            } else {
                tmpAngle = (int) (270 + (Math.random() * 10 - 5));
            }
            xTemp = Math.abs((int) ((tmpStep + (tmpStep * Math.random() * (0.5) - 0.25)) * Math.sin(tmpAngle)));
            yTemp = Math.abs((int) ((tmpStep + (tmpStep * Math.random() * (0.5) - 0.25)) * Math.cos(tmpAngle)));

            if (tmpAngle > 270 || tmpAngle < 90) {
                yTemp = -yTemp;
            }
            if (tmpAngle > 180) {
                xTemp = -xTemp;
            }

            rect = dp.getBounds();
            p = this.particles.get(this.drawableParticles.indexOf(dp));
            x = p.getCurrentLocation().getX();
            y = p.getCurrentLocation().getY();

            dp.setBounds(rect.left + xTemp, rect.top + yTemp,
                    rect.right + xTemp, rect.bottom + yTemp);
            p.updateLocation(x + xTemp, y + yTemp);

            if (isCollision(dp) || isAcrossWall(p)) {
                deadDrawables.add(dp);
                deadParticles.add(p);
                dp.setBounds(0, 0, 0, 0);
            }
            dp.draw(this.floorPlan);
        }
        this.drawableParticles.removeAll(deadDrawables);
        this.particles.removeAll(deadParticles);

        if (this.drawableParticles.size() != 0) {
            for (ShapeDrawable dp : deadDrawables) {
                p = deadParticles.get(deadDrawables.indexOf(dp));
                rand = new Random().nextInt(this.particles.size());
                rect = this.drawableParticles.get(rand).getBounds();
                x = this.particles.get(rand).getCurrentLocation().getX();
                y = this.particles.get(rand).getCurrentLocation().getY();
                dp.setBounds(rect.left, rect.top, rect.right, rect.bottom);
                p.updateLocation(x, y);
                this.drawableParticles.add(dp);
                this.particles.add(p);
                dp.draw(this.floorPlan);
            }

            int cellNumber = guessCell();
            if (cellNumber != 0) {
                cellTextView.setText("You are in cell " + cellNumber + "!");
            } else {
                cellTextView.setText("Locating...");
            }
        } else {
            resurrectParticles(deadDrawables, deadParticles);
        }
    }

    public void increaseCalibration(View view) {
        calibration++;
        if (calibration>360){
            calibration=1;
        }
    }

    public void decreaseCalibration(View view) {
        calibration--;
        if (calibration<0){
            calibration=359;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
