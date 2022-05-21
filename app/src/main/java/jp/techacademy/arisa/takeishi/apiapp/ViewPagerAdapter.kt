package jp.techacademy.arisa.takeishi.apiapp

//FragmentStateAdapterのクラスは作成する必要はある??

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    //titleIdsはタブの名前を格納したList
    val titleIds = listOf(R.string.tab_title_api, R.string.tab_title_favorite)

    //fragmentsはページの中身
    // ここでは0ページ目がApiFragment、1ページ目がFavoriteFragmentで表示される。
    val fragments = listOf(ApiFragment(), FavoriteFragment())

    //getItemCount()はViewPager2が何ページあるのかという数字を返す。fragmentsの数を返す。
    override fun getItemCount(): Int {
        return fragments.size
    }

    //createFragment(position: Int)は引数で受け取ったpositionのページのFragmentを返す。
    override fun createFragment(position: Int): Fragment{
        return fragments[position]
    }
}