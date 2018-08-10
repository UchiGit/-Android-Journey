package com.example.g015c1140.journey

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.post.*



class PostActivity : AppCompatActivity(), OnMapReadyCallback{

    //スポットリスト用アダプター
    lateinit var spotListAdapter: ArrayAdapter<String>

    //スポット名リスト用データ
    lateinit var spotNameList:MutableList<String>

    //交通手段ボタン用
    lateinit var transportationImageButton: MutableList<ImageButton>
    //交通手段ボタンフラグ用
    val TRANSPORTATION_IMAGE_FLG = mutableListOf(0,0,0,0,0,0,0)

    //受け渡しスポットリスト用
    var spotList = mutableListOf<SpotData>()

    /******************/
    //SelectSpotActivity用
    val RESULT_CODE = 1123

    //MapPinAdd用
    lateinit var mMap:GoogleMap
    val MARKER_LIST = mutableListOf<Marker>()
    /******************/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post)

        //タイトル名セット
        title = "プラン投稿"

        //交通手段ボタン設定
        transportationImageButton = mutableListOf(findViewById(R.id.walkImageButton), findViewById(R.id.bicycleImageButton), findViewById(R.id.carImageButton), findViewById(R.id.busImageButton), findViewById(R.id.trainImageButton), findViewById(R.id.airplaneImageButton), findViewById(R.id.boatImageButton))

        //ツールバーセット
        var toolbar = toolbar
        setSupportActionBar(toolbar)
        //戻るボタンセット
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        //ボトムバー設定
        var bottomavigation: BottomNavigationView = findViewById(R.id.navigation)
        // BottomNavigationViewHelperでアイテムのサイズ、アニメーションを調整
        AdjustmentBottomNavigation().disableShiftMode(bottomavigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //Map呼び出し
        val mapFragment = fragmentManager.findFragmentById(R.id.mapFragment) as MapFragment
        mapFragment.getMapAsync(this)

        //リストビュー設定
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        spotNameList = mutableListOf("スポット追加＋")
        spotListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,spotNameList)
        // ListViewにArrayAdapterを設定する
        val spotListView: ListView = findViewById(R.id.spotListView)
        spotListView.adapter = spotListAdapter

        // 項目をタップしたときの処理
        spotListView.setOnItemClickListener {parent, view, position, id ->
            // 一番上の項目をタップしたら
            if (position == 0) {
                if (spotList.size < 20) {
                    /************************************************/
                    startActivityForResult(Intent(this, SelectSpotActivity::class.java).putExtra("SPOTCNT",spotList.size),RESULT_CODE)
                    /************************************************/
                }
            }
        }

        // 項目を長押ししたときの処理
        spotListView.setOnItemLongClickListener { parent, view, position, id ->

            // 一番下の項目以外は長押しで削除
            when(position){
                0 -> return@setOnItemLongClickListener false

                else ->{
                    AlertDialog.Builder(this).apply {
                        setTitle("スポット削除")
                        setMessage("スポット:${spotNameList[position]} を削除しますか？")
                        setPositiveButton("削除", { _, _ ->
                            // 削除をタップしたときの処理
                            //spotListAdapter.remove(spotListAdapter.getItem(position))
                            spotNameList.removeAt(position)
                            spotList.removeAt(position -1)
                            //削除した項目以下の連番更新
                            for (_cnt in position until spotNameList.size){
                                spotNameList[_cnt] = "${_cnt}：${spotList[_cnt -1].title}"
                            }
                            spotNameList[0] = "スポット追加＋"
                            spotListAdapter.notifyDataSetChanged()

                            //Pin編集
                            mapPinEdit()
                        })
                        setNegativeButton("戻る", null)
                        show()
                    }
                    return@setOnItemLongClickListener true
                }
            }
        }
    }

    /************************************************/
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        Log.d("test" , "onResult   resultCode${resultCode}        requestCode$requestCode   Intent$intent")

        if(resultCode == RESULT_OK && requestCode == RESULT_CODE && intent != null){

            val selectSpotList = intent.getSerializableExtra("SPOTDATA") as MutableList<SpotData>

            spotList.addAll(selectSpotList)
            selectSpotList.forEach { spotNameList.add("${spotNameList.size}：${it.title}") }

            if (spotList.size >= 20){
                spotNameList[0] = "これ以上スポットを追加できません"
            }else{
                spotNameList[0] = "スポット追加＋"
            }
            spotListAdapter.notifyDataSetChanged()

            //Pin編集
            mapPinEdit()
        }
    }
    /************************************************/

    //GoogleMap 設定
    override fun onMapReady(googleMap: GoogleMap) {
        //val
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val skyTree = LatLng(35.710063, 139.8107)
        //mMap.addMarker(MarkerOptions().position(skyTree).title("東京スカイツリー"))

        var cameraPosition: CameraPosition = CameraPosition.Builder()
                .target(LatLng(35.710063, 139.8107))
                .zoom(15f)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun mapPinEdit(){

        //Pin全削除
        MARKER_LIST.forEach { it.remove() }
        MARKER_LIST.clear()

        when(spotList.size) {
            //全件削除された場合
            0 -> return
            else -> {
                var cnt = 1
                spotList.forEach {
                    //Pin追加
                    var marker = mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("$cnt"))
                    cnt++
                    //情報ウィンドウ表示
                    marker.showInfoWindow()
                    MARKER_LIST.add(marker)
                }

                //camera移動
                var cameraPosition
                        : CameraPosition = CameraPosition.Builder()
                        .target(LatLng(spotList[spotList.size - 1].latitude, spotList[spotList.size - 1].longitude))
                        .zoom(17f)
                        .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //戻るボタンタップ時
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("PrivateResource")
    fun onTransportationButtonTapped(view: View){

        when(view.id){
            R.id.walkImageButton ->{
                TRANSPORTATION_IMAGE_FLG[0] = if(TRANSPORTATION_IMAGE_FLG[0] == 1) {
                    transportationImageButton[0].setImageResource(R.drawable.s_walk_off)
                    0
                }else {
                    transportationImageButton[0].setImageResource(R.drawable.s_walk_on)
                    1
                }
            }

            R.id.bicycleImageButton ->{
                TRANSPORTATION_IMAGE_FLG[1] = if(TRANSPORTATION_IMAGE_FLG[1] == 1) {
                    transportationImageButton[1].setImageResource(R.drawable.s_bicycle_off)
                    0
                }else {
                    transportationImageButton[1].setImageResource(R.drawable.s_bicycle_on)
                    1
                }
            }

            R.id.carImageButton ->{
                TRANSPORTATION_IMAGE_FLG[2] = if(TRANSPORTATION_IMAGE_FLG[2] == 1) {
                    transportationImageButton[2].setImageResource(R.drawable.s_car_off)
                    0
                }else {
                    transportationImageButton[2].setImageResource(R.drawable.s_car_on)
                    1
                }

            }

            R.id.busImageButton ->{
                TRANSPORTATION_IMAGE_FLG[3] = if(TRANSPORTATION_IMAGE_FLG[3] == 1) {
                    transportationImageButton[3].setImageResource(R.drawable.s_bus_off)
                    0
                }else {
                    transportationImageButton[3].setImageResource(R.drawable.s_bus_on)
                    1
                }

            }

            R.id.trainImageButton ->{
                TRANSPORTATION_IMAGE_FLG[4] = if(TRANSPORTATION_IMAGE_FLG[4] == 1) {
                    transportationImageButton[4].setImageResource(R.drawable.s_train_off)
                    0
                }else {
                    transportationImageButton[4].setImageResource(R.drawable.s_train_on)
                    1
                }
            }

            R.id.airplaneImageButton ->{
                TRANSPORTATION_IMAGE_FLG[5] = if(TRANSPORTATION_IMAGE_FLG[5] == 1) {
                    transportationImageButton[5].setImageResource(R.drawable.s_airplane_off)
                    0
                }else {
                    transportationImageButton[5].setImageResource(R.drawable.s_airplane_on)
                    1
                }
            }

            R.id.boatImageButton ->{
                TRANSPORTATION_IMAGE_FLG[6] = if(TRANSPORTATION_IMAGE_FLG[6] == 1) {
                    transportationImageButton[6].setImageResource(R.drawable.s_boat_off)
                    0
                }else {
                    transportationImageButton[6].setImageResource(R.drawable.s_boat_on)
                    1
                }

            }
        }
    }

    //ボトムバータップ時
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                planTitleEditText.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                planTitleEditText.setText(R.string.title_search)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorite -> {
                planTitleEditText.setText(R.string.title_favorite)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                planTitleEditText.setText(R.string.title_setting)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    fun onPostButtonTapped(view: View){
        //全部がOKな場合

        if (spotList.size != 0) {
        }else{
            Toast.makeText(this,"スポットがない",Toast.LENGTH_SHORT).show()
        }

        /********************/
        //spotを投稿
        //スポットリストがある

        val psat = PostSpotAsyncTask()
        psat.setOnCallback(object : PostSpotAsyncTask.CallbackPostSpotAsyncTask() {
            override fun callback(result: String) {
                super.callback(result)
                // ここからAsyncTask処理後の処理を記述します。
                Log.d("test SpotCallback", "非同期処理$result")

                if (result == "HTTP-OK"){

                    //spot取得
                    //スポットリストがある
                    val gsat = GetSpotAsyncTask(spotList.size)
                    gsat.setOnCallback(object : GetSpotAsyncTask.CallbackGetSpotAsyncTask() {
                        override fun callback(result: ArrayList<String>) {
                            super.callback(result)
                            // resultにはdoInBackgroundの返り値が入ります。
                            // ここからAsyncTask処理後の処理を記述します。
                            Log.d("test PlanCallback", "非同期処理$result")

                            if (result[0].equals("HTTP-OK")){

                                Log.d("test", "PostPlanAsyncTask().execute(${planTitleEditText.text.toString()},${planDetailEditText.text.toString()},${TRANSPORTATION_IMAGE_FLG.toString().replace(" ","").substring(1,14)},${planMoneySpinner.selectedItem.toString()},${planPrefecturesSpinner.selectedItem.toString()},spot_id_a,spot_id_b)")
                                //Planを投稿
                                //入力OK　プランIDある
                                val ppat = PostPlanAsyncTask()
                                ppat.setOnCallback(object : PostPlanAsyncTask.CallbackPostPlanAsyncTask() {
                                    override fun callback(result: String) {
                                        super.callback(result)
                                        // resultにはdoInBackgroundの返り値が入ります。
                                        // ここからAsyncTask処理後の処理を記述します。
                                        Log.d("test PlanCallback", "非同期処理$result")

                                        //完了した関数呼び出し
                                        completePostAsyncTask()
                                    }
                                })
                                ppat.execute(
                                        planTitleEditText.text.toString(),
                                        planDetailEditText.text.toString(),
                                        TRANSPORTATION_IMAGE_FLG.toString().replace(" ","").substring(1,14),
                                        planMoneySpinner.selectedItem.toString(),
                                        planPrefecturesSpinner.selectedItem.toString(),
                                        //spot_id_a
                                        "1",
                                        //spot_id_b
                                        "2"
                                )

                            }
                        }
                    })
                    gsat.execute()
                }
            }
        })
        psat.execute(spotList)

        /***********************/
        /*
        //Planを投稿
        //入力OK　プランIDある
        Log.d("test", "PostPlanAsyncTask().execute(${planTitleEditText.text.toString()},${planDetailEditText.text.toString()},${TRANSPORTATION_IMAGE_FLG.toString().replace(" ","").substring(1,14)},${planMoneySpinner.selectedItem.toString()},${planPrefecturesSpinner.selectedItem.toString()},spot_id_a,spot_id_b)")
        val ppat = PostPlanAsyncTask()
        ppat.setOnCallback(object : PostPlanAsyncTask.CallbackPostPlanAsyncTask() {
            override fun callback(result: String) {
                super.callback(result)
                // resultにはdoInBackgroundの返り値が入ります。
                // ここからAsyncTask処理後の処理を記述します。
                Log.d("test PlanCallback", "非同期処理$result")
            }
        })
        ppat.execute(
                planTitleEditText.text.toString(),
                planDetailEditText.text.toString(),
                TRANSPORTATION_IMAGE_FLG.toString().replace(" ","").substring(1,14),
                planMoneySpinner.selectedItem.toString(),
                planPrefecturesSpinner.selectedItem.toString(),
                //spot_id_a
                "1",
                //spot_id_b
                "2"
        )

        /***********************/
        //spot取得
        //スポットリストがある

        /************************/

*/
        Toast.makeText(this, "投稿", Toast.LENGTH_SHORT).show()
        spotList.forEach { Log.d("test", "    spotList:${it.title}") }
        Log.d("test", "spotNameList:$spotNameList")
    }

    fun completePostAsyncTask(){
        Log.d("test", "complatePostAsyncTask")
        Toast.makeText(this, "投稿", Toast.LENGTH_SHORT).show()
    }
}
