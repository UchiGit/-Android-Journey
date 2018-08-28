package com.example.g015c1140.journey

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import kotlinx.android.synthetic.main.activity_select_spot.*
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


//import sun.util.locale.provider.LocaleProviderAdapter.getAdapter

class SelectSpotActivity : AppCompatActivity() {

    //インテントで来たリスト用
    var spotList = arrayListOf<SpotData>()

    //インテントするリスト用
    var newSpotList = arrayListOf<SpotData>()

    //spotListから生成する個人が追加した表示用のスポット名用
    val spotNameList = ArrayList<String>()

    //選択したリスト項目の名前を表示するリスト用
    val newSpotNameList = ArrayList<String>()

    //登録した用
    lateinit var userSpotListView : ListView

    //選択した用
    lateinit var selectSpotListView : ListView

    /****************/// test you
    lateinit var userSpotAdapter: ArrayAdapter<String>
    /******************/

    /****************/
    //インテント用スポットカウント
    var spotCnt = 0
    /****************/
    var nowSort = "昇順"

    val RESULT_SUBACTIVITY = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_spot)
        //spotList = intent.getSerializableExtra("SPOTDATA") as ArrayList<SpotData>

        /****************/
        spotCnt = intent.getIntExtra("SPOTCNT",100)
        /****************/

        val spinner = findViewById<Spinner>(R.id.sort)

        val df = SimpleDateFormat("yyyy/MM/dd ")

        for(_spotList in spotList) {
            spotNameList.add(df.format(_spotList.dateTime) + "：" + _spotList.title)
        }

        // テストのためuserSpotAdapterなどのvalなどの宣言をクラス変数に移動した
        //val
        userSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spotNameList)
        userSpotListView = findViewById(R.id.userSpotList)
        userSpotListView.adapter = userSpotAdapter

        val selectSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newSpotNameList)
        selectSpotListView = findViewById(R.id.selectSpotList)
        selectSpotListView.adapter = selectSpotAdapter

        userSpotListView.setOnItemClickListener { parent, _, position, _ ->

            /****************/
            if (spotCnt < 20) {
            /****************/
                val msg = (position + 1).toString() + "番目のアイテムが追加されました"
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
                val list = parent as ListView
                val item = list.getItemAtPosition(position) as String
                val adapter = list.adapter as ArrayAdapter<String>
                selectSpotAdapter.add(userSpotAdapter.getItem(position))
                newSpotList.add(spotList.get(position))
                selectSpotListView.adapter = selectSpotAdapter
                adapter.remove(item)
                spotList.removeAt(position)
                Log.d("test", newSpotNameList.toString())
            /****************/
                spotCnt++
            }else{
                Toast.makeText(this, "スポットは最大20件までです", Toast.LENGTH_SHORT).show()
            }
            /****************/
        }

        // リスナーを登録
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                val spinner = parent as Spinner
                val item = spinner.selectedItem as String
                if(nowSort != item){
                    sortList()
                    nowSort = item
                }
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>) {
                //
            }
        }

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            if (spotCnt < 20) {
               val intent = Intent(application, PlacePicker::class.java)
                startActivityForResult(intent, RESULT_SUBACTIVITY)
                spotCnt++
            }else{
                Toast.makeText(this, "スポットは最大20件までです", Toast.LENGTH_SHORT).show()
            }
        }

        /************/
        //toolbar設定
        setSupportActionBar(toolbar)
        title = "スポット追加"
        //戻るボタンセット
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        /************/

        /************/
        //ボトムバー設定
        var bottomavigation: BottomNavigationView = findViewById(R.id.navigation)
        // BottomNavigationViewHelperでアイテムのサイズ、アニメーションを調整
        AdjustmentBottomNavigation().disableShiftMode(bottomavigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        /****************/

    }

    /****************/
    //toolbar設置
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_selectspot, menu)
        return true
    }
    /****************/

    /****************/
    //toolbarタップ時
    override fun onOptionsItemSelected(item: MenuItem?) = when(item!!.itemId){
        //登録ボタンタップ時
        R.id.saveButton ->{
            Toast.makeText(this, "登録ボタン", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK,Intent().putExtra("SPOTDATA",newSpotList))
            finish()
            true
        }
        //戻るボタンタップ時
        android.R.id.home -> {
            Toast.makeText(this, "もどーるぼたんたっぷど", Toast.LENGTH_SHORT).show()
            finish()
            true
        }
        else -> false
    }
    /****************/

    /****************/
    //ボトムバータップ時
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                //spotNameEditText.setText(R.string.title_home)
                spotList.add(SpotData("tmp${spotList.size}",1.0,2.0,"","","","",Date()))
                spotNameList.add(spotList[spotList.size -1].title)
                userSpotAdapter.notifyDataSetChanged()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                //spotNameEditText.setText(R.string.title_search)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_favorite -> {
                //spotNameEditText.setText(R.string.title_favorite)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                //spotNameEditText.setText(R.string.title_setting)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    /****************/

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_SUBACTIVITY && null != intent) {
            Log.d("エックス", intent.getStringExtra("LatLngX"))
            Log.d("ワイ", intent.getStringExtra("LatLngY"))
            Log.d("名前", intent.getStringExtra("NAME"))
            //val GETPOINT = LatLng(Double.parseDouble(intent.getStringExtra("LatLngX")), Double.parseDouble(intent.getStringExtra("LatLngY")))
            /*for(_newSpotList in newSpotList) {
                newSpotNameList.add(_newSpotList.name)
            }*/
            val selectSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,newSpotNameList)
            selectSpotAdapter.add(intent.getStringExtra("NAME"))
            newSpotList.add(SpotData(intent.getStringExtra("NAME"),parseDouble(intent.getStringExtra("LatLngX")),parseDouble(intent.getStringExtra("LatLngY")),"","","","",Date()))

            selectSpotListView.adapter = selectSpotAdapter
        }
    }

    fun sortList(){
        //val tempSpotList = spotList
        //val tempSpotNameList = spotNameList
        spotList.reverse()
        spotNameList.reverse()
        val userSpotAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spotNameList)
        userSpotListView.adapter = userSpotAdapter
        /*spotList.clear()
        for(_tempSpot in tempSpotList) {
            spotList.add(_tempSpot)
        }*/
    }
}
