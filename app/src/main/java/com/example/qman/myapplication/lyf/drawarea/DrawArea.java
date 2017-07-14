package com.example.qman.myapplication.lyf.drawarea;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureEditResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.MarkerSymbol;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.areatab.AreaItemFragment;
import com.example.qman.myapplication.loginregister.MainActivity;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.GPSTracker;
import com.example.qman.myapplication.utils.Util;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.qman.myapplication.lyf.drawarea.generatePicFromMapviewAndUpLoadInfo.codeIdTemp;
import static com.example.qman.myapplication.lyf.drawarea.generatePicFromMapviewAndUpLoadInfo.runnable;

import android.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by lyf on 01/03/2017.
 */

public class DrawArea extends Fragment  {
    private MapView mMapView;
    private Button mExit;
    private SearchView mSearchView;
    private Button mPosition;
    private Button mSave;
    private Button mUndo;



    private ImageView bt_back;
    private enum EditMode {
        NONE, POINT, POLYLINE, POLYGON, SAVING
    }

    GraphicsLayer mGraphicsLayerEditing;

    EditMode mEditMode;
    ArrayList<Point> mPoints = new ArrayList<Point>();
    ArrayList<Point> mMidPoints = new ArrayList<Point>();

    boolean mMidPointSelected = false;

    boolean mVertexSelected = false;

    int mInsertingIndex;
    boolean mClosingTheApp = false;
    ArrayList<EditingStates> mEditingStates = new ArrayList<EditingStates>();
    String mMapState;

    SimpleMarkerSymbol mRedMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);

    SimpleMarkerSymbol mBlackMarkerSymbol = new SimpleMarkerSymbol(Color.BLACK, 20, SimpleMarkerSymbol.STYLE.CIRCLE);

    SimpleMarkerSymbol mGreenMarkerSymbol = new SimpleMarkerSymbol(Color.GREEN, 15, SimpleMarkerSymbol.STYLE.CIRCLE);

    Graphic mPolygLineOrPolygonGraphic;

    private GraphicsLayer graphicsLayerPosition;

    SaveArea SaveAreaDialog;
    String m_CropkindsStr="";//作物种类
    String m_fieldName="";//地块名称
    String mCodeIdOfArea;//地块代号
    File upfile;
    static String m_geometryStr;//地块几何形状字符串
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aera_item_selectordraw, container, false);

        final MarkerSymbol positionSymbol = new PictureMarkerSymbol(ContextCompat.getDrawable(getActivity(),R.drawable.positionsymbol));


        SaveAreaDialog=new SaveArea();//初始化保存弹出窗口

        mExit = (Button)view.findViewById(R.id.exitofareaSelectorDraw);
        mExit.setVisibility(View.INVISIBLE);

        mSearchView = (SearchView)view.findViewById(R.id.searchView);
        mPosition = (Button)view.findViewById(R.id.positionofareaSelectorDraw);

        mSave = (Button)view.findViewById(R.id.saveofareaSelectorDraw);
        mSave.setVisibility(View.INVISIBLE);

        mMapView = (MapView) view.findViewById(R.id.mapofselectordraw);

        mUndo = (Button)view.findViewById(R.id.undoofareaSelectorDraw);
        mUndo.setVisibility(View.INVISIBLE);

        bt_back = (ImageView) view.findViewById(R.id.bt_back);
        /*后退按钮*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityUtil.switchToFragment(getActivity(),new AreaFragment(),R.id.id_content);
                actionExit();
            }
        });
        //ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"勾画区域");
        //ActivityUtil.setOnlyVisibilitys(getActivity(),R.id.toolbar_title, R.id.toolbar_search, R.id.toolbar_add,R.id.toolbar_draw);
        // Set listeners on MapView
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(final Object source, final STATUS status) {
                if (STATUS.INITIALIZED == status) {
                    if (source instanceof MapView) {
                        mGraphicsLayerEditing = new GraphicsLayer();
                        mMapView.addLayer(mGraphicsLayerEditing);

                        graphicsLayerPosition = new GraphicsLayer();
                        mMapView.addLayer(graphicsLayerPosition);
                    }
                }
            }
        });
        mMapView.setOnTouchListener(new MyTouchListener(getActivity().getApplicationContext(), mMapView));

        // If map state (center and resolution) has been stored, update the MapView with this state
        if (!TextUtils.isEmpty(mMapState)) {
            mMapView.restoreState(mMapState);
        }

        mPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPosition.setBackgroundResource(R.drawable.position_draw_click);

                GPSTracker gps = new GPSTracker(getActivity().getApplicationContext());
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                Log.d("latitude+longitude",String.valueOf(latitude)+","+String.valueOf(longitude));

                Point position = new Point(longitude,latitude);
                Point positionProj = (Point)GeometryEngine.project(position, SpatialReference.create(4326),mMapView.getSpatialReference());
                //图层的创建
                Graphic graphicPoint = new Graphic(positionProj,positionSymbol);
                graphicsLayerPosition.removeAll();
                graphicsLayerPosition.addGraphic(graphicPoint);

                mMapView.centerAndZoom(latitude,longitude,16);

                //GPSTracker.getPositionNamebyLatLon(position,getActivity(),graphicsLayerPosition,mMapView);
            }
        });

        SaveAreaDialog.setOnButtonClickListener(new SaveArea.savearea_Listener(){
            @Override
            //点击 保存按钮
            public void savearea_sureClick(String fieldname,ArrayList<String> Cropkinds)
            {

                for(int i = 0;i < Cropkinds.size(); i ++){
                    m_CropkindsStr+=(Cropkinds.get(i))+"/";
                }
                m_fieldName=fieldname;
                mCodeIdOfArea = "10"+ (int)((Math.random()*9+1)*1000);
                Bitmap bitmap= Util.getViewBitmap(mMapView);

                String FileDirectory = Util.saveMyBitmap(mCodeIdOfArea,bitmap);//保存缩略图，并返回文件路径

                Log.d(TAG,FileDirectory);

                bitmap.recycle();
                // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                upfile = new File(FileDirectory);


                generatePicFromMapviewAndUpLoadInfo m_generatePic=new generatePicFromMapviewAndUpLoadInfo(
                        getActivity(),
                        upfile,
                        mCodeIdOfArea,
                        m_geometryStr,
                        m_fieldName,
                        m_CropkindsStr
                );

                new Thread(m_generatePic.runnable).start();

                SaveAreaDialog.dismiss();
            }
            @Override
            //点击取消按钮
            public void savearea_cancleClick()
            {
                SaveAreaDialog.dismiss();
            }

        });

        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEditMode = EditMode.POLYGON;
        clear();
        // Set up use of magnifier on a long press on the map
        mMapView.setShowMagnifierOnLongPress(true);

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                actionExit();

                //getFragmentManager().popBackStack();

            }
        });
        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUndo.setBackgroundResource(R.drawable.undo_click);
                actionUndo();
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSave.setBackgroundResource(R.drawable.save_draw_click);

                SaveAreaDialog.show(getFragmentManager(), "SaveAreaDialog");

                actionSave();
            }
        });


    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (mEditMode != EditMode.NONE && mEditMode != EditMode.SAVING && mEditingStates.size() > 0) {
                        // There's an edit in progress, so ask for confirmation
                        //mClosingTheApp = true;
                        showConfirmExitDialogFragment();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Clears feature editing data and updates action bar.
     */
    void clear() {
        // Clear feature editing data
        mPoints.clear();
        mMidPoints.clear();
        mEditingStates.clear();

        mMidPointSelected = false;
        mVertexSelected = false;
        mInsertingIndex = 0;

        if (mGraphicsLayerEditing != null) {
            mGraphicsLayerEditing.removeAll();
        }

        // Update action bar to reflect the new state
        /*
        updateActionBar();
        int resId;
        switch (mEditMode) {
            case POINT:
                resId = R.string.title_add_point;
                break;
            case POLYGON:
                resId = R.string.title_add_polygon;
                break;
            case POLYLINE:
                resId = R.string.title_add_polyline;
                break;
            case NONE:
            default:
                resId = R.string.app_name;
                break;
        }
        getActionBar().setTitle(resId);
        */
    }
    /**
     * Handles the 'Discard' action.
     */
    private void actionExit() {
        if (mEditingStates.size() > 0) {
            // There's an edit in progress, so ask for confirmation
            mClosingTheApp = false;
            showConfirmExitDialogFragment();
        } else {
            // No edit in progress, so just exit edit mode
            exitEditMode();
        }
    }
    /**
     * Shows dialog asking user to confirm discarding the feature being added.
     */
    private void showConfirmExitDialogFragment() {

        new AlertDialog.Builder(getActivity()).setTitle("系统提示")//设置对话框标题

                .setMessage("请确认所有数据都保存后再退出当前界面！")//设置显示的内容

                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件


                        dialog.dismiss();
                        if (mClosingTheApp) {
                            getActivity().finish();

                        } else {
                            exitEditMode();
                        }
                        getFragmentManager().popBackStack();
                    }

                }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮
                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件

                        dialog.dismiss();

                    }

                }).show();//在按键响应事件中显示此对话框

    }

    /**
     * Exits the edit mode state.
     */
    void exitEditMode() {
        mEditMode = EditMode.NONE;
        clear();
        mMapView.setShowMagnifierOnLongPress(false);
        getFragmentManager().popBackStack();
    }

    /**
     * Handles the 'Undo' action.
     */
    private void actionUndo() {

        showUndoandHideSearchView();
        isSaveValidandShowSavebar();


        mEditingStates.remove(mEditingStates.size() - 1);
        mPoints.clear();
        if (mEditingStates.size() == 0) {

            mUndo.setVisibility(View.GONE);
            mSearchView.setVisibility(View.VISIBLE);

            mMidPointSelected = false;
            mVertexSelected = false;
            mInsertingIndex = 0;
        } else {
            EditingStates state = mEditingStates.get(mEditingStates.size() - 1);
            mPoints.addAll(state.points);
            Log.d(TAG, "# of points = " + mPoints.size());
            mMidPointSelected = state.midPointSelected;
            mVertexSelected = state.vertexSelected;
            mInsertingIndex = state.insertingIndex;
        }
        refresh();
    }

    /**
     * Handles the 'Save' action. The edits made are applied and hence saved on the server.
     */
    private void actionSave() {
                try
                {

                    String geometrystr = GeometryEngine.geometryToJson(mMapView.getSpatialReference(),mPolygLineOrPolygonGraphic.getGeometry());
                    //String symbolstr=mPolygLineOrPolygonGraphic.getSymbol().toJson();
                    //SaveDrawArea mSaveDrawArea = new SaveDrawArea();

                    //ActivityUtil.putParam(getActivity(),"DrawAreaString",str);
                    //ActivityUtil.switchToFragment(getActivity(),mSaveDrawArea,R.id.id_content);
                    //m_geometryStr=Graphic.toJson(mPolygLineOrPolygonGraphic).toString();
//                    JSONObject ajsonObject = new JSONObject();
//                    ajsonObject.put("geometry",geometrystr);
//                    ajsonObject.put("symbol",symbolstr);
                    m_geometryStr=geometrystr;

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


    }
    /**
     * Redraws everything on the mGraphicsLayerEditing layer following an edit and updates the items shown on the action
     * bar.
     */
    void refresh() {
        if (mGraphicsLayerEditing != null) {
            mGraphicsLayerEditing.removeAll();
        }
        drawPolylineOrPolygon();
        drawMidPoints();
        drawVertices();

        //updateActionBar();
    }
    /**
     * Checks if it's valid to save the feature currently being created.
     *
     * @return true if valid.
     */
    private boolean showUndoandHideSearchView() {
        //int minPoints = 1;

        if(mEditingStates.size() == 0)
        {
            mUndo.setVisibility(View.GONE);
            //mSearchView.setVisibility(View.VISIBLE);


            return true;
        }
        else{

            mUndo.setVisibility(View.VISIBLE);
            //mSearchView.setVisibility(View.GONE);

            return false;
        }

    }
    /**
     * Checks if it's valid to save the feature currently being created.
     *
     * @return true if valid.
     */
    private boolean isSaveValidandShowSavebar() {
        int minPoints;
        switch (mEditMode) {
            case POINT:
                minPoints = 1;
                break;
            case POLYGON:
                minPoints = 3;
                break;
            case POLYLINE:
                minPoints = 2;
                break;
            default:
                return false;
        }
//        return mPoints.size() >= minPoints;
        if(mPoints.size() >= minPoints)
        {
            mSave.setVisibility(View.VISIBLE);
            return true;
        }
        else{
            mSave.setVisibility(View.GONE);
            return false;
        }

    }
    /**
     * Draws polyline or polygon (dependent on current mEditMode) between the vertices in mPoints.
     */
    private void drawPolylineOrPolygon() {
        //Graphic graphic;
        MultiPath multipath;

        // Create and add graphics layer if it doesn't already exist
        if (mGraphicsLayerEditing == null) {
            mGraphicsLayerEditing = new GraphicsLayer();
            mMapView.addLayer(mGraphicsLayerEditing);
        }

        if (mPoints.size() > 1) {

            // Build a MultiPath containing the vertices
            if (mEditMode == EditMode.POLYLINE) {
                multipath = new Polyline();
            } else {
                multipath = new Polygon();
            }
            multipath.startPath(mPoints.get(0));
            for (int i = 1; i < mPoints.size(); i++) {
                multipath.lineTo(mPoints.get(i));
            }

            // Draw it using a line or fill symbol
            if (mEditMode == EditMode.POLYLINE) {
                mPolygLineOrPolygonGraphic = new Graphic(multipath, new SimpleLineSymbol(Color.BLACK, 4));
            } else {
                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
                simpleFillSymbol.setAlpha(100);
                simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 4));
                mPolygLineOrPolygonGraphic = new Graphic(multipath, (simpleFillSymbol));
//
            }
            mGraphicsLayerEditing.addGraphic(mPolygLineOrPolygonGraphic);
        }
    }

    /**
     * Draws mid-point half way between each pair of vertices in mPoints.
     */
    private void drawMidPoints() {
        int index;
        Graphic graphic;

        mMidPoints.clear();
        if (mPoints.size() > 1) {

            // Build new list of mid-points
            for (int i = 1; i < mPoints.size(); i++) {
                Point p1 = mPoints.get(i - 1);
                Point p2 = mPoints.get(i);
                mMidPoints.add(new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2));
            }
            if (mEditMode == EditMode.POLYGON && mPoints.size() > 2) {
                // Complete the circle
                Point p1 = mPoints.get(0);
                Point p2 = mPoints.get(mPoints.size() - 1);
                mMidPoints.add(new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2));
            }

            // Draw the mid-points
            index = 0;
            for (Point pt : mMidPoints) {
                if (mMidPointSelected && mInsertingIndex == index) {
                    graphic = new Graphic(pt, mRedMarkerSymbol);
                } else {
                    graphic = new Graphic(pt, mGreenMarkerSymbol);
                }
                mGraphicsLayerEditing.addGraphic(graphic);
                index++;
            }
        }
    }

    /**
     * Draws point for each vertex in mPoints.
     */
    private void drawVertices() {
        int index = 0;
        SimpleMarkerSymbol symbol;

        for (Point pt : mPoints) {
            if (mVertexSelected && index == mInsertingIndex) {
                // This vertex is currently selected so make it red
                symbol = mRedMarkerSymbol;
            } else if (index == mPoints.size() - 1 && !mMidPointSelected && !mVertexSelected) {
                // Last vertex and none currently selected so make it red
                symbol = mRedMarkerSymbol;
            } else {
                // Otherwise make it black
                symbol = mBlackMarkerSymbol;
            }
            Graphic graphic = new Graphic(pt, symbol);
            mGraphicsLayerEditing.addGraphic(graphic);
            index++;
        }
    }

    /**
     * An instance of this class is created when a new point is added/moved/deleted. It records the state of editing at
     * that time and allows edit operations to be undone.
     */
    private class EditingStates {
        ArrayList<Point> points = new ArrayList<Point>();

        boolean midPointSelected = false;

        boolean vertexSelected = false;

        int insertingIndex;

        public EditingStates(ArrayList<Point> points, boolean midpointselected, boolean vertexselected, int insertingindex) {
            this.points.addAll(points);
            this.midPointSelected = midpointselected;
            this.vertexSelected = vertexselected;
            this.insertingIndex = insertingindex;
        }
    }

    /**
     * The MapView's touch listener.
     */
    private class MyTouchListener extends MapOnTouchListener {
        MapView mapView;

        public MyTouchListener(Context context, MapView view) {
            super(context, view);
            mapView = view;
        }

        @Override
        public boolean onLongPressUp(MotionEvent point) {
            handleTap(point);
            super.onLongPressUp(point);
            return true;
        }

        @Override
        public boolean onSingleTap(final MotionEvent e) {
            handleTap(e);
            return true;
        }

        /***
         * Handle a tap on the map (or the end of a magnifier long-press event).
         *
         * @param e The point that was tapped.
         */
        private void handleTap(final MotionEvent e) {

            // Ignore the tap if we're not creating a feature just now
            if (mEditMode == EditMode.NONE || mEditMode == EditMode.SAVING) {
                return;
            }

            Point point = mapView.toMapPoint(new Point(e.getX(), e.getY()));

            // If we're creating a point, clear any existing point
            if (mEditMode == EditMode.POINT) {
                mPoints.clear();
            }

            // If a point is currently selected, move that point to tap point
            if (mMidPointSelected || mVertexSelected) {
                movePoint(point);
            } else {
                // If tap coincides with a mid-point, select that mid-point
                int idx1 = getSelectedIndex(e.getX(), e.getY(), mMidPoints, mapView);
                if (idx1 != -1) {
                    mMidPointSelected = true;
                    mInsertingIndex = idx1;
                } else {
                    // If tap coincides with a vertex, select that vertex
                    int idx2 = getSelectedIndex(e.getX(), e.getY(), mPoints, mapView);
                    if (idx2 != -1) {
                        mVertexSelected = true;
                        mInsertingIndex = idx2;
                    } else {
                        // No matching point above, add new vertex at tap point
                        mPoints.add(point);
                        mEditingStates.add(new EditingStates(mPoints, mMidPointSelected, mVertexSelected, mInsertingIndex));
                    }
                }
            }

            // Redraw the graphics layer
            refresh();
            // check show savebar or notx
            isSaveValidandShowSavebar();
            // check show undo button and hide searchview or not
            showUndoandHideSearchView();
        }

        /**
         * Checks if a given location coincides (within a tolerance) with a point in a given array.
         *
         * @param x Screen coordinate of location to check.
         * @param y Screen coordinate of location to check.
         * @param points Array of points to check.
         * @param map MapView containing the points.
         * @return Index within points of matching point, or -1 if none.
         */
        private int getSelectedIndex(double x, double y, ArrayList<Point> points, MapView map) {
            final int TOLERANCE = 40; // Tolerance in pixels

            if (points == null || points.size() == 0) {
                return -1;
            }

            // Find closest point
            int index = -1;
            double distSQ_Small = Double.MAX_VALUE;
            for (int i = 0; i < points.size(); i++) {
                Point p = map.toScreenPoint(points.get(i));
                double diffx = p.getX() - x;
                double diffy = p.getY() - y;
                double distSQ = diffx * diffx + diffy * diffy;
                if (distSQ < distSQ_Small) {
                    index = i;
                    distSQ_Small = distSQ;
                }
            }

            // Check if it's close enough
            if (distSQ_Small < (TOLERANCE * TOLERANCE)) {
                return index;
            }
            return -1;
        }

        /**
         * Moves the currently selected point to a given location.
         *
         * @param point Location to move the point to.
         */
        private void movePoint(Point point) {
            if (mMidPointSelected) {
                // Move mid-point to the new location and make it a vertex
                mPoints.add(mInsertingIndex + 1, point);
            } else {
                // Must be a vertex: move it to the new location
                ArrayList<Point> temp = new ArrayList<Point>();
                for (int i = 0; i < mPoints.size(); i++) {
                    if (i == mInsertingIndex) {
                        temp.add(point);
                    } else {
                        temp.add(mPoints.get(i));
                    }
                }
                mPoints.clear();
                mPoints.addAll(temp);
            }
            // Go back to the normal drawing mode and save the new editing state
            mMidPointSelected = false;
            mVertexSelected = false;
            mEditingStates.add(new EditingStates(mPoints, mMidPointSelected, mVertexSelected, mInsertingIndex));
        }

    }
}
