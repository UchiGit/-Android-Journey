package com.example.g015c1140.journey

import android.annotation.SuppressLint
import android.support.design.internal.BaselineLayout
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.util.TypedValue
import android.view.View




class BottomNavigationViewHelper {

    @SuppressLint("RestrictedApi")
            /**
     * BottomNavigationViewのアイテムのサイズの調整、アイコンサイズ調整、タイトルの削除
     *
     * @param view
     */

    fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView

        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false

            for (i in 0 until menuView.childCount) {

                /**
                 * アイテムの幅調整
                 */
                val bottomNavigationItemView = menuView.getChildAt(i) as BottomNavigationItemView

                bottomNavigationItemView.setShiftingMode(false)
                // チェックされた値を設定すると、ビューが更新されるみたい

                bottomNavigationItemView.setChecked(false)

                /**
                 * アイテムのテキストを非表示にする。
                 * アイテムのテキストビューをくくってるBaselineLayoutをGONE
                 */
                val smallLabel = menuView.getChildAt(i).findViewById<View>(android.support.design.R.id.smallLabel)
                val baselineLayout = smallLabel.parent as BaselineLayout
                baselineLayout.visibility = View.GONE

                /**
                 * アイコンサイズを40dpに調整
                 */
                val iconView = menuView.getChildAt(i).findViewById<View>(android.support.design.R.id.icon)
                val layoutParams = iconView.layoutParams
                val displayMetrics = view.resources.displayMetrics
                layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
                layoutParams.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, displayMetrics).toInt()
                iconView.layoutParams = layoutParams

            }
        } catch (e: NoSuchFieldException ) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}